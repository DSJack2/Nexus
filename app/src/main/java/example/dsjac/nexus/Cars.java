package example.dsjac.nexus;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.LyftStyle;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.*;

import com.lyft.networking.ApiConfig;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.Arrays;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Cars extends AppCompatActivity implements
               OnMapReadyCallback,
               ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private static double currentLatitude;
    private static double currentLongitude;
    private FusedLocationProviderClient client;
    private static double destinationLatitude;
    private static double destinationLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
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

        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);


        if(ActivityCompat.checkSelfPermission(Cars.this, Manifest.permission.ACCESS_FINE_LOCATION) > 0){
            return;
        }

        client.getLastLocation().addOnSuccessListener(Cars.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    updateLocation(location);
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLocation)
                            .title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));

                    PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                            getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

                    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                        @Override
                        public void onPlaceSelected(Place place) {
                            updateDestination(place);
                            mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                                    .title("Final Destination"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                            mMap.moveCamera(CameraUpdateFactory.zoomIn());
                            configureLyft(place);
                            configureUber(place);
                        }
                        @Override
                        public void onError(Status status) {
                            // TODO: Handle the error.
                            Log.i("TAG", "An error occurred: " + status);
                        }
                    });
                }
            }
        });
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    public void updateLocation(Location location){
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    public void updateDestination(Place place){
        LatLng destination = place.getLatLng();
        destinationLatitude = destination.latitude;
        destinationLongitude = destination.longitude;
    }

    public void configureLyft(Place place){

        LatLng destination = place.getLatLng();
        destinationLatitude = destination.latitude;
        destinationLongitude = destination.longitude;

        ApiConfig lyftApiConfig = new ApiConfig.Builder()
                .setClientId("-")
                .setClientToken("-")
                .build();

        LyftButton lyftRequestButton = findViewById(R.id.lyft_button);
        lyftRequestButton.setApiConfig(lyftApiConfig);
        lyftRequestButton.setLyftStyle(LyftStyle.HOT_PINK);

        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                .setPickupLocation(currentLatitude, currentLongitude)
                .setDropoffLocation(destinationLatitude, destinationLongitude);
        rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);

        lyftRequestButton.setRideParams(rideParamsBuilder.build());
        lyftRequestButton.load();
    }

    public void configureUber(Place place){

        LatLng destination = place.getLatLng();
        destinationLatitude = destination.latitude;
        destinationLongitude = destination.longitude;

        // Uber API Config
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("-")
                .setServerToken("-")
//                .setRedirectUri("http://localhost")
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                .setEnvironment(SessionConfiguration.Environment.PRODUCTION)
                .build();
        UberSdk.initialize(config);

        // Create the ride request object to be used on Cars Activity
        RideRequestButton uberRequestButton = new RideRequestButton(Cars.this);
        ConstraintLayout layout = new ConstraintLayout(this);
        layout.addView(uberRequestButton);

        // set parameters for the uber ride button
        RideParameters rideParams = new RideParameters.Builder()
                // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                .setProductId("-")
                // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                .setDropoffLocation(
                        37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")
                // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                .setPickupLocation(37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")
                .build();

        uberRequestButton.setRideParameters(rideParams);
        ServerTokenSession session = new ServerTokenSession(config);
        uberRequestButton.setSession(session);
        uberRequestButton.loadRideInformation();
    }

}
