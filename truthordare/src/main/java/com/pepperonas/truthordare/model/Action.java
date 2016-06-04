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
public abstract class Action {

    int id = 0;
    private String text = "";
    private GenderType genderType = GenderType.BOTH;
    private boolean isMultipleTimesPlayable = true;
    private int playedById = -1;


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


    public GenderType getGenderType() {
        return genderType;
    }


    public void setGenderType(GenderType genderType) {
        this.genderType = genderType;
    }


    public void setIsMultipleTimesPlayable(boolean isMultipleTimesPlayable) {
        this.isMultipleTimesPlayable = isMultipleTimesPlayable;
    }


    public boolean isMultipleTimesPlayable() {
        return isMultipleTimesPlayable;
    }


    public int getPlayedById() {
        return playedById;
    }


    public void setPlayedById(int playedById) {
        this.playedById = playedById;
    }
}
