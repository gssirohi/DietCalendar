package com.techticz.powerkit.constant

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 31/8/18.
 */
class Environment {

    companion object {
        var showNetworkLogs = true
        var showSnackbarAsDefaultFeedback = true

        //public static final String BASE_URL_RFS = "http://172.16.6.227:6060/corpccwebapp/mobile/";
        val BASE_URL_RFS = "https://securerfs2.yatra.com/corpccwebapp/mobile/common/"
        //    public static final String BASE_URL_RFS = "http://172.16.1.131:6565/ccwebapp/mobile/";
        val BASE_URL_RFS_227 = "http://172.16.6.227:6060/corpccwebapp/mobile/"
        //    public static String BASE_URL_PROD = "http://172.16.1.131:6565/ccwebapp/mobile/";
        var baseUrl = BASE_URL_RFS
    }

}