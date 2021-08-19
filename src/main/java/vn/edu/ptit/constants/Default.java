package vn.edu.ptit.constants;

import java.time.ZoneId;

public final class Default {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_LOG_FORMAT = "{0}|{1}|{2}|{3}";
    public static final String DEFAULT_LOG_FIELDS = "meta.queryId;meta.query;ctx.user;ctx.remoteClientAddress";
    public static final String DEFAULT_SPLIT_LOG_FIELDS = "spl.queryId;spl.payload;spl.startTime;spl.failureMessage";

    public static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("Asia/Ho_Chi_Minh");

}
