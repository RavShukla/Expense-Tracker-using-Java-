package model;

public class Expense {

    private final String title;
    private final double amount;
    private final String category;
    private final String date;
    private final String user;

    public Expense(String title, double amount, String category, String date, String user) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getUser() {
        return user;
    }
}
