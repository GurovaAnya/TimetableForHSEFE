package org.hse.android.models;

import com.google.gson.annotations.SerializedName;

import org.hse.android.models.TimeZone;

public class TimeResponse{
    @SerializedName("time_zone")
    private TimeZone timeZone;

    public TimeZone getTimeZone(){
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
