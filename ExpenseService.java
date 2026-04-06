package services;

import dao.ExpenseDao;
import model.Expense;

import java.util.List;

public class ExpenseService {

    private final ExpenseDao expenseDao;

    public ExpenseService() {
        this.expenseDao = new ExpenseDao();
    }

    public void addExpense(String title, int amount, String category, String user) {
        Expense expense = buildExpense(title, amount, category, user);
        expenseDao.insertExpense(expense);
    }

    public List<Expense> getAllExpenses(String user) {
        validateUser(user);
        return expenseDao.getAllExpensesForUser(user.trim());
    }

    public List<Expense> getExpensesByCategory(String category, String user) {
        validateCategory(category);
        validateUser(user);
        return expenseDao.getExpensesByCategoryForUser(category, user.trim());
    }

    public int getTotalExpense(String user) {
        validateUser(user);
        return expenseDao.getTotalExpenseForUser(user.trim());
    }

    public boolean deleteExpense(Expense expense, String user) {
        if (expense == null) {
            throw new IllegalArgumentException("Select an expense to delete.");
        }
        validateUser(user);

        Expense scopedExpense = new Expense(
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate(),
                user.trim()
        );
        return expenseDao.deleteExpenseForUser(scopedExpense);
    }

    public boolean deleteExpense(String title, String user) {
        validateTitle(title);
        validateUser(user);
        return expenseDao.deleteExpense(title, user.trim());
    }

    private Expense buildExpense(String title, int amount, String category, String user) {
        validateTitle(title);
        validateCategory(category);
        validateUser(user);
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative.");
        }

        return new Expense(
                title.trim(),
                amount,
                category.trim(),
                java.time.LocalDate.now().toString(),
                user.trim()
        );
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required.");
        }
    }

    private void validateUser(String user) {
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("User session is invalid.");
        }
    }
}
