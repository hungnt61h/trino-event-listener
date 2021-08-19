import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class Test {

    @org.junit.jupiter.api.Test
    public void playground() {
        Instant date = Calendar.getInstance().toInstant();
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        System.out.println(date.toString());
        System.out.println(sdf.format(date));
    }

}
