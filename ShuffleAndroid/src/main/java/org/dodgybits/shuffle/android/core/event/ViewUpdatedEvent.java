/**
 * Copyright (C) 2014 Android Shuffle Open Source Project
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
package org.dodgybits.shuffle.android.core.event;

import org.dodgybits.shuffle.android.core.view.MainView;

public class ViewUpdatedEvent {
    private final MainView mMainView;

    public ViewUpdatedEvent(MainView mainView) {
        mMainView = mainView;
    }

    public MainView getMainView() {
        return mMainView;
    }

    @Override
    public String toString() {
        return "ViewUpdatedEvent{" +
                "mMainView=" + mMainView +
                '}';
    }
}