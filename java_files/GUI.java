import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

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
    }
    private void loadAllMenuItemsForCashier() 
    {

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
            default:
                break;
          }
    }
    //connection to database
    private static void connectToDatabase() 
    {
      String databaseName = "team_11_db";
      String databaseUser = "team_11";
      String databasePassword = ""; 
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
