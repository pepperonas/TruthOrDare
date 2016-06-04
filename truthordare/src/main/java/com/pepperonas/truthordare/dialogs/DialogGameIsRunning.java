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

import com.pepperonas.truthordare.MainActivity;
import com.pepperonas.truthordare.R;
import com.pepperonas.truthordare.fragments.FragmentSelectAction;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class DialogGameIsRunning {

    public DialogGameIsRunning(final MainActivity main, final int mLastSelectedNavItemPos) {
        new AlertDialog.Builder(main)
                .setTitle(main.getString(R.string.warning))
                .setMessage(main.getString(R.string.dlg_game_is_running_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentSelectAction.resetRoundCounter();
                        main.getPlayers().clear();
                        main.setNavigationViewSubtitle(main.getString(R.string.start_a_game));
                        if (mLastSelectedNavItemPos < 3) {
                            selectFromFirstGroup(main, mLastSelectedNavItemPos);
                        } else {
                            selectFromSecondGroup(main, mLastSelectedNavItemPos);
                        }

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void selectFromFirstGroup(MainActivity main, int mLastSelectedNavItemPos) {
        main.selectNavViewItem(main.getNavigationView()
                                   .getMenu().getItem(0).getSubMenu()
                                   .getItem(mLastSelectedNavItemPos));
    }


    /**
     * IMPORTANT NOTE:
     * If we add more items to the NavigationView, may leads to rearrange the selection here.
     */
    private void selectFromSecondGroup(MainActivity main, int mLastSelectedNavItemPos) {
        main.selectNavViewItem(main.getNavigationView()
                                   .getMenu().getItem(1).getSubMenu()
                                   .getItem(mLastSelectedNavItemPos == 3 ? 0 : 1));
    }
}
