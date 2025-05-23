import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * GUI class of interface for POS system
 * @author michael ades, jason agnew, casey ear, jeremy tran, william xu
 */
public class GUI extends JFrame implements ActionListener {
    static JFrame f;


    private static Connection conn = null;
    //Tabbed Pane for manager menu
    private JTabbedPane tabbedPane;

    //Live Date & Time Display
    private JLabel dateTimeLabel;

    //Input fields for initial login screen
    static JTextField userIdField;
    static JPasswordField passwordField;
    static JButton loginButton;

    //CASHIER PANEL
    private JPanel cashierPanel;
    private JTextField searchField;
    private JButton searchButton;
    private JPanel menuItemsPanel;
    private JScrollPane menuItemsScroll;
    private JTextArea orderArea;
    private JButton submitOrderButton;

    //close button
    private JButton closeButton;

    //MANAGER PANEL
    private JPanel managerPanel;
    private DefaultTableModel inventoryTableModel;
    private JComboBox<String> salesReportComboBox;
    private JComboBox<String> productComboBox;

    //MANAGE EMPLOYEES PANEL
    private JPanel employeesPanel;
    private JTable employeesTable;
    private DefaultTableModel employeesTableModel;

    // For adding a new employee
    private JTextField empNameField;
    private JTextField empPasswordField;
    private JCheckBox managerCheckBox;
    private JButton addEmployeeButton;
    private JButton deleteEmployeeButton;

    private static boolean isManager;

    public static void main(String[] args)
    {
        connectToDatabase();
        SwingUtilities.invokeLater(() -> showLoginPage());
    }

    private void updateDateTime(){
        Calendar calendar = Calendar.getInstance();

        //day, month day, year
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM dd, yyyy");
        String date = dateFormatter.format(calendar.getTime());

        //hour, minute, am/pm
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss:a");
        String time = timeFormatter.format(calendar.getTime());

        dateTimeLabel.setText(date + " " + time);
    }

    public GUI(boolean isManager)
    {
        super("Team 11 DB GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);

        //initialize panel
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        //cashier panel
        buildCashierPanel();
        tabbedPane.addTab("Cashier", cashierPanel);

        if (isManager){
            //manager panel
            buildManagerPanel();
            tabbedPane.addTab("Manager", managerPanel);

            //employee management tab
            buildEmployeeManagementPanel();
            tabbedPane.addTab("Employees", employeesPanel);
        }

        //add live date + clock to top border
        dateTimeLabel = new JLabel();
        updateDateTime();
        add(dateTimeLabel, BorderLayout.NORTH);

        Timer timer = new Timer(1000, this);
        timer.setActionCommand("updateDateTime");
        timer.start();

        //exit button
        closeButton = new JButton("Logout");
        closeButton.addActionListener(this);
        add(closeButton, BorderLayout.SOUTH);
    }

    //create dedicated initial login page
    public static void showLoginPage() {
        f = new JFrame("Login");

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(50, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Image panel
        JLabel imageLabel = new JLabel(new ImageIcon("../images/logo.png"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(imageLabel, gbc);

        // Fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints fieldsGbc = new GridBagConstraints();
        fieldsGbc.insets = new Insets(5, 5, 5, 5); // Padding
        fieldsGbc.anchor = GridBagConstraints.WEST;

        // Username label and field
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy = 0;
        fieldsPanel.add(new JLabel("Username:"), fieldsGbc);

        fieldsGbc.gridx = 1;
        userIdField = new JTextField(30);
        userIdField.setPreferredSize(new Dimension(300, 30));
        fieldsPanel.add(userIdField, fieldsGbc);

        // Password label and field
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy = 1;
        fieldsPanel.add(new JLabel("Password:"), fieldsGbc);

        fieldsGbc.gridx = 1;
        passwordField = new JPasswordField(30);
        passwordField.setPreferredSize(new Dimension(300, 30));
        fieldsPanel.add(passwordField, fieldsGbc);

        // Login button
        fieldsGbc.gridx = 1;
        fieldsGbc.gridy = 2;
        fieldsGbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.addActionListener(new GUI(isManager));
        fieldsPanel.add(loginButton, fieldsGbc);

        // Add fields panel to main panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(fieldsPanel, gbc);

        f.add(mainPanel);

        f.setSize(1000, 700);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void showMainPage(boolean isManager) {
        // Dispose the login frame
        f.dispose();

        // Create the main application frame
        GUI app = new GUI(isManager);
        app.setVisible(true);
    }

    private void buildCashierPanel()
    {
        cashierPanel = new JPanel(new BorderLayout(10, 10));
        cashierPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // top search panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search Items:");
        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);

        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        // left = menu items, right = order area
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        // menu items in a grid
        menuItemsPanel = new JPanel(new GridLayout(0, 5, 5, 5));
        menuItemsScroll = new JScrollPane(menuItemsPanel);

        //order area
        orderArea = new JTextArea();
        orderArea.setEditable(true);
        JScrollPane orderScroll = new JScrollPane(orderArea);

        centerPanel.add(menuItemsScroll);
        centerPanel.add(orderScroll);

        // submit order
        JPanel orderPaymentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cashPaymentButton = new JButton("Pay with Cash");
        JButton cardPaymentButton = new JButton("Pay with Card");
        cashPaymentButton.addActionListener(this);
        cardPaymentButton.addActionListener(this);
        orderPaymentPanel.add(cashPaymentButton);
        orderPaymentPanel.add(cardPaymentButton);

        // cashier panel
        cashierPanel.add(topPanel, BorderLayout.NORTH);
        cashierPanel.add(centerPanel, BorderLayout.CENTER);
        cashierPanel.add(orderPaymentPanel, BorderLayout.SOUTH);

        // load menu items from DB
        loadAllMenuItemsForCashier();

    }

    /**
     * Creates the manager page of the POS system
     * @author jason agnew, casey ear, jeremy tran, william xu
     * @throws SQLException if there is a problem running the SQL query
     */
    private void buildManagerPanel()
    {
        managerPanel = new JPanel(new BorderLayout(10, 10));
        managerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel revenueLabel = new JLabel("Reports:");

        productComboBox = new JComboBox<>();

        //populate the productCombobBox with all of the items
        populateProductComboBox(productComboBox);

        JComboBox<String> timeRangeComboBox = new JComboBox<>(new String[] {"1 Week", "1 Month", "3 Months"});

        topPanel.add(revenueLabel);
        topPanel.add(productComboBox);
        topPanel.add(timeRangeComboBox);

        //add button for x report
        JButton xReportButton = new JButton("X-Report");
        xReportButton.addActionListener(this);
        topPanel.add(xReportButton);

        //add button for z report
        JButton endOfDayButton = new JButton("End of Day (Z-Report)");
        endOfDayButton.addActionListener(e -> showZReportDialog());
        topPanel.add(endOfDayButton);

        // === CENTER PANEL ===
        JPanel chartPanel = new JPanel() {};
        chartPanel.setPreferredSize(new Dimension(800, 100));

        // We'll put Inventory on the left, Orders on the right.
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // ----- Left: Inventory -----
        //inventoryTable data
        String[] inventoryColumns = {"ID","Product", "Stock", "Status"};
        inventoryTableModel = new DefaultTableModel(inventoryColumns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int col)
            {
                return col == 2;
            }
        };

        JTable inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.getColumn(inventoryColumns[0]).setPreferredWidth(20);
        JScrollPane inventoryScrollPane = new JScrollPane(inventoryTable);

        // We can wrap the table in a panel with a title
        JPanel inventoryPanel = new JPanel(new BorderLayout());
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("Inventory"));
        inventoryPanel.add(inventoryScrollPane, BorderLayout.CENTER);

        //populates the inventory table with the inventory from the database
        populateInventoryTable(inventoryTableModel);

        //add buttons for managing inventory
        JPanel inventoryButtonsPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Inventory");
        JButton removeButton = new JButton("Remove Inventory");
        inventoryButtonsPanel.add(addButton);
        inventoryButtonsPanel.add(removeButton);
        inventoryPanel.add(inventoryButtonsPanel, BorderLayout.SOUTH);

        //add inventory button actions
        addButton.addActionListener(this);
        removeButton.addActionListener(this);

        //adding CellEditorListener to detect cell edits
        inventoryTable.getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener()
        {
            @Override
            public void editingStopped(ChangeEvent e)
            {
                int row = inventoryTable.getSelectedRow();
                int col = inventoryTable.getSelectedColumn();

                //check to see if the selected column is the Stock column, since we only want this one to be modified
                if (col == 2)
                {
                    //new stock value
                    Object newValue = inventoryTable.getValueAt(row,col);
                    //name of the inventory item that needs stock to be updated
                    Object invName = inventoryTable.getValueAt(row,1);
                    //update the database
                    updateInventoryQuantity(Integer.parseInt(newValue.toString()), invName.toString());
                    populateInventoryTable(inventoryTableModel);
                    JOptionPane.showMessageDialog(null, "Quantity for " + invName + " was changed to: " + newValue);
                }
            }

            @Override
            public void editingCanceled(ChangeEvent e)
            {
                JOptionPane.showMessageDialog(null, "Cell Row Was Cancelled");
            }
        });

        // ----- Right: Items -----
        String[] itemColumns = {"ID", "Name", "Price", "Calories", "Sales"};
        DefaultTableModel itemTableModel = new DefaultTableModel(itemColumns, 0);

        JTable itemTable = new JTable(itemTableModel);
        JScrollPane itemScrollPane = new JScrollPane(itemTable);

        // We can wrap the table in a panel with a title
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createTitledBorder("Items"));
        itemPanel.add(itemScrollPane, BorderLayout.CENTER);

        // Add button for managing items
        JButton manageItemsButton = new JButton("Manage Items");
        JButton removeItemsButton = new JButton("Remove Items");
        JPanel itemButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        itemButtonPanel.add(manageItemsButton);
        itemButtonPanel.add(removeItemsButton);
        itemPanel.add(itemButtonPanel, BorderLayout.SOUTH);

        // Add event listener for manage items button
        manageItemsButton.addActionListener(evt -> {
            itemManagement(itemTableModel);
        });
        removeItemsButton.addActionListener(evt -> {
            String itemID = (String) JOptionPane.showInputDialog(null, "Item ID:", "Remove Item", JOptionPane.PLAIN_MESSAGE);
            if(itemID != null) {
                removeItem(Integer.parseInt(itemID));
                loadItemsManager(itemTableModel);
            }
        });

        // Load items
        loadItemsManager(itemTableModel);

        // Add both sub-panels to bottomPanel
        bottomPanel.add(inventoryPanel);
        bottomPanel.add(itemPanel);

        // === ASSEMBLE EVERYTHING ===
        managerPanel.add(topPanel, BorderLayout.NORTH);
        managerPanel.add(chartPanel, BorderLayout.CENTER);
        managerPanel.add(bottomPanel, BorderLayout.SOUTH);

        //sales report panel for manager panel
        salesReportComboBox = new JComboBox<>(new String[]{"12 hours", "1 day", "2 days", "1 week", "1 month", "3 months"});

        //add action listener to fetch sales data for the selected week
        salesReportComboBox.addActionListener(this);
        salesReportComboBox.setActionCommand("Generate Inventory Usage and Sales Reports");

        topPanel.add(new JLabel("Generate Inventory Usage and Sales Reports for:"));
        topPanel.add(salesReportComboBox);
    }

