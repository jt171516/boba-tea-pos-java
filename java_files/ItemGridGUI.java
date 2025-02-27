import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import javax.swing.*;
public class ItemGridGUI {
    static JFrame itemFrame;
    public static void main(String[] args) {
        itemFrame = new JFrame("Item Grid");
        JPanel itemPanel = new JPanel();
        itemPanel = new JPanel(new GridLayout(0,3,10,10));
        Connection conn = null;
        String database_name = "team_11_db";
        String database_url = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + database_name;
        login myCredentials = new login();
        try {
            conn = DriverManager.getConnection(database_url, login.user, login.pswd);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        JOptionPane.showMessageDialog(null,"Opened database successfully");

        try{
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT * FROM Item;";
            JButton itemButton1 = new JButton("<html><center>Honey Milk<br><center>Tea<br><center>(black/green/oolong)<br><center>Cal: 476  Price: $6.10</html>");
            JButton itemButton2 = new JButton("Coffee");
            JButton itemButton3 = new JButton("Tea");
            JButton itemButton4 = new JButton("Cappuccino");
            JButton itemButton5 = new JButton("Espresso");
            JButton itemButton6 = new JButton("Latte");
            JButton itemButton7 = new JButton("Mocha");
            JButton itemButton8 = new JButton("Americano");
            JButton itemButton9 = new JButton("Black Eye");
            itemPanel.add(itemButton1);
            itemPanel.add(itemButton2);
            itemPanel.add(itemButton3);
            itemPanel.add(itemButton4);
            itemPanel.add(itemButton5);
            itemPanel.add(itemButton6);
            itemPanel.add(itemButton7);
            itemPanel.add(itemButton8);
            itemPanel.add(itemButton9);
            //turn queries into graphical table, not database output
            itemFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            itemFrame.setSize(400,400);
            itemFrame.getContentPane().add(itemPanel);
            itemFrame.setVisible(true);
        }
        catch(Exception e){

        }
        itemFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        itemFrame.setSize(400,400);
        itemFrame.setVisible(true);
    }
}



