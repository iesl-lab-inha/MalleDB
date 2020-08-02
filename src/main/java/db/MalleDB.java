package db;

import connectors.Cassandra;
import connectors.LevelDB;
import connectors.MySQL;
import interfaces.SubDB;
import util.Item;
import util.Options;
import util.Status;
import util.Var;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public Status init() {
        return init(new Options(true));
    }

    @Override
    public Status init(Options options) {
        metadb = new MySQL();
        metadb.init();

        if(options.isUsingDefault()) {
            usingOneSubDB = true;
            if (Var.SUB_DB == Var.DB_LIST.MYSQL) {
                blockdb = new MySQL();
            } else if (Var.SUB_DB == Var.DB_LIST.LEVELDB) {
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

    @Override
    public Status insert(String key, String value) {
        //System.out.println("Value: " + value);
        String v = value;
        String chunk;
        int order;
        Item meta;
        ArrayList<Item>[] blocks = new ArrayList[Var.bCOUNTER];
        for(int i = 0; i < Var.bCOUNTER; i++){
            blocks[i] =  new ArrayList<>();
        }
       // System.out.println("Check 0");

        int[] counters = new int[Var.bCOUNTER];
       // System.out.println("0");
        for (int i = 0; i < Var.bCOUNTER; i++){
            //System.out.println("1");

            if(v.length() < Var.BLOCKS[i] && i != Var.bCOUNTER - 1) {
                //System.out.println("2");
                continue;
            }
            order = 0;
            while (!v.equals("")) {
                //System.out.println("3");
                order++;
                if (i == Var.bCOUNTER - 1 && v.length() <= Var.BLOCKS[i]) {
                    //System.out.println("4");
                    //System.out.println("if");
                    chunk = v;
                    v = "";
                } else {
                    //System.out.println("5");
                    //System.out.println("else");
                    if (v.length() < Var.BLOCKS[i]){
                        order--;
                        break;
                    }
                    //System.out.println("6");
                    chunk = v.substring(0, Var.BLOCKS[i]);
                    v = v.substring(Var.BLOCKS[i]);
                    //System.out.println("7");
                }
                //System.out.println("8");
                blocks[i].add(new Item(order, i + 1, key, chunk));
            }

            counters[i] = order;
        }
        meta = new Item(key, counters);
        //System.out.println("Check 1");
        metadb.insert(meta);

        if(usingOneSubDB) {
            //System.out.println("Check 2");
            for (int i = 0; i < Var.bCOUNTER; i++) {
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
        //System.out.println("Check 3");

        return Status.OK;
    }

    @Override
    public Status insert(List<String> keys, List<String> values) {

        String chunk;
        int order;
        List<Item> metaList = new ArrayList<>();
        ArrayList<Item>[] blocks = new ArrayList[Var.bCOUNTER];
        int[] counters = new int[Var.bCOUNTER];

        for(int n = 0; n < values.size(); n++) {
            //TODO: implement
        }

        for(int i = 0; i < metaList.size(); i++){
            metadb.insert(metaList.get(i));
        }
        for(int i = 0; i < Var.bCOUNTER; i++){
            for (int j = 0; j < blocks[i].size(); j++)
                blockdb.insert(blocks[i].get(j));
        }
        return Status.OK;
    }

    @Override
    public Status read(String key) {

        Item item = new Item();
        item.setKey(key);
        item = metadb.readMeta(item);
        StringBuilder sb = new StringBuilder();

        if(usingOneSubDB) {
            for (int i = 0; i < Var.bCOUNTER; i++) {
                List<Item> blocks = blockdb.readAll(Var.TABLES_MYSQL[i], item);
                for (Item block : blocks) {
                    sb.append(block.getValue());
                }
            }
        }else{
            List<Item> mblocks = mdb.readAll(Var.TABLES_MYSQL[0], item);
            for (Item block : mblocks) {
                sb.append(block.getValue());
            }

            List<Item> bblocks = bdb.readAll(Var.TABLES_MYSQL[1], item);
            for (Item block : bblocks) {
                sb.append(block.getValue());
            }

            List<Item> tblocks = tdb.readAll(Var.TABLES_MYSQL[2], item);
            for (Item block : tblocks) {
                sb.append(block.getValue());
            }
        }
        System.out.println("Read Value: " + sb.toString());
        return Status.OK;
    }

    @Override
    public Status update(String key, String value) {
        //Item item = new Item();
        //item.setKey(key);
        //item.setValue(value);

        delete(key);
        return insert(key, value);
    }

    @Override
    public Status delete(String key) {
        Item item = new Item();
        item.setKey(key);
        item = metadb.readMeta(item);

        if(usingOneSubDB) {
            for (int i = 0; i < Var.bCOUNTER; i++) {
                if (item.getCounters()[i] > 0) {
                    //System.out.println("i = " + i + " Block: " + item.getCounters()[i]);
                    blockdb.delete(Var.TABLES_MYSQL[i], item);
                    System.out.println("Block deleted...");
                }
            }
        }else {
            if(item.getCounters()[0] > 0){
                mdb.delete(Var.TABLES_MYSQL[0], item);
            }
            if(item.getCounters()[1] > 0){
                bdb.delete(Var.TABLES_MYSQL[1], item);
            }
            if(item.getCounters()[2] > 0){
                tdb.delete(Var.TABLES_MYSQL[2], item);
            }
        }
        metadb.delete(Var.TABLE_META_MYSQL, item);
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
            // 0 to AlphaNumericString variable length
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
