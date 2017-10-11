package tmall.util;

public class DateUtil {
    public static java.sql.Timestamp dateToTimestamp(java.util.Date date){
        if (null == date) {
            return null;
        }
        return new java.sql.Timestamp(date.getTime());
    }

    public static java.util.Date timestampToDate(java.sql.Timestamp timestamp) {
        if (null == timestamp) {
            return null;
        }
        return new java.util.Date(timestamp.getTime());
    }
}
