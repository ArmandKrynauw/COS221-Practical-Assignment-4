/*
* App class is used to implement the user interface of the Java App.
*
* @author Armand Krynauw
*/

package za.ac.up.cs.cos221;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;

public class App {
    private static JFrame frame = new JFrame();
    private static JTabbedPane tabbedPane = new JTabbedPane();
    private static JPanel staff = new JPanel();
    private static JPanel films = new JPanel();
    private static JPanel inventory = new JPanel();
    private static JPanel clients = new JPanel();

    public static void main(String[] args) throws Exception {
        createTabbedPane();
        setStaffPane();
        setInventoryPane();
    }

    private static void createTabbedPane() {
        frame.setSize(1000, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(tabbedPane);

        tabbedPane.add("Staff", staff);
        tabbedPane.add("Films", films);
        tabbedPane.add("Inventory", inventory);
        tabbedPane.add("Clients", clients);

        tabbedPane.setFont(new Font("SanSerif", Font.CENTER_BASELINE, 15));
    }

    private static void setStaffPane() {
        String[] list = { "First Name", "Last Name", "Address", "Address 2", "District", "City", "Store", "Active" };
        Vector<String> headings = new Vector<String>(Arrays.asList(list));

        Database database = new Database();
        Vector<Vector<String>> data = database.getStaffData();

        if (data == null) {
            setErrorMessage(staff);
            return;
        }

        JTable table = new JTable(data, headings);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        staff.setLayout(new GridLayout(1, 1));
        staff.add(scrollPane);

        // Refresh pane to load new content (otherwise content will load on resize)
        staff.revalidate();
        staff.repaint();
    }

    private static void setInventoryPane() {
        String[] list = { "Store", "Genre", "Number of Movies" };
        Vector<String> headings = new Vector<String>(Arrays.asList(list));

        Database database = new Database();
        Vector<Vector<String>> data = database.getInventoryData();

        if (data == null) {
            setErrorMessage(inventory);
            return;
        }

        JTable table = new JTable(data, headings);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        inventory.setLayout(new GridLayout(1, 1));
        inventory.add(scrollPane);
    }

    /***************************************************************************************************************************
     * HELPER FUNCTIONS
     ***************************************************************************************************************************/

    private static void styleTable(JTable table) {
        styleTableHeader(table);
        centreTableCellValues(table);

        table.setGridColor(Color.BLACK);

        // Disable editing of cell values (also disables cell selection unfortunately)
        table.setEnabled(false);
    }

    private static void centreTableCellValues(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private static void styleTableHeader(JTable table) {
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    private static void setErrorMessage(JPanel panel) {
        JLabel label = new JLabel("Data could not be retrieved");
        label.setFont(new Font("Verdana", 1, 16));
        panel.add(label);
    }
}
