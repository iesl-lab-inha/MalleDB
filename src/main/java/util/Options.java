package util;

public class Options {

    public static final int[] BLOCKS = {4194304, 65535, 255};
    public static final int bCOUNTER = 3;

    public static final String DELIM = ":";

    //MYSQL Configurations starts
    public static String SERVER_MYSQL = "jdbc:mysql://localhost/";
    public static String USER_MYSQL = "iesl";
    public static String PASSW_MYSQL = "12345678";
    public static final String DB_MYSQL = "malledb";
    public static final String TABLE_META_MYSQL = "metatable";
    public static final String TABLE_MDATA_MYSQL = "mdatatable";
    public static final String TABLE_BDATA_MYSQL = "bdatatable";
    public static final String TABLE_TDATA_MYSQL = "tdatatable";

    public static final String[] TABLES_MYSQL = {"mdatatable", "bdatatable", "tdatatable"};

    //MYSQL Configurations ends

    //LevelDB Configurations starts
    public static String DB_LEVELDB = "leveldb";
    //LevelDB Configurations starts



    //Cassandra Configurations starts
    public static String IP_CASS = "127.0.0.1";
    public static int PORT_CASS = 0;
    public static String REP_STRATEGY_CASS = "SimpleStrategy";
    public static int REP_FACTOR_CASS = 3;
    public static final String KEYSPACE_CASS = "malledb";
    public static final String[] TABLES_CASS = {"mdatatable", "bdatatable", "tdatatable"};
    //Cassandra Configurations ends

    private boolean usingDefault;

    public enum DB_TYPE{MYSQL, LEVELDB, CASSANDRA};
    public static  DB_TYPE SUB_DB = DB_TYPE.MYSQL;
    private DB_TYPE dbMedium;
    private DB_TYPE dbBlob;
    private DB_TYPE dbTiny;

    public Options(DB_TYPE dbMedium, DB_TYPE dbBlob, DB_TYPE dbTiny) {
        this.usingDefault = false;
        this.dbMedium = dbMedium;
        this.dbBlob = dbBlob;
        this.dbTiny = dbTiny;
    }

    public Options(DB_TYPE subDB) {
        this.usingDefault = true;
        SUB_DB = subDB;
        this.dbMedium = null;
        this.dbBlob = null;
        this.dbTiny = null;
    }

    public Options() {
        this.usingDefault = true;
    }

    public void setMySQLConf(String server, String user, String password){
        SERVER_MYSQL = server;
        USER_MYSQL = user;
        PASSW_MYSQL = password;
    }

    public void setLevelDBConf(String db){
        DB_LEVELDB = db;
    }

    public void setCassandraDBConf(String server, int port, String repStrategy, int repFactor){
        IP_CASS = server;
        PORT_CASS = port;
        REP_STRATEGY_CASS = repStrategy;
        REP_FACTOR_CASS = repFactor;
    }

    public boolean isUsingDefault() {
        return usingDefault;
    }

    public DB_TYPE getDbMedium() {
        return dbMedium;
    }

    public DB_TYPE getDbBlob() {
        return dbBlob;
    }

    public DB_TYPE getDbTiny() {
        return dbTiny;
    }
}
