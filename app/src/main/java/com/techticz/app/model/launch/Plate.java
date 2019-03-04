
package com.techticz.app.model.launch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Plate {

    @SerializedName("browse")
    @Expose
    private Boolean browse;
    @SerializedName("create")
    @Expose
    private Boolean create;
    @SerializedName("view")
    @Expose
    private Boolean view;
    @SerializedName("update")
    @Expose
    private Boolean update;
    @SerializedName("delete")
    @Expose
    private Boolean delete;

    public Boolean getBrowse() {
        return browse;
    }

    public void setBrowse(Boolean browse) {
        this.browse = browse;
    }

    public Boolean getCreate() {
        return create;
    }

    public void setCreate(Boolean create) {
        this.create = create;
    }

    public Boolean getView() {
        return view;
    }

    public void setView(Boolean view) {
        this.view = view;
    }

    public Boolean getUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

}
