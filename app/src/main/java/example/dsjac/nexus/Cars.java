package example.dsjac.nexus;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.LyftStyle;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.error.ApiError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

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


        if (ActivityCompat.checkSelfPermission(Cars.this, Manifest.permission.ACCESS_FINE_LOCATION) > 0) {
            return;
        }

        client.getLastLocation().addOnSuccessListener(Cars.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    public void updateLocation(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    public void updateDestination(Place place) {
        LatLng destination = place.getLatLng();
        destinationLatitude = destination.latitude;
        destinationLongitude = destination.longitude;
    }

    public void configureLyft(Place place) {

        LatLng destination = place.getLatLng();
        destinationLatitude = destination.latitude;
        destinationLongitude = destination.longitude;

        ApiConfig lyftApiConfig = new ApiConfig.Builder()
                .setClientId(Keys.LYFT_CLIENT_ID)
                .setClientToken(Keys.LYFT_SERVER_TOKEN)
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

    public void configureUber(Place place) {

        LatLng destination = place.getLatLng();
        destinationLatitude = destination.latitude;
        destinationLongitude = destination.longitude;

        RideRequestButton uberRequestButton = findViewById(R.id.uberRequestButton);

        // set parameters for the uber ride button
        RideParameters rideParams = new RideParameters.Builder()
                .setProductId("717fdb6c-af0f-4132-8418-b3edb5d06c0d")
                .setPickupLocation(currentLatitude, currentLongitude, "Current Location", "")
                .setDropoffLocation(destinationLatitude, destinationLongitude, (String) place.getName(), "")
                .build();

        // Uber API Config
        SessionConfiguration uberConfig = new SessionConfiguration.Builder()
                .setClientId(Keys.UBER_CLIENT_ID)
                .setServerToken(Keys.UBER_SERVER_TOKEN)
                .setRedirectUri("http://localhost:3000")
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();
        UberSdk.initialize(uberConfig);
        ServerTokenSession uberSession = new ServerTokenSession(uberConfig);
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
        uberRequestButton.setRideParameters(rideParams);
        uberRequestButton.setSession(uberSession);
        uberRequestButton.setCallback(callback);
        uberRequestButton.loadRideInformation();
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
                for (int i = 0; i < prices.length(); ++i) {
                    JSONObject obj = prices.optJSONObject(i);
                    try {
                        if (obj.get("localized_display_name") == "UberX") {
                            lowPrice = (int) obj.get("low_estimate");
                            highPrice = (int) obj.get("high_estimate");
                        }
                    } catch (JSONException e) {
                        //not sure what to do here
                    }
                }
            }
        });

        NexusRestClient.get("api/uberTimeEstimate", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray times) {
                int time;
                for (int i = 0; i < times.length(); ++i) {
                    JSONObject obj = times.optJSONObject(i);
                    try {
                        if (obj.get("localized_display_name") == "uberX") {
                            time = (int) obj.get("estimate") / 60;
                        }
                    } catch (JSONException e) {
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
                for (int i = 0; i < prices.length(); ++i) {
                    JSONObject obj = prices.optJSONObject(i);
                    try {
                        if (obj.get("ride_type") == "lyft") {
                            lowPrice = (int) obj.get("estimated_cost_cents_min") / 100;
                            highPrice = (int) obj.get("estimated_cost_cents_max") / 100;
                        }
                    } catch (JSONException e) {
                        //not sure what to do here
                    }
                }

            }
        });

        NexusRestClient.get("api/lyftTimeEstimate", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray times) {
                int time;
                for (int i = 0; i < times.length(); ++i) {
                    JSONObject obj = times.optJSONObject(i);
                    try {
                        if (obj.get("ride_type") == "lyft") {
                            time = (int) obj.get("eta_seconds ") / 60;
                        }
                    } catch (JSONException e) {
                        //not sure what to do here
                    }
                }
            }

        });
    }

}
