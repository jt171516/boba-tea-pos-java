import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        menuItemsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
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
      Object[][] inventoryData = {
          {"Item 1", 100, 1400, "Refill Recommended"},
          {"Item 2", 200, 1000, "Refill Recommended"},
          {"Item 3", 200, 700,  "Refill Recommended"},
          {"Item 4", 500, 200,  ""}
      };

      JTable inventoryTable = new JTable(inventoryData, inventoryColumns);
      JScrollPane inventoryScrollPane = new JScrollPane(inventoryTable);

      // We can wrap the table in a panel with a title
      JPanel inventoryPanel = new JPanel(new BorderLayout());
      inventoryPanel.setBorder(BorderFactory.createTitledBorder("Inventory"));
      inventoryPanel.add(inventoryScrollPane, BorderLayout.CENTER);

      // ----- Right: Orders -----
      String[] ordersColumns = {"Product", "Order #", "Quantity", "Arrival"};
      Object[][] ordersData = {
          {"Item 5", "#11111", 1000, "2/10/25"},
          {"Item 6", "#11111", 1000, "2/10/25"},
          {"Item 7", "#22222", 500,  "2/15/25"},
          {"Item 8", "#22222", 500,  "2/15/25"}
      };

      JTable ordersTable = new JTable(ordersData, ordersColumns);
      JScrollPane ordersScrollPane = new JScrollPane(ordersTable);

      JPanel ordersPanel = new JPanel(new BorderLayout());
      ordersPanel.setBorder(BorderFactory.createTitledBorder("Orders"));
      ordersPanel.add(ordersScrollPane, BorderLayout.CENTER);

      // Add both sub-panels to bottomPanel
      bottomPanel.add(inventoryPanel);
      bottomPanel.add(ordersPanel);

      // === ASSEMBLE EVERYTHING ===
      managerPanel.add(topPanel, BorderLayout.NORTH);
      managerPanel.add(chartPanel, BorderLayout.CENTER);
      managerPanel.add(bottomPanel, BorderLayout.SOUTH);
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
          JButton itemButton = new JButton(name + " " + price);

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
