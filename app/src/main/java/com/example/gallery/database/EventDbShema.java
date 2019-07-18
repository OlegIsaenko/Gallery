package com.example.gallery.database;

public class EventDbShema {
    public static final class EventTable {
        public static final String NAME = "events";

        public static final class Cols {

            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String DESCRIPTION = "description";
            public static final String LOCATION = "location";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
        }
    }
}
