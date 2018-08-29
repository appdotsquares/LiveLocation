package ds.com.livelocation.location;

import ds.com.livelocation.location.geofencing.utils.TransitionGeofence;

/**
 * Created by mrm on 4/1/15.
 */
public interface OnGeofencingTransitionListener {
    void onGeofenceTransition(TransitionGeofence transitionGeofence);
}