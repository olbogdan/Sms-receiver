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

package in.flinbor.demo.smsreceiver.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Class contain query for creation SMS table
 * creation triggered by {@link SmsDatabaseHelper}
 */
public class SmsTable {
	public static final String TABLE_SMS 		= "sms";
	public static final String COLUMN_ID 		= "_id";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_CONTENT 	= "content";
	public static final String COLUMN_SENDER 	= "sender";

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SMS
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_TIMESTAMP + " integer not null, "
			+ COLUMN_CONTENT + " text not null, "
			+ COLUMN_SENDER + " text not null"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
		onCreate(database);
	}
}
