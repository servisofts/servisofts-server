package Servisofts;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class SUtil {
    private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    private static final DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static String formatTimestamp(Date date) {
        return formatter.format(date);
    }

    public static String now() {
        return formatter.format(new Date());
    }

    public static Date parseTimestamp(String timestamp) throws ParseException {
        if (timestamp.indexOf("\\.") > -1) {
            return formatter.parse(timestamp);
        }
        return formatter2.parse(timestamp);
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
    
    // ******* TEST PARSE FECHA **********
    // public static void main(String[] args) throws ParseException {
    //     String fecha1 = "2023-03-01T00:00:00";
    //     String fecha2 = "2023-03-01T00:00:00.000";
    //     String fecha3 = "2023-03-01T00:00:00.000000";
    //     System.out.println(SUtil.parseTimestamp(fecha1).toString());
    //     System.out.println(SUtil.parseTimestamp(fecha2).toString());
    //     System.out.println(SUtil.parseTimestamp(fecha3).toString());
    // }
}
