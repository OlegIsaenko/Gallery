package com.example.gallery.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.gallery.database.EventBaseHelper;
import com.example.gallery.database.EventCursorWrapper;
import com.example.gallery.database.EventDbShema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventLab {
    private static EventLab sEventLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private EventLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new EventBaseHelper(mContext).getWritableDatabase();
    }

    public static EventLab get(Context context) {
        if (sEventLab == null) {
            sEventLab = new EventLab(context);
        }
        return sEventLab;
    }

    public EventPhoto getEvent(UUID uuid) {
        EventCursorWrapper cursor = queryEvents(
                EventDbShema.EventTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getEvent();
        } finally {
            cursor.close();
        }
    }

    public List<EventPhoto> getEvents() {
        List<EventPhoto> eventPhotos = new ArrayList<>();

        EventCursorWrapper cursor = queryEvents(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                eventPhotos.add(cursor.getEvent());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return eventPhotos;
    }

    public void addEvent(EventPhoto eventPhoto) {
        ContentValues contentValues = getContentValues(eventPhoto);
        mDatabase.insert(EventDbShema.EventTable.NAME, null, contentValues);
    }

    public void updateEvent(EventPhoto eventPhoto) {
        String uuid = eventPhoto.getUUID().toString();
        ContentValues contentValues = getContentValues(eventPhoto);

        mDatabase.update(EventDbShema.EventTable.NAME, contentValues,
                EventDbShema.EventTable.Cols.UUID + " = ?",
                new String[]{uuid});
    }

    public void deleteEvent(EventPhoto eventPhoto) {
        mDatabase.delete(EventDbShema.EventTable.NAME, EventDbShema.EventTable.Cols.UUID + " = ?",
                new String[]{eventPhoto.getUUID().toString()});
        getPhotoFile(eventPhoto).delete();
    }

    private static ContentValues getContentValues(EventPhoto eventPhoto) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventDbShema.EventTable.Cols.UUID, eventPhoto.getUUID().toString());
        contentValues.put(EventDbShema.EventTable.Cols.TITLE, eventPhoto.getTitle());
        contentValues.put(EventDbShema.EventTable.Cols.DATE, eventPhoto.getDate().getTime());
        contentValues.put(EventDbShema.EventTable.Cols.DESCRIPTION, eventPhoto.getDescription());
        contentValues.put(EventDbShema.EventTable.Cols.LOCATION, eventPhoto.getLocation());
        contentValues.put(EventDbShema.EventTable.Cols.LATITUDE, eventPhoto.getLat());
        contentValues.put(EventDbShema.EventTable.Cols.LONGITUDE, eventPhoto.getLng());
        return contentValues;
    }

    private EventCursorWrapper queryEvents(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(EventDbShema.EventTable.NAME,
                null, whereClause, whereArgs, null, null, null);
        return new EventCursorWrapper(cursor);
    }

    public File getPhotoFile(EventPhoto eventPhoto) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, eventPhoto.getPhotoFileName());
    }
}
