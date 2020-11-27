package com.example.googlemapdummysk.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RestaurantInfo {

    public String lat;
    public String lng;
    public String name;
    public String id;
    public String icon;
    public String vicinity;
    public float distance;
    public boolean isDetailsAvailable;
    public String internationalPhoneNumber;
    public String formatedAddress;
    public ArrayList<RestaurantPhoto> photos;
    public String hours;


    public RestaurantInfo(){
        photos = new ArrayList<RestaurantPhoto>();
    }
    public void calculateDistance(LatLng center){
        Location l=new Location("");
        l.setLongitude(center.longitude);
        l.setLatitude(center.latitude);
        Location l1=new Location("");
        l1.setLongitude(Double.parseDouble(lng));
        l1.setLatitude(Double.parseDouble(lat));
        // Calculate distance and convert to miles
        distance = l.distanceTo(l1)/1600;

    }

    public float distanceTo(RestaurantInfo loc){
        Location l=new Location("");
        l.setLongitude(Double.parseDouble(loc.lng));
        l.setLatitude(Double.parseDouble(loc.lat));
        Location l1=new Location("");
        l1.setLongitude(Double.parseDouble(lng));
        l1.setLatitude(Double.parseDouble(lat));
        // Calculate distance and convert to miles
        return l.distanceTo(l1)/1600;

    }
    public int compareTo(RestaurantInfo o1) {
        if (distance < o1.distance ) {
            return -1;
        } else if( distance > o1.distance){
            return 1;
        }else{
            return 0;
        }
    }

    public void addPhoto(RestaurantPhoto photo){
        photos.add(photo);
        isDetailsAvailable = true;
    }

    @Override
    public boolean equals(Object anotherObject){
        if( anotherObject instanceof  RestaurantInfo){
            RestaurantInfo pEL = (RestaurantInfo)anotherObject;
            float dis = this.distanceTo(pEL);
            if( dis < 250.0){
                if( this.name.equals(pEL.name))
                    return true;
            }
        }

        return false;
    }


    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", latlng=" + lat+","+lng +
                '\'' +
                '}';
    }
}
