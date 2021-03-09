package org.hse.android;

import com.google.gson.annotations.SerializedName;

public class TimeResponse{
    @SerializedName("time_zone")
    private org.hse.android.TimeZone timeZone;

    public org.hse.android.TimeZone getTimeZone(){
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
