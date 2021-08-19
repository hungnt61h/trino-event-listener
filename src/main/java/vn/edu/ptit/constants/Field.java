package vn.edu.ptit.constants;

public final class Field {

    // Metadata fields
    public static final String META_QUERY_ID = "meta.queryId";
    public static final String META_TRANSACTION_ID = "meta.transactionId";
    public static final String META_QUERY = "meta.query";
    public static final String META_UPDATE_TYPE = "meta.updateType";
    public static final String META_PREPARED_QUERY = "meta.preparedQuery";
    public static final String META_QUERY_STATE = "meta.queryState";
    public static final String META_TABLES = "meta.tables";
    public static final String META_ROUTINES = "meta.routines";
    public static final String META_URI = "meta.uri";
    public static final String META_PLAN = "meta.plan";
    public static final String META_PAYLOAD = "meta.payload";

    // Context field
    public static final String CTX_USER = "ctx.user";
    public static final String CTX_PRINCIPAL = "ctx.principal";
    public static final String CTX_GROUPS = "ctx.groups";
    public static final String CTX_TRACE_TOKEN = "ctx.traceToken";
    public static final String CTX_REMOTE_CLIENT_ADDRESS = "ctx.remoteClientAddress";
    public static final String CTX_USER_AGENT = "ctx.userAgent";
    public static final String CTX_CLIENT_INFO = "ctx.clientInfo";
    public static final String CTX_CLIENT_TAGS = "ctx.clientCapabilities";
    public static final String CTX_CLIENT_CAPABILITIES = "ctx.clientTags";
    public static final String CTX_SOURCE = "ctx.source";
    public static final String CTX_CATALOG = "ctx.catalog";
    public static final String CTX_SCHEMA = "ctx.schema";
    public static final String CTX_RESOURCE_GROUP_ID = "ctx.resourceGroupId";
    public static final String CTX_SESSION_PROPERTIES = "ctx.sessionProperties";
    public static final String CTX_RESOURCE_ESTIMATES = "ctx.resourceEstimates";
    public static final String CTX_SERVER_ADDRESS = "ctx.serverAddress";
    public static final String CTX_SERVER_VERSION = "ctx.serverVersion";
    public static final String CTX_ENVIRONMENT = "ctx.environment";
    public static final String CTX_QUERY_TYPE = "ctx.queryType";

    // Creation event
    public static final String CRT_CREATED_TIME = "crt.createdTime";

    // Completion event
    public static final String CPL_ERROR_CODE = "cpl.errorCode";
    public static final String CPL_FAILURE_TYPE = "cpl.failureType";
    public static final String CPL_FAILURE_MESSAGE = "cpl.failureMessage";
    public static final String CPL_FAILURE_TASK = "cpl.failureTask";
    public static final String CPL_FAILURE_HOST = "cpl.failureHost";
    public static final String CPL_FAILURES_JSON = "cpl.failuresJson";

    public static final String CPL_QUERY_WARNINGS = "cpl.queryWarnings";

    public static final String CPL_CREATE_TIME = "cpl.createTime";
    public static final String CPL_EXECUTION_START_TIME = "cpl.executionStartTime";
    public static final String CPL_END_TIME = "cpl.endTime";

    public static final String CPL_STATISTICS = "cpl.statistics";   // to-do
    public static final String CPL_IO_METADATA = "cpl.ioMetadata";   // to-do

    // Split event
    public static final String SPL_FAILURE_TYPE = "spl.failureType";
    public static final String SPL_FAILURE_MESSAGE = "spl.failureMessage";
    public static final String SPL_PAYLOAD = "spl.payload";
    public static final String SPL_QUERY_ID = "spl.queryId";
    public static final String SPL_STAGE_ID = "spl.stageId";
    public static final String SPL_TASK_ID = "spl.taskId";
    public static final String SPL_CATALOG_NAME = "spl.catalogName";
    public static final String SPL_CREATE_TIME = "spl.createTime";
    public static final String SPL_START_TIME = "spl.startTime";
    public static final String SPL_END_TIME = "spl.endTime";
    public static final String SPL_STATISTICS = "spl.statistics";   // to-do

}
