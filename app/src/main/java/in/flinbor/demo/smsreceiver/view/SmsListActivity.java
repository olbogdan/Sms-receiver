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

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import in.flinbor.demo.smsreceiver.R;
import in.flinbor.demo.smsreceiver.data.SmsContentProvider;
import in.flinbor.demo.smsreceiver.data.SmsTable;

/**
 * Activity with list of SMS
 * SMS loaded by cursor loader to list adapter
 * list automatically updated when new SMS inserted to database
 */
public class SmsListActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private SimpleCursorAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_list);
		fillData();
	}

	/**
	 * create CursorAdapter and initialize it by data
	 */
	private void fillData() {
		String[] from = new String[] {SmsTable.COLUMN_SENDER, SmsTable.COLUMN_CONTENT, SmsTable.COLUMN_TIMESTAMP};
		int[] to = new int[] {R.id.text_view_sender, R.id.text_view_content, R.id.text_view_timestamp};
		
		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.layout_sms_list_item, null, from, to, 0);
		
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (columnIndex == cursor.getColumnIndex(SmsTable.COLUMN_TIMESTAMP)) {
					DateFormat f = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
					String formattedDate = f.format(new Date(cursor.getLong(columnIndex)));
					((TextView) view).setText(formattedDate);
					return true;
				}
				return false;
			}
		});
		setListAdapter(adapter);
	}

	/**
	 * show Content of SMS in AlertDialog
     */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Uri messageUri = Uri.parse(SmsContentProvider.CONTENT_URI + "/" + id);
		String[] projection = {SmsTable.COLUMN_TIMESTAMP, SmsTable.COLUMN_CONTENT};
		Cursor cursor = getContentResolver().query(messageUri, projection, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			String message = cursor.getString(cursor.getColumnIndexOrThrow(SmsTable.COLUMN_CONTENT));
			new AlertDialog.Builder(this).setMessage(message).create().show();
			cursor.close();
		} else {
			Toast.makeText(this, getString(R.string.warn_sms_loading_fail), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * create CursorLoader, load data in descending timestamp (newest first)
     */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = {SmsTable.COLUMN_ID, SmsTable.COLUMN_TIMESTAMP, SmsTable.COLUMN_CONTENT, SmsTable.COLUMN_SENDER};
		CursorLoader cursorLoader = new CursorLoader(this, SmsContentProvider.CONTENT_URI,
				projection, null, null, "-" + SmsTable.COLUMN_TIMESTAMP);
		return cursorLoader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}
