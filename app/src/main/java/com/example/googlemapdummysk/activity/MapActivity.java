
package com.example.googlemapdummysk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;

import com.example.googlemapdummysk.models.RestaurantInfo;
import com.example.googlemapdummysk.retrofit.APIServiceInterface;
import com.example.googlemapdummysk.retrofit.ApiClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemapdummysk.R;
import com.google.gson.JsonElement;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1235;
    private static final int REQUEST_CHECK_SETTINGS = 3456;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    AutocompleteSessionToken autocompleteSessionToken;
    private List<AutocompletePrediction> predictionList;

    private Location mLastLocation;
    private LocationRequest locationRequest;
    private LocationCallback mLocationCallback;

    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button findRestaurantButton;

    private final float DEFAULT_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Context context;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyApGbZ8BiqkxzSAvHwEEFpnPD721preR5s");
        // Create a new PlacesClient instance
        placesClient = Places.createClient(this);
        autocompleteSessionToken = AutocompleteSessionToken.newInstance();


        materialSearchBar = findViewById(R.id.searchBar);
        findRestaurantButton = findViewById(R.id.findRestaurantButton);


        findRestaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNearByRestaurantList();
            }
        });



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapView = mapFragment.getView();
        addPlacesAPIOnSearchBar();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = 18;
        enabledCurrentLocationButton();

        //check if gps is enabled or not and then request user to enable it

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
                //  mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void enabledCurrentLocationButton() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 40, 180);
        }

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if(materialSearchBar.isSuggestionsVisible()){
                    materialSearchBar.clearSuggestions();
                }
                if(materialSearchBar.isSearchOpened()){
                    materialSearchBar.closeSearch();
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                getDeviceLocation();
            }else{
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    mLastLocation = task.getResult();
                    if (mLastLocation != null) {
                        moveCameraOnUpdatedLocation();
                    } else {
                        createNewLocationRequest();
                    }
                } else {
                    Toast.makeText(MapActivity.this,
                            "Unable to get last Device Location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void createNewLocationRequest() {
        /*locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);*/
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }
                mLastLocation = locationResult.getLastLocation();
                mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                moveCameraOnUpdatedLocation();
            }
        };
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    public void moveCameraOnUpdatedLocation() {
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }


    public void addPlacesAPIOnSearchBar() {
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {

                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.closeSearch();
                }

            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        //.setCountry("IN")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(autocompleteSessionToken)
                        .setQuery(charSequence.toString())
                        .build();

                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionList = new ArrayList<>();
                                for (AutocompletePrediction prediction : predictionList) {
                                    suggestionList.add(prediction.getFullText(null).toString());
                                }
                                if (!materialSearchBar.isSuggestionsVisible()) {
                                    materialSearchBar.showSuggestionsList();
                                }
                                materialSearchBar.updateLastSuggestions(suggestionList);
                            }
                        } else {
                            Toast.makeText(MapActivity.this, "predication fetching task unsuccessful.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String placeId = selectedPrediction.getPlaceId();
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                },1000);
                // hide key board
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                fetchPlacesRequest(placeId);
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });
    }

    public void fetchPlacesRequest(String placeId) {

        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
        FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                LatLng latLng = place.getLatLng();
                if (latLng != null)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    apiException.printStackTrace();
                    int statusCode = apiException.getStatusCode();
                    Log.d("MyTag", "onFailure: Place Not found" + e.getMessage());
                }
            }
        });
    }

    private void getNearByRestaurantList(){

        String locationString = mLastLocation.getLatitude()+","+ mLastLocation.getLongitude();
        String radius = "5000";
        String type = "restaurant";

        APIServiceInterface apiServiceInterface = ApiClient.getInstance().getClient();
        Call<JsonElement> call = apiServiceInterface.doPlaces(locationString,radius,type,getString(R.string.google_maps_key));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        System.out.println();
                        populateRestaurantFromJson(object);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                call.cancel();
                Toast.makeText(MapActivity.this, "Restaurant failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    List<RestaurantInfo> restaurantList = new ArrayList();

    private void populateRestaurantFromJson(JSONObject response){
        try {
            JSONArray results =response.getJSONArray("results");

            int length=results.length();
            for(int i=0;i<length;i++){
                JSONObject obj= results.getJSONObject(i);
                RestaurantInfo place=new RestaurantInfo();
                place.name= obj.getString("name");
                place.id= obj.getString("place_id");
                place.icon= obj.getString("icon");
                place.vicinity= obj.getString("vicinity");
                JSONObject geometry=obj.getJSONObject("geometry");
                JSONObject location=geometry.getJSONObject("location");
                place.lat= location.getString("lat");
                place.lng = location.getString("lng");
              //  place.calculateDistance(center);
                restaurantList.add(place);


            }
            createMarkersForRestaurant();
        }catch(Exception e){
            Toast.makeText(this, "Error while fetching places. Please try again later", Toast.LENGTH_SHORT).show();

        }
       // Collections.sort(restaurantList);
        Log.d("Places", restaurantList.toString());
    }


    private void createMarkersForRestaurant(){

        mMap.clear();
        View mViewMarker;
        int markerPosition =1;
        for(RestaurantInfo place: restaurantList) {
            mViewMarker = ((LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.custom_place_marker, null);

            TextView number = (TextView) mViewMarker.findViewById(R.id.markerText);

            System.out.println();

            number.setText(""+markerPosition);

            LatLng new_location = new LatLng(Double.parseDouble(place.lat), Double.parseDouble(place.lng));
            //         googleMap.addMarker(new MarkerOptions().position(new_location).title("" + markerPosition)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon));
            MarkerOptions markerOptions = new MarkerOptions().
                    position(new_location).title("" + markerPosition).icon(BitmapDescriptorFactory
                    .fromBitmap(createDrawableFromView(MapActivity.this, mViewMarker)));

            /*MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(place.lat), Double.parseDouble(place.lng)))
                    .title(place.name);*/
            mMap.addMarker(markerOptions);
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
            markerPosition++;

        }
    }


    // Display marker on map using game icon
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;

    }

}