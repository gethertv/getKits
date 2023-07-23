package dev.gether.getkits.utils;


import dev.gether.getkits.GetKits;

import java.text.DecimalFormat;

public class Timer {

    // "{d}{h}{m}{s}"
    static String format = GetKits.getInstance().getConfig().getString("format");
    static String day = GetKits.getInstance().getConfig().getString("day");
    static String hour = GetKits.getInstance().getConfig().getString("hour");
    static String min = GetKits.getInstance().getConfig().getString("min");
    static String sec = GetKits.getInstance().getConfig().getString("sec");

    static DecimalFormat df = new DecimalFormat("00");
    public static String getTime(int second)
    {
        boolean day = false;
        boolean hour = false;
        boolean min = false;

        if(second>=86400)
            day = true;

        if(second>=3600)
            hour = true;

        if(second>=60)
            min = true;

        String format = Timer.format;

        String d = Timer.day;
        String h = Timer.hour;
        String m = Timer.min;
        String s = Timer.sec;

        format = format
                .replace("{d}", day ? d.replace("{day}", df.format(second/86400)) : "")
                .replace("{h}", hour ? h.replace("{hour}", df.format((second/3600)%24)) : "")
                .replace("{m}", min ? m.replace("{min}", df.format((second/60)%60)) : "")
                .replace("{s}", s.replace("{sec}", df.format(second%60)));


        return ColorFixer.addColors(format);
    }
}
