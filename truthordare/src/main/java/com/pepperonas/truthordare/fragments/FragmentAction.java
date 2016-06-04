/*
 * Copyright (c) 2016 Martin Pfeffer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pepperonas.truthordare.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pepperonas.andbasx.base.TextViewUtils;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.jbasx.div.MaterialColor;
import com.pepperonas.truthordare.MainActivity;
import com.pepperonas.truthordare.R;
import com.pepperonas.truthordare.model.Action;
import com.pepperonas.truthordare.model.Dare;
import com.pepperonas.truthordare.model.Player;
import com.pepperonas.truthordare.dialogs.DialogCry4Help;
import com.pepperonas.truthordare.dialogs.DialogImpossibleTask;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class FragmentAction extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentAction";

    public static final int DARE = 0, TRUTH = 1;

    private static int sSelection;

    private static Player sReader, sActor;

    private MainActivity mMain;

    private RadioButton mRbJoker, mRbImpossible;
    private Action mAction;


    public static FragmentAction newInstance(int i, int selection, Player reader, Player actor) {
        FragmentAction fragment = new FragmentAction();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        sSelection = selection;
        sReader = reader;
        sActor = actor;

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_action, container, false);
        mMain = (MainActivity) getActivity();

        mMain.setTitle(sSelection == DARE ? getString(R.string.dare)
                                          : getString(R.string.truth));

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated()");

        mAction = (sSelection == DARE) ? loadDare() : loadTruth();

        TextView tvReader = (TextView) view.findViewById(R.id.tv_reader);
        TextView tvAction = (TextView) view.findViewById(R.id.tv_action);
        mRbJoker = (RadioButton) view.findViewById(R.id.rb_joker);
        mRbImpossible = (RadioButton) view.findViewById(R.id.rb_impossible);
        Button btnApply = (Button) view.findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(this);

        Button btnCry4Help = (Button) view.findViewById(R.id.btn_cry_4_help);

        mRbJoker.setText(String.format("%s: %d", getString(R.string.jokers), sActor.getJokers()));
        mRbJoker.setEnabled(sActor.getJokers() > 0);
        if (sActor.getJokers() <= 0) {
            mRbJoker.setChecked(false);
            if (mMain.getPlayers().size() > 2) {
                btnCry4Help.setVisibility(View.VISIBLE);
                btnCry4Help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DialogCry4Help(mMain, sReader, sActor, mAction);
                    }
                });
            } else {
                btnCry4Help.setVisibility(View.VISIBLE);
                btnCry4Help.setText(R.string.Cry4Help);
                btnCry4Help.setEnabled(false);
                ToastUtils.toastShort("'Cry4Help' needs at least 3 players");
            }
        } else {
            btnCry4Help.setVisibility(View.INVISIBLE);
            btnCry4Help.setOnClickListener(null);
        }


        if (sSelection == DARE) {
            tvReader.setText(String.format("%s, %s %s %s", sReader.getName(), getString(R.string.tell), sActor.getName(), getString(R.string.what_to_do)));
        } else {
            tvReader.setText(String.format("%s, %s", sReader.getName(), getString(R.string.please_read_the_question)));
        }

        TextViewUtils.setItalic(tvAction);
        String action = "uuups..";

        if (mAction != null && mAction.getText() != null) {
            action = mAction.getText();
        }

        TextViewUtils.colorize(tvAction, sActor.getName() + " " + action, MaterialColor.TEAL_100);
    }


    @Override
    public void onClick(View v) {
        if (mAction == null) {
            mMain.makeFragmentTransaction(FragmentSelectAction.newInstance(0));
            return;
        }

        if (mRbJoker.isChecked()) {
            sActor.setJokers(sActor.getJokers() - 1);
        }

        if (mRbImpossible.isChecked()) {
            new DialogImpossibleTask(mMain);
        } else {
            if (mAction instanceof Dare) {
                mMain.getDatabase().updateDare(mAction.getId(), sActor.getId());
            } else {
                mMain.getDatabase().updateTruth(mAction.getId(), sActor.getId());
            }
            sActor.getStrike().setPlayable(mAction);
            mMain.makeFragmentTransaction(FragmentSelectAction.newInstance(0));
        }
    }


    private Action loadDare() {
        return mMain.getDatabase().getDare(sActor, true);
    }


    private Action loadTruth() {
        return mMain.getDatabase().getTruth(sActor, true);
    }

}
