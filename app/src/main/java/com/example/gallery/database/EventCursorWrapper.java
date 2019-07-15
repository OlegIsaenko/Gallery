package com.example.gallery.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.gallery.model.EventPhoto;

import java.util.Date;
import java.util.UUID;

public class EventCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public EventCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public EventPhoto getEvent() {
        String uuid = getString(getColumnIndex(EventDbShema.EventTable.Cols.UUID));
        String title = getString(getColumnIndex(EventDbShema.EventTable.Cols.TITLE));
        long date = getLong(getColumnIndex(EventDbShema.EventTable.Cols.DATE));
        String description = getString(getColumnIndex(EventDbShema.EventTable.Cols.DESCRIPTION));
        double latitude = getDouble(getColumnIndex(EventDbShema.EventTable.Cols.LATITUDE));
        double longitude = getDouble(getColumnIndex(EventDbShema.EventTable.Cols.LONGITUDE));

        EventPhoto event = new EventPhoto(UUID.fromString(uuid));
        event.setTitle(title);
        event.setDate(new Date(date));
        event.setDescription(description);
        event.setLat(latitude);
        event.setLng(longitude);

        return event;
    }
}
