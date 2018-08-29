package ds.com.livelocation.location.location;

import android.content.Context;
import android.location.Location;

import ds.com.livelocation.location.OnLocationUpdatedListener;
import ds.com.livelocation.location.location.config.LocationParams;
import ds.com.livelocation.location.utils.Logger;

// ds.com.livelocation.location
// ds.com.livelocation.location

/**
 * Created by mrm on 20/12/14.
 */
public interface LocationProvider {
    void init(Context context, Logger logger);

    void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate);

    void stop();

    Location getLastLocation();

}
