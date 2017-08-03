package com.parithi.theorangestation.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by earul on 8/5/16.
 */
public class Contract {

    // CHANGE IN MANIFEST ALSO
//    public static final String CONTENT_AUTHORITY = "com.parithi.theorangestation.data"; --> CHENNAI
//    public static final String CONTENT_AUTHORITY = "com.parithi.theorangestation.coimbatoredata"; --> COIMBATORE

    public static final String CONTENT_AUTHORITY = "com.parithi.theorangestation.coimbatoredata";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TAXI = "taxi";

    public static final class Taxi implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAXI).build();

        public static final String[] PROJECTION = {
                Taxi._ID,
                Taxi.NAME,
                Taxi.PHONE_NUMBER
        };

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TAXI;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TAXI;

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PHONE_NUMBER = "phone_number";


        public static Uri buildTaxiUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromFactUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
