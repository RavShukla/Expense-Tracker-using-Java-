package ui;

import services.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    

    private static final Color BACKGROUND = new Color(241, 244, 236);
    private static final Color PANEL = new Color(255, 252, 244);
    private static final Color PRIMARY = new Color(44, 85, 48);
    private static final Color ACCENT = new Color(216, 157, 71);

    private final UserService userService;

    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;
    private JPasswordField confirmPasswordField;
    private JTabbedPane tabs;

    public LoginFrame() {
        userService = new UserService();

        setTitle("Expense Tracker");
        setSize(500, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(buildContent());
        setVisible(true);
    }

    private JComponent buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(), BorderLayout.CENTER);
        return root;
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BACKGROUND);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Expense Tracker");
        title.setFont(new Font("Georgia", Font.BOLD, 28));
        title.setForeground(PRIMARY);

        JLabel subtitle = new JLabel("A clean multi-user desktop tracker for everyday spending.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(78, 86, 81));

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);
        return header;
    }

    private JComponent buildTabs() {
        tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.addTab("Login", buildLoginPanel());
        tabs.addTab("Register", buildRegisterPanel());
        return tabs;
    }

    private JComponent buildLoginPanel() {
        JPanel panel = buildCardPanel();
        loginUsernameField = new JTextField();
        loginPasswordField = new JPasswordField();

        panel.add(buildField("Username", loginUsernameField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildField("Password", loginPasswordField));
        panel.add(Box.createVerticalStrut(18));

        JButton loginButton = createButton("Login", PRIMARY, Color.WHITE);
        loginButton.addActionListener(e -> attemptLogin());
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(buildHintLabel("Log in to view only your own expenses and totals."));
        return panel;
    }

    private JComponent buildRegisterPanel() {
        JPanel panel = buildCardPanel();
        registerUsernameField = new JTextField();
        registerPasswordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        panel.add(buildField("Username", registerUsernameField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildField("Password", registerPasswordField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildField("Confirm Password", confirmPasswordField));
        panel.add(Box.createVerticalStrut(18));

        JButton registerButton = createButton("Create Account", ACCENT, new Color(45, 31, 5));
        registerButton.addActionListener(e -> attemptRegister());
        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(buildHintLabel("Passwords are hashed before they are stored in MongoDB."));
        return panel;
    }

    private JPanel buildCardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JComponent buildField(String label, JComponent input) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        fieldLabel.setForeground(PRIMARY);

        input.setFont(new Font("SansSerif", Font.PLAIN, 14));
        input.setPreferredSize(new Dimension(220, 34));

        wrapper.add(fieldLabel, BorderLayout.NORTH);
        wrapper.add(input, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel buildHintLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(105, 109, 104));
        return label;
    }

    private JButton createButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        return button;
    }

    private void attemptLogin() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());

        try {
            boolean loggedIn = userService.login(username, password);
            if (!loggedIn) {
                showError("Invalid username or password.", "Login Failed");
                return;
            }

            new MainFrame(userService.normalizeUsername(username));
            dispose();
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage(), "Login Failed");
        } catch (Exception ex) {
            showError("Unable to complete login right now.", "Login Failed");
        }
    }

    private void attemptRegister() {
        String username = registerUsernameField.getText();
        String password = new String(registerPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!password.equals(confirmPassword)) {
            showWarning("Passwords do not match.", "Registration Failed");
            return;
        }

        try {
            boolean created = userService.register(username, password);
            if (!created) {
                showWarning("That username already exists.", "Registration Failed");
                return;
            }

            loginUsernameField.setText(userService.normalizeUsername(username));
            loginPasswordField.setText("");
            registerUsernameField.setText("");
            registerPasswordField.setText("");
            confirmPasswordField.setText("");
            tabs.setSelectedIndex(0);

            JOptionPane.showMessageDialog(this, "Account created. You can log in now.");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage(), "Registration Failed");
        } catch (Exception ex) {
            showError("Unable to create the account right now.", "Registration Failed");
        }
    }

    private void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
