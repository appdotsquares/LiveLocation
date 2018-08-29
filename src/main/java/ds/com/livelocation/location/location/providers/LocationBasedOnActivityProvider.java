package ds.com.livelocation.location.location.providers;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import ds.com.livelocation.location.OnActivityUpdatedListener;
import ds.com.livelocation.location.OnLocationUpdatedListener;
import ds.com.livelocation.location.activity.config.ActivityParams;
import ds.com.livelocation.location.activity.providers.ActivityGooglePlayServicesProvider;
import ds.com.livelocation.location.location.LocationProvider;
import ds.com.livelocation.location.location.config.LocationParams;
import ds.com.livelocation.location.utils.Logger;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by mrm on 20/12/14.
 */
public class LocationBasedOnActivityProvider implements LocationProvider, OnActivityUpdatedListener {
    private final ActivityGooglePlayServicesProvider activityProvider;
    private final LocationGooglePlayServicesProvider locationProvider;
    private final LocationBasedOnActivityListener locationBasedOnActivityListener;
    private OnLocationUpdatedListener locationUpdatedListener;
    private LocationParams locationParams;

    public LocationBasedOnActivityProvider(@NonNull LocationBasedOnActivityListener locationBasedOnActivityListener) {
        activityProvider = new ActivityGooglePlayServicesProvider();
        locationProvider = new LocationGooglePlayServicesProvider();
        this.locationBasedOnActivityListener = locationBasedOnActivityListener;
    }

    @Override
    public void init(Context context, Logger logger) {
        locationProvider.init(context, logger);
        activityProvider.init(context, logger);
    }

    @Override
    public void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate) {
        if (singleUpdate) {
            throw new IllegalArgumentException("singleUpdate cannot be set to true");
        }
        locationProvider.start(listener, params, false);
        activityProvider.start(this, ActivityParams.NORMAL);
        this.locationParams = params;
        this.locationUpdatedListener = listener;
    }

    @Override
    public void stop() {
        locationProvider.stop();
        activityProvider.stop();
    }

    @Override
    public Location getLastLocation() {
        return locationProvider.getLastLocation();
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        LocationParams params = locationBasedOnActivityListener.locationParamsForActivity(detectedActivity);
        if (params != null && locationParams != null && !locationParams.equals(params)) {
            start(locationUpdatedListener, params, false);
        }
    }

    public interface LocationBasedOnActivityListener {
        public LocationParams locationParamsForActivity(DetectedActivity detectedActivity);
    }
}
