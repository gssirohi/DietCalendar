
package com.techticz.app.model.food;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Status {

    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("createdOn")
    @Expose
    private String createdOn;
    @SerializedName("verifiedBy")
    @Expose
    private String verifiedBy;
    @SerializedName("verified")
    @Expose
    private Boolean verified;
    @SerializedName("verifiedOn")
    @Expose
    private String verifiedOn;
    @SerializedName("published")
    @Expose
    private Boolean published;
    @SerializedName("publishedBy")
    @Expose
    private String publishedBy;
    @SerializedName("publishedOn")
    @Expose
    private String publishedOn;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getVerifiedOn() {
        return verifiedOn;
    }

    public void setVerifiedOn(String verifiedOn) {
        this.verifiedOn = verifiedOn;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }

    public String getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(String publishedOn) {
        this.publishedOn = publishedOn;
    }

}
