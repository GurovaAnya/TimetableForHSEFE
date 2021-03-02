package org.hse.android;

public enum PreferenceValues {
    AVATAR_PREFERENCE_KEY ("avatar"),
    NAME_KEY ("user_name");

    private String value;

    PreferenceValues(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
