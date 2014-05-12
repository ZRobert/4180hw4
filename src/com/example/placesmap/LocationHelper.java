//Homework 4
//LocationHelper.java
//Robert Payne
package com.example.placesmap;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class LocationHelper {

	    private static final int MAX_LAST_LOCATION_AGE = 30000;
	    private static final int LOCATION_FOUND = 1;
	    private static final int LOCATION_NOT_FOUND = 2;
	    private static final int PROVIDER_NOT_ENABLED = 3;
	    private LocationManager locationMngr;
	    private Handler handler;
	    private Runnable doAfterTimeExpires;
	    private Context context;
	    ArrayList<LatLng> pts;
	    GoogleMap map;
	    Places activity;
	    public LocationHelper(LocationManager locationManager, Handler handler, Context context, GoogleMap mMap, ArrayList<LatLng> points, Places activity) {
	        this.locationMngr = locationManager;
	        this.handler = handler;
	        this.context = context;
	        pts = points;
	        map = mMap;
	        this.activity = activity; 
	        this.doAfterTimeExpires = new Runnable() {
	            @Override
	            public void run() {
	                endLocationListen(null, LOCATION_NOT_FOUND);
	} };
	}
	    public void getCurrentLocation(int maxWaitSeconds) {
	        Location lastLoc = locationMngr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	        if (lastLoc != null && lastLoc.getTime() >= (System.currentTimeMillis() - MAX_LAST_LOCATION_AGE)) {
	            endLocationListen(lastLoc,LOCATION_FOUND);
	        } else {
	            locationMngr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, locationListner);
	            handler.postDelayed(doAfterTimeExpires, maxWaitSeconds * 1000);
	        }
	}
	    private LocationListener locationListner = new LocationListener() {
	         public void onStatusChanged(String provider, int status, Bundle extras) {
	             if (status == LocationProvider.OUT_OF_SERVICE) {
	                 endLocationListen(null, PROVIDER_NOT_ENABLED);
	                 
	             }
	}
	         public void onProviderEnabled(String provider) {
	         }
	         public void onProviderDisabled(String provider) {
	             endLocationListen(null,PROVIDER_NOT_ENABLED);
	         }
	         public void onLocationChanged(Location location) {
	             if (location != null) {
	                 endLocationListen(location, LOCATION_FOUND);
	      //         pts =  activity.updatePoints(new LatLng(location.getLatitude(), location.getLongitude()));
	     //           activity.drawLine();
	             } 
	             
	      }
	    };
	    private void endLocationListen(Location location, int errorCode) {
	         handler.removeCallbacks(doAfterTimeExpires);
	         locationMngr.removeUpdates(locationListner);
	         switch (errorCode) {
	         case PROVIDER_NOT_ENABLED:
	             Toast.makeText(context, "GPS not enabled", Toast.LENGTH_SHORT).show();
	             break;
	         case LOCATION_NOT_FOUND:
	             Toast.makeText(context, "Unable to find location", Toast.LENGTH_SHORT).show();
	             break;
	         case LOCATION_FOUND:
	             Toast.makeText(context, location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
	             break;
	}
	}
	    
}
