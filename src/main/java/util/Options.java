package util;

public class Options {
    private boolean usingDefault;

    public enum DB_TYPE{MYSQL, LEVELDB, CASSANDRA};
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
