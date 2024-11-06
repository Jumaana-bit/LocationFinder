package com.example.locationfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "LocationFinder.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_LOCATION = "location";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    private Geocoder geocoder;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_LOCATION + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ADDRESS + " TEXT UNIQUE, "
                + COLUMN_LATITUDE + " REAL, "
                + COLUMN_LONGITUDE + " REAL)";
        db.execSQL(createTable);
        insertPredefinedLocations(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
            onCreate(db);
        }
    }

    // Add a new location, geocoding if latitude/longitude are not provided
    public boolean addLocation(String address, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ADDRESS, address);
        contentValues.put(COLUMN_LATITUDE, latitude);
        contentValues.put(COLUMN_LONGITUDE, longitude);

        long result = db.insert(TABLE_LOCATION, null, contentValues);
        db.close();

        // If the result is -1, insertion failed
        return result != -1;
    }


    private void insertPredefinedLocations(SQLiteDatabase db) {
        // Example list of 100 GTA locations with addresses and approximate coordinates
        Object[][] gtaLocations = {
                // Oshawa
                {"Oshawa", 43.8971, -78.8658},
                {"1288 Ritson Rd N, Oshawa", 43.9255, -78.8663},
                {"1319 Airport Blvd, Oshawa", 43.9224, -78.8986},

                // Ajax
                {"Ajax", 43.8509, -79.0204},
                {"250 Bayly St W, Ajax", 43.8494, -79.0183},
                {"899 Harwood Ave S, Ajax", 43.8335, -79.0248},

                // Pickering
                {"Pickering", 43.8351, -79.0868},
                {"1355 Kingston Rd, Pickering", 43.8355, -79.0834},
                {"1855 Liverpool Rd, Pickering", 43.8285, -79.0869},

                // Scarborough
                {"Scarborough", 43.7755, -79.2577},
                {"300 Borough Dr, Scarborough", 43.7727, -79.2563},
                {"525 Markham Rd, Scarborough", 43.7568, -79.2216},

                // Toronto (Downtown and neighborhoods)
                {"Downtown Toronto", 43.6532, -79.3832},
                {"528 Sheppard Ave E, Toronto", 43.7676, -79.3832},
                {"100 Queen St W, Toronto", 43.6534, -79.3841},
                {"130 King St W, Toronto", 43.6484, -79.3815},
                {"1 Yonge St, Toronto", 43.6447, -79.3762},
                {"250 Yonge St, Toronto", 43.6548, -79.3801},
                {"900 Dufferin St, Toronto", 43.6595, -79.4358},
                {"255 Bloor St W, Toronto", 43.6678, -79.3992},
                {"40 Bay St, Toronto", 43.6444, -79.3787},
                {"85 York Blvd, Toronto", 43.7714, -79.3789},
                {"200 Front St W, Toronto", 43.6455, -79.3887},
                {"333 Bay St, Toronto", 43.6509, -79.3805},

                // Etobicoke
                {"Etobicoke", 43.6205, -79.5132},
                {"925 Albion Rd, Etobicoke", 43.7358, -79.5854},
                {"125 The Queensway, Etobicoke", 43.6381, -79.4909},

                // North York
                {"North York", 43.7615, -79.4111},
                {"5100 Yonge St, North York", 43.7699, -79.4137},
                {"10 Parkway Forest Dr, North York", 43.7768, -79.3464},

                // Mississauga
                {"Mississauga", 43.5890, -79.6441},
                {"100 City Centre Dr, Mississauga", 43.5925, -79.6447},
                {"400 Dundas St E, Mississauga", 43.5864, -79.6098},
                {"5685 Fallsview Blvd, Mississauga", 43.6012, -79.7371},

                // Brampton
                {"Brampton", 43.7315, -79.7624},
                {"20 Queen St W, Brampton", 43.6863, -79.7596},
                {"499 Main St S, Brampton", 43.6678, -79.7375},

                // Markham
                {"Markham", 43.8561, -79.3370},
                {"179 Enterprise Blvd, Markham", 43.8615, -79.3373},
                {"5990 14th Ave, Markham", 43.8491, -79.3172},

                // Vaughan
                {"Vaughan", 43.8372, -79.5083},
                {"1 Bass Pro Mills Dr, Vaughan", 43.8225, -79.5370},
                {"9222 Keele St, Vaughan", 43.8418, -79.5029},

                // Richmond Hill
                {"Richmond Hill", 43.8828, -79.4403},
                {"9350 Yonge St, Richmond Hill", 43.8765, -79.4393},
                {"60 Major Mackenzie Dr E, Richmond Hill", 43.8785, -79.4309},

                // Thornhill
                {"Thornhill", 43.8180, -79.4020},
                {"50 John St, Thornhill", 43.8186, -79.4122},
                {"180 Steeles Ave W, Thornhill", 43.8056, -79.4398},

                // Newmarket
                {"Newmarket", 44.0592, -79.4613},
                {"200 Davis Dr, Newmarket", 44.0596, -79.4568},
                {"17600 Yonge St, Newmarket", 44.0581, -79.4723},

                // Oakville
                {"Oakville", 43.4675, -79.6877},
                {"240 Leighland Ave, Oakville", 43.4536, -79.6860},
                {"146 Lakeshore Rd E, Oakville", 43.4446, -79.6716},

                // Milton
                {"Milton", 43.5183, -79.8774},
                {"123 Main St, Milton", 43.5142, -79.8829},
                {"550 Ontario St S, Milton", 43.5175, -79.8715},

                // Aurora
                {"Aurora", 44.0065, -79.4504},
                {"135 Industrial Pkwy N, Aurora", 44.0093, -79.4526},
                {"1 Henderson Dr, Aurora", 44.0049, -79.4557},

                // More locations in Toronto neighborhoods
                {"High Park, Toronto", 43.6465, -79.4637},
                {"Toronto Zoo", 43.8177, -79.1859},
                {"Casa Loma, Toronto", 43.6780, -79.4094},
                {"University of Toronto, St. George Campus", 43.6629, -79.3957},
                {"Royal Ontario Museum", 43.6677, -79.3948},
                {"Exhibition Place, Toronto", 43.6331, -79.4207},
                {"Toronto Islands", 43.6205, -79.3783},
                {"Scarborough Bluffs", 43.7116, -79.2314},
                {"Ontario Science Centre", 43.7166, -79.3407},
                {"Evergreen Brick Works, Toronto", 43.6841, -79.3644},
                {"Distillery District, Toronto", 43.6503, -79.3591},
                {"Nathan Phillips Square, Toronto", 43.6520, -79.3832},
                {"St. Lawrence Market, Toronto", 43.6486, -79.3715},
                {"Rogers Centre, Toronto", 43.6414, -79.3893},
                {"Scotiabank Arena, Toronto", 43.6435, -79.3791},
                {"The Beaches, Toronto", 43.6683, -79.2970},

                // Extra locations to reach 100
                {"Port Credit, Mississauga", 43.5516, -79.5826},
                {"Danforth, Toronto", 43.6792, -79.3293},
                {"Yorkdale Mall, Toronto", 43.7254, -79.4522},
                {"Liberty Village, Toronto", 43.6386, -79.4224},
                {"King Street West, Toronto", 43.6444, -79.3967}
        };

        for (Object[] location : gtaLocations) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ADDRESS, (String) location[0]);
            values.put(COLUMN_LATITUDE, (double) location[1]);
            values.put(COLUMN_LONGITUDE, (double) location[2]);
            db.insert(TABLE_LOCATION, null, values);

        }
    }

    // Update location by address
    public boolean updateLocation(String address, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        int rowsAffected = db.update("location", values, "address = ?", new String[]{address});
        db.close();
        return rowsAffected > 0;
    }

    // Delete location by address
    public boolean deleteLocation(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("location", "address = ?", new String[]{address});
        db.close();
        return rowsDeleted > 0;
    }

    // Find a location by address (for querying purposes)
    public String getLocation(String address) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database for available addresses (for debugging)
        Cursor allAddressesCursor = db.rawQuery("SELECT " + COLUMN_ADDRESS + " FROM " + TABLE_LOCATION, null);
        StringBuilder addressList = new StringBuilder("Addresses in DB:\n");
        while (allAddressesCursor.moveToNext()) {
            addressList.append(allAddressesCursor.getString(0)).append("\n");
        }
        allAddressesCursor.close();
        //Log.d(TAG, addressList.toString()); // Log all addresses for debugging

        // Original query to find the specific address
        String query = "SELECT " + COLUMN_LATITUDE + ", " + COLUMN_LONGITUDE + " FROM " + TABLE_LOCATION
                + " WHERE " + COLUMN_ADDRESS + " LIKE ? COLLATE NOCASE";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + address + "%"});

        if (cursor.moveToFirst()) {
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));
            cursor.close();
            db.close();
            return "Latitude: " + latitude + ", Longitude: " + longitude;
        } else {
            cursor.close();
            db.close();
            return "Location not found";
        }
    }
}
