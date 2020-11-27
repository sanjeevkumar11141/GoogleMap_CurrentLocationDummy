package com.example.googlemapdummysk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NewPlacesAPISdkActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 200;
    PlacesClient placesClient;
    List placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
    Button btn_nearby_places;
    LatLng latLng;
    String address;
    private String placeId;
    private List<PlaceLikelihood> placeLikelihoods;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_places_a_p_i_sdk);

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyApGbZ8BiqkxzSAvHwEEFpnPD721preR5s");

        // Create a new Places client instance.
        placesClient = Places.createClient(this);

        btn_nearby_places = findViewById(R.id.btn_nearby_places);

        //Get the current place and nearby places of your device
        getCurrentPlace();

        btn_nearby_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //setting Marker at nearby places
                setMarkerAtNearbyPlaces();
            }
        });
    }

    private void getCurrentPlace() {

        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            placesClient.findCurrentPlace(request).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()) {

                        FindCurrentPlaceResponse response = (FindCurrentPlaceResponse) task.getResult();
                        placeLikelihoods = new ArrayList<>();
                        placeLikelihoods.addAll(response.getPlaceLikelihoods());

                        //response.getPlaceLikelihoods() will return list of PlaceLikelihood
                        //we need to create a custom comparator to sort list by likelihoods
                        /*Collections.sort(placeLikelihoods, new Comparator() {
                            @Override
                            public int compare(PlaceLikelihood placeLikelihood, PlaceLikelihood t1) {
                                return new Double(placeLikelihood.getLikelihood()).compareTo(t1.getLikelihood());
                            }
                        });*/

                        //After sort ,it will order by ascending , we just reverse it to get first item as nearest place
                        Collections.reverse(placeLikelihoods);

                        placeId = placeLikelihoods.get(0).getPlace().getId();
                        latLng = placeLikelihoods.get(0).getPlace().getLatLng();
                        address = placeLikelihoods.get(0).getPlace().getAddress();

                        //Removing item of the list at 0 index
                        placeLikelihoods.remove(0);

                        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        supportMapFragment.getMapAsync(NewPlacesAPISdkActivity.this);
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(NewPlacesAPISdkActivity.this, "Place not found: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {

            //Requesting permission if it is not already granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

    }

    private void setMarkerAtNearbyPlaces() {

        for (PlaceLikelihood place : placeLikelihoods) {

            MarkerOptions markerOptions = new MarkerOptions().position(place.getPlace().getLatLng()).title(place.getPlace().getAddress());

            map.addMarker(markerOptions);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Adding marker in map at current location
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.addMarker(markerOptions);
    }

    //handling request permission response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
                    getCurrentPlace();
                } else
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                break;
        }
    }

}