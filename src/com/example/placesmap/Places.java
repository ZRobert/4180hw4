//Homework 4
//Places.java
//Robert Payne
package com.example.placesmap;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Places extends Activity {
	ArrayList<Location> places;
	Location location;
	String lat;
	String lng;
	String radius;
	String type;
	String sensor;
	GoogleMap mMap;
	LocationManager locationMngr;
	int counter = 0;
	ArrayList<LatLng> points;
    PolylineOptions poly = new PolylineOptions()
    .color(Color.RED);
    
	private static final int MENU1 = Menu.FIRST;
	private static final int MENU2 = Menu.FIRST + 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mMap.setMyLocationEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);




		locationMngr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		location = locationMngr
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = locationMngr
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		} else{
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
			new LatLng((Double)location.getLatitude(),(Double)location.getLongitude()) , 18));

		}

		LocationListener simpleLocationListener = new LocationListener() {
			int count = 0;

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng((Double)location.getLatitude(),(Double)location.getLongitude()) , 18));
				
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			public void onLocationChanged(Location location) {
				String loc = location.getLatitude() + ", "
						+ location.getLongitude();
				points.add(new LatLng(location.getLatitude(), location.getLongitude())); 
				Log.d("Points added...", location.getLatitude() + " , " + location.getLongitude());
				draw();
				if (count++ > 10) {
					locationMngr.removeUpdates(this);
				}
			}

		}; 
	}
	
	protected void onResume() {
		super.onResume(); 
		if (!locationMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("GPS not enabled")
					.setMessage("Would like to enable the GPS settings")
					.setCancelable(true)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent i = new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									startActivity(i);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			LocationHelper locationHelper = new LocationHelper(locationMngr,
					new Handler(), this, mMap, points, this);
			locationHelper.getCurrentLocation(3000);
	 	} 
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU1, 0, "Scan Bar Code");
		menu.add(0, MENU2, 0, "Restart Hunt");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU1:
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.initiateScan();
			return true;
		case MENU2:
			mMap.clear();
			return true;
		default:
			return super.onOptionsItemSelected(item); 
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  Log.d("INTENT RESULT", scanResult.toString());
		  if (scanResult != null) {
			  
			  Log.d("SCAN RESULT", scanResult + "");
			  String[] resultString = scanResult.toString().split(",|:|;");
	
				  Log.d("Result String", resultString[2] + "-----" + 2); 	//
				  Log.d("Result String", resultString[3] + "-----" +3);
				  Log.d("Result String", resultString[4] + "-----" +4);
				  Log.d("Result String", resultString[5] + "-----" +5);
				  Log.d("Result String", resultString[6] + "-----" +6);
				  Log.d("Result String", resultString[7] + "-----" +7);
				mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(resultString[3]), Double.valueOf(resultString[4]))).title(resultString[2]));
				mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(resultString[6]), Double.valueOf(resultString[7].substring(0, 10)))).title(resultString[5]));
			//	 Polyline line = mMap.addPolyline(new PolylineOptions()
			//     .add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0))
			//     .width(5)
			//     .color(Color.RED));
		//		points.add(new LatLng(Double.valueOf(resultString[3]), Double.valueOf(resultString[4])));
		//		points.add(new LatLng(Double.valueOf(resultString[6]), Double.valueOf(resultString[7].substring(0, 10))));
		//		draw();
		  }
		  else{
			  Log.d("SCAN RESULT", "NONE");
		  }
		  // else continue with any other code you need in the method
		 
		}

	private void draw() {
		Polyline line = mMap.addPolyline(new PolylineOptions()
	     .width(5)
	     .color(Color.RED));
		line.setPoints(points);
		Log.d("Draw", "" + points.toString());
		for(int i = 0; i < points.size(); i++){
			mMap.addPolyline(new PolylineOptions().add(points.get(i)));
		}
	}

	public ArrayList<LatLng> updatePoints(LatLng latLng) {
		points.add(latLng);
		return points;
		// TODO Auto-generated method stub
		
	}

	public void drawLine() {
		// TODO Auto-generated method stub
		Polyline line = mMap.addPolyline(new PolylineOptions()
	     .width(5)
	     .color(Color.RED));
		line.setPoints(points);
	}


}
