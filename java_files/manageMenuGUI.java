import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class manageMenuGUI extends JPanel {
    DefaultTableModel model = new DefaultTableModel();
    JTable table = new JTable(model);

    public manageMenuGUI() {
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Price");
        model.addColumn("Calories");
        model.addColumn("Sales");

        table.setPreferredScrollableViewportSize(new Dimension(500, 300));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        Connection conn = null;
        String database_name = "team_11_db";
        String database_user = "team_11";
        String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);
        dbSetup myCredentials = new dbSetup();
        try {
            conn = DriverManager.getConnection(database_url, database_user, dbSetup.pswd);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        JOptionPane.showMessageDialog(null,"Opened database successfully");

        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT * FROM Item";
            ResultSet result = stmt.executeQuery(sqlStatement);

            while (result.next()) {
                // Get data from the current row
                Integer id = result.getInt("id");
                String name = result.getString("name");
                Integer price = result.getInt("price");
                Integer calories = result.getInt("calories");
                Integer sales = result.getInt("sales");

                model.addRow(new Object[]{id, name, price, calories, sales});
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Menu Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new manageMenuGUI());
        frame.pack();
        frame.setVisible(true);
    }
}

