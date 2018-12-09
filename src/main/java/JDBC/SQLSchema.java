package JDBC;

/**
 * This class holds the SQL Schema information for consistent SQL queries
 */

public class SQLSchema {

    // Server
    public static String TABLE_SERVER = "server";
    public static String SERVER_COL_ID = "id";
    public static String SERVER_COL_NAME = "name";

    // Custom Command
    public static String TABLE_CUSTOMCOMMAND = "customcommand";
    public static String CC_COL_SERVERID = "serverid";
    public static String CC_COL_NAME = "name";
    public static String CC_COL_COMMAND = "command";

    // Disabled Command
    public static String TABLE_DISABLEDCOMMAND = "disabledcommand";
    public static String DC_COL_SERVERID = "serverid";
    public static String DC_COL_NAME = "name";

    // Post
    public static String TABLE_POST = "post";
    public static String POST_COL_SERVERID = "serverid";
    public static String POST_COL_GROUPID = "groupid";
    public static String POST_COL_NAME = "name";
    public static String POST_COL_GROUPDATE = "groupdate";
    public static String POST_COL_PLAYERS = "players";
    public static String POST_COL_SUBS = "subs";
    public static String POST_COL_TYPE = "sys_type";
    public static String POST_COL_MSG_ID = "msg_id";
    public static String POST_COL_OWNER_ID = "owner_id";
    public static String POST_COL_TYPE_CODE = "type_code";
    public static String POST_COL_ROLLCALL_COUNT = "rollcall_count";

    // AutoChannel
    public static String TABLE_AC = "autochannel";
    public static String AC_COL_SERVERID = "serverid";
    public static String AC_COL_CHANNELNAME = "channelname";
    public static String AC_COL_CHANNELID = "channelid";

    // AutoRole
    public static String TABLE_AR = "autorole";
    public static String AR_COL_SERVERID = "serverid";
    public static String AR_COL_ROLENAME = "rolename";
    public static String AR_COL_MESSAGEID = "messageid";
    public static String AR_COL_ROLEID = "roleid";

    // EventBoard
    public static String TABLE_EB = "eventboard";
    public static String EB_COL_SERVERID = "serverid";
    public static String EB_COL_CHANNELID = "channelid";
    public static String EB_COL_TYPE = "sys_type";
}
