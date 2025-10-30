package com.example.loksyon;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.loksyon.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class currentlocation extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private FusedLocationProviderClient fusedLocationClient;
    private EditText latitudeTextView;
    private EditText longitudeTextView;
    private EditText addressTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_currentlocation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        latitudeTextView = view.findViewById(R.id.latitudeTextView);
        longitudeTextView = view.findViewById(R.id.longitudeTextView);
        addressTextView = view.findViewById(R.id.addressTextView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, proceed with getting the location
            getCurrentLocation();
        }

        view.findViewById(R.id.button).setOnClickListener(v -> getCurrentLocation());

        // Set up the eksbutton to close the fragment
        ImageButton eksButton = view.findViewById(R.id.eksbutton);
        eksButton.setOnClickListener(v -> closeFragment());

        // Set up the copyButton to copy location information
        Button copyButton = view.findViewById(R.id.copyButton);
        copyButton.setOnClickListener(v -> copyLocationInfo());
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, get current location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            // Get latitude and longitude
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Display latitude and longitude
                            latitudeTextView.setText("" + latitude);
                            longitudeTextView.setText("" + longitude);

                            // Get address from latitude and longitude
                            getAddressFromLocation(latitude, longitude);
                        } else {
                            Toast.makeText(requireContext(), "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                // Display address
                addressTextView.setText("" + address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with getting the location
                getCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void closeFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    private void copyLocationInfo() {
        // Get the text from EditText fields
        String latitude = latitudeTextView.getText().toString();
        String longitude = longitudeTextView.getText().toString();
        String address = addressTextView.getText().toString();

        // Combine the text
        String locationInfo = "[SOS MESSAGE]\n\nHello! I am reaching out through the LOKSYON app for urgent assistance.\nI urgently require your emergency service at my current location.\n\nCoordinates: " + latitude + "," + longitude + "\nAddress: " + address + "\n\nIf there is any uncertainty regarding the authenticity of this SOS message, please do not hesitate to contact me directly at my phone number.";

        // Copy the text to clipboard
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Location Info", locationInfo);
        clipboard.setPrimaryClip(clip);

        // Notify the user
        Toast.makeText(requireContext(), "Location information copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}
