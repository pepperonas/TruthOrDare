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

package com.pepperonas.truthordare.model;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class Truth extends Action {

    public Truth() {

    }


    public Truth(int id, String text, GenderType genderType, boolean isMultipleTimesPlayable, int playedById) {
        this.setId(id);
        this.setText(text);
        this.setGenderType(genderType);
        this.setIsMultipleTimesPlayable(isMultipleTimesPlayable);
        this.setPlayedById(playedById);
    }


    private boolean isGroupQuestion = false;


    public boolean isGroupQuestion() {
        return isGroupQuestion;
    }


    public void setIsGroupQuestion(boolean isGroupQuestion) {
        this.isGroupQuestion = isGroupQuestion;
    }
}
