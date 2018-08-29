package ds.com.livelocation.location;

import ds.com.livelocation.location.geocoding.utils.LocationAddress;

import java.util.List;

/**
 * Created by mrm on 4/1/15.
 */
public interface OnGeocodingListener {
    void onLocationResolved(String name, List<LocationAddress> results);
}