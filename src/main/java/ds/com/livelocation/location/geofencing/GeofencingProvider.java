package ds.com.livelocation.location.geofencing;

import android.content.Context;

import ds.com.livelocation.location.OnGeofencingTransitionListener;
import ds.com.livelocation.location.geofencing.model.GeofenceModel;
import ds.com.livelocation.location.utils.Logger;

import java.util.List;

/**
 * Created by mrm on 20/12/14.
 */
public interface GeofencingProvider {
    void init(Context context, Logger logger);

    void start(OnGeofencingTransitionListener listener);

    void addGeofence(GeofenceModel geofence);

    void addGeofences(List<GeofenceModel> geofenceList);

    void removeGeofence(String geofenceId);

    void removeGeofences(List<String> geofenceIds);

    void stop();

}
