package org.hse.android.models;

import org.hse.android.models.ScheduleItem;

public class ScheduleItemHeader extends ScheduleItem {
    private String header;

    public String getTitle() {
        return header;
    }

    public void setTitle(String header) {
        this.header = header;
    }
}
