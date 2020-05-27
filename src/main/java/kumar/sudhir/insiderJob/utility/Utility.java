package kumar.sudhir.insiderJob.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static String convertUnixTimeToHumanReadTIme( long unixTime){
        long unixSeconds = unixTime;
        // convert seconds to milliseconds
        Date date = new Date(unixSeconds*1000L);
        // the format of your date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
