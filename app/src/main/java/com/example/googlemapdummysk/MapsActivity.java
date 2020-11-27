package com.example.googlemapdummysk;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MyTag";
    private static final int REQUEST_LOCATION_PERMISSION = 1089;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(28.465655, 77.839844);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Malagarh"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

       /* Malagarh
        Uttar Pradesh 203001
        28.465655, 77.839844*/

        /*1: World
        5: Landmass/continent
        10: City
        15: Streets
        20: Buildings*/

        /*float zoomLevel = 15;
        LatLng myHome = new LatLng(28.465655, 77.839844);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myHome, zoomLevel));
        //  mMap.addMarker(new MarkerOptions().position(myHome).title("Marker in Malagarh"));
        setMapLongClick(mMap);
        setPoiClick(mMap);

        GroundOverlayOptions homeOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.sk))
                .position(myHome, 100);

        mMap.addGroundOverlay(homeOverlay);*/

        enableMyLocation();

        LatLng customMarkerLocationOne = new LatLng(28.583911, 77.319116);

        View mViewMarker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_marker_layout, null);
        TextView number = (TextView) mViewMarker.findViewById(R.id.markerText);
        number.setText(""+5);




        /*mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                LatLng customMarkerLocationOne = new LatLng(28.583911, 77.319116);
                LatLng customMarkerLocationTwo = new LatLng(28.583078, 77.313744);
                LatLng customMarkerLocationThree = new LatLng(28.580903, 77.317408);
                LatLng customMarkerLocationFour = new LatLng(28.580108, 77.315271);






                *//*mMap.addMarker(new MarkerOptions().position(customMarkerLocationOne).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createDrawableFromView(MapsActivity.this,mViewMarker)))).setTitle("Sanjeev Kumar");*//*



               *//* MarkerOptions markerOptions = new MarkerOptions().
                        position(customMarkerLocationOne).title("" + markerPosition).icon(BitmapDescriptorFactory
                        .fromBitmap(ViewUtil.createDrawableFromView(getActivity(), mViewMarker)));*//*




                *//*mMap.addMarker(new MarkerOptions().position(customMarkerLocationTwo).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(MapsActivity.this,R.drawable.ic_profile,"2")))).setTitle("Hotel Nirulas Noida");

                mMap.addMarker(new MarkerOptions().position(customMarkerLocationThree).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(MapsActivity.this,R.drawable.ic_girl,"3")))).setTitle("Acha Khao Acha Khilao");
                mMap.addMarker(new MarkerOptions().position(customMarkerLocationFour).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(MapsActivity.this,R.drawable.ic_girl,"4")))).setTitle("Subway Sector 16 Noida");

*//*
                //LatLngBound will cover all your marker on Google Maps
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(customMarkerLocationOne); //Taking Point A (First LatLng)
                builder.include(customMarkerLocationThree); //Taking Point B (Second LatLng)
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                mMap.moveCamera(cu);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);




            }
        });*/
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }


    private void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String snippet = String.format(Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f",
                        latLng.latitude,
                        latLng.longitude);

                map.addMarker(new MarkerOptions().position(latLng).title("MY TTILE")
                        .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_BLUE)));

            }
        });
    }

   /* By default, points of interest (POIs) appear on the map along
   with their corresponding icons. POIs include parks, schools, government buildings,
   and more. When the map type is set to normal, business POIs also appear on the map.
    Business POIs represent businesses such as shops, restaurants, and hotels.*/

    private void setPoiClick(final GoogleMap map) {
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                Marker poiMarker = mMap.addMarker(new MarkerOptions()
                        .position(poi.latLng)
                        .title(poi.name));

                poiMarker.showInfoWindow();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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




    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource/*, String addressNumber*/) {

       /* View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        TextView txt_name = (TextView)marker.findViewById(R.id.text);
        txt_name.setText(addressNumber);*/

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_marker_layout, null);
        TextView number = (TextView) marker.findViewById(R.id.markerText);
        number.setText("1");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }
}