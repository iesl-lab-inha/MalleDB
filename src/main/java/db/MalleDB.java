package db;

import connectors.Cassandra;
import connectors.LevelDB;
import connectors.MySQL;
import interfaces.SubDB;
import util.Item;
import util.Options;
import util.Status;

import java.util.ArrayList;
import java.util.List;

public class MalleDB implements interfaces.MalleDB {

    private SubDB metadb;
    private SubDB blockdb;
    private SubDB mdb;
    private SubDB bdb;
    private SubDB tdb;
    private boolean usingOneSubDB = false;

    public static void main(String[] args) {

       // String key = generateRandomString(20);
       // System.out.println("Before: " + key);

        //byte[] arr = key.getBytes();
        //String key1 = new String(arr);

        //System.out.println("After: " + key1);


        MalleDB malleDB = new MalleDB();
        Options options = new Options(Options.DB_TYPE.MYSQL, Options.DB_TYPE.CASSANDRA, Options.DB_TYPE.LEVELDB);
        malleDB.init(options);
        malleDB.create();

        //4294304

        String key = generateRandomString(20);
        String value = generateRandomString(500);

        System.out.println("Key: " + key);
        System.out.println("Value: " + value);

        malleDB.insert(key, value);


        malleDB.read(key);

       // String key = "E1O1IByghZN0ryG7raPf";
        malleDB.delete(key);


        malleDB.close();

    }

    //Initialize with default configuration
    @Override
    public Status init() {
        return init(new Options(true));
    }

    //Initialize with custom configuration
    @Override
    public Status init(Options options) {
        metadb = new MySQL();
        metadb.init();

        if(options.isUsingDefault()) {
            usingOneSubDB = true;
            if (Options.SUB_DB == Options.DB_TYPE.MYSQL) {
                blockdb = new MySQL();
            } else if (Options.SUB_DB == Options.DB_TYPE.LEVELDB) {
                blockdb = new LevelDB();
            } else {
                blockdb = new Cassandra();
            }
            blockdb.init();

            mdb = null;
            bdb = null;
            tdb = null;

        }else {
            if(options.getDbMedium() == Options.DB_TYPE.MYSQL){
                mdb = new MySQL();
            }else if(options.getDbMedium() == Options.DB_TYPE.LEVELDB){
                mdb = new LevelDB();
            }else{
                mdb = new Cassandra();
            }

            if(options.getDbBlob() == Options.DB_TYPE.MYSQL){
                bdb = new MySQL();
            }else if(options.getDbBlob() == Options.DB_TYPE.LEVELDB){
                bdb = new LevelDB();
            }else{
                bdb = new Cassandra();
            }

            if(options.getDbTiny() == Options.DB_TYPE.MYSQL){
                tdb = new MySQL();
            }else if(options.getDbTiny() == Options.DB_TYPE.LEVELDB){
                tdb = new LevelDB();
            }else{
                tdb = new Cassandra();
            }

            mdb.init();
            bdb.init();
            tdb.init();
            blockdb = null;
        }
        return Status.OK;
    }

    //Create databses and tables
    @Override
    public Status create() {
        metadb.create();
        if(usingOneSubDB) {
            blockdb.create();
        }else{
            mdb.create();
            bdb.create();
            tdb.create();
        }
        return Status.OK;
    }

    //Close the connection
    @Override
    public Status close() {
        metadb.close();
        if(usingOneSubDB){
            blockdb.close();
        }else{
            mdb.close();
            bdb.close();
            tdb.close();
        }
        return null;
    }

