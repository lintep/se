import java.time.LocalDate;

// کلاس اصلی برای مدیریت تاریخ‌ها
public class DateUtil {
    public static boolean isTodayOrYesterday(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        return date.equals(today) || date.equals(yesterday);
    }
    
    // محاسبه تفاوت بین دو تاریخ بدون استفاده از ChronoUnit
    public static long getDaysBetween(LocalDate startDate, LocalDate endDate) {
        return Math.abs(startDate.toEpochDay() - endDate.toEpochDay());
    }
}
