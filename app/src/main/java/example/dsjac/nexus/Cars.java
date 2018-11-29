package example.dsjac.nexus;

import android.preference.PreferenceActivity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.widget.RelativeLayout;
import example.dsjac.nexus.NexusRestClient;

import org.json.*;
import com.loopj.android.http.*;
import org.apache.http.Header;

import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.LyftStyle;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.*;

import com.lyft.networking.ApiConfig;
import com.uber.sdk.rides.client.error.ApiError;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;

public class Cars extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Uber API Config
        SessionConfiguration uberConfig = new SessionConfiguration.Builder()
            .setClientId("xxxxxx")
            .setServerToken("xxxxx")
            .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
            .build();
        UberSdk.initialize(uberConfig);
        ServerTokenSession uberSession = new ServerTokenSession(uberConfig);

        // Create the ride request object to be used on Cars Activity
        RideRequestButton uberRequestButton = new RideRequestButton(Cars.this);
        ConstraintLayout layout = new ConstraintLayout(this);
        layout.addView(uberRequestButton);

        // set parameters for the uber ride button
        RideParameters rideParams = new RideParameters.Builder()
            // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
            .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
            // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
            .setDropoffLocation(37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")
            // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
            .setPickupLocation(37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")
            .build();

        uberRequestButton.setRideParameters(rideParams);
        uberRequestButton.setSession(uberSession);

        // Make request for ride information
        getUberEstimate(37.775304, -122.417522, 37.759234, -122.4135125);
        uberRequestButton.loadRideInformation();




        // Lyft API Config
        ApiConfig lyftApiConfig = new ApiConfig.Builder()
                .setClientId("xxxxxx")
                .setClientToken("xxxxxx")
                .build();

        LyftButton lyftRequestButton = findViewById(R.id.lyft_button);
        lyftRequestButton.setApiConfig(lyftApiConfig);
        lyftRequestButton.setLyftStyle(LyftStyle.HOT_PINK);

        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
            .setPickupLocation(37.775304, -122.417522)
            .setDropoffLocation(37.759234, -122.4135125);

        rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);

        lyftRequestButton.setRideParams(rideParamsBuilder.build());
        lyftRequestButton.load();
    }


    private void getUberEstimate(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        NexusRestClient client = new NexusRestClient();
        RequestParams params = new RequestParams();
        params.put("start_latitude", startLatitude);
        params.put("start_longitude", startLongitude);
        params.put("end_latitude", endLatitude);
        params.put("end_latitude", endLongitude);

        client.get("api/uberPriceEstimate", params, new JsonHttpResponseHandler() {

//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject repsonse){
//
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray prices) {
//
//            }
        });
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
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng vandy = new LatLng(36.1447, -86.8027);
        LatLng tsu = new LatLng(36.1668, -86.8276);
        googleMap.addMarker(new MarkerOptions().position(vandy)
                .title("Marker at Vandy"));
        googleMap.addMarker(new MarkerOptions().position(tsu)
                .title("Marker at TSU"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(vandy));
        googleMap.animateCamera( CameraUpdateFactory.zoomTo( 12.0f ) );

    }
}
