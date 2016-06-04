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

package com.pepperonas.truthordare.dialogs;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.truthordare.MainActivity;
import com.pepperonas.truthordare.R;
import com.pepperonas.truthordare.model.Action;
import com.pepperonas.truthordare.model.Dare;
import com.pepperonas.truthordare.model.Gender;
import com.pepperonas.truthordare.model.Player;
import com.pepperonas.truthordare.fragments.FragmentAction;
import com.pepperonas.truthordare.fragments.FragmentSelectAction;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class DialogCry4Help {

    public DialogCry4Help(final MainActivity main, final Player reader, final Player actor, final Action action) {

        String genderInfo = actor.getGender() == Gender.FEMALE ? "she" : "he";

        new AlertDialog.Builder(main)
                .setTitle(main.getString(R.string.dlg_title_cry_4_help))
                .setMessage("Should " + actor.getName() + " leave the game? If not, " + genderInfo + " will get a new try.")
                .setPositiveButton("ONE MORE TRY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (action instanceof Dare) {
                            main.makeFragmentTransaction(FragmentAction.newInstance(0, FragmentAction.DARE, reader, actor));
                        } else {
                            main.makeFragmentTransaction(FragmentAction.newInstance(0, FragmentAction.TRUTH, reader, actor));
                        }
                    }
                })
                .setNegativeButton(genderInfo + " MUST LEAVE!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        main.getPlayers().remove(actor.getId());
                        ToastUtils.toastShort(actor.getName() + " is OUT!");
                        if (main.getCurrentPlayerCounter() >= main.getPlayers().size())
                            main.setCurrentPlayerCounter(0);
                        dialog.dismiss();
                        main.makeFragmentTransaction(FragmentSelectAction.newInstance(0));
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
