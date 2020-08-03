package connectors;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import interfaces.SubDB;
import util.Item;
import util.Status;
import util.Options;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Cassandra extends SubDB {

    private static Cluster cluster;

    private static Session session;

    @Override
    public Status init() {
        Cluster.Builder b = Cluster.builder().addContactPoint(Options.IP_CASS);
        if (Options.PORT_CASS != 0) {
            b.withPort(Options.PORT_CASS);
        }
        cluster = b.build();
        session = cluster.connect();
        System.out.println("Connected to Cassandra...");
        return Status.OK;
    }

    @Override
    public Status create() {
        StringBuilder sb =
                new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ")
                        .append(Options.KEYSPACE_CASS).append(" WITH replication = {")
                        .append("'class':'").append(Options.REP_STRATEGY_CASS)
                        .append("','replication_factor':").append(Options.REP_FACTOR_CASS)
                        .append("};");
        String query = sb.toString();
        session.execute(query);
        System.out.println("Keyspace Created: " + Options.KEYSPACE_CASS);

        for(int i = 0; i < Options.bCOUNTER; i++){
            query = "CREATE TABLE IF NOT EXISTS " + Options.KEYSPACE_CASS + "." + Options.TABLES_CASS[i] + "(" +
                    "        d_order int PRIMARY KEY,\n" +
                    "        d_key text,\n" +
                    "        d_value blob,\n" +
                    "        );";

            session.execute(query);
            System.out.println("Table Created: " + Options.KEYSPACE_CASS + "." + Options.TABLES_CASS[i]);
        }

        return Status.OK;
    }

    @Override
    public Status close() {
        session.close();
        cluster.close();
        return Status.OK;
    }

    @Override
    public Status insert(Item item) {

        String table = Options.TABLES_CASS[item.getType() - 1];

        StringBuilder sb =
                new StringBuilder("INSERT INTO ")
                        .append(Options.KEYSPACE_CASS).append(".").append(table)
                        .append("(d_order, d_key, d_value) VALUES (")
                        .append(item.getOrder()).append(", '")
                        .append(item.getKey()).append("', ")
                        .append("textAsBlob('").append(item.getValue()).append("'));");
        String query = sb.toString();
        //System.out.println("Query: " + query);
        session.execute(query);
        System.out.println("Item \"" + item.getKey() + "\" inserted to \"" + table + "\"...");

        return Status.OK;
    }

    @Override
    public List<Item> readAll(String table, Item item) {
        List<Item> items = new ArrayList<>();

        StringBuilder sb = new StringBuilder("SELECT * FROM ")
                .append(Options.KEYSPACE_CASS).append(".").append(table).append(" WHERE ")
                .append("d_key = '").append(item.getKey()).append("' ALLOW FILTERING;");

        String query = sb.toString();
        System.out.println("Query: " + query);
        ResultSet rs = session.execute(query);
        List<Row> rows = rs.all();
        for (Row r: rows) {
            items.add(new Item(r.getInt("d_order"), 0, item.getKey(), StandardCharsets.UTF_8.decode(r.getBytes("d_value")).toString()));
        }

        return items;
    }


    @Override
    public Status update(String table, Item item) {
        return null;
    }

    private int countByKey(String table, Item item) {

        StringBuilder sb = new StringBuilder("SELECT * FROM ")
                .append(Options.KEYSPACE_CASS).append(".").append(table).append(" WHERE ")
                .append("d_key = '").append(item.getKey()).append("' ALLOW FILTERING;");

        String query = sb.toString();
        //System.out.println("Query: " + query);
        ResultSet rs = session.execute(query);
        List<Row> rows = rs.all();

        return rows.size();
    }

    @Override
    public Status delete(String table, Item item) {

        int counter = countByKey(table, item);

        for(int i = 1; i <= counter; i++) {
            StringBuilder sb = new StringBuilder("DELETE FROM ")
                    .append(Options.KEYSPACE_CASS).append(".").append(table).append(" WHERE ")
                    .append("d_order = ").append(i).append(";");

            String query = sb.toString();
            //System.out.println("Query: " + query);
            session.execute(query);
        }
        return Status.OK;
    }

}
