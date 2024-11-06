package com.example.locationfinder;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.locationfinder.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText addressEditText, latitudeEditText, longitudeEditText;
    private Button addButton, queryButton, updateButton, deleteButton;
    private TextView latitudeTextView, longitudeTextView, longitudeLabel, latitudeLabel;

    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        addressEditText = findViewById(R.id.addressEditText);
        latitudeEditText = findViewById(R.id.latitudeEditText);
        longitudeEditText = findViewById(R.id.longitudeEditText);
        addButton = findViewById(R.id.addButton);
        queryButton = findViewById(R.id.queryButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        longitudeLabel = findViewById(R.id.longitudeLabel);
        latitudeLabel = findViewById(R.id.latitudeLabel);

        latitudeEditText.setVisibility(View.GONE);
        longitudeEditText.setVisibility(View.GONE);
        latitudeLabel.setVisibility(View.GONE); // Hide the labels
        longitudeLabel.setVisibility(View.GONE); // Hide the labels

        // Initialize Geocoder once
        geocoder = new Geocoder(this, Locale.getDefault());

        addButton.setOnClickListener(v -> {
            String address = addressEditText.getText().toString().trim();

            if (!address.isEmpty()) {
                // Check if Geocoder is available
                if (Geocoder.isPresent()) {
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(address, 1); // Find the first match
                        if (addresses != null && !addresses.isEmpty()) {
                            Address location = addresses.get(0);
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Update the UI with latitude and longitude
                            latitudeTextView.setText("Latitude: " + latitude);
                            longitudeTextView.setText("Longitude: " + longitude);

                            // Add the data to the database
                            boolean isInserted = dbHelper.addLocation(address, latitude, longitude);
                            if (isInserted) {
                                Toast.makeText(this, "Location added successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to add location", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No location found for the address", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error in geocoding: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Geocoder is not available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Query Location
        queryButton.setOnClickListener(v -> {
            String address = addressEditText.getText().toString().trim();
            if (!address.isEmpty()) {
                String coordinates = dbHelper.getLocation(address);
                if (coordinates != null) {
                    // Assuming getLocation returns latitude and longitude in "latitude,longitude" format
                    String[] latLon = coordinates.split(",");
                    if (latLon.length == 2) {
                        String latitude = latLon[0].trim();
                        String longitude = latLon[1].trim();

                        // Set latitude and longitude to TextViews
                        latitudeTextView.setText( latitude);
                        longitudeTextView.setText(longitude);
                    }
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter an address to query", Toast.LENGTH_SHORT).show();
            }
        });

        // Update Location
        updateButton.setOnClickListener(v -> {
            latitudeEditText.setVisibility(View.VISIBLE);
            longitudeEditText.setVisibility(View.VISIBLE);

            String address = addressEditText.getText().toString().trim();
            if (!address.isEmpty()) {
                try {
                    double newLatitude = Double.parseDouble(latitudeEditText.getText().toString().trim());
                    double newLongitude = Double.parseDouble(longitudeEditText.getText().toString().trim());

                    boolean isUpdated = dbHelper.updateLocation(address, newLatitude, newLongitude);
                    Toast.makeText(this, isUpdated ? "Location updated" : "Failed to update location", Toast.LENGTH_SHORT).show();

                    // Hide the EditTexts after updating
                    latitudeEditText.setVisibility(View.GONE);
                    longitudeEditText.setVisibility(View.GONE);
                    latitudeLabel.setVisibility(View.VISIBLE);
                    longitudeLabel.setVisibility(View.VISIBLE);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Enter longitude", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter the address", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete Location
        deleteButton.setOnClickListener(v -> {
            String address = addressEditText.getText().toString().trim();
            if (!address.isEmpty()) {
                boolean isDeleted = dbHelper.deleteLocation(address);
                Toast.makeText(this, isDeleted ? "Location deleted" : "Failed to delete location", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter the address to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }
}



