package ds.com.livelocation;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ds.com.livelocation.location.MyLocation;
import ds.com.livelocation.location.OnLocationUpdatedListener;
import ds.com.livelocation.location.location.config.LocationAccuracy;
import ds.com.livelocation.location.location.config.LocationParams;
import ds.com.livelocation.location.location.providers.LocationGooglePlayServicesProvider;
import ds.com.livelocation.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;


public final class LocationLive extends LiveData<Location> implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public MyLocation myLocation;
    public boolean showProgress = false;
    private static LocationGooglePlayServicesProvider provider;
    public static final int LOCATION_CODE = 1001;
    private static LocationParams.Builder locationBuilder = new LocationParams.Builder();
    private Context context;
    LocationParams locationParams;
    Custom manual;

    private LocationLive() {
    }


    // -- getters, hashCode, equals -- //

    // Static inner Builder class
    public static class Builder {
        private LocationLive liveData = new LocationLive();
        boolean checkGps = true;
        Context context;
        Custom manual;

        LocationParams locationParams = LocationParams.BEST_EFFORT;

        public Builder with(Context val) {
            liveData.context = val;
            context = val;

            provider = new LocationGooglePlayServicesProvider();
//            provider.setCheckLocationSettings(true);
            locationBuilder.setAccuracy(LocationAccuracy.HIGH);
            locationBuilder.setInterval(0);
            locationBuilder.setDistance(0);
            return this;
        }


        public Builder custom(Custom val) {
            manual = val;
            return this;
        }
        public Builder showProgress(boolean val) {
            liveData.showProgress = val;
            return this;
        }

        public Builder custom(LocationParams val) {
            this.locationParams = val;
            return this;
        }

        public Builder checkGps(boolean val) {

            this.checkGps = val;
            return this;
        }


        public LocationLive build() {
//             Validate object
            if (context == null) {
                throw new IllegalArgumentException("lifecycle owner must not null");
            }

            if (liveData.hasActiveObservers())
                liveData.removeObservers((LifecycleOwner) this.context);

            liveData.observe((LifecycleOwner) context, location ->{
                        ((LocationLive.LocationUpdate) context).onLocationUpdate(location);
                        Util.hideProgress();
            }
            );

            if (checkGps)
                liveData.checkGps(context);
            else
                liveData.checkLocationPermission(context);

            return liveData;
        }
    }


    OnLocationUpdatedListener locationListener = location -> {
        postValue(location);
    };

    public interface LocationUpdate {
        void onLocationUpdate(Location location);
    }

    public LocationLive stop() {
        MyLocation.with(context).location().stop();
        return this;
    }

    public LocationLive start() {

        if(showProgress)
            Util.showProgress(context);

        myLocation = new MyLocation.Builder(context).logging(true).build();

        if (locationParams != null)
            myLocation.location(provider).continuous().config(locationParams).start(locationListener);

        else if (manual != null) {
            locationBuilder.setInterval(manual.interval);
            locationBuilder.setDistance(manual.distance);
            locationBuilder.setAccuracy(manual.accuracy);
            myLocation.location(provider).continuous().config(locationBuilder.build()).start(locationListener);

        } else
            myLocation.location(provider).continuous().config(LocationParams.NAVIGATION).start(locationListener);

        return this;
    }


    public static class Custom {
        public long interval = 0;
        public long distance = 0;
        public LocationAccuracy accuracy = LocationAccuracy.HIGH;

        public Custom() {
        }

        public Custom(long interval, long distance, LocationAccuracy accuracy) {
            this.interval = interval;
            this.distance = distance;
            this.accuracy = accuracy;
        }

        public long getInterval() {
            return interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public long getDistance() {
            return distance;
        }

        public void setDistance(long distance) {
            this.distance = distance;
        }

        public LocationAccuracy getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(LocationAccuracy accuracy) {
            this.accuracy = accuracy;
        }

        public Custom interval(int val) {
            interval = val;
            return this;
        }

        public Custom distance(int val) {
            distance = val;
            return this;
        }

        public Custom accuracy(LocationAccuracy val) {
            accuracy = val;
            return this;
        }

        public Custom done() {

            return this;
        }
    }


    /*
     * check location permission
     * */
    private void checkLocationPermission(Context context) {
        try {
            TedPermission.with(context)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            start();
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                        }
                    })
                    .setDeniedMessage("Location permission is required for core functionality.")
                    .setRationaleMessage("Please turn on location permission.")
                    .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .check();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_CODE) {
            checkLocationPermission(context);
        }
    }

    public static Boolean isGpsEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void checkGps(Context context) {
        if (!MyLocation.with(context).location().state().isGpsAvailable() || !isGpsEnabled(context)) {
            autoOnGPS(context);
        } else
            checkLocationPermission(context);
    }

    /*
     * Request user to enable GPS if its not enabled
     * */
    private void autoOnGPS(Context context) {
        GoogleApiClient googleApiClient = null;

        googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).addConnectionCallbacks(LocationLive.this).addOnConnectionFailedListener(LocationLive.this).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); // this is the key ingredient

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();

            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    checkLocationPermission(context);
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult((Activity) context, LOCATION_CODE);
                    } catch (Exception e) { /*Ignore the error.*/}
                    break;
            }
        });
    }

    public void getAddress(Location location) {
        MyLocation.with(context).geocoding()
                .reverse(location, (location1, list) -> {
                    if ((list != null && list.size() > 0)) {
                        String addressLine = list.get(0).getAddressLine(0);
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}

 /*   public Builder interval(int val) {

            locationBuilder.setInterval(val);
            return this;
        }
*/

     /*   public Builder distance(int val) {

            locationBuilder.setDistance(val);
            return this;
        }*/

     /*   public Builder accuracy(LocationAccuracy val) {

            locationBuilder.setAccuracy(val);
            return this;
        }*/
