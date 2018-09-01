package com.techticz.networking.model;

import android.arch.persistence.room.Ignore;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 13/12/17.
 */

public class CommonResponse {

    @SerializedName("isUpdateRequired")
    String isUpdateRequired;
    @SerializedName("isForceUpdate")
    String isForceUpdate;
    @SerializedName("newAppVersion")
    String newAppVersion;
    @SerializedName("remindLaterCount")
    int remindLaterCount;
    @SerializedName("messageTitle")
    String messageTitle;
    @SerializedName("updatePopUpBodyMessage")
    String updatePopUpBodyMessage;
    @SerializedName("featuresHeader")
    String featuresHeader;
    @SerializedName("upgradeBtnMsg")
    String upgradeBtnMsg;
    @SerializedName("remindLatterBtnMsg")
    String remindLatterBtnMsg;
    @SerializedName("features")
    @Ignore
    public List<AppUpdateFeatures> appUpdateFeatures;
    @SerializedName("showRegistrationPromocode")
    String showRegistrationPromocode;

    public CommonResponse() {
    }

    public List<AppUpdateFeatures> getAppUpdateFeatures() {
        return appUpdateFeatures;
    }

    public void setAppUpdateFeatures(List<AppUpdateFeatures> appUpdateFeatures) {
        this.appUpdateFeatures = appUpdateFeatures;
    }

    public String getIsUpdateRequired() {
        return isUpdateRequired;
    }

    public void setIsUpdateRequired(String isUpdateRequired) {
        this.isUpdateRequired = isUpdateRequired;
    }

    public String getIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(String isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public String getNewAppVersion() {
        return newAppVersion;
    }

    public void setNewAppVersion(String newAppVersion) {
        this.newAppVersion = newAppVersion;
    }

    public int getRemindLaterCount() {
        return remindLaterCount;
    }

    public void setRemindLaterCount(int remindLaterCount) {
        this.remindLaterCount = remindLaterCount;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getUpdatePopUpBodyMessage() {
        return updatePopUpBodyMessage;
    }

    public void setUpdatePopUpBodyMessage(String updatePopUpBodyMessage) {
        this.updatePopUpBodyMessage = updatePopUpBodyMessage;
    }

    public String getFeaturesHeader() {
        return featuresHeader;
    }

    public void setFeaturesHeader(String featuresHeader) {
        this.featuresHeader = featuresHeader;
    }

    public String getUpgradeBtnMsg() {
        return upgradeBtnMsg;
    }

    public void setUpgradeBtnMsg(String upgradeBtnMsg) {
        this.upgradeBtnMsg = upgradeBtnMsg;
    }

    public String getRemindLatterBtnMsg() {
        return remindLatterBtnMsg;
    }

    public void setRemindLatterBtnMsg(String remindLatterBtnMsg) {
        this.remindLatterBtnMsg = remindLatterBtnMsg;
    }


    public String getShowRegistrationPromocode() {
        return showRegistrationPromocode;
    }

    public void setShowRegistrationPromocode(String showRegistrationPromocode) {
        this.showRegistrationPromocode = showRegistrationPromocode;
    }
}
