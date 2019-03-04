
package com.techticz.app.model.launch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Launching {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("launchMessage")
    @Expose
    private String launchMessage;
    @SerializedName("launchCode")
    @Expose
    private String launchCode;
    @SerializedName("features")
    @Expose
    private Features features;
    @SerializedName("appVersionInfo")
    @Expose
    private AppVersionInfo appVersionInfo;
    @SerializedName("documentVersionInfo")
    @Expose
    private DocumentVersionInfo documentVersionInfo;
    @SerializedName("loginInfo")
    @Expose
    private LoginInfo loginInfo;

    public String getLaunchMessage() {
        return launchMessage;
    }

    public void setLaunchMessage(String launchMessage) {
        this.launchMessage = launchMessage;
    }

    public String getLaunchCode() {
        return launchCode;
    }

    public void setLaunchCode(String launchCode) {
        this.launchCode = launchCode;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }

    public AppVersionInfo getAppVersionInfo() {
        return appVersionInfo;
    }

    public void setAppVersionInfo(AppVersionInfo appVersionInfo) {
        this.appVersionInfo = appVersionInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    public DocumentVersionInfo getDocumentVersionInfo() {
        return documentVersionInfo;
    }

    public void setDocumentVersionInfo(DocumentVersionInfo documentVersionInfo) {
        this.documentVersionInfo = documentVersionInfo;
    }
}
