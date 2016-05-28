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

package in.flinbor.demo.smsreceiver.view;

import android.Manifest;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import in.flinbor.demo.smsreceiver.R;
import in.flinbor.demo.smsreceiver.util.Utils;

/**
 * Base activity for request Runtime Permissions
 */
public abstract class BaseActivity extends ListActivity {
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Here, thisActivity is the current activity
        if (!Utils.isSmsPermissionGranted(this)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Toast.makeText(this, getString(R.string.permission_sms_granted), Toast.LENGTH_LONG).show();
            setAppAsDefaultMessenger();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setAppAsDefaultMessenger();
                } else {
                    // permission denied
                    Toast.makeText(this, R.string.permission_sms_disabled, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * show dialog to ask user set application as default SMS application
     */
    private void setAppAsDefaultMessenger() {
        if (!Utils.isDefaultSmsApp(this)) {
            Utils.setDefaultSmsApp(this);
        }

    }
}
