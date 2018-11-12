package com.techticz.auth.constant;

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 2/9/17.
 */

public enum AuthProviders {
    FACEBOOK(1),
    GOOGLE(2),
    PHONE(3);

    public final int code;

    AuthProviders(int i) {
        code = i;
    }

    public int getCode() {
        return code;
    }

    public static AuthProviders getbyCode(int code) {
        if(code == GOOGLE.code) return GOOGLE;
        if(code == FACEBOOK.code) return FACEBOOK;
        if(code == PHONE.code) return PHONE;
        return null;
    }
}
