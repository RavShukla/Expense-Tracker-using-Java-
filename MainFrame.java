package ui;

import model.Expense;
import services.ExpenseService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private static final Color BACKGROUND = new Color(244, 239, 232);
    private static final Color PANEL = new Color(255, 251, 245);
    private static final Color PRIMARY = new Color(35, 74, 94);
    private static final Color SECONDARY = new Color(188, 127, 63);

    private final ExpenseService expenseService;
    private final String currentUser;

    private JTextField titleField;
    private JTextField amountField;
    private JTextField categoryField;
    private JTextField searchField;
    private JLabel totalValueLabel;
    private JLabel countValueLabel;
    private JLabel statusValueLabel;
    private DefaultTableModel tableModel;
    private JTable expenseTable;
    private List<Expense> currentExpenses;

    public MainFrame(String user) {
        this.currentUser = user.trim();
        this.expenseService = new ExpenseService();
        this.currentExpenses = new ArrayList<>();

        setTitle("Expense Tracker - " + currentUser);
        setSize(1020, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(buildContent());

        refreshAllExpenses();
        setVisible(true);
    }

    private JComponent buildContent() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildCenterPanel(), BorderLayout.CENTER);

        return root;
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);

        JPanel textBlock = new JPanel();
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setBackground(BACKGROUND);

        JLabel title = new JLabel("Your Expense Desk");
        title.setFont(new Font("Georgia", Font.BOLD, 28));
        title.setForeground(PRIMARY);

        JLabel subtitle = new JLabel("Signed in as " + currentUser + " | all expense data is user-specific");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(92, 92, 92));

        textBlock.add(title);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(subtitle);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        logoutButton.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        header.add(textBlock, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);
        return header;
    }

    private JComponent buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBackground(PANEL);
        sidebar.setBorder(new EmptyBorder(18, 18, 18, 18));

        titleField = new JTextField();
        amountField = new JTextField();
        categoryField = new JTextField();
        searchField = new JTextField();

        sidebar.add(sectionTitle("Add Expense"));
        sidebar.add(labeledField("Title", titleField));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(labeledField("Amount", amountField));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(labeledField("Category", categoryField));
        sidebar.add(Box.createVerticalStrut(12));

        JButton addButton = createButton("Add", PRIMARY, Color.WHITE);
        addButton.addActionListener(e -> addExpense());
        sidebar.add(addButton);

        sidebar.add(Box.createVerticalStrut(22));
        sidebar.add(sectionTitle("Browse Expenses"));
        sidebar.add(labeledField("Search by category", searchField));
        sidebar.add(Box.createVerticalStrut(10));

        JButton searchButton = createButton("Search", SECONDARY, new Color(54, 30, 6));
        searchButton.addActionListener(e -> searchExpenses());
        sidebar.add(searchButton);
        sidebar.add(Box.createVerticalStrut(10));

        JButton viewButton = createButton("View", PRIMARY, Color.WHITE);
        viewButton.addActionListener(e -> refreshAllExpenses());
        sidebar.add(viewButton);
        sidebar.add(Box.createVerticalStrut(10));

        JButton deleteButton = createButton("Delete", SECONDARY, new Color(54, 30, 6));
        deleteButton.addActionListener(e -> deleteSelectedExpense());
        sidebar.add(deleteButton);
        sidebar.add(Box.createVerticalStrut(10));

        JButton totalButton = createButton("Total", PRIMARY, Color.WHITE);
        totalButton.addActionListener(e -> showTotalDialog());
        sidebar.add(totalButton);

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JComponent buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(14, 14));
        center.setOpaque(false);
        center.add(buildStatsPanel(), BorderLayout.NORTH);
        center.add(buildTablePanel(), BorderLayout.CENTER);
        return center;
    }

    private JComponent buildStatsPanel() {
        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 12));
        stats.setOpaque(false);

        totalValueLabel = createStatCard(stats, "Total Expense", "Rs 0");
        countValueLabel = createStatCard(stats, "Entries", "0");
        statusValueLabel = createStatCard(stats, "Status", "Ready");

        return stats;
    }

    private JLabel createStatCard(JPanel parent, String label, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL);
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel cardLabel = new JLabel(label);
        cardLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        cardLabel.setForeground(new Color(102, 99, 93));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        valueLabel.setForeground(PRIMARY);

        card.add(cardLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        parent.add(card);
        return valueLabel;
    }

    private JComponent buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        tableModel = new DefaultTableModel(new Object[]{"Title", "Amount", "Category", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setRowHeight(26);
        expenseTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        expenseTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panel.add(new JScrollPane(expenseTable), BorderLayout.CENTER);
        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Georgia", Font.BOLD, 18));
        label.setForeground(PRIMARY);
        return label;
    }

    private JComponent labeledField(String label, JTextField field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setOpaque(false);

        JLabel title = new JLabel(label);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        title.setForeground(new Color(79, 79, 79));

        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(220, 34));

        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton createButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    private void addExpense() {
        try {
            int amount = Integer.parseInt(amountField.getText().trim());
            expenseService.addExpense(titleField.getText(), amount, categoryField.getText(), currentUser);

            titleField.setText("");
            amountField.setText("");
            categoryField.setText("");
            refreshAllExpenses();
            setStatus("Expense added successfully.");
        } catch (NumberFormatException ex) {
            showWarning("Amount must be numeric.");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage());
        } catch (Exception ex) {
            showWarning("Unable to add the expense right now.");
        }
    }

    private void searchExpenses() {
        try {
            List<Expense> expenses = expenseService.getExpensesByCategory(searchField.getText(), currentUser);
            loadExpenses(expenses);
            if (expenses.isEmpty()) {
                setStatus("No expenses found for that category.");
                JOptionPane.showMessageDialog(this, "No expenses found for that category.", "Search Result",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            setStatus("Showing category results.");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage());
        } catch (Exception ex) {
            showWarning("Unable to search expenses right now.");
        }
    }

    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= currentExpenses.size()) {
            showWarning("Select an expense from the table to delete.");
            return;
        }

        try {
            boolean deleted = expenseService.deleteExpense(currentExpenses.get(selectedRow), currentUser);
            if (!deleted) {
                showWarning("Unable to delete the selected expense.");
                return;
            }

            refreshAllExpenses();
            setStatus("Selected expense deleted.");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage());
        } catch (Exception ex) {
            showWarning("Unable to delete the expense right now.");
        }
    }

    private void showTotalDialog() {
        try {
            int total = expenseService.getTotalExpense(currentUser);
            totalValueLabel.setText("Rs " + total);
            setStatus("Total updated.");
            JOptionPane.showMessageDialog(this, "Total Expense: Rs " + total, "Total Expense",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showWarning("Unable to fetch the total right now.");
        }
    }

    private void refreshAllExpenses() {
        try {
            loadExpenses(expenseService.getAllExpenses(currentUser));
            setStatus("Showing all expenses.");
        } catch (Exception ex) {
            showWarning("Unable to load expenses right now.");
        }
    }

    private void loadExpenses(List<Expense> expenses) {
        currentExpenses = new ArrayList<>(expenses);
        tableModel.setRowCount(0);

        for (Expense expense : currentExpenses) {
            tableModel.addRow(new Object[]{
                    expense.getTitle(),
                    (int) expense.getAmount(),
                    expense.getCategory(),
                    expense.getDate()
            });
        }

        countValueLabel.setText(String.valueOf(currentExpenses.size()));
        totalValueLabel.setText("Rs " + expenseService.getTotalExpense(currentUser));
    }

    private void setStatus(String message) {
        statusValueLabel.setText(message);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Expense Tracker", JOptionPane.WARNING_MESSAGE);
        setStatus(message);
    }
}
