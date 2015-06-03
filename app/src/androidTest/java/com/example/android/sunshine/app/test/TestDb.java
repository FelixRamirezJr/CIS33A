package com.example.android.sunshine.app.test;

/**
 * Created by felix on 6/2/15.
 * All of the Following Code Was extracted From the CIS 33A Android Class.
 */
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.optimusprime.cis53hw2_03495.WeatherContract.LocationEntry;
import com.example.optimusprime.cis53hw2_03495.WeatherContract.WeatherEntry;
import com.example.optimusprime.cis53hw2_03495.WeatherDbHelper;

import java.util.Map;
import java.util.Set;

import static android.database.DatabaseUtils.dumpCursorToString;

public class TestDb extends AndroidTestCase
{

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    static final String TEST_LOCATION = "99705";
    static final String TEST_DATE = "20141205";

    // These Variables are for The Insertion Case.
    static final String INSERT_LOCATION = "99705"; // Going to the Same Location.
    static final String INSERT_DATE = "20151306";

    public void testCreateDb() throws Throwable
    {
        Log.d(LOG_TAG, "HMMMM ThIS IS CREATING THE DATABASE");
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb()
    {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, testValues);

        Log.d("MyApp","This should be the Size of the Data" + testValues.size());


        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        String [] columns = {
                LocationEntry._ID,
                LocationEntry.COLUMN_LOCATION_SETTING,
                LocationEntry.COLUMN_CITY_NAME,
                LocationEntry.COLUMN_COORD_LAT,
                LocationEntry.COLUMN_COORD_LONG
        };

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = createWeatherValues(locationRowId);

        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
        assertTrue(weatherRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
                WeatherEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );


        // This Will print the Query Data.
        dumpCursorToString(weatherCursor);
        Log.d("MyApp", "This Will Query The Data: " + weatherCursor.toString());
        Log.d("MyApp", "The is the Size of the Query: " + weatherCursor.getColumnCount());

        Log.d("OUTPUT", "Should Have Printed Stuff");


        validateCursor(weatherCursor, weatherValues);
        dbHelper.close();

       // insertAgain();
    }

    //////////////////////////////////////////////////////////////////////////////
    /////////////////////// INSERT INTO THE DATABSE AGAIN! //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    void insertAgain ()
    {
       // db = openOrCreateDatabase( "weather.db"        , SQLiteDatabase.CREATE_IF_NECESSARY        , null          );

        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues NewValues = createValuesForInsertaion(); // Calls A different Method To Insert.

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, NewValues);

        assertTrue(locationRowId != -1);

        // Creating The Cursors...
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, NewValues); // Validate It

        //////////////////// END OF THE FIRST INSERTION /////////////////////

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues NewWeatherVals = insertWeatherValues(locationRowId); // Inserting New Weather Values..

        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, NewWeatherVals);
        assertTrue(weatherRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
                WeatherEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        validateCursor(weatherCursor, NewWeatherVals);
        dbHelper.close();

    }

    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, TEST_DATE);
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        return weatherValues;
    }


    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        testValues.put(LocationEntry.COLUMN_CITY_NAME, "North Pole");
        testValues.put(LocationEntry.COLUMN_COORD_LAT, 64.7488);
        testValues.put(LocationEntry.COLUMN_COORD_LONG, -147.353);

        return testValues;
    }

    //////////////////////////////////////////////////////////////
    /////////////////// Used For Insertion //////////////////////
    /////////////////////////////////////////////////////////////

    static ContentValues  createValuesForInsertaion ()
    {
        ContentValues insertValues = new ContentValues();
        insertValues.put(LocationEntry.COLUMN_LOCATION_SETTING,INSERT_LOCATION); // Placing The insert Location
        insertValues.put(LocationEntry.COLUMN_CITY_NAME, "North Pole");
        insertValues.put(LocationEntry.COLUMN_COORD_LAT, 64.7488);
        insertValues.put(LocationEntry.COLUMN_COORD_LONG, -147.353);

        return insertValues;

    }
    static ContentValues insertWeatherValues (long locationRowId)
    {

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, INSERT_DATE);
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 100);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 5.6);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 6.6);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 66);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "An Apocalypse");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 100);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 322); // Changed From 321
        return weatherValues;
    }
    /////////////////////////////// End of Insertion Methods //////////////////////

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}
