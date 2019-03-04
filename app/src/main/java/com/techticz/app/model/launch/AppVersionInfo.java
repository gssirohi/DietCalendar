
package com.techticz.app.model.launch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppVersionInfo {

    @SerializedName("currentVersion")
    @Expose
    private CurrentVersion currentVersion;
    @SerializedName("updateInfo")
    @Expose
    private UpdateInfo updateInfo;

    public CurrentVersion getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(CurrentVersion currentVersion) {
        this.currentVersion = currentVersion;
    }

    public UpdateInfo getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(UpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
    }

}
