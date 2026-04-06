package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import db.DBConnection;
import model.Expense;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDao {

    private final MongoCollection<Document> collection;

    public ExpenseDao() {
        MongoDatabase db = DBConnection.getDatabase();
        collection = db.getCollection("expenses");
    }

    public void insertExpense(Expense expense) {
        collection.insertOne(toDocument(expense));
    }

    public void addExpense(String title, int amount, String category) {
        insertExpense(new Expense(title, amount, category, java.time.LocalDate.now().toString(), ""));
    }

    public void addExpense(String title, int amount, String category, String user) {
        insertExpense(new Expense(title, amount, category, java.time.LocalDate.now().toString(), user));
    }

    public List<Expense> getAllExpensesForUser(String user) {
        Document query = new Document("user", user.trim());
        return toExpenses(collection.find(query).sort(Sorts.descending("date")));
    }

    public List<Expense> getAllExpenses() {
        return toExpenses(collection.find().sort(Sorts.descending("date")));
    }

    public List<Expense> getExpensesByCategoryForUser(String category, String user) {
        Document query = new Document("categoryKey", normalizeValue(category))
                .append("user", user.trim());
        return toExpenses(collection.find(query).sort(Sorts.descending("date")));
    }

    public List<Expense> getExpensesByCategory(String category) {
        Document query = new Document("categoryKey", normalizeValue(category));
        return toExpenses(collection.find(query).sort(Sorts.descending("date")));
    }

    public int getTotalExpenseForUser(String user) {
        int total = 0;
        for (Expense expense : getAllExpensesForUser(user)) {
            total += (int) expense.getAmount();
        }
        return total;
    }

    public int getTotalExpense() {
        int total = 0;
        for (Expense expense : getAllExpenses()) {
            total += (int) expense.getAmount();
        }
        return total;
    }

    public boolean deleteExpenseForUser(Expense expense) {
        Document query = new Document("title", expense.getTitle())
                .append("amount", (int) expense.getAmount())
                .append("category", expense.getCategory())
                .append("date", expense.getDate())
                .append("user", expense.getUser());
        return collection.deleteOne(query).getDeletedCount() > 0;
    }

    public boolean deleteExpense(String title) {
        Document query = new Document("titleKey", normalizeValue(title));
        return collection.deleteOne(query).getDeletedCount() > 0;
    }

    public boolean deleteExpense(String title, String user) {
        Document query = new Document("titleKey", normalizeValue(title))
                .append("user", user.trim());
        return collection.deleteOne(query).getDeletedCount() > 0;
    }

    private List<Expense> toExpenses(Iterable<Document> documents) {
        List<Expense> expenses = new ArrayList<>();
        for (Document doc : documents) {
            expenses.add(fromDocument(doc));
        }
        return expenses;
    }

    private Expense fromDocument(Document doc) {
        return new Expense(
                doc.getString("title"),
                doc.getInteger("amount", 0),
                doc.getString("category"),
                doc.getString("date"),
                doc.getString("user")
        );
    }

    private Document toDocument(Expense expense) {
        return new Document("title", expense.getTitle())
                .append("titleKey", normalizeValue(expense.getTitle()))
                .append("amount", (int) expense.getAmount())
                .append("category", expense.getCategory())
                .append("categoryKey", normalizeValue(expense.getCategory()))
                .append("date", expense.getDate())
                .append("user", expense.getUser());
    }

    private String normalizeValue(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
