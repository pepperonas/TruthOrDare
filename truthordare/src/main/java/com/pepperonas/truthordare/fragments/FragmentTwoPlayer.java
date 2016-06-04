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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.truthordare.MainActivity;
import com.pepperonas.truthordare.R;
import com.pepperonas.truthordare.model.Gender;
import com.pepperonas.truthordare.model.Player;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class FragmentTwoPlayer extends Fragment implements MenuItem.OnMenuItemClickListener {

    private static final String TAG = "FragmentTwoPlayer";

    private MainActivity mMain;

    private LinearLayout mLinearFrame;


    public static FragmentTwoPlayer newInstance(int i) {
        FragmentTwoPlayer fragment = new FragmentTwoPlayer();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 0, 0, "Start").setIcon(R.drawable.ic_marked_circle_white_24dp)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.getItem(0).setOnMenuItemClickListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_two_player, container, false);
        mMain = (MainActivity) getActivity();
        mMain.setTitle(getString(R.string.two_player));
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated()");

        mLinearFrame = (LinearLayout) view.findViewById(R.id.layout_two_player_frame);

        addLayout(1, savedInstanceState);
        addLayout(2, savedInstanceState);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Player tmpPlayer;

        for (int i = 0; i < mLinearFrame.getChildCount(); i++) {

            if (mLinearFrame.getChildAt(i) instanceof Button) continue;

            LinearLayout playerLayout = (LinearLayout) mLinearFrame.getChildAt(i);

            EditText etName = (EditText) playerLayout.findViewById(R.id.et_name);
            EditText etJokers = (EditText) playerLayout.findViewById(R.id.et_jokers);
            RadioButton radioFemale = (RadioButton) playerLayout.findViewById(R.id.rb_female);

            if (ensureInput(etName, etJokers)) return false;

            mMain.getPlayers().add(tmpPlayer = new Player(
                    mMain.getPlayers().size(),
                    etName.getText().toString(),
                    Integer.parseInt(etJokers.getText().toString()),
                    radioFemale.isChecked() ? Gender.FEMALE : Gender.MALE));

            mMain.getDatabase().addPlayer(tmpPlayer.getId(), tmpPlayer.getName(), tmpPlayer.getGender(), true);
        }

        showPlayerLog();

        mMain.makeFragmentTransaction(FragmentSelectAction.newInstance(0));

        return true;
    }


    private boolean ensureInput(EditText etName, EditText etJokers) {
        if (etJokers.getText().toString().isEmpty()
            || etName.getText().toString().isEmpty()) {
            ToastUtils.toastShort(R.string.data_missing);
            mMain.getPlayers().clear();
            return true;
        }
        return false;
    }


    private void addLayout(int i, Bundle savedInstanceState) {
        View v = getLayoutInflater(savedInstanceState).inflate(R.layout.player_layout, null);

        TextView tvPlayerNumber = (TextView) v.findViewById(R.id.tv_player_number);
        tvPlayerNumber.setText(String.format("%d. %s", i, getString(R.string.player)));

        setDefaultData(i, v);

        mLinearFrame.addView(v);
    }


    // TODO: remove
    private void setDefaultData(int i, View v) {
        EditText etName = (EditText) v.findViewById(R.id.et_name);
        EditText etJokers = (EditText) v.findViewById(R.id.et_jokers);
        if (i == 1) {
            etName.setText("Julia");
            etJokers.setText("2");
        } else {
            etName.setText("Ismail");
            etJokers.setText("2");
            RadioButton radioMale = (RadioButton) v.findViewById(R.id.rb_male);
            radioMale.setChecked(true);
        }
    }


    // TODO: remove
    private void showPlayerLog() {
        int i = 0;
        for (Player player : mMain.getPlayers()) {
            Log.d(TAG, "..\n" + (i++) + ". Player " + "Name: " + player.getName() + " " +
                       "Gender: " + (player.getGender() == Gender.FEMALE ? "female" : "male") + " " +
                       "Joker: " + player.getJokers());
        }
    }
}
