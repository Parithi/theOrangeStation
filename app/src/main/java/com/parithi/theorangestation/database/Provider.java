package com.parithi.theorangestation.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by earul on 8/5/16.
 */
public class Provider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private DBOpenHelper dbOpenHelper;
    private static final SQLiteQueryBuilder tableQueryBuilder;


    private static final int TAXI = 100;
    private static final int TAXI_WITH_ID = 101;

    static {
        tableQueryBuilder = new SQLiteQueryBuilder();
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        matcher.addURI(authority, Contract.PATH_TAXI , TAXI);
        matcher.addURI(authority, Contract.PATH_TAXI + "/#", TAXI_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbOpenHelper = new DBOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case TAXI_WITH_ID: {
                retCursor = getTableById(uri, projection, sortOrder);
                break;
            }
            case TAXI: {
                retCursor = dbOpenHelper.getReadableDatabase().query(
                        Contract.PATH_TAXI,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case TAXI_WITH_ID:
                return Contract.Taxi.CONTENT_ITEM_TYPE;
            case TAXI:
                return Contract.Taxi.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        Log.d("PROVIDER","URI : " + uri);
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TAXI: {
                try {
                    long _id = db.insertOrThrow(Contract.PATH_TAXI, null, contentValues);
                    if (_id > 0)
                        returnUri = Contract.Taxi.buildTaxiUriWithId(_id);
                    else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                } catch (SQLiteConstraintException exception){
                    if(exception.getMessage().contains("UNIQUE constraint failed")){
                        update(uri,contentValues, Contract.Taxi._ID + "=?", new String[]{contentValues.getAsString(Contract.Taxi._ID)});
                        return Contract.Taxi.buildTaxiUriWithId(contentValues.getAsLong(Contract.Taxi._ID));
                    } else {
                        throw exception;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    throw e;
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(getContext() !=null && getContext().getContentResolver()!=null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        if ( null == selection ) selection = "1";
        switch (match) {
            case TAXI:
                rowsDeleted = db.delete(Contract.PATH_TAXI, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            if(getContext()!=null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TAXI:
                rowsUpdated = db.update(Contract.PATH_TAXI, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            if(getContext()!=null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case TAXI:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(Contract.PATH_TAXI, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnCount++;
                        } else {
                            db.update(Contract.PATH_TAXI,value, Contract.Taxi._ID + " = ?" , new String[]{_id+""});
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if(getContext()!=null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }


    // Fact Functions

    private Cursor getTableById(Uri uri, String[] projection, String sortOrder) {
        String id = Contract.Taxi.getIdFromFactUri(uri);
        tableQueryBuilder.setTables(Contract.PATH_TAXI);
        return tableQueryBuilder.query(dbOpenHelper.getReadableDatabase(),
                projection,
                Contract.Taxi._ID + "=?",
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }
}
