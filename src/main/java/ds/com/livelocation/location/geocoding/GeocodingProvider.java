package ds.com.livelocation.location.geocoding;

import android.content.Context;
import android.location.Location;

import ds.com.livelocation.location.OnGeocodingListener;
import ds.com.livelocation.location.OnReverseGeocodingListener;
import ds.com.livelocation.location.utils.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public interface GeocodingProvider {
    void init(Context context, Logger logger);

    void addName(String name, int maxResults);

    void addLocation(Location location, int maxResults);

    void start(OnGeocodingListener geocodingListener, OnReverseGeocodingListener reverseGeocodingListener);

    void stop();

}