    private void populateProductComboBox (JComboBox<String> productComboBox)
    {
        productComboBox.removeAllItems();
        productComboBox.addItem("All Products");

        String sql = "SELECT name FROM item";

        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                productComboBox.addItem(rs.getString("name"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load products: " + e.getMessage());
        }
    }

    private void buildEmployeeManagementPanel() {
        employeesPanel = new JPanel(new BorderLayout(10, 10));
        employeesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Form to add a new employee using GridBagLayout for better control
        JPanel addPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.anchor = GridBagConstraints.WEST;

        // Name label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        addPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        empNameField = new JTextField(15); // Increased column size for better visibility
        addPanel.add(empNameField, gbc);

        //Password label
        gbc.gridx = 2;
        addPanel.add(new JLabel("Password:"), gbc);

        //Password field
        gbc.gridx = 3;
        empPasswordField = new JTextField(15);
        addPanel.add(empPasswordField, gbc);

        // Manager checkbox
        gbc.gridx = 4;
        managerCheckBox = new JCheckBox("Manager");
        addPanel.add(managerCheckBox, gbc);

        // Add Employee button
        gbc.gridx = 5;
        addEmployeeButton = new JButton("Add Employee");
        addEmployeeButton.addActionListener(e -> addEmployee(empPasswordField.getText()));
        addPanel.add(addEmployeeButton, gbc);

        // Table setup
        String[] columns = { "ID", "Name", "Password", "Manager"};
        employeesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID column is not editable
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) { // Manager column is Boolean
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        employeesTable = new JTable(employeesTableModel);
        JScrollPane tableScroll = new JScrollPane(employeesTable);
        employeesTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                int id = (int) employeesTableModel.getValueAt(row, 0);

                if (column == 1) { // Name column
                    String newName = (String) employeesTableModel.getValueAt(row, column);
                    updateEmployeeName(id, newName);
                } else if (column == 3) { // Manager column
                    boolean isManager = (boolean) employeesTableModel.getValueAt(row, column);
                    updateEmployeeManagerStatus(id, isManager);
                } else if (column == 2) {
                    String newPassword = (String) employeesTableModel.getValueAt(row, column);
                    updateEmployeePassword(id, newPassword);
                }
            }
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteEmployeeButton = new JButton("Delete Selected");
        deleteEmployeeButton.addActionListener(e -> deleteSelectedEmployee());
        bottomPanel.add(deleteEmployeeButton);

        // Assemble the employeesPanel
        employeesPanel.add(addPanel, BorderLayout.NORTH);
        employeesPanel.add(tableScroll, BorderLayout.CENTER);
        employeesPanel.add(bottomPanel, BorderLayout.SOUTH);

        loadAllEmployees();
    }
    private void updateEmployeeName(int id, String newName) {
        String sql = "UPDATE employee SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating name: " + ex.getMessage());
            loadAllEmployees(); // Refresh data on error
        }
    }
    private void updateEmployeeManagerStatus(int id, boolean isManager) {
        String sql = "UPDATE employee SET manager = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isManager);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating manager status: " + ex.getMessage());
            loadAllEmployees(); // Refresh data on error
        }
    }

    private void updateEmployeePassword(int id, String newPassword) {
        String sql = "UPDATE employee SET password = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating password: " + ex.getMessage());
            loadAllEmployees(); // Refresh data on error
        }
    }

    private void loadAllEmployees() {
        // Clear old data
        employeesTableModel.setRowCount(0);

        String sql = "SELECT id, name, manager, password FROM employee";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                boolean isManager = rs.getBoolean("manager");

                // Convert boolean to string or keep it boolean
                employeesTableModel.addRow(new Object[]{id, name, password, isManager});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employees: " + e.getMessage());
        }
    }
    private void addEmployee(String password) {
        String name = empNameField.getText().trim();
        boolean isManager = managerCheckBox.isSelected();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.");
            return;
        }

        // 1) get nextId
        int nextId = 0;
        String getMaxIdSQL = "SELECT COALESCE(MAX(id), 0) + 1 AS next_id FROM employee";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(getMaxIdSQL)) {
            if (rs.next()) {
                nextId = rs.getInt("next_id");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error getting next ID: " + ex.getMessage());
            return;
        }

        // 2) Insert row with nextId
        String sql = "INSERT INTO employee (id, name, password, manager) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nextId);
            pstmt.setString(2, name);
            pstmt.setString(3, password);
            pstmt.setBoolean(4, isManager);
            pstmt.executeUpdate();

            // 3) refresh
            loadAllEmployees();

            // 4) clear UI
            empNameField.setText("");
            empPasswordField.setText("");
            managerCheckBox.setSelected(false);

            JOptionPane.showMessageDialog(this, "Employee added successfully (ID=" + nextId + ")!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage());
        }
    }

    private void deleteSelectedEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            return;
        }

        // ID is in the first column (index 0)
        int employeeId = (int) employeesTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this employee (ID=" + employeeId + ")?",
                "Delete Employee",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM employee WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, employeeId);
                pstmt.executeUpdate();

                // Refresh table
                loadAllEmployees();

                JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting employee: " + e.getMessage());
            }
        }
    }

    private void loadAllMenuItemsForCashier()
    {
        if (conn == null)
        {
            return;
        }
        //remove old menu items
        menuItemsPanel.removeAll();
        try
        {
            Statement epicStatement = conn.createStatement();
            String sql = "SELECT name, price FROM Item";
            ResultSet rs = epicStatement.executeQuery(sql);

            while(rs.next())
            {
                String name = rs.getString("name");
                double price = rs.getDouble("price");

                //add button
                JButton itemButton = new JButton("<html>" + name + "<br>$" + price + "</html>");

                itemButton.setFont(new Font("Arial", Font.PLAIN, 12));
                itemButton.setMargin(new Insets(1, 1, 1, 1));
                itemButton.setPreferredSize(new Dimension(80, 60));

                itemButton.addActionListener(evt -> {
                    orderArea.append(name + " - $" + price + "\n");
                });
                menuItemsPanel.add(itemButton);
            }
            rs.close();
            epicStatement.close();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, "LOADING MENU ITEMS ERROR sad " + e.getMessage());
        }
    }

    private void searchMenuItemsForCashier(String query)
    {
        // \set query '%{param query here}%'
        // SELECT name FROM item
        // WHERE name LIKE :'query';

        String searchQuery = "'%" + query + "%'";
        String sql = "SELECT name, price FROM item WHERE name LIKE " + searchQuery;

        menuItemsPanel.removeAll();
        try(PreparedStatement stmt = conn.prepareStatement(sql))
        {
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                String name = rs.getString("name");
                double price = rs.getDouble("price");

                JButton itemButton = new JButton(name + " " + price);

                itemButton.addActionListener(evt -> {
                    orderArea.append(name + " - $" + price + "\n");
                });
                menuItemsPanel.add(itemButton);
            }
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Searching menu items error" + e.getMessage());
        }

    }

    JPanel inventoryReportPanel = null;

    private void generateInventoryUsageReport()
    {
        DefaultTableModel inventoryModel = new DefaultTableModel(new String[]{"Inventory Name", "Inventory Count"}, 0);

        //loop through the inventoryNames and inventoryCounts to populate the table
        for (int i = 0; i < inventoryNames.size(); i++) {
            String inventoryName = inventoryNames.get(i);
            int inventoryCount = inventoryCounts.get(i);
            inventoryModel.addRow(new Object[]{inventoryName, inventoryCount});
        }

        if (inventoryReportPanel != null)
        {
            managerPanel.remove(inventoryReportPanel);
        }

        inventoryReportPanel = new JPanel(new BorderLayout());
        JTable inventoryTable = new JTable(inventoryModel);
        JScrollPane inventoryScrollPane = new JScrollPane(inventoryTable);

        JButton closeButton = new JButton("X");
        closeButton.setPreferredSize(new Dimension(50, 15));

        closeButton.addActionListener(e -> {
            managerPanel.remove(inventoryReportPanel);
            managerPanel.revalidate();
            managerPanel.repaint();
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(closeButton, BorderLayout.EAST);

        inventoryReportPanel.setBorder(BorderFactory.createTitledBorder("Inventory Usage Report"));
        inventoryReportPanel.add(inventoryScrollPane, BorderLayout.CENTER);
        inventoryReportPanel.add(buttonPanel, BorderLayout.NORTH);

        managerPanel.add(inventoryReportPanel, BorderLayout.WEST);

        managerPanel.revalidate();
        managerPanel.repaint();
    }

    //function to calculate the total count of inventory used in the orders for a specific item
    private int getInventoryCount(int inventoryId, int itemId, int totalQuantity)
    {
        int inventoryCount = 0;

        //query to count how many times this specific inventory is associated with the given item
        String fetchInventorySQL = "SELECT COUNT(*) AS inventory_count " +
                "FROM iteminventoryjunction ii " +
                "WHERE ii.inventoryid = ? AND ii.itemid = ?";

        try (PreparedStatement fetchstmt = conn.prepareStatement(fetchInventorySQL))
        {
            fetchstmt.setInt(1, inventoryId);
            fetchstmt.setInt(2, itemId);
            ResultSet rs = fetchstmt.executeQuery();

            if (rs.next())
            {
                inventoryCount = rs.getInt("inventory_count");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        //multiply by the total quantity to get the total inventory used for all ordered items of this type
        return inventoryCount*totalQuantity;
    }

    private JPanel salesReportPanel = null;
    private ArrayList<Integer> inventoryCounts = new ArrayList<>();
    private ArrayList<String> inventoryNames = new ArrayList<>();

    /**
     * Creates sales report given a specific time period
     * @author jason agnew, casey ear
     * @throws SQLException if there is a problem running the SQL query
     */
    //fucntion to generate the sales report for a specific time period
    private void generateSalesReport(String period)
    {
        if (conn == null)
        {
            return;
        }

        String sql = "";

        //queries for different periods (12 hours, 1 day, 1 week, etc.)
        switch (period) {
            case "12 hours":
                sql = "SELECT i.name AS item_name, oi.itemid, COUNT(oi.itemid) AS total_quantity, " +
                        "SUM(i.price) AS item_sales " +
                        "FROM orders o " +
                        "JOIN ordersitemjunction oi ON o.id = oi.orderid " +
                        "JOIN item i ON oi.itemid = i.id " +
                        "WHERE o.timestamp >= NOW() - INTERVAL '12 hours' " +
                        "GROUP BY oi.itemid, i.name";
                break;
            case "1 day":
                sql = "SELECT i.name AS item_name, oi.itemid, COUNT(oi.itemid) AS total_quantity, " +
                        "SUM(i.price) AS item_sales " +
                        "FROM orders o " +
                        "JOIN ordersitemjunction oi ON o.id = oi.orderid " +
                        "JOIN item i ON oi.itemid = i.id " +
                        "WHERE o.timestamp >= NOW() - INTERVAL '1 day' " +
                        "GROUP BY oi.itemid, i.name";
                break;
            case "2 days":
                sql = "SELECT i.name AS item_name, oi.itemid, COUNT(oi.itemid) AS total_quantity, " +
                        "SUM(i.price) AS item_sales " +
                        "FROM orders o " +
                        "JOIN ordersitemjunction oi ON o.id = oi.orderid " +
                        "JOIN item i ON oi.itemid = i.id " +
                        "WHERE o.timestamp >= NOW() - INTERVAL '2 days' " +
                        "GROUP BY oi.itemid, i.name";
                break;
            case "1 week":
                sql = "SELECT i.name AS item_name, oi.itemid, COUNT(oi.itemid) AS total_quantity, " +
                        "SUM(i.price) AS item_sales " +
                        "FROM orders o " +
                        "JOIN ordersitemjunction oi ON o.id = oi.orderid " +
                        "JOIN item i ON oi.itemid = i.id " +
                        "WHERE o.timestamp >= NOW() - INTERVAL '1 week' " +
                        "GROUP BY oi.itemid, i.name";
                break;
            case "1 month":
                sql = "SELECT i.name AS item_name, oi.itemid, COUNT(oi.itemid) AS total_quantity, " +
                        "SUM(i.price) AS item_sales " +
                        "FROM orders o " +
                        "JOIN ordersitemjunction oi ON o.id = oi.orderid " +
                        "JOIN item i ON oi.itemid = i.id " +
                        "WHERE o.timestamp >= NOW() - INTERVAL '1 month' " +
                        "GROUP BY oi.itemid, i.name";
                break;
            case "3 months":
                sql = "SELECT i.name AS item_name, oi.itemid, COUNT(oi.itemid) AS total_quantity, " +
                        "SUM(i.price) AS item_sales " +
                        "FROM orders o " +
                        "JOIN ordersitemjunction oi ON o.id = oi.orderid " +
                        "JOIN item i ON oi.itemid = i.id " +
                        "WHERE o.timestamp >= NOW() - INTERVAL '3 months' " +
                        "GROUP BY oi.itemid, i.name";
                break;
            default:
                return;
        }

        DefaultTableModel model = new DefaultTableModel(new String[]{"Item name", "Total Quantity", "Item Sales"}, 0);

        try (PreparedStatement Stmt = conn.prepareStatement(sql))
        {
            ResultSet result = Stmt.executeQuery();

            //to track inventory usage for each item
            HashMap<String, Integer> inventoryMap = new HashMap<>();

            while (result.next())
            {
                String itemName = result.getString("item_name");
                int itemId = result.getInt("itemid");
                int totalQuantity = result.getInt("total_quantity");
                double salesValue = result.getDouble("item_sales");
                model.addRow(new Object[]{itemName, totalQuantity, "$" + salesValue});

                //query to get inventory details related to this item
                String inventorySQL = "SELECT inventory.id, inventory.name FROM inventory " +
                        "JOIN iteminventoryjunction ii ON inventory.id = ii.inventoryid " +
                        "WHERE ii.itemid = ?";

                try (PreparedStatement invStmt = conn.prepareStatement(inventorySQL))
                {
                    invStmt.setInt(1, itemId);
                    ResultSet invResult = invStmt.executeQuery();

                    while (invResult.next())
                    {
                        String inventoryName = invResult.getString("name");
                        int inventoryId = invResult.getInt("id");
                        //get the inventory count
                        int inventoryCount = getInventoryCount(inventoryId, itemId, totalQuantity);

                        //update the inventory count for this inventory item
                        inventoryMap.put(inventoryName, inventoryMap.getOrDefault(inventoryName, 0) + inventoryCount);
                    }
                }
            }
            inventoryCounts.clear();
            inventoryNames.clear();

            //stor the final inventory counts and names
            for (Map.Entry<String, Integer> entry : inventoryMap.entrySet())
            {
                inventoryNames.add(entry.getKey());
                inventoryCounts.add(entry.getValue());
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error executing query: " + e.getMessage());
            return;
        }

        if (salesReportPanel != null)
        {
            managerPanel.remove(salesReportPanel);
        }

        //create panel for the sales report
        salesReportPanel = new JPanel(new BorderLayout());
        JTable salesTable = new JTable(model);
        JScrollPane salesScrollPane = new JScrollPane(salesTable);

        //create close sales report button
        JButton closeButton = new JButton("X");
        closeButton.setPreferredSize(new Dimension(50, 15));

        //add event listener to close sales report panel once button is selected
        closeButton.addActionListener(e -> {
            managerPanel.remove(salesReportPanel);
            salesReportPanel = null;
            managerPanel.revalidate();
            managerPanel.repaint();
        });

        //button panel layout and styling
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(closeButton, BorderLayout.EAST);

        //sales panel layout and styling
        salesReportPanel.setBorder(BorderFactory.createTitledBorder("Sales Report"));
        salesReportPanel.add(salesScrollPane, BorderLayout.CENTER);
        salesReportPanel.add(buttonPanel, BorderLayout.NORTH);

        //add the updated sales report panel to the right side of the manager panel
        managerPanel.add(salesReportPanel, BorderLayout.EAST);

        //refresh the panel properly
        managerPanel.revalidate();
        managerPanel.repaint();
    }

    /**
     * Inserts inventory item into inventory table in database
     * @author jason agnew
     * @param id id of inventory item to be added
     * @param name name of inventory item to be added
     * @param qty quantity of inventory item to be added
     * @throws SQLException if there is a problem running the SQL query
     */
    public void addInventoryItem(int id, String name, int qty)
    {
        if (conn == null) {
            return;
        }

        //check if an item with the same name already exists in the database
        String checkSql = "SELECT COUNT(*) FROM inventory WHERE name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql))
        {
            checkStmt.setString(1,name);
            ResultSet result = checkStmt.executeQuery();

            if (result.next() && result.getInt(1) > 0)
            {
                JOptionPane.showMessageDialog(this,"An item with this name already exists, add failed.", "Duplicate Item", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error executing query: " + e.getMessage());
            return;
        }

        //if item doesn't exit, proceed with the insert
        String sql = "INSERT INTO inventory (id, name, qty) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setInt(3, qty);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error executing query: " + e.getMessage());
            return;
        }
    }

    /**
     * Removes inventory items from the database
     * @author jason agnew
     * @param itemId the id of the inventory item to be removed
     * @param name name of the inventory item to be removed
     * @throws SQLException if there is a problem running the SQL query
     */
    private void removeInventoryItem(int itemId, String name)
    {
        if (conn == null)
        {
            return;
        }

        //delete inventory item from inventory table
        String sql = "DELETE FROM inventory WHERE id = ? AND name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, itemId);
            stmt.setString(2, name);
            stmt.executeUpdate();
        }

        //if item is already in iteminventoryjunction, then pop up cannot remove warning
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Cannot remove inventory as items depend upon it", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    /**
     * Updates quantity of selected inventory item in database
     * @author jason agnew
     * @param qty updated quantity of inventory item
     * @param invName name of the inventory item to be updated
     * @throws SQLException if there is a problem running the SQL query
     */
    private void updateInventoryQuantity(int qty, String invName)
    {
        if (conn == null)
        {
            return;
        }
        String sql = "UPDATE inventory SET qty = ? WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1,qty);
            stmt.setString(2, invName);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error executing query: " + e.getMessage());
            return;
        }
    }

    /**
     * Removes an item and its associated inventory items from the database
     * @author jeremy tran
     * @param id the id of the item to be removed
     * @throws SQLException if there is a problem running the SQL query
     */
    private void removeItem(int id)
    {
        if (conn == null)
        {
            return;
        }
        String removeItemInventoryJunctions = "DELETE FROM itemInventoryJunction WHERE itemID = ?";
        try (PreparedStatement removeJunctionStmt = conn.prepareStatement(removeItemInventoryJunctions))
        {
            removeJunctionStmt.setInt(1, id);
            removeJunctionStmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error executing query: " + e.getMessage());
        }

        String removeItem = "DELETE FROM item WHERE id = ?";
        try (PreparedStatement removeItemStmt = conn.prepareStatement(removeItem))
        {
            removeItemStmt.setInt(1, id);
            removeItemStmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error executing query: " + e.getMessage());
            return;
        }
    }

    /**
     * Populates table model of inventory table on manager page
     * @author jason agnew
     * @param inventoryTableModel table model of inventory table on manager page
     * @throws SQLException if there is a problem running the SQL query
     */
    public void populateInventoryTable(DefaultTableModel inventoryTableModel)
    {
        if (conn == null)
        {
            return;
        }

        String sql = "SELECT id, name, qty FROM inventory";

        //reset the inventory table
        inventoryTableModel.setRowCount(0);

        try (PreparedStatement invStmt = conn.prepareStatement(sql))
        {
            ResultSet result = invStmt.executeQuery();

            while (result.next())
            {
                int invId = result.getInt("id");
                String invName = result.getString("name");
                int stock = result.getInt("qty");
                String status = (stock < 10) ? "Refill Recommended" : "";

                inventoryTableModel.addRow(new Object[]{invId, invName, stock, status});
            }
            result.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error executing query: " + e.getMessage());
            return;
        }
    }


    //helper function which gets the id of an item based on its name
    private int getItemId (String itemName)
    {

        String sql = "SELECT id FROM item WHERE name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1,itemName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                return rs.getInt("id");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean hasZeroStock(int itemId)
    {
        String checkSql = "SELECT COUNT(*) FROM iteminventoryjunction " +
                "JOIN inventory ON iteminventoryjunction.inventoryid = inventory.id " +
                "WHERE itemid = ? AND qty = 0";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql))
        {
            checkStmt.setInt(1,itemId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next())
            {
                //if count > 0, at least one inventory has qty = 0
                return rs.getInt(1) > 0;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private boolean reduceStock (int itemId)
    {

        String fetchInventorySQL = "SELECT inventory.id, inventory.qty FROM inventory " +
                "JOIN iteminventoryjunction ON inventory.id = iteminventoryjunction.inventoryid " +
                "WHERE iteminventoryjunction.itemid = ? " +
                "ORDER BY inventory.qty DESC"; //priortize highest stock first

        try (PreparedStatement fetchstmt = conn.prepareStatement(fetchInventorySQL))
        {
            fetchstmt.setInt(1,itemId);
            ResultSet rs = fetchstmt.executeQuery();

            while (rs.next())
            {
                int inventoryId = rs.getInt("id");
                int currentQty = rs.getInt("qty");

                updateStock(inventoryId);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false; //no available stock in inventory
    }

    private boolean updateStock(int inventoryId)
    {
        String updateStockSQL = "UPDATE inventory SET qty = qty - 1 WHERE id = ?";

        try (PreparedStatement updateStmt = conn.prepareStatement(updateStockSQL))
        {
            updateStmt.setInt(1,inventoryId);
            int affectedRows = updateStmt.executeUpdate();
            return affectedRows > 0; //if there are zero inventory rows without a qty > 0, then return false
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Loads the table model for the items table in the manager panel
     * @author jeremy tran
     * @param model the table model of the JTable of items on the manager page
     * @throws SQLException if there is a problem running the SQL query
     */
    private void loadItemsManager(DefaultTableModel model) {
        if (conn == null) {

            return;
        }

        // Clear existing data
        while (model.getRowCount() > 0)
        {
            model.removeRow(0);
        }

        try
        {
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT * FROM Item";
            ResultSet rs = stmt.executeQuery(sqlStatement);

            while (rs.next())
            {
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
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "LOADING MENU ITEMS ERROR sad " + e.getMessage());
        }
    }

    /**
     * Creates the items panel in the manager section to add, edit, or remove items
     * @author jeremy tran
     * @param model the table model of the JTable of items on the manager page
     * @throws SQLException if there is a problem running the SQL query
     */
    private void itemManagement(DefaultTableModel model)
    {
        JDialog dialog = new JDialog(this, "Manage Items");
        dialog.setSize(400, 300);

        JPanel itemManagementPanel = new JPanel(new BorderLayout(10, 10));
        itemManagementPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

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

        itemManagementPanel.add(fieldsPanel, BorderLayout.CENTER);
        itemManagementPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(itemManagementPanel);

        addEditButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    int itemID = Integer.parseInt(idField.getText());
                    String itemName = nameField.getText();
                    String itemPrice = priceField.getText();
                    String itemCalories = caloriesField.getText();
                    String itemSale = salesField.getText();

                    boolean idExists = checkItemID(itemID);

                    if (idExists)
                    {
                        try
                        {
                            Statement stmt = conn.createStatement();
                            StringBuilder updateItem = new StringBuilder("UPDATE item SET");

                            boolean previousUpdate = false;
                            if (!itemName.trim().isEmpty())
                            {
                                updateItem.append(" name = '" + itemName + "'");
                                previousUpdate = true;
                            }

                            if (!itemPrice.trim().isEmpty())
                            {
                                if (previousUpdate)
                                {
                                    updateItem.append(",");
                                }
                                updateItem.append(" price = " + itemPrice);
                                previousUpdate = true;
                            }

                            if (!itemCalories.trim().isEmpty())
                            {
                                if (previousUpdate)
                                {
                                    updateItem.append(",");
                                }
                                updateItem.append(" calories = " + itemCalories);
                                previousUpdate = true;
                            }

                            if (!itemSale.trim().isEmpty())
                            {
                                if (previousUpdate)
                                {
                                    updateItem.append(",");
                                }
                                updateItem.append(" sales = " + itemSale);
                            }

                            updateItem.append(" WHERE id = " + itemID + ";");

                            stmt.executeUpdate(updateItem.toString());
                            JOptionPane.showMessageDialog(dialog, "Item updated");
                            stmt.close();
                        }
                        catch (Exception ex)
                        {
                            JOptionPane.showMessageDialog(dialog, "Error updating item: " + ex.getMessage());
                        }
                    }

                    else
                    {
                        try
                        {
                            Statement stmt = conn.createStatement();
                            String insertItem = "INSERT INTO Item (id, name, price, calories, sales) VALUES (" + itemID + ", '" + itemName + "', " + itemPrice + ", " + itemCalories + ", " + itemSale + ")";

                            stmt.executeUpdate(insertItem);
                            JOptionPane.showMessageDialog(dialog, "New item added");
                            stmt.close();

                            itemInventoryJunctionDialog(itemID);
                        }
                        catch (Exception ex)
                        {
                            JOptionPane.showMessageDialog(dialog, "Error adding new item: " + ex.getMessage());
                        }
                    }

                    loadItemsManager(model);
                    populateProductComboBox(productComboBox);
                    dialog.dispose();
                }
                catch (NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter valid numbers for ID, Price, Calories, and Sales");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.dispose();
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Creates the dialog for selecting which inventory items are associate with newly added items
     * @author jeremy tran
     * @param newItemID the id of the item that was just added to the database
     * @throws SQLException if there is a problem running the SQL query
     */
    private void itemInventoryJunctionDialog(int newItemID)
    {
        JDialog dialog = new JDialog(this, "Inventory Items");
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        DefaultTableModel itemInventoryModel = new DefaultTableModel(new String[]{"Inventory ID", "Name", "Select"}, 0)
        {
            @Override
            public Class<?> getColumnClass(int columnIndex)
            {
                if (columnIndex == 2)
                {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        // Load inventory data
        loadItemInventoryModel(itemInventoryModel);

        JTable itemInventoryConnectionTable = new JTable(itemInventoryModel);

        JButton addInventoryButton = new JButton("Add Inventory");
        addInventoryButton.addActionListener(e ->
        {
            JTextField invAddId = new JTextField();
            JTextField invAddName = new JTextField();
            JTextField invAddQty = new JTextField();
            Object[] addMsg = {"ID: ", invAddId, "Inventory Name:", invAddName, "Quantity:", invAddQty};
            int addOption = JOptionPane.showConfirmDialog(null, addMsg, "Add New Inventory", JOptionPane.OK_CANCEL_OPTION);
            if(addOption == JOptionPane.OK_OPTION) {
                int invAddIdInt = Integer.parseInt(invAddId.getText());
                String invAddNameText = invAddName.getText();
                int invAddQtyInt = Integer.parseInt(invAddQty.getText());
                addInventoryItem(invAddIdInt, invAddNameText, invAddQtyInt);
                populateInventoryTable(inventoryTableModel);
                loadItemInventoryModel(itemInventoryModel);
            }
        });

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener( e ->
        {
            for (int row = 0; row < itemInventoryConnectionTable.getRowCount(); row++)
            {
                boolean isSelected = (boolean) itemInventoryModel.getValueAt(row, 2);
                if (isSelected)
                {
                    int inventoryID = (Integer) itemInventoryModel.getValueAt(row, 0);

                    String insertItemInventoryJunction = "INSERT INTO itemInventoryJunction (itemID, inventoryID) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertItemInventoryJunction))
                    {
                        pstmt.setInt(1, newItemID);
                        pstmt.setInt(2, inventoryID);
                        pstmt.executeUpdate();
                    }
                    catch (SQLException ex)
                    {
                        JOptionPane.showMessageDialog(this, "Error inserting into item/inventory junction table: " + ex.getMessage());
                    }
                }
            }
            dialog.dispose();
        });

        buttonPanel.add(addInventoryButton);
        buttonPanel.add(submitButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.add(new JScrollPane(itemInventoryConnectionTable));

    }

    /**
     * Loads the table model for the inventory selection table after adding a new item
     * @author jeremy tran
     * @param itemInventoryModel table model of the inventory selection table
     * @throws SQLException if there is a problem running the SQL query
     */
    private void loadItemInventoryModel(DefaultTableModel itemInventoryModel)
    {
        itemInventoryModel.setRowCount(0);

        String getInventory = "SELECT id, name FROM inventory";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(getInventory))
        {
            while(rs.next())
            {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                boolean select = false;

                itemInventoryModel.addRow(new Object[]{id, name, select});
            }
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(this, "Error reading inventory: " + ex.getMessage());
        }
    }

    /**
     * Check if the itemID already exists or not
     * @author jeremy tran
     * @param itemID id of the current item
     * @return true if the item exists and false if the item does not exist
     * @throws SQLException if there is a problem running the SQL query
     */
    private boolean checkItemID(int itemID)
    {
        boolean exists = false;
        try
        {
            Statement stmt = conn.createStatement();
            String sql = "SELECT EXISTS(SELECT 1 FROM Item WHERE id = " + itemID + ")";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next())
            {
                exists = rs.getBoolean(1);
            }

            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Error checking item existence: " + e.getMessage());
        }
        return exists;
    }

    private void xReport() {
        if (conn == null) {
            return;
        }
    
        String sql =
        "SELECT " +
        "   EXTRACT(HOUR FROM o.timestamp) AS hour, " +
        "   COUNT(DISTINCT o.id) AS total_orders, " +
        "   COALESCE(SUM(CASE WHEN o.totalprice > 0 THEN o.totalprice ELSE 0 END), 0) AS total_sales, " +
        "   COALESCE(SUM(CASE WHEN o.totalprice < 0 THEN -o.totalprice ELSE 0 END), 0) AS total_returns, " +
        "   COUNT(oj.itemid) AS total_items, " +
        "   COUNT(CASE WHEN o.payment = 'cash' THEN 1 END) AS cash_payments, " +
        "   COUNT(CASE WHEN o.payment = 'card' THEN 1 END) AS card_payments " +
        "FROM orders o " +
        "LEFT JOIN ordersitemjunction oj ON o.id = oj.orderid " +
        "WHERE DATE(o.timestamp) = CURRENT_DATE " +
        "  AND o.is_closed = FALSE " + 
        "GROUP BY hour " +
        "ORDER BY hour;";
    
        String[] columns = {"Hour", "Total Orders", "Sales", "Returns", "Total Items", "Cash", "Card"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
    
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            ResultSet rst = st.executeQuery();
            while (rst.next()) {
                int hour = rst.getInt("hour");
                int totalOrders = rst.getInt("total_orders");
                int totalSales = rst.getInt("total_sales");
                int totalReturns = rst.getInt("total_returns");
                int totalItems = rst.getInt("total_items");
                int cash = rst.getInt("cash_payments");
                int card = rst.getInt("card_payments");
                model.addRow(new Object[]{hour, totalOrders, totalSales, totalReturns, totalItems, cash, card});
            }
            rst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "X-report failed: " + e.getMessage());
            return;
        }
    
        JDialog dialog = new JDialog(this, "X-Report: Hourly Sales Summary");
        dialog.setSize(800, 400);
        JTable table = new JTable(model);
        dialog.add(new JScrollPane(table));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private int getItemIdByName(String itemName) throws SQLException
    {
        String sql = "SELECT id FROM item WHERE name = ? LIMIT 1";
        try (PreparedStatement epicStatement = conn.prepareStatement(sql))
        {
            epicStatement.setString(1, itemName);
            try (ResultSet rst = epicStatement.executeQuery())
            {
                if (rst.next())
                {
                    return rst.getInt("id");
                }
                else
                {
                    return -1; //error
                }
            }
        }
    }

    private void showZReportDialog() {
        if (conn == null) return;
    
        double totalSales = 0.0;
        double totalCash = 0.0;
        double totalCard = 0.0;
        int cashTransactions = 0;
        int cardTransactions = 0;
        int totalItemsSold = 0;
    
        String sql = "SELECT " +
                     "COALESCE(SUM(o.totalprice), 0) AS total_sales, " +
                     "COALESCE(SUM(CASE WHEN o.payment = 'cash' THEN o.totalprice ELSE 0 END), 0) AS cash_total, " +
                     "COALESCE(SUM(CASE WHEN o.payment = 'card' THEN o.totalprice ELSE 0 END), 0) AS card_total, " +
                     "COUNT(CASE WHEN o.payment = 'cash' THEN 1 END) AS cash_count, " +
                     "COUNT(CASE WHEN o.payment = 'card' THEN 1 END) AS card_count, " +
                     "COUNT(oj.itemid) AS total_items " +
                     "FROM orders o " +
                     "LEFT JOIN ordersitemjunction oj ON o.id = oj.orderid " +
                     "WHERE DATE(o.timestamp) = CURRENT_DATE " +
                     "  AND o.is_closed = false;";
    
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalSales = rs.getDouble("total_sales");
                totalCash = rs.getDouble("cash_total");
                totalCard = rs.getDouble("card_total");
                cashTransactions = rs.getInt("cash_count");
                cardTransactions = rs.getInt("card_count");
                totalItemsSold = rs.getInt("total_items");
            }
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Z-Report Error: " + e.getMessage());
            return;
        }
    
        JDialog zDialog = new JDialog(this, "Z-Report - End of Day", true);
        zDialog.setSize(450, 350);
        zDialog.setLayout(new BorderLayout());
    
        String reportText = String.format(
            "End of Day Summary:\n\n" +
            "Total Sales: $%.2f\n" +
            "Total Items Sold: %d\n\n" +
            "Cash Transactions: %d ($%.2f)\n" +
            "Card Transactions: %d ($%.2f)\n\n" +
            "This will close the day and reset counters!",
            totalSales, totalItemsSold,
            cashTransactions, totalCash,
            cardTransactions, totalCard
        );
    
        JTextArea reportArea = new JTextArea(reportText);
        reportArea.setEditable(false);
        reportArea.setMargin(new Insets(10, 10, 10, 10));
        zDialog.add(reportArea, BorderLayout.CENTER);
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Close Day");
        JButton cancelButton = new JButton("Cancel");
    
        confirmButton.addActionListener(e -> {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                    "UPDATE orders SET is_closed = true " +
                    "WHERE DATE(timestamp) = CURRENT_DATE;"
                );
                
                stmt.executeUpdate("UPDATE item SET sales = 0;");
                
                JOptionPane.showMessageDialog(zDialog, "Day closed successfully!");
                zDialog.dispose();
                
                dispose();
                showLoginPage();
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(zDialog, "Error closing day: " + ex.getMessage());
            }
        });
    
        cancelButton.addActionListener(e -> zDialog.dispose());
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        zDialog.add(buttonPanel, BorderLayout.SOUTH);
    
        zDialog.setLocationRelativeTo(this);
        zDialog.setVisible(true);
    }

    /**
     * Adds order to orders table and orders/item junction table in database
     * @author michael ades, jeremy tran
     * @param paymentMethod payment method of customer
     * @throws SQLException if there is a problem running the SQL query
     */
    private void submitOrder(String paymentMethod)
    {
        String orderText = orderArea.getText().trim();
        if (orderText.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "no order to submit!!!!");
            return;
        }
        else
        {
            String[] lines = orderText.split("\\n");
            double totalPrice = 0.0;
            StringBuilder orderItems = new StringBuilder();
            java.util.List<String> itemNames = new ArrayList<>(); //item names

            for (String line : lines)
            {
                String[] parts = line.split(" - \\$");
                if (parts.length == 2)
                {
                    String itemName = parts[0].trim();
                    double price = 0.0;
                    try
                    {
                        price = Double.parseDouble(parts[1].trim());
                    } catch (NumberFormatException numberFormatIssue)
                    {
                        continue;
                    }
                    totalPrice += price;
                    if (orderItems.length() > 0)
                    {
                        orderItems.append(", ");
                    }
                    orderItems.append(itemName);

                    //get item id from itemName in item table
                    int itemId = getItemId(itemName);

                    //check if any inventory needed for the item has a qty of zero
                    if (hasZeroStock(itemId))
                    {
                        JOptionPane.showMessageDialog(this, "Order cannot be created! " + itemName + " has an inventory with 0 stock");
                        orderArea.setText("");
                        return;
                    }

                    //reduce stock from inventory
                    reduceStock(itemId);

                    populateInventoryTable(inventoryTableModel);

                    //store item in name in list for junc ins
                    itemNames.add(itemName);

                }
            }

            if (itemNames.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "no valid items in order sad");
                return;
            }
            int newOrderId = -1;
            try
            {
                String sql = "INSERT INTO Orders (name, totalprice, timestamp, payment) VALUES (?, ?, CURRENT_TIMESTAMP, ?) RETURNING id";
                PreparedStatement pStatement = conn.prepareStatement(sql);
                pStatement.setString(1, orderItems.toString());
                pStatement.setDouble(2, totalPrice);
                pStatement.setString(3, paymentMethod);
                try (ResultSet rs = pStatement.executeQuery())
                {
                    if (rs.next())
                    {
                        newOrderId = rs.getInt("id");
                    }
                }
            }
            catch (SQLException ex)
            {
                JOptionPane.showMessageDialog(this, "error insert order sad: " + ex.getMessage());
                return;
            }
            catch(Exception e1)
            {
                JOptionPane.showMessageDialog(this, "submitting order failed sad " + e1.getMessage());
            }
            if (newOrderId == -1)
            {
                JOptionPane.showMessageDialog(this, "fail to get new order id sad");
                return;
            }
            for (String itemName : itemNames)
            {
                try
                {
                    int itemId = getItemIdByName(itemName);
                    if (itemId == -1)
                    {
                        JOptionPane.showMessageDialog(this, "no item found with name: " + itemName);
                        continue;
                    }

                    String junctionSql =
                            "INSERT INTO ordersitemjunction (orderid, itemid) " +
                                    "VALUES (?, ?)";
                    try (PreparedStatement junctionStmt = conn.prepareStatement(junctionSql))
                    {
                        junctionStmt.setInt(1, newOrderId);
                        junctionStmt.setInt(2, itemId);
                        junctionStmt.executeUpdate();

                        //update sales
                        String updateSalesSql = "UPDATE item SET sales = sales + 1 WHERE id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSalesSql))
                        {
                            updateStmt.setInt(1, itemId);
                            updateStmt.executeUpdate();
                        }
                    }
                }
                catch (SQLException ex)
                {
                    JOptionPane.showMessageDialog(this, "error insert item: " + itemName
                            + ": into ordersitemjunction: " + ex.getMessage());
                }
            }
            JOptionPane.showMessageDialog(this,
                    "order submitted!!!!!\nitems: " + orderItems.toString() + "\total price: $" + totalPrice + "\nPayment method: " + paymentMethod);
            orderArea.setText("");
        }
    }

    /**
     * Handles action events triggered through user interaction
     * @author michael ades, jason agnew, casey ear, jeremy tran
     * @param e Action Event object containing event details
     * @throws SQLException if there is a problem running the SQL query
     */
    //action listener
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        switch (cmd)
        {
            case "Close":
                closeConnection();
                dispose();
                System.exit(ABORT);
                break;
            case "Login":
                try {
                    Statement stmt = conn.createStatement();
                    String sqlStatement = String.format("SELECT * FROM employee WHERE name='%s'", userIdField.getText());
                    ResultSet result = stmt.executeQuery(sqlStatement);

                    if (result.next()) {
                        String locPassword = result.getString("password");
                        boolean isManager = result.getBoolean("manager");

                        String inputPassword = new String(passwordField.getPassword());
                        if (locPassword.equals(inputPassword)){
                            showMainPage(isManager);
                        } else{
                            JOptionPane.showMessageDialog(null,"Invalid password.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error accessing Database.");
                }
                break;
            case "Search":
                String query = searchField.getText().trim();
                if (!query.isEmpty())
                {
                    searchMenuItemsForCashier(query);
                } else
                {
                    loadAllMenuItemsForCashier();
                }
                break;
            case "Pay with Cash":
                String cashPayment = "cash";
                submitOrder(cashPayment);
                break;
            case "Pay with Card":
                String cardPayment = "card";
                submitOrder(cardPayment);
                break;
            case "Logout":
                dispose();
                showLoginPage();
                break;
            case "updateDateTime":
                updateDateTime();
                break;
            case "Add Inventory":
                JTextField invAddId = new JTextField();
                JTextField invAddName = new JTextField();
                JTextField invAddQty = new JTextField();
                Object[] addMsg = {"ID: ", invAddId, "Inventory Name:", invAddName, "Quantity:", invAddQty};
                int addOption = JOptionPane.showConfirmDialog(null, addMsg, "Add New Inventory", JOptionPane.OK_CANCEL_OPTION);
                if(addOption == JOptionPane.OK_OPTION) {
                    int invAddIdInt = Integer.parseInt(invAddId.getText());
                    String invAddNameText = invAddName.getText();
                    int invAddQtyInt = Integer.parseInt(invAddQty.getText());
                    addInventoryItem(invAddIdInt, invAddNameText, invAddQtyInt);
                    populateInventoryTable(inventoryTableModel);
                }
                break;
            case "Remove Inventory":
                JTextField invRemoveId = new JTextField();
                JTextField invRemoveName = new JTextField();
                Object[] removeMsg = {"ID: ", invRemoveId, "Inventory Name:", invRemoveName};
                int removeOption =  JOptionPane.showConfirmDialog(null, removeMsg, "Remove Inventory", JOptionPane.OK_CANCEL_OPTION);
                if(removeOption == JOptionPane.OK_OPTION) {
                    int invRemoveIdInt = Integer.parseInt(invRemoveId.getText());
                    String invRemoveNameText = invRemoveName.getText();
                    removeInventoryItem(invRemoveIdInt, invRemoveNameText);
                    populateInventoryTable(inventoryTableModel);
                }
                break;
            case "Generate Inventory Usage and Sales Reports":
                String selectedPeriod = (String) salesReportComboBox.getSelectedItem();
                generateSalesReport(selectedPeriod);
                generateInventoryUsageReport();
                break;
            case "X-Report":
                xReport();
                break;
            default:
                break;
        }
    }
    //connection to database
    private static void connectToDatabase()
    {
        String databaseName = "team_11_db";
        String databaseUser = "team_11";
        String databasePassword = "bayleef93";
        String url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", databaseName);

        try
        {
            conn = DriverManager.getConnection(url, databaseUser, databasePassword);
            // JOptionPane.showMessageDialog(null, "Opened database successfully");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            System.exit(0);
        }
    }
    //close connection
    private void closeConnection()
    {
        try
        {
            if (conn != null)
            {
                conn.close();
                JOptionPane.showMessageDialog(this, "Connection Closed.");
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Connection NOT Closed: " + e.getMessage());
        }
    }
}