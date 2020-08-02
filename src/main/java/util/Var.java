package util;

public class Var {
    public static final int[] BLOCKS = {4194304, 65535, 255};
    public static final int bCOUNTER = 3;

    public enum DB_LIST{MYSQL, LEVELDB, CASSANDRA};
    public static final DB_LIST SUB_DB = DB_LIST.CASSANDRA;

    public static final String DB_URL = "";

    public static final String DELIM = ":";

    //MYSQL Configurations starts
    public static final String SERVER_MYSQL = "jdbc:mysql://localhost/";
    public static final String USER_MYSQL = "iesl";
    public static final String PASSW_MYSQL = "12345678";
    public static final String DB_MYSQL = "malledb";
    public static final String TABLE_META_MYSQL = "metatable";
    public static final String TABLE_MDATA_MYSQL = "mdatatable";
    public static final String TABLE_BDATA_MYSQL = "bdatatable";
    public static final String TABLE_TDATA_MYSQL = "tdatatable";

    public static final String[] TABLES_MYSQL = {"mdatatable", "bdatatable", "tdatatable"};

    //MYSQL Configurations ends

    //LevelDB Configurations starts
    public static final String DB_LEVELDB = "leveldb";
    //LevelDB Configurations starts



    //Cassandra Configurations starts
    public static final String IP_CASS = "127.0.0.1";
    public static final int PORT_CASS = 0;
    public static final String REP_STRATEGY_CASS = "SimpleStrategy";
    public static final int REP_FACTOR_CASS = 3;
    public static final String KEYSPACE_CASS = "malledb";
    public static final String[] TABLES_CASS = {"mdatatable", "bdatatable", "tdatatable"};
    //Cassandra Configurations ends



}
