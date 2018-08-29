package ds.com.livelocation.location.activity.config;

/**
 * Created by mrm on 3/1/15.
 */
public class ActivityParams {
    // Defaults
    public static final ds.com.livelocation.location.activity.config.ActivityParams NORMAL = new Builder().setInterval(500).build();

    private long interval;

    ActivityParams(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ds.com.livelocation.location.activity.config.ActivityParams)) return false;

        ds.com.livelocation.location.activity.config.ActivityParams that = (ds.com.livelocation.location.activity.config.ActivityParams) o;

        return interval == that.interval;

    }

    @Override
    public int hashCode() {
        return (int) (interval ^ (interval >>> 32));
    }

    public static class Builder {
        private long interval;

        public Builder setInterval(long interval) {
            this.interval = interval;
            return this;
        }

        public ds.com.livelocation.location.activity.config.ActivityParams build() {
            return new ds.com.livelocation.location.activity.config.ActivityParams(interval);
        }
    }
}
