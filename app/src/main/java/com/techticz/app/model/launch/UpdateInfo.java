
package com.techticz.app.model.launch;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateInfo {

    @SerializedName("forceBelowVersion")
    @Expose
    private Integer forceBelowVersion;
    @SerializedName("suggestBelowVersion")
    @Expose
    private Integer suggestBelowVersion;
    @SerializedName("forceMsg")
    @Expose
    private String forceMsg;
    @SerializedName("suggestMsg")
    @Expose
    private String suggestMsg;
    @SerializedName("bannerUrl")
    @Expose
    private String bannerUrl;
    @SerializedName("updateFeatures")
    @Expose
    private List<UpdateFeature> updateFeatures = null;

    public Integer getForceBelowVersion() {
        return forceBelowVersion;
    }

    public void setForceBelowVersion(Integer forceBelowVersion) {
        this.forceBelowVersion = forceBelowVersion;
    }

    public Integer getSuggestBelowVersion() {
        return suggestBelowVersion;
    }

    public void setSuggestBelowVersion(Integer suggestBelowVersion) {
        this.suggestBelowVersion = suggestBelowVersion;
    }

    public String getForceMsg() {
        return forceMsg;
    }

    public void setForceMsg(String forceMsg) {
        this.forceMsg = forceMsg;
    }

    public String getSuggestMsg() {
        return suggestMsg;
    }

    public void setSuggestMsg(String suggestMsg) {
        this.suggestMsg = suggestMsg;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public List<UpdateFeature> getUpdateFeatures() {
        return updateFeatures;
    }

    public void setUpdateFeatures(List<UpdateFeature> updateFeatures) {
        this.updateFeatures = updateFeatures;
    }

}
