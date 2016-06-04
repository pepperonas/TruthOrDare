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
import android.widget.TextView;

import com.pepperonas.andbasx.base.TextViewUtils;
import com.pepperonas.jbasx.div.MaterialColor;
import com.pepperonas.truthordare.MainActivity;
import com.pepperonas.truthordare.R;
import com.pepperonas.truthordare.model.Player;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class FragmentSelectAction extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentSelectAction";

    private static int sRountCounter = 0;

    private MainActivity mMain;

    private Player mReader, mActor;


    public static FragmentSelectAction newInstance(int i) {
        FragmentSelectAction fragment = new FragmentSelectAction();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        sRountCounter++;

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_action, null, false);
        mMain = (MainActivity) getActivity();
        mMain.setTitle(getString(R.string.select_action));
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated()");

        TextView tvCounter = (TextView) view.findViewById(R.id.tv_round_ctr);
        TextView tvReader = (TextView) view.findViewById(R.id.tv_reader);
        TextView tvAction = (TextView) view.findViewById(R.id.tv_player);

        initPlayers();

        String roundCounterText = String.format(mMain.getResources().getString(R.string.round_counter), sRountCounter);
        TextViewUtils.resizeColorized(tvCounter, roundCounterText, MaterialColor.PINK_A400, TextViewUtils.TextSize.LARGE);
        TextViewUtils.setBoldAndItalic(tvCounter);

        tvReader.setText(String.format("%s %s.", mReader.getName(), getString(R.string.reads_the_question)));
        tvAction.setText(String.format("%s, %s?", getString(R.string.truth_or_dare), mActor.getName()));

        mMain.setNavigationViewSubtitle(mMain.getString(R.string.game_is_running) + " " + sRountCounter);

        setListeners(view);
    }


    @Override
    public void onClick(View v) {
        logClick((Button) v);

        switch (v.getId()) {
            case R.id.btn_dare:
                mMain.makeFragmentTransaction(FragmentAction.newInstance(0, FragmentAction.DARE, mReader, mActor));
                break;

            case R.id.btn_truth:
                mMain.makeFragmentTransaction(FragmentAction.newInstance(0, FragmentAction.TRUTH, mReader, mActor));
                break;
        }
    }


    private void initPlayers() {
        mReader = mMain.getCurrentPlayer();
        mMain.updateCurrentPlayer();
        mActor = mMain.getCurrentPlayer();
    }


    private void setListeners(View view) {
        Button btnDare = (Button) view.findViewById(R.id.btn_dare);
        Button btnTruth = (Button) view.findViewById(R.id.btn_truth);
        btnDare.setOnClickListener(this);
        btnTruth.setOnClickListener(this);
    }


    public static void resetRoundCounter() {
        sRountCounter = 0;
    }


    public static int getRoundCounter() {
        return sRountCounter;
    }


    // TODO: remove
    private void logClick(Button v) {
        Button clicked = v;
        Log.d(TAG, "onClick " + clicked.getText().toString());
    }
}
