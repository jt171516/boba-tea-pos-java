import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class manageMenuGUI extends JFrame implements ActionListener {
    static JFrame f;

    private static Connection conn = null;

    // Menu management components
    private JPanel menuTablePanel;
    private DefaultTableModel model;
    private JTable table;
    private JButton manageItemsButton;
    private JButton closeButton;

    public static void main(String[] args) {
        connectToDatabase();
        SwingUtilities.invokeLater(() -> {
            manageMenuGUI app = new manageMenuGUI();
            app.setVisible(true);
        });
    }

    //connection to database
    private static void connectToDatabase() {
        String databaseName = "team_11_db";
        String databaseUser = "team_11";
        String databasePassword = "bayleef93";
        String url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", databaseName);

        try {
            conn = DriverManager.getConnection(url, databaseUser, databasePassword);
            JOptionPane.showMessageDialog(null, "Opened database successfully");
        }
        catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            System.exit(0);
        }
    }

    public manageMenuGUI() {
        super("Menu GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,700);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        add(mainPanel);

        // Create menu table panel
        buildMenuTablePanel();
        mainPanel.add(menuTablePanel, BorderLayout.CENTER);

        // Exit Button
        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        add(closeButton, BorderLayout.SOUTH);
    }

    private void buildMenuTablePanel() {
        menuTablePanel = new JPanel(new BorderLayout(10, 10));
        menuTablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create table model
        model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add columns to table model
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Price");
        model.addColumn("Calories");
        model.addColumn("Sales");

        // Create table
        table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(500, 300));
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        menuTablePanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel for the table
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        manageItemsButton = new JButton("Manage Items");
        manageItemsButton.addActionListener(this);
        tableButtonPanel.add(manageItemsButton);

        menuTablePanel.add(tableButtonPanel, BorderLayout.SOUTH);

        // Load menu items from DB
        loadMenuItemsManager();
    }

    private void loadMenuItemsManager() {
        if (conn == null) {
            return;
        }

        // Clear existing data
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }

        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT * FROM Item";
            ResultSet rs = stmt.executeQuery(sqlStatement);

            while (rs.next()) {
                // Get data from the current row
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                Integer price = rs.getInt("price");
                Integer calories = rs.getInt("calories");
                Integer sales = rs.getInt("sales");

                model.addRow(new Object[]{id, name, price, calories, sales});
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "LOADING MENU ITEMS ERROR sad " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "Manage Items":
                itemManagement();
                break;
            case "Close":
                closeConnection();
                dispose();
                break;
            default:
                break;
        }
    }

    private void closeConnection() {
        try {
            if (conn != null)
            {
                conn.close();
                JOptionPane.showMessageDialog(this, "Connection Closed.");
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Connection NOT Closed: " + e.getMessage());
        }
    }

    private void itemManagement() {
        JDialog dialog = new JDialog(this, "Manage Items");
        dialog.setSize(400, 300);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 5, 10, 10));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField caloriesField = new JTextField();
        JTextField salesField = new JTextField();

        fieldsPanel.add(new JLabel("ID:", JLabel.CENTER));
        fieldsPanel.add(new JLabel("Name:", JLabel.CENTER));
        fieldsPanel.add(new JLabel("Price:", JLabel.CENTER));
        fieldsPanel.add(new JLabel("Calories:", JLabel.CENTER));
        fieldsPanel.add(new JLabel("Sales:", JLabel.CENTER));

        fieldsPanel.add(idField);
        fieldsPanel.add(nameField);
        fieldsPanel.add(priceField);
        fieldsPanel.add(caloriesField);
        fieldsPanel.add(salesField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton addEditButton = new JButton("Add/Edit Item");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(addEditButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);

        addEditButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int itemID = Integer.parseInt(idField.getText());
                    String itemName = nameField.getText();
                    String itemPrice = priceField.getText();
                    String itemCalories = caloriesField.getText();
                    String itemSale = salesField.getText();

                    boolean idExists = checkItemID(itemID);

                    if (idExists) {
                        if (!itemPrice.isEmpty()) {
                            try {
                                Statement stmt = conn.createStatement();
                                String updatePrice = "UPDATE Item SET price=" + itemPrice + " WHERE id=" + itemID;

                                stmt.executeUpdate(updatePrice);
                                JOptionPane.showMessageDialog(dialog, "Item price updated");
                                stmt.close();
                            }
                            catch (Exception ex) {
                                JOptionPane.showMessageDialog(dialog, "Error updating price: " + ex.getMessage());
                            }
                        }
                    }
                    else {
                        try {
                            Statement stmt = conn.createStatement();
                            String insertItem = "INSERT INTO Item (id, name, price, calories, sales) VALUES (" + itemID + ", '" + itemName + "', " + itemPrice + ", " + itemCalories + ", " + itemSale + ")";

                            stmt.executeUpdate(insertItem);
                            JOptionPane.showMessageDialog(dialog, "New item added");
                            stmt.close();
                        }
                        catch (Exception ex) {
                            JOptionPane.showMessageDialog(dialog, "Error adding new item: " + ex.getMessage());
                        }
                    }

                    loadMenuItemsManager();
                    dialog.dispose();
                }
                catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter valid numbers for ID, Price, Calories, and Sales");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private boolean checkItemID(int itemID) {
        boolean exists = false;
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT EXISTS(SELECT 1 FROM Item WHERE id = " + itemID + ")";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                exists = rs.getBoolean(1);
            }

            rs.close();
            stmt.close();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking item existence: " + e.getMessage());
        }
        return exists;
    }
}


