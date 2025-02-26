import java.sql.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI extends JFrame implements ActionListener {
    static JFrame f;

    public static void main(String[] args)
    {
      //Building the connection
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

      String name = "";
      try{
        //create a statement object
        Statement stmt = conn.createStatement();
        //create a SQL statement
        String sqlStatement = "SELECT * FROM Item";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sqlStatement);
        while (result.next()) {
          //      this command gets the data from the "name" attribute
          name += result.getString("name")+"\n";
        }
      } catch (Exception e){
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
      // create a new frame
      f = new JFrame("DB GUI");

      // create a object
      GUI s = new GUI();

      // create a panel
      JPanel p = new JPanel();

      JButton b = new JButton("Close");

      // add actionlistener to button
      b.addActionListener(s);

      JTextArea textArea = new JTextArea(name);

      p.add(textArea);

      // add button to panel
      p.add(b);

      // add panel to frame
      f.add(p);

      // set the size of frame
      f.setSize(400, 400);

      f.setVisible(true);

      //closing the connection
      try {
        conn.close();
        JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception e) {
        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
    }

    // if button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
        if (s.equals("Close")) {
            f.dispose();
        }
    }
}
