/*
* App class is used to implement the user interface of the Java App.
*
* @author Armand Krynauw
*/

package za.ac.up.cs.cos221;

import javax.swing.*;
import java.awt.*;

public class App {
    private static JFrame frame = new JFrame();
    private static JTabbedPane tabbedPane = new JTabbedPane();
    private static JPanel staff = new JPanel();
    private static JPanel films = new JPanel();
    private static JPanel inventory = new JPanel();
    private static JPanel clients = new JPanel();

    public static void main(String[] args) throws Exception {
        createTabbedPane();
    }

    private static void createTabbedPane() {
        frame.setSize(600, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(tabbedPane);

        tabbedPane.add("Staff", staff);
        tabbedPane.add("Films", films);
        tabbedPane.add("Inventory", inventory);
        tabbedPane.add("Clients", clients);

        Font font = new Font("SanSerif", Font.CENTER_BASELINE, 15);
        tabbedPane.setFont(font);
    }
}
