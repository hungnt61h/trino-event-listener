package vn.edu.ptit.utils;

import io.trino.spi.eventlistener.QueryContext;
import io.trino.spi.eventlistener.QueryMetadata;
import io.trino.spi.eventlistener.TableInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import vn.edu.ptit.constants.Field;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PluginUtil {

    public static boolean isDeclaredProperty(Map<String, String> config, String property) {
        return config.containsKey(property) && config.get(property) != null;
    }

    public static boolean isBlankProperty(Map<String, String> config, String property) {
        return config.get(property).isBlank();
    }

    public static boolean isAvailableProperty(Map<String, String> config, String property) {
        if (isDeclaredProperty(config, property)) {
            return !isBlankProperty(config, property);
        } else {
            return false;
        }
    }

    public static Logger loadLog4j2ConfigFile(String configPath, Class<?> className) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.setConfigLocation(new File(configPath).toURI());
        return context.getLogger(className);
    }

    public static List<String> generateFields(String fields) {
        String[] fieldNames = fields.split(";");
        List<String> fieldList = new ArrayList<>();

        for (String field : fieldNames) {
            fieldList.add(field.trim());
        }

        return fieldList;
    }

    public static String flattenQuery(String query) {
        // TO-DO
        return query;
    }

    public static String getMetaTables(List<TableInfo> tableInfos) {
        StringBuilder sb = new StringBuilder();
        tableInfos.forEach(t -> sb
                .append(t.getCatalog()).append(".")
                .append(t.getSchema()).append(".")
                .append(t.getTable()).append(","));
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static String getSessionProperties(Map<String, String> sessionProperties) {
        StringBuilder sb = new StringBuilder();
        sessionProperties.forEach((k, v) -> sb.append(k).append("->").append(v).append(","));
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static String getMetaField(QueryMetadata meta, String field, boolean isInlineQuery) {
        switch (field) {
            case Field.META_QUERY_ID:
                return meta.getQueryId();
            case Field.META_TRANSACTION_ID:
                return meta.getTransactionId().orElse(null);
            case Field.META_QUERY:
                if (isInlineQuery) {
                    return flattenQuery(meta.getQuery());
                } else {
                    return meta.getQuery();
                }
            case Field.META_UPDATE_TYPE:
                return meta.getUpdateType().orElse(null);
            case Field.META_PREPARED_QUERY:
                return meta.getPreparedQuery().orElse(null);
            case Field.META_QUERY_STATE:
                return meta.getQueryState();
            case Field.META_TABLES:
                return getMetaTables(meta.getTables());
            case Field.META_ROUTINES:
                // future work
                return null;
            case Field.META_URI:
                return meta.getUri().toString();
            case Field.META_PLAN:
                return meta.getPlan().orElse(null);
            case Field.META_PAYLOAD:
                return meta.getPayload().orElse(null);
            default:
                return null;
        }
    }

    public static String getContextField(QueryContext context, String field) {
        switch (field) {
            case Field.CTX_USER:
                return context.getUser();
            case Field.CTX_PRINCIPAL:
                return context.getPrincipal().orElse(null);
            case Field.CTX_GROUPS:
                return String.join(",", context.getGroups());
            case Field.CTX_TRACE_TOKEN:
                return context.getTraceToken().orElse(null);
            case Field.CTX_REMOTE_CLIENT_ADDRESS:
                return context.getRemoteClientAddress().orElse(null);
            case Field.CTX_USER_AGENT:
                return context.getUserAgent().orElse(null);
            case Field.CTX_CLIENT_INFO:
                return context.getClientInfo().orElse(null);
            case Field.CTX_CLIENT_TAGS:
                return String.join(",", context.getClientTags());
            case Field.CTX_CLIENT_CAPABILITIES:
                return String.join(",", context.getClientCapabilities());
            case Field.CTX_SOURCE:
                return context.getSource().orElse(null);
            case Field.CTX_CATALOG:
                return context.getCatalog().orElse(null);
            case Field.CTX_SCHEMA:
                return context.getSchema().orElse(null);
            case Field.CTX_RESOURCE_GROUP_ID:
                // need diving deeper next time
                return Objects.requireNonNull(context.getResourceGroupId().orElse(null)).toString();
            case Field.CTX_SESSION_PROPERTIES:
                return getSessionProperties(context.getSessionProperties());
            case Field.CTX_RESOURCE_ESTIMATES:
                return context.getResourceEstimates().toString();
            case Field.CTX_SERVER_ADDRESS:
                return context.getServerAddress();
            case Field.CTX_SERVER_VERSION:
                return context.getServerVersion();
            case Field.CTX_ENVIRONMENT:
                return context.getEnvironment();
            case Field.CTX_QUERY_TYPE:
                return Objects.requireNonNull(context.getQueryType().orElse(null)).toString();
            default:
                return null;
        }
    }

}
