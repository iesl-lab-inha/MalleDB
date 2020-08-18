package interfaces;

import util.Options;
import util.Status;

import java.util.List;

public interface MalleDB {
    Status init();
    Status init(Options options);
    Status create();
    Status close();

    Status insert(String key, String value);
    //Status insert(List<String> keys, List<String> values);
    Status read(String key);
    Status update(String key, String value);
    Status delete(String key);
}
