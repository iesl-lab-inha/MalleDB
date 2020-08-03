package connectors;

import interfaces.SubDB;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import static org.fusesource.leveldbjni.JniDBFactory.*;
import util.Item;
import util.Status;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelDB extends SubDB {

    private static Options options = null;
    private static DB db = null;

    @Override
    public Status init() {
        try {
            options = new Options();
            options.createIfMissing(true);
            db = factory.open(new File(util.Options.DB_LEVELDB), options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Status.OK;
    }

    @Override
    public Status close() {
        try {
            if(db != null)
                db.close();
            options = null;
            db = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Status.OK;
    }

    @Override
    public Status insert(Item item) {
        String key = item.getType() + util.Options.DELIM + item.getOrder() + util.Options.DELIM + item.getKey();
        String value = item.getValue();
        System.out.println("Inserting: Key: " + key + " Value: " + value);
        db.put(key.getBytes(), value.getBytes());
        return Status.OK;
    }


    @Override
    public List<Item> readAll(String table, Item item) {
        List<Item> items = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < util.Options.bCOUNTER; i++){
            if(table.equals(util.Options.TABLES_MYSQL[i])){
                index = i;
            }
        }

        for(int i = 1; i <= item.getCounters()[index]; i++) {
            String key = (index + 1) + util.Options.DELIM + i + util.Options.DELIM + item.getKey();
            System.out.println("Reading: Key: " + key);
            byte[] value = db.get(key.getBytes());


            items.add(new Item(i, item.getType(), item.getKey(), new String(value)));
            //item.setValue(Arrays.toString(value));
        }
        return items;
    }

    @Override
    public Status update(String table, Item item) {
        Status status;
        status = delete(table, item);
        status = insert(item);
        return status;
    }

    @Override
    public Status delete(String table, Item item) {
        String key = item.getKey();
        db.delete(bytes(key));
        return Status.OK;

    }
}
