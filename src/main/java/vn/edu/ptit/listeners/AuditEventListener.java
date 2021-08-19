package vn.edu.ptit.listeners;

import io.trino.spi.eventlistener.EventListener;
import io.trino.spi.eventlistener.QueryCompletedEvent;
import io.trino.spi.eventlistener.QueryCreatedEvent;
import io.trino.spi.eventlistener.SplitCompletedEvent;
import org.apache.logging.log4j.Logger;
import vn.edu.ptit.constants.Config;
import vn.edu.ptit.constants.Default;
import vn.edu.ptit.constants.Field;
import vn.edu.ptit.utils.PluginUtil;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AuditEventListener implements EventListener {

    private final Logger logger;
    private final DateTimeFormatter dtf;
    private final boolean isInlineQuery;

    private String creationLogFormat;
    private String completionLogFormat;
    private String splitLogFormat;

    private List<String> creationFields;
    private List<String> completionFields;
    private List<String> splitFields;

    private boolean creationEnable = false;
    private boolean completionEnable = false;
    private boolean splitEnable = false;

    public AuditEventListener(Map<String, String> config) throws IOException {
        if (PluginUtil.isAvailableProperty(config, Config.LOG4J_PATH)) {
            this.logger = PluginUtil.loadLog4j2ConfigFile(config.get(Config.LOG4J_PATH), AuditEventListener.class);
        } else {
            throw new IOException(Config.LOG4J_PATH + " property does not exist");
        }

        if (PluginUtil.isAvailableProperty(config, Config.CRT_ENABLE)) {
            loadCreationListenerConfig(config);
        }

        if (PluginUtil.isAvailableProperty(config, Config.CPL_ENABLE)) {
            loadCompletionListenerConfig(config);
        }

        if (PluginUtil.isAvailableProperty(config, Config.SPL_ENABLE)) {
            loadSplitListenerConfig(config);
        }

        if (PluginUtil.isAvailableProperty(config, Config.DATETIME_FORMAT)) {
            this.dtf = DateTimeFormatter.ofPattern(config.get(Config.DATETIME_FORMAT)).withZone(Default.DEFAULT_TIMEZONE);
        } else {
            this.dtf = DateTimeFormatter.ofPattern(Default.DEFAULT_DATE_FORMAT).withZone(Default.DEFAULT_TIMEZONE);
        }

        if (PluginUtil.isAvailableProperty(config, Config.INLINE_QUERY)) {
            this.isInlineQuery = Boolean.parseBoolean(config.get(Config.INLINE_QUERY));
        } else {
            this.isInlineQuery = false;
        }
    }

    private void loadCreationListenerConfig(Map<String, String> config) {
        if (Boolean.parseBoolean(config.get(Config.CRT_ENABLE))) {
            this.creationEnable = true;

            if (PluginUtil.isAvailableProperty(config, Config.CRT_LOG_FORMAT)) {
                this.creationLogFormat = "[CREATION] - " + config.get(Config.CRT_LOG_FORMAT);
            } else {
                this.creationLogFormat = Default.DEFAULT_LOG_FORMAT;
            }

            if (PluginUtil.isAvailableProperty(config, Config.CRT_LOG_FIELDS)) {
                this.creationFields = PluginUtil.generateFields(config.get(Config.CRT_LOG_FIELDS));
            } else {
                this.creationFields = PluginUtil.generateFields(Default.DEFAULT_LOG_FIELDS);
            }
        }
    }

    private void loadCompletionListenerConfig(Map<String, String> config) {
        if (Boolean.parseBoolean(config.get(Config.CPL_ENABLE))) {
            this.completionEnable = true;

            if (PluginUtil.isAvailableProperty(config, Config.CPL_LOG_FORMAT)) {
                this.completionLogFormat = "[COMPLETION] - " + config.get(Config.CPL_LOG_FORMAT);
            } else {
                this.completionLogFormat = Default.DEFAULT_LOG_FORMAT;
            }

            if (PluginUtil.isAvailableProperty(config, Config.CPL_LOG_FIELDS)) {
                this.completionFields = PluginUtil.generateFields(config.get(Config.CPL_LOG_FIELDS));
            } else {
                this.completionFields = PluginUtil.generateFields(Default.DEFAULT_LOG_FIELDS);
            }
        }
    }

    private void loadSplitListenerConfig(Map<String, String> config) {
        if (Boolean.parseBoolean(config.get(Config.SPL_ENABLE))) {
            this.splitEnable = true;

            if (PluginUtil.isAvailableProperty(config, Config.SPL_LOG_FORMAT)) {
                this.splitLogFormat = "[SPLIT] - " + config.get(Config.SPL_LOG_FORMAT);
            } else {
                this.splitLogFormat = Default.DEFAULT_LOG_FORMAT;
            }

            if (PluginUtil.isAvailableProperty(config, Config.SPL_LOG_FIELDS)) {
                this.splitFields = PluginUtil.generateFields(config.get(Config.SPL_LOG_FIELDS));
            } else {
                this.splitFields = PluginUtil.generateFields(Default.DEFAULT_SPLIT_LOG_FIELDS);
            }
        }
    }

    @Override
    public void queryCreated(QueryCreatedEvent event) {
        if (this.creationEnable) {
            List<String> fieldValues = new ArrayList<>();

            this.creationFields.forEach(field -> fieldValues.add(getCreationEventField(event, field)));

            String message = MessageFormat.format(this.creationLogFormat, fieldValues.toArray());
            this.logger.info(message);
        }
    }

    private String getCreationEventField(QueryCreatedEvent event, String field) {
        String value;

        value = PluginUtil.getMetaField(event.getMetadata(), field, this.isInlineQuery);
        if (value == null) {
            value = PluginUtil.getContextField(event.getContext(), field);
        }
        if (value == null) {
            if (field.equals(Field.CRT_CREATED_TIME))
                value = this.dtf.format(event.getCreateTime());
        }

        return value;
    }

    @Override
    public void queryCompleted(QueryCompletedEvent event) {
        if (this.completionEnable) {
            List<String> fieldValues = new ArrayList<>();

            this.completionFields.forEach(field -> fieldValues.add(getCompletionEventField(event, field)));

            String message = MessageFormat.format(this.completionLogFormat, fieldValues.toArray());
            this.logger.info(message);
        }
    }

    private String getCompletionEventField(QueryCompletedEvent event, String field) {
        String value;

        value = PluginUtil.getMetaField(event.getMetadata(), field, this.isInlineQuery);
        if (value == null) {
            value = PluginUtil.getContextField(event.getContext(), field);
        }
        if (value == null) {
            switch (field) {
                case Field.CPL_CREATE_TIME:
                    return this.dtf.format(event.getCreateTime());
                case Field.CPL_EXECUTION_START_TIME:
                    return this.dtf.format(event.getExecutionStartTime());
                case Field.CPL_END_TIME:
                    return this.dtf.format(event.getEndTime());
                case Field.CPL_QUERY_WARNINGS:
                    StringBuilder sb = new StringBuilder("{ ");

                    event.getWarnings().forEach(warning -> sb.append(warning.getWarningCode())
                            .append(": ")
                            .append(warning.getMessage())
                            .append(","));
                    sb.deleteCharAt(sb.length() - 1);

                    sb.append(" }");
                    return sb.toString();
                case Field.CPL_ERROR_CODE:
                    return String.valueOf(Objects.requireNonNull(event.getFailureInfo().orElse(null)).getErrorCode().getCode());
                case Field.CPL_FAILURE_TYPE:
                    return Objects.requireNonNull(event.getFailureInfo().orElse(null)).getFailureType().orElse(null);
                case Field.CPL_FAILURE_MESSAGE:
                    return Objects.requireNonNull(event.getFailureInfo().orElse(null)).getFailureMessage().orElse(null);
                case Field.CPL_FAILURE_TASK:
                    return Objects.requireNonNull(event.getFailureInfo().orElse(null)).getFailureTask().orElse(null);
                case Field.CPL_FAILURE_HOST:
                    return Objects.requireNonNull(event.getFailureInfo().orElse(null)).getFailureHost().orElse(null);
                case Field.CPL_FAILURES_JSON:
                    return Objects.requireNonNull(event.getFailureInfo().orElse(null)).getFailuresJson();
                case Field.CPL_STATISTICS:
                    return event.getStatistics().toString();
                case Field.CPL_IO_METADATA:
                    return event.getIoMetadata().toString();
                default:
                    return null;
            }
        }

        return value;
    }

    @Override
    public void splitCompleted(SplitCompletedEvent event) {
        if (this.splitEnable) {
            List<String> fieldValues = new ArrayList<>();

            this.splitFields.forEach(field -> fieldValues.add(getSplitEventField(event, field)));

            String message = MessageFormat.format(this.splitLogFormat, fieldValues.toArray());
            this.logger.info(message);
        }
    }

    private String getSplitEventField(SplitCompletedEvent event, String field) {
        switch (field) {
            case Field.SPL_FAILURE_TYPE:
                return Objects.requireNonNull(event.getFailureInfo().orElse(null)).getFailureType();
            case Field.SPL_FAILURE_MESSAGE:
                return Objects.requireNonNull(event.getFailureInfo().orElse(null)).getFailureMessage();
            case Field.SPL_PAYLOAD:
                return event.getPayload();
            case Field.SPL_QUERY_ID:
                return event.getQueryId();
            case Field.SPL_STAGE_ID:
                return event.getStageId();
            case Field.SPL_TASK_ID:
                return event.getTaskId();
            case Field.SPL_CATALOG_NAME:
                return event.getCatalogName().orElse(null);
            case Field.SPL_CREATE_TIME:
                return this.dtf.format(event.getCreateTime());
            case Field.SPL_START_TIME:
                if (event.getStartTime().isEmpty()) {
                    return null;
                } else {
                    return this.dtf.format(event.getStartTime().get());
                }
            case Field.SPL_END_TIME:
                if (event.getEndTime().isEmpty()) {
                    return null;
                } else {
                    return this.dtf.format(event.getEndTime().get());
                }
            case Field.SPL_STATISTICS:
                return event.getStatistics().toString();
            default:
                return null;
        }
    }

}
