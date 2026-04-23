package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import db.DBConnection;
import model.User;
import org.bson.Document;

public class UserDao {

    private final MongoCollection<Document> collection;

    public UserDao() {
        MongoDatabase db = DBConnection.getDatabase();
        collection = db.getCollection("users");
        collection.createIndex(Indexes.ascending("usernameKey"), new IndexOptions().unique(true));
    }

    public boolean registerUser(User user) {
        if (userExists(user.getUsername())) {
            return false;
        }

        collection.insertOne(toDocument(user));
        return true;
    }

    public boolean registerUser(String username, String passwordHash) {
        User user = new User(username, passwordHash, java.time.LocalDateTime.now().toString());
        return registerUser(user);
    }

    public boolean authenticateUser(String username, String passwordHash) {
        Document query = new Document("usernameKey", normalizeUsername(username))
                .append("passwordHash", passwordHash);
        return collection.find(query).first() != null;
    }

    public boolean loginUser(String username, String passwordHash) {
        return authenticateUser(username, passwordHash);
    }

    public boolean userExists(String username) {
        return findByUsername(normalizeUsername(username)) != null;
    }

    private Document findByUsername(String usernameKey) {
        return collection.find(new Document("usernameKey", usernameKey)).first();
    }

    private Document toDocument(User user) {
        return new Document("username", user.getUsername())
                .append("usernameKey", normalizeUsername(user.getUsername()))
                .append("passwordHash", user.getPasswordHash())
                .append("createdAt", user.getCreatedAt());
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }
}
