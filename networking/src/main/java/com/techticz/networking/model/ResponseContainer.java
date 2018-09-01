
package com.techticz.networking.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseContainer {

    @SerializedName("resCode")
    @Expose
    private Integer resCode;
    @SerializedName("resMessage")
    @Expose
    private String resMessage;
    @SerializedName("interactionId")
    @Expose
    private String interactionId;
    @SerializedName("interationType")
    @Expose
    private String interationType;
    @SerializedName("sessionId")
    @Expose
    private String sessionId;

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getResMessage() {
        return resMessage;
    }

    public void setResMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public String getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(String interactionId) {
        this.interactionId = interactionId;
    }

    public String getInterationType() {
        return interationType;
    }

    public void setInterationType(String interationType) {
        this.interationType = interationType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
