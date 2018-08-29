package ds.com.livelocation.location.location.providers;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import ds.com.livelocation.location.OnLocationUpdatedListener;
import ds.com.livelocation.location.location.LocationProvider;
import ds.com.livelocation.location.location.config.LocationParams;
import ds.com.livelocation.location.utils.GooglePlayServicesListener;
import ds.com.livelocation.location.utils.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by mrm on 20/12/14.
 */
public class LocationGooglePlayServicesWithFallbackProvider implements LocationProvider, GooglePlayServicesListener {

    private Logger logger;
    private OnLocationUpdatedListener listener;
    private boolean shouldStart = false;
    private Context context;
    private LocationParams params;
    private boolean singleUpdate = false;

    private LocationProvider provider;

    public LocationGooglePlayServicesWithFallbackProvider(Context context) {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
            provider = new LocationGooglePlayServicesProvider(this);
        } else {
            provider = new LocationManagerProvider();
        }
    }

    @Override
    public void init(Context context, Logger logger) {
        this.logger = logger;
        this.context = context;

        logger.d("Currently selected provider = " + provider.getClass().getSimpleName());

        provider.init(context, logger);
    }

    @Override
    public void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate) {
        shouldStart = true;
        this.listener = listener;
        this.params = params;
        this.singleUpdate = singleUpdate;
        provider.start(listener, params, singleUpdate);
    }

    @Override
    public void stop() {
        provider.stop();
        shouldStart = false;
    }

    @Override
    public Location getLastLocation() {
        return provider.getLastLocation();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Nothing to do here
    }

    @Override
    public void onConnectionSuspended(int i) {
        fallbackToLocationManager();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        fallbackToLocationManager();
    }

    private void fallbackToLocationManager() {
        logger.d("FusedLocationProvider not working, falling back and using LocationManager");
        provider = new LocationManagerProvider();
        provider.init(context, logger);
        if (shouldStart) {
            provider.start(listener, params, singleUpdate);
        }
    }
}
