package edu.wtbu.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {
    public static Date ignoreTime(Date date){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
    public static boolean isSame(Date date1,Date date2){
        return ignoreTime(date1).equals(ignoreTime(date2));
    }
    public static int compareDates(Date date1,Date date2){
        return ignoreTime(date1).compareTo(date2);
    }
}
