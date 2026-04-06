package db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public final class DBConnection {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "expense_db";
    private static final MongoClient CLIENT = MongoClients.create(CONNECTION_STRING);
    private static final MongoDatabase DATABASE = CLIENT.getDatabase(DATABASE_NAME);

    private DBConnection() {
    }

    public static MongoDatabase getDatabase() {
        return DATABASE;
    }
}
