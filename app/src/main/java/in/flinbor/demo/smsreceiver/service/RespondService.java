/*
 * Copyright 2016 Flinbor Aleksandr Bogdanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.flinbor.demo.smsreceiver.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Stub required for Setting app as default SMS messaging app
 */
public class RespondService extends IntentService {
    private static final String TAG = "RespondService";
    public RespondService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        //stub
    }
}
