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
        orderArea.setEditable(false);
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
    private void loadAllMenuItemsForCashier() 
    {
      if (conn == null) 
      {
        return;
      }
      //remove old menu items
      menuItemsPanel.removeAll();

    }

    private void searchMenuItemsForCashier(String query)
    {
      // \set query '%{param query here}%'
      // SELECT name FROM item
      // WHERE name LIKE :'query';

      String searchQuery = '%' + query + '%';
      try(Statement stmt = conn.createStatement()){
        ResultSet rs = stmt.executeQuery("SELECT name FROM item WHERE name LIKE :'" + searchQuery + "'");

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
        stmt.close();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Searching menu items error" + e.getMessage());
      }

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
