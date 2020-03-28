package ca.hh.codejam_android;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    private GoogleMap mMap;



    //List<SiteInfo> siteInfoList =  new  ArrayList<>();

    double latitude;
    double longitude;
    String number;
    String prob;
    double tarrif;
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private static final String TAG = MapsActivity.class.getSimpleName();
    String hours;


    public MapsActivity() throws IOException {
    }


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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Obtain the map
        mMap = googleMap;
        UiSettings mapUiSettings = mMap.getUiSettings();
        mapUiSettings.setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        //mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        final Context mContext = this;

        // City latitude and longitude
        LatLng montreal = new LatLng(45.50884, -73.58781);

        // Position
        CameraPosition pos = new CameraPosition.Builder().target(montreal).zoom(11).tilt(40).build();

        // Add a marker in Montreal and move the camera
        // mMap.addMarker(new MarkerOptions().position(montreal).title("Marker in Montreal"));

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

        this.setUpClusterer();
        // Update position
        //mMap.moveCamera(pos);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLUE);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });


    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }



    // Declare a variable for the cluster manager.
    //private ClusterManager<MyItem> mClusterManager;

    private void setUpClusterer() {
        // Position the map.
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        ClusterManager<MyItem> mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Add cluster items (markers) to the cluster manager.
        addItems(mClusterManager);

        final CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, mClusterManager);

        mClusterManager.setRenderer(renderer);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

    }

    private void addItems(ClusterManager<MyItem> thisClusterManager) {

        InputStream is = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,  Charset.forName("UTF-8")));

        String info = "";
        List<SiteInfo> siteInfoList =  new  ArrayList<>();

        // Put in place just for testing

        while (true) {
            try {
                if ((info = reader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] line = info.split(",");
            latitude = Double.parseDouble(line[3]);
            longitude = Double.parseDouble(line[2]);
            tarrif = Double.parseDouble(line[4]);
            hours = String.valueOf(line[5]);
            number = String.valueOf(line[1]);
            prob = String.valueOf(line[6]);

            siteInfoList.add(new SiteInfo(number, tarrif, latitude, longitude, hours, prob));
        }

        for (SiteInfo p : siteInfoList) {
            MyItem parkingSpot = new MyItem(p.latitude, p.longitude, p.number, p.tarrif + p.hours+"\nProbability of Availability: " + p.prob + "%");
            thisClusterManager.addItem(parkingSpot);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }
}

class SiteInfo {

    String number;
    String tarrif;
    double latitude;
    double longitude;
    String hours;
    String prob;
    Calendar sCalendar;

    public SiteInfo (String number, double tarrif, double latitude, double longitude, String hours, String prob) {
        this.number = number;
        this.tarrif = "Fare is " + Double.toString(tarrif/100) + "$/hr";
        this.latitude = latitude;
        this.longitude = longitude;
        this.hours = "\nHours: " + hours;
        this.prob = getProb(prob);
    }

    public String getProb(String probability) {
        int stDev = 10;
        String prob;
        sCalendar = Calendar.getInstance();
        Random r = new Random();
        if (sCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||  sCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            if (probability.contains("Vieux-Montreal")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+30;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+58;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+64;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else if (probability.contains("Sud-Est")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+30;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+52;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+44;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else if (probability.contains("Quartier des spectacles et Quartier latin")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+30;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+50;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+52;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else if (probability.contains("Sud du Quartier des affaires")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+24;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+43;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+47;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else if (probability.contains("Quartier des affaires et Quartier international")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+54;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+79;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+79;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else if (probability.contains("Nord du Quartier des affaires")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+45;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+65;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+59;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else return prob = "Unavailable";
        } else {
            if (probability.contains("Vieux-Montreal")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+77;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+82;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+60;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 18 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 21) {
                    int probValue = (int) r.nextGaussian()*stDev+66;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else if (probability.contains("Sud-Est")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+57;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+61;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+39;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 18 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 21) {
                    int probValue = (int) r.nextGaussian()*stDev+37;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else if (probability.contains("Quartier des spectacles et Quartier latin")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+49;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+60;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+45;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 18 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 21) {
                    int probValue = (int) r.nextGaussian()*stDev+61;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else if (probability.contains("Sud du Quartier des affaires")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+46;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+57;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+41;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 18 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 21) {
                    int probValue = (int) r.nextGaussian()*stDev+47;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else if (probability.contains("Quartier des affaires et Quartier international")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+77;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+85;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+76;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 18 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 21) {
                    int probValue = (int) r.nextGaussian()*stDev+66;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else if (probability.contains("Nord du Quartier des affaires")) {
                if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 9 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                    int probValue = (int) r.nextGaussian()*stDev+76;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 12 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 15) {
                    int probValue = (int) r.nextGaussian()*stDev+77;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 15 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 18) {
                    int probValue = (int) r.nextGaussian()*stDev+58;
                    return prob = String.valueOf(probValue);
                }
                else if (sCalendar.get(Calendar.HOUR_OF_DAY) >= 18 && sCalendar.get(Calendar.HOUR_OF_DAY) <= 21) {
                    int probValue = (int) r.nextGaussian()*stDev+55;
                    return prob = String.valueOf(probValue);
                }
                else return prob = "Unavailable";
            }
            else return prob = "Unavailable";
        }

    }
}
