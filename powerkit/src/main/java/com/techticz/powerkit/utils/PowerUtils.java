package com.techticz.powerkit.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PowerUtils
{
    public static String getTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        return timeStamp;
    }
}
