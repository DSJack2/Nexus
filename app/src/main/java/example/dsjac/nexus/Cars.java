package example.dsjac.nexus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.LyftStyle;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.*;

import com.lyft.networking.ApiConfig;

import java.util.Arrays;

public class Cars extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        // Uber API Config
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("xxxxxxxx")
                .setServerToken("xxxxxxx")
//                .setRedirectUri("http://localhost")
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                .build();
        UberSdk.initialize(config);

        // Create the ride request object to be used on Cars Activity
        RideRequestButton uberRequestButton = new RideRequestButton(Cars.this);
        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(uberRequestButton);

        // Lyft API Config
        ApiConfig lyftApiConfig = new ApiConfig.Builder()
                .setClientId("xxxxxxx")
                .setClientToken("xxxxxxxxxxxxxxxx")
                .build();

        LyftButton lyftRequestButton = findViewById(R.id.lyft_button);
        lyftRequestButton.setApiConfig(lyftApiConfig);
        lyftRequestButton.setLyftStyle(LyftStyle.HOT_PINK);
    }
}
