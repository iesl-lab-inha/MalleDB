package util;

public class Options {

    public static final int[] BLOCKS = {4194304, 65535, 255};
    public static final int bCOUNTER = 3;

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

    public Options(boolean usingDefault) {
        this.usingDefault = usingDefault;
    }

    public Options() {
        this.usingDefault = false;
    }

    public void setUsingDefault(boolean usingDefault) {
        this.usingDefault = usingDefault;
    }

    public void setDbMedium(DB_TYPE dbMedium) {
        this.dbMedium = dbMedium;
    }

    public void setDbBlob(DB_TYPE dbBlob) {
        this.dbBlob = dbBlob;
    }

    public void setDbTiny(DB_TYPE dbTiny) {
        this.dbTiny = dbTiny;
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
