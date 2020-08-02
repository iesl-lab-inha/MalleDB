package interfaces;

import util.Item;
import util.Status;

import java.util.List;

public abstract class SubDB {
    public Status init(){return null;};
    public Status create(){return null;};
    public Status close(){return null;};

    public Status insert(Item item){return null;};
    public Item readMeta(Item item){return null;};
    public List<Item> readAll(String table, Item item){return null;};
    public Status update(String table, Item item){return null;};
    public Status delete(String table, Item item){return null;};

    public List<Item> sortByOrder(List<Item> items){
        return items;
    }
}
