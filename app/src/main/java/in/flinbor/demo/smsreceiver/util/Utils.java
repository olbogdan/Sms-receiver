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

package in.flinbor.demo.smsreceiver.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;


public class Utils {

    /**
     * Check if your app is the default system SMS app
     *
     * @param context The Context
     * @return True if it is default, False otherwise
     */
    public static boolean isDefaultSmsApp(Context context) {
        return context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
    }

    /**
     * Trigger the intent to open the system dialog that asks the user to change the default SMS app
     *
     * @param context The Context
     */
    public static void setDefaultSmsApp(Context context) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
            context.startActivity(intent);

    }

    /**
     * Check is RECEIVE_SMS permission granted
     *
     * @param context The Context
     * @return return true if granted, false if not
     */
    public static boolean isSmsPermissionGranted(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
