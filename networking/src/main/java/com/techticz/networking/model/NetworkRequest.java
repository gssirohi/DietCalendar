package com.techticz.networking.model;

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 4/12/17.
 */

public class NetworkRequest {
    private String platform = "android";
    private String deviceId = "889c36a073dbe787";
    private String appVersion = "207004";
    private String playStore = "GOOGLE";
    private String mode = "INSTALL";
    private String deviceType = "mobile";
    private int osVersion = 25;
    private String appId = "com.yatra.corporate";
    private String sessionId = "1191ca84-892a-4802-b2ad-86e4b0e1acb7";

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getPlayStore() {
        return playStore;
    }

    public void setPlayStore(String playStore) {
        this.playStore = playStore;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(int osVersion) {
        this.osVersion = osVersion;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
