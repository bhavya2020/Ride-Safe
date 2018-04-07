package com.example.bhavya.safego.data;

import android.provider.BaseColumns;

/**
 * Created by bhavya on 7/4/18.
 */

public class gyroscopeContract {
    public static final class gyroscope implements BaseColumns {
        public static final String TABLE_NAME = "gyroscope";
        public static final String X= "x";
        public static final String Y= "y";
        public static final String Z= "z";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
