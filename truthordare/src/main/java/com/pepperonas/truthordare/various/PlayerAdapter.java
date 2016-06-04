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

package com.pepperonas.truthordare.various;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pepperonas.truthordare.MainActivity;
import com.pepperonas.truthordare.R;
import com.pepperonas.truthordare.model.Gender;
import com.pepperonas.truthordare.model.Player;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private static final String TAG = "PlayerAdapter";

    private MainActivity mMain;

    private int lastPosition = -1;


    public PlayerAdapter(MainActivity main) {
        mMain = main;
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }


    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_layout, parent, false);
        return new PlayerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final PlayerViewHolder holder, final int pos) {
        holder.tvPlayerNumber.setText(String.format("%d. %s", pos + 1, mMain.getString(R.string.player)));

        holder.name.setText(mMain.getPlayers().get(pos).getName());

        holder.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


            @Override
            public void afterTextChanged(Editable s) {
                storeOnTheFly(holder, getPlayerNumber(holder.tvPlayerNumber.getText().toString().split("\\.")[0]));
            }
        });

        holder.jokers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


            @Override
            public void afterTextChanged(Editable s) {
                storeOnTheFly(holder, getPlayerNumber(holder.tvPlayerNumber.getText().toString().split("\\.")[0]));
            }
        });

        holder.rbMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                storeOnTheFly(holder, getPlayerNumber(holder.tvPlayerNumber.getText().toString().split("\\.")[0]));
            }
        });



        if (mMain.getPlayers().get(pos).getJokers() == -1) {
            holder.jokers.setText("0");
        } else holder.jokers.setText(String.valueOf(mMain.getPlayers().get(pos).getJokers()));

        if (mMain.getPlayers().get(pos).getGender() == Gender.FEMALE) {
            holder.rbFemale.setChecked(true);
        } else holder.rbMale.setChecked(true);


        setAnimation(holder.cv, pos);
    }


    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mMain, R.anim.anim_push_down_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    private int getPlayerNumber(String value) {return Integer.parseInt(String.valueOf(value)) - 1;}


    private void storeOnTheFly(PlayerViewHolder holder, int pos) {
        String name = "";
        int jokers = 0;
        Gender gender;

        if (holder.name != null) {
            if (holder.name.getText() != null) {
                name = holder.name.getText().toString();
            }
        }
        if (holder.jokers != null) {
            if (holder.jokers.getText() != null && !holder.jokers.getText().toString().isEmpty()) {
                try {
                    jokers = Integer.parseInt(holder.jokers.getText().toString());
                } catch (NumberFormatException e) {
                    Log.e(TAG, "afterTextChanged ");
                }

            }
        }
        if (holder.rbFemale != null) {
            if (holder.rbFemale.isChecked()) {
                gender = Gender.FEMALE;
            } else {
                gender = Gender.MALE;
            }
        } else {
            gender = Gender.MALE;
        }

        mMain.getPlayers().set(pos, new Player(pos, name, jokers, gender));
    }


    @Override
    public int getItemCount() {
        return mMain.getPlayers().size();
    }


    public class PlayerViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPlayerNumber;
        private CardView cv;
        private EditText name;
        private EditText jokers;
        private RadioButton rbFemale, rbMale;


        public PlayerViewHolder(View itemView) {
            super(itemView);
            tvPlayerNumber = (TextView) itemView.findViewById(R.id.tv_player_number);
            cv = (CardView) itemView.findViewById(R.id.cv);
            name = (EditText) itemView.findViewById(R.id.et_name);
            jokers = (EditText) itemView.findViewById(R.id.et_jokers);
            rbFemale = (RadioButton) itemView.findViewById(R.id.rb_female);
            rbMale = (RadioButton) itemView.findViewById(R.id.rb_male);
        }
    }
}