    //Insert data
    @Override
    public Status insert(String key, String value) {

        //Initialize the variables starts
        String chunk;
        int order;
        Item meta;
        ArrayList<Item>[] blocks = new ArrayList[Options.bCOUNTER];
        for(int i = 0; i < Options.bCOUNTER; i++){
            blocks[i] =  new ArrayList<>();
        }
        int[] counters = new int[Options.bCOUNTER];
        //Initialize the variables ends

        //Memtable managements part starts
        for (int i = 0; i < Options.bCOUNTER; i++){
            //check if the value size smaller than the block but the block is not tiny
            if(value.length() < Options.BLOCKS[i] && i != Options.bCOUNTER - 1) {
                continue;
            }
            //Initialize the order to 0
            order = 0;
            //Repeat while the value size is not 0
            while (!value.equals("")) {
                order++;
                //Check if the block is tiny and value size is smaller
                if (i == Options.bCOUNTER - 1 && value.length() <= Options.BLOCKS[i]) {
                    chunk = value;
                    value = "";
                } else { //Otherwise
                    //Break the while if the value size is smaller
                    if (value.length() < Options.BLOCKS[i]){
                        order--;
                        break;
                    }
                    //Initialize the chunk as the block size
                    chunk = value.substring(0, Options.BLOCKS[i]);
                    //Update the value
                    value = value.substring(Options.BLOCKS[i]);
                }
                //Add new block to Memtable
                blocks[i].add(new Item(order, i + 1, key, chunk));
            }
            //Update metadata counter
            counters[i] = order;
        }
        //Memtable managements part ends

        //Database part starts
        //Initialize and insert metadata
        meta = new Item(key, counters);
        metadb.insert(meta);

        //Insert to databases
        if(usingOneSubDB) {
            for (int i = 0; i < Options.bCOUNTER; i++) {
                for (int j = 0; j < blocks[i].size(); j++)
                    blockdb.insert(blocks[i].get(j));
            }
        }else{
            for (int i = 0; i < blocks[0].size(); i++) {
                mdb.insert(blocks[0].get(i));
            }
            for (int i = 0; i < blocks[1].size(); i++) {
                bdb.insert(blocks[1].get(i));
            }
            for (int i = 0; i < blocks[2].size(); i++) {
                tdb.insert(blocks[2].get(i));
            }
        }
        //Database part ends

        return Status.OK;
    }

    @Override
    public Status insert(List<String> keys, List<String> values) {

        String chunk;
        int order;
        List<Item> metaList = new ArrayList<>();
        ArrayList<Item>[] blocks = new ArrayList[Options.bCOUNTER];
        int[] counters = new int[Options.bCOUNTER];

        for(int n = 0; n < values.size(); n++) {
            //TODO: implement
        }

        for(int i = 0; i < metaList.size(); i++){
            metadb.insert(metaList.get(i));
        }
        for(int i = 0; i < Options.bCOUNTER; i++){
            for (int j = 0; j < blocks[i].size(); j++)
                blockdb.insert(blocks[i].get(j));
        }
        return Status.OK;
    }

    @Override
    public Status read(String key) {
        //Initialize the Item
        Item item = new Item();
        item.setKey(key);
        item = metadb.readMeta(item);
        StringBuilder sb = new StringBuilder();

        //Read each block from the sub database
        if(usingOneSubDB) {
            for (int i = 0; i < Options.bCOUNTER; i++) {
                List<Item> blocks = blockdb.readAll(Options.TABLES_MYSQL[i], item);
                for (Item block : blocks) {
                    //Append with the value
                    sb.append(block.getValue());
                }
            }
        }else{
            List<Item> mblocks = mdb.readAll(Options.TABLES_MYSQL[0], item);
            for (Item block : mblocks) {
                sb.append(block.getValue());
            }

            List<Item> bblocks = bdb.readAll(Options.TABLES_MYSQL[1], item);
            for (Item block : bblocks) {
                sb.append(block.getValue());
            }

            List<Item> tblocks = tdb.readAll(Options.TABLES_MYSQL[2], item);
            for (Item block : tblocks) {
                sb.append(block.getValue());
            }
        }
        System.out.println("Read Value: " + sb.toString());
        return Status.OK;
    }

    @Override
    public Status update(String key, String value) {
        delete(key);
        return insert(key, value);
    }

    @Override
    public Status delete(String key) {
        Item item = new Item();
        item.setKey(key);
        item = metadb.readMeta(item);

        if(usingOneSubDB) {
            for (int i = 0; i < Options.bCOUNTER; i++) {
                if (item.getCounters()[i] > 0) {
                    blockdb.delete(Options.TABLES_MYSQL[i], item);
                    System.out.println("Block deleted...");
                }
            }
        }else {
            if(item.getCounters()[0] > 0){
                mdb.delete(Options.TABLES_MYSQL[0], item);
            }
            if(item.getCounters()[1] > 0){
                bdb.delete(Options.TABLES_MYSQL[1], item);
            }
            if(item.getCounters()[2] > 0){
                tdb.delete(Options.TABLES_MYSQL[2], item);
            }
        }
        metadb.delete(Options.TABLE_META_MYSQL, item);
        System.out.println("Meta deleted...");
        return Status.OK;
    }

    static String generateRandomString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString Optionsiable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
