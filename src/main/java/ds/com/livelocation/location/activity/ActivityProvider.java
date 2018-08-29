package ds.com.livelocation.location.activity;

import android.content.Context;

import ds.com.livelocation.location.OnActivityUpdatedListener;
import ds.com.livelocation.location.activity.config.ActivityParams;
import ds.com.livelocation.location.utils.Logger;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by mrm on 3/1/15.
 */
public interface ActivityProvider {
    void init(Context context, Logger logger);

    void start(OnActivityUpdatedListener listener, ActivityParams params);

    void stop();

    DetectedActivity getLastActivity();
}
