
package com.techticz.app.model.dietplan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class AdminInfo {
    public AdminInfo() {
    }

    @SerializedName("createdOn")
    @Expose
    String createdOn;
    @SerializedName("createdBy")
    @Expose
    String createdBy;
    @SerializedName("published")
    @Expose
    Boolean published;
    @SerializedName("featured")
    @Expose
    Boolean featured;

    @SerializedName("publishedOn")
    @Expose
    String publishedOn;
    @SerializedName("publishedBy")
    @Expose
    String publishedBy;
    @SerializedName("verified")
    @Expose
    Boolean verified;
    @SerializedName("verifiedOn")
    @Expose
    String verifiedOn;
    @SerializedName("verifiedBy")
    @Expose
    String verifiedBy;
    @SerializedName("lastModifiedOn")
    @Expose
    String lastModifiedOn;
    @SerializedName("lastModifiedBy")
    @Expose
    String lastModifiedBy;
    @SerializedName("hasLock")
    @Expose
    Boolean hasLock;

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(String publishedOn) {
        this.publishedOn = publishedOn;
    }

    public String getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
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

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public String getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(String lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Boolean getHasLock() {
        return hasLock;
    }

    public void setHasLock(Boolean hasLock) {
        this.hasLock = hasLock;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
}
