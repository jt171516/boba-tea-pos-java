import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GUI extends JFrame implements ActionListener {
    static JFrame f;

    private static Connection conn = null;
    //Tabbed Pane for manager menu
    private JTabbedPane tabbedPane;

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

    //MANAGE EMPLOYEES PANEL
    private JPanel employeesPanel;
    private JTable employeesTable;
    private DefaultTableModel employeesTableModel;

    // For adding a new employee
    private JTextField empNameField;
    private JCheckBox managerCheckBox;
    private JButton addEmployeeButton;
    private JButton deleteEmployeeButton;

    public static void main(String[] args) 
    {
      connectToDatabase();
      SwingUtilities.invokeLater(() -> 
      {
          GUI app = new GUI();
          app.setVisible(true);
      });
  }

    public GUI()
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

      //manager panel
      buildManagerPanel();
      tabbedPane.addTab("Manager", managerPanel);

      //employee management tab
      buildEmployeeManagementPanel();
      tabbedPane.addTab("Employees", employeesPanel);
      //exit button
      closeButton = new JButton("Close");
      closeButton.addActionListener(this);
      add(closeButton, BorderLayout.SOUTH);
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
        submitOrderButton = new JButton("Submit Order");
        submitOrderButton.addActionListener(this);

        // cashier pannel
        cashierPanel.add(topPanel, BorderLayout.NORTH);
        cashierPanel.add(centerPanel, BorderLayout.CENTER);
        cashierPanel.add(submitOrderButton, BorderLayout.SOUTH);

        // load menu items from DB
        loadAllMenuItemsForCashier();
        
    }
    private void buildManagerPanel()  
    {
      managerPanel = new JPanel(new BorderLayout(10, 10));
      managerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

      JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JLabel revenueLabel = new JLabel("Revenue:");
      
      JComboBox<String> productComboBox = new JComboBox<>(new String[] {"All Products", "Item 1", "Item 2"});
      
      JComboBox<String> timeRangeComboBox = new JComboBox<>(new String[] {"1 Week", "1 Month", "3 Months"});
      
      topPanel.add(revenueLabel);
      topPanel.add(productComboBox);
      topPanel.add(timeRangeComboBox);

      // === CENTER PANEL ===
      JPanel chartPanel = new JPanel() {
          @Override
          protected void paintComponent(Graphics g) {
              super.paintComponent(g);
              // Simple placeholder line graph:
              g.drawLine(20, getHeight() - 20, getWidth() - 20, 20);
          }
      };
      chartPanel.setPreferredSize(new Dimension(800, 200));
      chartPanel.setBorder(BorderFactory.createTitledBorder("Revenue Chart"));

      // We'll put Inventory on the left, Orders on the right.
      JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));

      // ----- Left :Inventory -----
      // Example table data
      String[] inventoryColumns = {"Product", "Stock", "Sales", "Status"};
      DefaultTableModel inventoryTableModel = new DefaultTableModel(inventoryColumns, 0);

      JTable inventoryTable = new JTable(inventoryTableModel);
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
        addButton.addActionListener(evt -> {
            JTextField itemId = new JTextField();
            JTextField itemNameField = new JTextField();
            JTextField qtyField = new JTextField();
            Object[] message = {"ID: ", itemId, "Inventory Name:", itemNameField, "Quantity:", qtyField};
            int option = JOptionPane.showConfirmDialog(null, message, "Add New Inventory", JOptionPane.OK_CANCEL_OPTION);
            if(option == JOptionPane.OK_OPTION) {
                int itemIdInt = Integer.parseInt(itemId.getText());
                String itemName = itemNameField.getText();
                int qty = Integer.parseInt(qtyField.getText());
                addInventoryItem(itemIdInt, itemName, qty);
                populateInventoryTable(inventoryTableModel);
            }
        });
        removeButton.addActionListener(evt -> {
            JTextField itemId = new JTextField();
            JTextField itemNameField = new JTextField();
            Object[] message = {"ID: ", itemId, "Inventory Name:", itemNameField};
            int option =  JOptionPane.showConfirmDialog(null, message, "Remove Item", JOptionPane.OK_CANCEL_OPTION);
            if(option == JOptionPane.OK_OPTION) {
                int itemIdInt = Integer.parseInt(itemId.getText());
                String itemName = itemNameField.getText();
                removeInventoryItem(itemIdInt, itemName);
                populateInventoryTable(inventoryTableModel);
            }
        });

      // ----- Right: Orders -----
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
      JComboBox<String> weekComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
      JButton salesReportButton = new JButton("Show Weekly Sales");

      //add event listener to fetch sales data for the selected week
      salesReportButton.addActionListener(evt -> {
          int selectedWeek = Integer.parseInt((String) weekComboBox.getSelectedItem());
          weeklySalesReport(selectedWeek);
      });

      topPanel.add(new JLabel("Select Week:"));
      topPanel.add(weekComboBox);
      topPanel.add(salesReportButton);
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
    
        // Manager checkbox
        gbc.gridx = 2;
        managerCheckBox = new JCheckBox("Manager");
        addPanel.add(managerCheckBox, gbc);
    
        // Add Employee button
        gbc.gridx = 3;
        addEmployeeButton = new JButton("Add Employee");
        addEmployeeButton.addActionListener(e -> addEmployee());
        addPanel.add(addEmployeeButton, gbc);
    
        // Table setup remains the same
        String[] columns = { "ID", "Name", "Manager" };
        employeesTableModel = new DefaultTableModel(columns, 0);
        employeesTable = new JTable(employeesTableModel);
        JScrollPane tableScroll = new JScrollPane(employeesTable);
    
        // Delete button setup remains the same
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
    private void loadAllEmployees() {
        // Clear old data
        employeesTableModel.setRowCount(0);
    
        String sql = "SELECT id, name, manager FROM employee"; 
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                boolean isManager = rs.getBoolean("manager");
                
                // Convert boolean to string or keep it boolean
                employeesTableModel.addRow(new Object[]{id, name, isManager});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employees: " + e.getMessage());
        }
    }
    private void addEmployee() {
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
        String sql = "INSERT INTO employee (id, name, manager) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nextId);
            pstmt.setString(2, name);
            pstmt.setBoolean(3, isManager);
            pstmt.executeUpdate();
    
            // 3) refresh
            loadAllEmployees();
    
            // 4) clear UI
            empNameField.setText("");
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
      try(PreparedStatement stmt = conn.prepareStatement(sql)){
        ResultSet rs = stmt.executeQuery();

        while(rs.next()){
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

    private JPanel salesReportPanel = null;
    private void weeklySalesReport(int week)
    {
        if (conn == null)
        {
            return;
        }

        //sql query to fetch the weekly order count
        String sql = "SELECT orderCount FROM (" +
                "SELECT COUNT(id) AS orderCount, EXTRACT(WEEK FROM timestamp) AS week " +
                "FROM orders GROUP BY week) AS ordersInWeek " +
                "WHERE week = ?";

        //add table header
        DefaultTableModel model = new DefaultTableModel(new String[]{"Orders in Week " + week}, 0);

        try (PreparedStatement weeklyStmt = conn.prepareStatement(sql))
        {
            weeklyStmt.setInt(1, week);
            ResultSet result = weeklyStmt.executeQuery();

            while (result.next())
            {
                model.addRow(new Object[]{result.getInt("orderCount")});
            }
            result.close();
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
        salesReportPanel.setBorder(BorderFactory.createTitledBorder("Weekly Sales Report"));
        salesReportPanel.add(salesScrollPane, BorderLayout.CENTER);
        salesReportPanel.add(buttonPanel, BorderLayout.NORTH);

        //add the updated sales report panel to the right side of the manager panel
        managerPanel.add(salesReportPanel, BorderLayout.EAST);

        //refresh the panel properly
        managerPanel.revalidate();
        managerPanel.repaint();
    }

    private void addInventoryItem(int id, String name, int qty)
    {
        if (conn == null) {
            return;
        }
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

    private void removeInventoryItem(int itemId, String name)
    {
        if (conn == null)
        {
            return;
        }
        String sql = "DELETE FROM inventory WHERE id = ? AND name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, itemId);
            stmt.setString(2, name);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error executing query: " + e.getMessage());
            return;
        }
    }

    private void removeItem(int id)
    {
        if (conn == null)
        {
            return;
        }
        String sql = "DELETE FROM item WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error executing query: " + e.getMessage());
            return;
        }
    }

    private void populateInventoryTable(DefaultTableModel inventoryTableModel)
    {
        if (conn == null)
        {
            return;
        }

        String sql = "SELECT name, qty FROM inventory";

        try (PreparedStatement invStmt = conn.prepareStatement(sql))
        {
            ResultSet result = invStmt.executeQuery();

            while (result.next())
            {
                String invName = result.getString("name");
                int stock = result.getInt("qty");
                String status = (stock < 10) ? "Refill Recommended" : "";

                inventoryTableModel.addRow(new Object[]{invName, stock, "-", status});
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

    private void loadItemsManager(DefaultTableModel model) {
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

    private void itemManagement(DefaultTableModel model) {
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

                    loadItemsManager(model);
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
            case "Submit Order":
            String orderText = orderArea.getText().trim();
            if (orderText.isEmpty()) 
            {
                JOptionPane.showMessageDialog(this, "no order to submit!!!!");
            } 
            else 
            {
                String[] lines = orderText.split("\\n");
                double totalPrice = 0.0;
                StringBuilder orderItems = new StringBuilder();

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
                  }
                }
              try
              {
                String sql = "INSERT INTO Orders (name, totalprice, timestamp) VALUES (?, ?, CURRENT_TIMESTAMP)";
                PreparedStatement pStatement = conn.prepareStatement(sql);
                pStatement.setString(1, orderItems.toString());
                pStatement.setDouble(2, totalPrice);
                pStatement.executeUpdate();
                pStatement.close();
                JOptionPane.showMessageDialog(this, "order submitted!\n" + "items: " + orderItems.toString() + "\ntotal price: $" + totalPrice);
                orderArea.setText("");
              }
              catch(Exception e1)
              {
                JOptionPane.showMessageDialog(this, "submitting order failed sad " + e1.getMessage());
              }
            }
                
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
          JOptionPane.showMessageDialog(null, "Opened database successfully");
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
