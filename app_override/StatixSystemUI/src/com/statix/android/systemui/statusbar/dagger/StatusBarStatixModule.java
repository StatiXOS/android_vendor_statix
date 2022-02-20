/*
 * Copyright (C) 2021 StatiXOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.statix.android.systemui.statusbar.dagger;

import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule;
import com.android.systemui.statusbar.notification.row.NotificationRowModule;

import com.statix.android.systemui.statusbar.phone.dagger.StatusBarStatixPhoneModule;

import dagger.Module;

@Module(includes = {StatusBarStatixPhoneModule.class, StatusBarDependenciesModule.class,
        NotificationsModule.class, NotificationRowModule.class})
public interface StatusBarStatixModule {
}
