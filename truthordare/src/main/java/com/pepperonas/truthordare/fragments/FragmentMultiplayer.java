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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.truthordare.MainActivity;
import com.pepperonas.truthordare.R;
import com.pepperonas.truthordare.config.Toaster;
import com.pepperonas.truthordare.model.Gender;
import com.pepperonas.truthordare.model.Player;
import com.pepperonas.truthordare.various.PlayerAdapter;
import com.pepperonas.truthordare.various.SwipeableRecyclerViewTouchListener;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class FragmentMultiplayer extends Fragment implements MenuItem.OnMenuItemClickListener {

    private static final String TAG = "FrgmtMltplyr";

    private MainActivity mMain;

    private PlayerAdapter mAdapter;
    private LinearLayoutManager mLlm;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private int mMutedColor;


    public static FragmentMultiplayer newInstance(int i) {
        FragmentMultiplayer fragment = new FragmentMultiplayer();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Toaster.swipeToRemove();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_multiplayer, container, false);
        mMain = (MainActivity) getActivity();
        mMain.setTitle(getString(R.string.multiplayer));
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(0, 0, 0, "Start").setIcon(R.drawable.ic_marked_circle_white_24dp)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.getItem(0).setOnMenuItemClickListener(this);
    }


    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated()");

        initCollapsingToolbar();

        initRecycler();

        FloatingActionButton fabAddPlayer = (FloatingActionButton) view.findViewById(R.id.fab_add_player);
        fabAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMain.getPlayers().add(new Player());
                mAdapter.notifyItemInserted(mMain.getPlayers().size() - 1);
                mCollapsingToolbar.setTitle(mMain.getPlayers().size() + " " + getString(R.string.players));
            }
        });
    }


    private void initCollapsingToolbar() {
        mCollapsingToolbar = (CollapsingToolbarLayout) mMain.findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setTitle(2 + " " + getString(R.string.players));

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.collapsing_toolbar_background);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                mMutedColor = palette.getMutedColor(ContextCompat.getColor(mMain, R.color.primary_500));
                mCollapsingToolbar.setContentScrimColor(mMutedColor);
            }
        });
    }


    private void initRecycler() {
        RecyclerView rv = (RecyclerView) getActivity().findViewById(R.id.recycler);
        rv.setHasFixedSize(true);
        mLlm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLlm);

        setDefaultData();

        mAdapter = new PlayerAdapter(mMain);
        rv.setAdapter(mAdapter);

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(
                        rv, new SwipeableRecyclerViewTouchListener.SwipeListener() {
                    @Override
                    public boolean canSwipe(int position) {
                        return true;
                    }


                    @Override
                    public void onDismissedBySwipeLeft(RecyclerView rv, int[] reverseSortedPositions) {
                        removeFromRecycler(reverseSortedPositions);
                    }


                    @Override
                    public void onDismissedBySwipeRight(RecyclerView rv, int[] reverseSortedPositions) {
                        removeFromRecycler(reverseSortedPositions);

                    }
                });

        rv.addOnItemTouchListener(swipeTouchListener);
    }


    private void removeFromRecycler(int[] reverseSortedPositions) {
        if (mMain.getPlayers().size() < 3) {
            ToastUtils.toastShort(R.string.at_least_two_players);
            return;
        }

        int removed = 0;
        for (int i : reverseSortedPositions) {
            removed = i;
        }
        mMain.getPlayers().remove(removed);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick  " + "");

        if (mLlm.getChildCount() < 2) {
            ToastUtils.toastShort(R.string.at_least_two_players);
            return false;
        }

        mMain.getPlayers().clear();

        Player tmpPlayer;

        for (int i = 0; i < mLlm.getChildCount(); i++) {

            if (mLlm.getChildAt(i) instanceof Button) continue;

            LinearLayout playerLayout = (LinearLayout) mLlm.getChildAt(i);

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
            return true;
        }
        return false;
    }


    // TODO: remove
    private void setDefaultData() {
        mMain.getPlayers().add(new Player(
                mMain.getPlayers().size(),
                "Alf", 2, Gender.MALE));
        mMain.getPlayers().add(new Player(
                mMain.getPlayers().size(),
                "Bernadette", 2, Gender.FEMALE));
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
