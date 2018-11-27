package example.dsjac.nexus;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.widget.RelativeLayout;

import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.LyftStyle;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.*;

import com.lyft.networking.ApiConfig;
import com.uber.sdk.rides.client.error.ApiError;

import java.util.Arrays;

public class Cars extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

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

        // Uber API Config
        SessionConfiguration uberConfig = new SessionConfiguration.Builder()
                .setClientId("ZWBGzmZHqvaCQXK3Tf2UVc3ijY3iyOoH")
                .setServerToken("ExQMNvmuTdVGFEs9eVdVUzTO25IwkycKqG9KP7tP")
//                .setRedirectUri("http://localhost")
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();
        UberSdk.initialize(uberConfig);
        ServerTokenSession uberSession = new ServerTokenSession(uberConfig);

        RideRequestButtonCallback uberCallback = new RideRequestButtonCallback() {
            @Override
            public void onRideInformationLoaded() {

            }

            @Override
            public void onError(ApiError apiError) {

            }

            @Override
            public void onError(Throwable throwable) {

            }
        };

        uberRequestButton.setRideParameters(rideParams);
        uberRequestButton.setSession(uberSession);
        uberRequestButton.setCallback(uberCallback);
        uberRequestButton.loadRideInformation();


        // Lyft API Config
        ApiConfig lyftApiConfig = new ApiConfig.Builder()
                .setClientId("uG3ayZblOqHr")
                .setClientToken("XiJQCYnZR6qhvPsnyMbOyrgIQB2esrddDmBwSIVZbDO9fRriljuBKQaIg3NZOI2ssf6XsGq4E/K07tOUyG0QZQQEBY4kOSoMsGg90lYWXd+V1H8yAfB8llc=")
                .build();

        LyftButton lyftRequestButton = findViewById(R.id.lyft_button);
        lyftRequestButton.setApiConfig(lyftApiConfig);
        lyftRequestButton.setLyftStyle(LyftStyle.HOT_PINK);

        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                .setPickupLocation(37.7766048, -122.3943629)
                .setDropoffLocation(37.759234, -122.4135125);
        rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);

        lyftRequestButton.setRideParams(rideParamsBuilder.build());
        lyftRequestButton.load();
    }
}
