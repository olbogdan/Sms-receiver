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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import in.flinbor.demo.smsreceiver.data.SmsContentProvider;
import in.flinbor.demo.smsreceiver.data.SmsTable;
import in.flinbor.demo.smsreceiver.data.model.SMSObject;
import in.flinbor.demo.smsreceiver.receiver.SmsDeliver;
import in.flinbor.demo.smsreceiver.receiver.SmsReceiver;
import in.flinbor.demo.smsreceiver.view.NotificationDialogActivity;

/**
 * This service is triggered internally only and is used to process incoming SMS messages
 * that the {@link SmsDeliver} passes over. It's
 * preferable to handle these in a service in case there is significant work to do which may exceed
 * the time allowed in a receiver.
 */
public class MessagingService extends IntentService {
    private static final String TAG = MessagingService.class.getSimpleName();
    /**
     * These actions are for this app only and are used by {@link SmsDeliver} & {@link SmsReceiver}  to start this service
      */
    public static final String ACTION_MY_RECEIVE_SMS = "in.bogdanov.flinborsms.demo.smsreseiver.service.RECEIVE_SMS";
    private static int notificationId = 1;

    public MessagingService() {
        super(TAG);
    }

    /**
     * Store received SMS data
     * show status bar notification
     *
     * @param intent intent with SMS bundle
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String intentAction = intent.getAction();
        if (ACTION_MY_RECEIVE_SMS.equals(intentAction)) {

            SmsMessage[] parts = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            SMSObject messageObject = null;
            /**
             * message could be divided on parts because of limit of content length
             * iterate through the parts to create message
             */
            for (int i = 0; i < parts.length; i++) {
                SmsMessage part = parts[i];
                if (messageObject == null) {
                    messageObject = new SMSObject(part.getDisplayOriginatingAddress());
                }
                messageObject.addContent(part.getDisplayMessageBody());
            }

            if (messageObject != null) {
                Log.i(TAG, "message: " + messageObject.getSender() + " - " + messageObject.getTimestamp());
                saveSms(messageObject);

                // release wakelock that was created by the WakefulBroadcastReceiver
                SmsDeliver.completeWakefulIntent(intent);

                // show notification
                createNotification(messageObject);
            }
        }
    }

    /**
     * Show status bar notification with sender’s number and timestamp
     * User can click on the status bar notification, which will bring up an dialog to
     * show the contents of the selected SMS
     * Once contents of SMS displayed to user, status bar notification removed
     *
     * @param messageObject message object
     */
    private void createNotification(SMSObject messageObject) {
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        String formattedDate = f.format(messageObject.getTimestamp());

        Intent intent = new Intent(this, NotificationDialogActivity.class);
        intent.putExtra(NotificationDialogActivity.SMS_BUNDLE, messageObject);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.sym_action_email)
                        .setContentTitle(messageObject.getSender())
                        .setContentText(formattedDate)
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true);
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(notificationId++, mBuilder.build());
    }

    /**
     * save message into database
     *
     * @param message message to store
     * @return Uri of inserted message
     */
    private Uri saveSms(SMSObject message) {
        Date now = new Date();
        long timeStamp = now.getTime();

        ContentValues values = new ContentValues();
        values.put(SmsTable.COLUMN_TIMESTAMP, timeStamp);
        values.put(SmsTable.COLUMN_CONTENT, message.getContent());
        values.put(SmsTable.COLUMN_SENDER, message.getSender());

        return getContentResolver().insert(SmsContentProvider.CONTENT_URI, values);
    }
}