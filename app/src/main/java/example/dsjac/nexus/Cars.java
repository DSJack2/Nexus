package example.dsjac.nexus;

import android.preference.PreferenceActivity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.RelativeLayout;
import example.dsjac.nexus.NexusRestClient;

import org.json.*;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;

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

        // Create the ride request object to be used on Cars Activity
        RideRequestButton uberRequestButton = findViewById(R.id.rideRequestButton);;
        ConstraintLayout layout = new ConstraintLayout(this);
        //layout.addView(uberRequestButton);

        // set parameters for the uber ride button
        RideParameters rideParams = new RideParameters.Builder()
                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                .setPickupLocation(36.1447, -86.8027, "Vanderbilt University", "2201 West End Ave, Nashville")
                .setDropoffLocation(36.1668, -86.8276, "Tennessee State University", "3500 John A Merritt Blvd, Nashville")
                .build();

        // Uber API Config
        SessionConfiguration uberConfig = new SessionConfiguration.Builder()
            .setClientId("xxxxx")
            .setServerToken("xxxxx")
            .setRedirectUri("http://localhost:3000")
            .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
            .build();
        UberSdk.initialize(uberConfig);
        ServerTokenSession uberSession = new ServerTokenSession(uberConfig);
        uberRequestButton.setSession(uberSession);
        uberRequestButton.setRideParameters(rideParams);

        RideRequestButtonCallback callback = new RideRequestButtonCallback() {

            @Override
            public void onRideInformationLoaded() {

            }

            @Override
            public void onError(ApiError apiError) {
                Log.d("testApi", apiError.toString());
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("testThrow", throwable.toString());
            }
        };

        // Make request for ride information
        //getUberEstimate(37.775304, -122.417522, 37.759234, -122.4135125);
        uberRequestButton.setCallback(callback);
        uberRequestButton.loadRideInformation();




        // Lyft API Config
        ApiConfig lyftApiConfig = new ApiConfig.Builder()
                .setClientId("xxxxxxx")
                .setClientToken("xxxxxx")
                .build();

        LyftButton lyftRequestButton = findViewById(R.id.lyft_button);
        lyftRequestButton.setApiConfig(lyftApiConfig);
        lyftRequestButton.setLyftStyle(LyftStyle.HOT_PINK);

        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
            .setPickupLocation(36.1447, -86.8027)
            .setDropoffLocation(36.1668, -86.8276);

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

        NexusRestClient.get("api/uberPriceEstimate", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray prices) {
                int lowPrice, highPrice;
                for(int i = 0; i < prices.length(); ++i){
                    JSONObject obj = prices.optJSONObject(i);
                    try{
                        if(obj.get("localized_display_name") == "UberX"){
                            lowPrice = (int) obj.get("low_estimate");
                            highPrice = (int) obj.get("high_estimate");
                        }
                    }catch (JSONException e){
                        //not sure what to do here
                    }
                }
            }
        });

        NexusRestClient.get("api/uberTimeEstimate", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray times) {
                int time;
                for(int i = 0; i < times.length(); ++i){
                    JSONObject obj = times.optJSONObject(i);
                    try{
                        if(obj.get("localized_display_name") == "uberX"){
                            time = (int) obj.get("estimate") / 60;
                        }
                    }catch (JSONException e){
                        //not sure what to do here
                    }
                }
            }
        });
    }

    private void getLyftEstimate(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        NexusRestClient client = new NexusRestClient();
        RequestParams params = new RequestParams();
        params.put("start_latitude", startLatitude);
        params.put("start_longitude", startLongitude);
        params.put("end_latitude", endLatitude);
        params.put("end_latitude", endLongitude);

        NexusRestClient.get("api/lyftPriceEstimate", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray prices) {
                    int lowPrice, highPrice;
                    for(int i = 0; i < prices.length(); ++i){
                        JSONObject obj = prices.optJSONObject(i);
                        try{
                            if(obj.get("ride_type") == "lyft"){
                                lowPrice = (int) obj.get("estimated_cost_cents_min") / 100;
                                highPrice = (int) obj.get("estimated_cost_cents_max") / 100;
                            }
                        }catch (JSONException e){
                            //not sure what to do here
                        }
                    }

            }
        });

        NexusRestClient.get("api/lyftTimeEstimate", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray times) {
                int time;
                for(int i = 0; i < times.length(); ++i){
                    JSONObject obj = times.optJSONObject(i);
                    try{
                        if(obj.get("ride_type") == "lyft"){
                            time = (int) obj.get("eta_seconds ") / 60;
                        }
                    }catch (JSONException e){
                        //not sure what to do here
                    }
                }
            }

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
