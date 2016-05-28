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

package in.flinbor.demo.smsreceiver.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import in.flinbor.demo.smsreceiver.service.MessagingService;
import in.flinbor.demo.smsreceiver.util.Utils;

/**
 * BroadcastReceiver for listening SMS_RECEIVED_ACTION
 * Works only as an observer of new messages, cannot write to the SMS Provider on Android 4.4 and up
 * can not be aborted, works asynchronously with another Broadcasts
 */
public class SmsReceiver extends WakefulBroadcastReceiver {
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Utils.isDefaultSmsApp(context)) { //check is app not default sms app
            if (intent != null && intent.getAction() != null &&
                    ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
                handleIncomingSms(context, intent);
            }
        }
    }

    private void handleIncomingSms(Context context, Intent intent) {
        // we'll start a wakeful service to handle the SMS
        intent.setAction(MessagingService.ACTION_MY_RECEIVE_SMS);
        intent.setClass(context, MessagingService.class);
        startWakefulService(context, intent);
    }
}
