/*
* App class is used to implement the user interface of the Java App.
*
* @author Armand Krynauw
*/

package za.ac.up.cs.cos221;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.*;

public class App {
    private static JFrame frame = new JFrame("DVD Rental Store");
    private static JTabbedPane tabbedPane = new JTabbedPane();
    private static JPanel staff = new JPanel();
    private static JPanel films = new JPanel();
    private static JPanel inventory = new JPanel();
    private static JPanel customers = new JPanel();

    public static void main(String[] args) throws Exception {
        createTabbedPane();
        setStaffPane();
        setFilmsPane();
        setInventoryPane();
        setCustomersPane();
        showFrame();
    }

    // ======================================================================================
    // Main App Components
    // ======================================================================================

    private static void showFrame() {
        frame.setSize(1600, 850);
        frame.setMinimumSize(new Dimension(200, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void createTabbedPane() {
        frame.add(tabbedPane);
        tabbedPane.add("Staff", staff);
        tabbedPane.add("Films", films);
        tabbedPane.add("Inventory", inventory);
        tabbedPane.add("Customers", customers);
        tabbedPane.setFont(new Font("SanSerif", Font.CENTER_BASELINE, 15));
    }

    private static void setStaffPane() {
        String[] list = {
                "First Name", "Last Name", "Address", "Address 2",
                "District", "City", "Store", "Active"
        };

        Vector<String> headings = new Vector<String>(Arrays.asList(list));
        Vector<Vector<String>> data = Database.getInstance().getStaffData();

        if (data == null) {
            showDatabaseErrorMessage(customers);
            return;
        }

        JTable table = new JTable(data, headings);
        styleTable(table);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        // Filter text box
        JPanel filterContainer = new JPanel(new BorderLayout());
        JLabel filterLabel = new JLabel("Filter: ");
        filterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JTextField filterField = new JTextField();
        filterContainer.add(filterLabel, BorderLayout.WEST);
        filterContainer.add(filterField, BorderLayout.CENTER);

        // Filter buttons
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new FlowLayout());
        JButton filterButton = new JButton("Filter");
        JButton removeFilterButton = new JButton("Remove Filter");
        filterButton.setPreferredSize(new Dimension(150, 40));
        removeFilterButton.setPreferredSize(new Dimension(150, 40));
        filterButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        removeFilterButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        buttonContainer.add(removeFilterButton);
        buttonContainer.add(filterButton);

        // Table panel
        JScrollPane scrollPane = new JScrollPane(table);
        staff.setLayout(new BorderLayout());
        staff.add(filterContainer, BorderLayout.PAGE_START);
        staff.add(scrollPane, BorderLayout.CENTER);
        staff.add(buttonContainer, BorderLayout.PAGE_END);

        /*--------------------------FILTER EVENT LISTENERS--------------------------*/
        removeFilterButton.addActionListener(e -> {
            filterField.setText("");
            sorter.setRowFilter(null);
        });

        filterButton.addActionListener(e -> {
            String filterText = filterField.getText();

            if (filterText.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                // (?i) means case insensitive search
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + filterText));
            }
        });
    }

    private static void setFilmsPane() {
        String[] list = {
                "Title", "Rating", "Length", "Language", "Release Year",
                "Rental Duration", "Rental Rate", "Replacement Cost"
        };

        Vector<String> headings = new Vector<String>(Arrays.asList(list));
        Vector<Vector<String>> data = Database.getInstance().getFilmData();

        if (data == null) {
            showDatabaseErrorMessage(films);
            return;
        }

        JTable table = new JTable(data, headings);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        films.setLayout(new BoxLayout(films, BoxLayout.PAGE_AXIS));
        films.add(scrollPane);

        // Create box to contain button (helps with centering)
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new FlowLayout());
        buttonContainer.setMaximumSize(new Dimension(2000, 40));

        // Create button to show pop-up for new film form
        JButton button = new JButton("Insert New Film");
        button.setPreferredSize(new Dimension(150, 35));
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.addActionListener(e -> createNewFilm());

        buttonContainer.add(button);
        films.add(buttonContainer);

        customers.revalidate();
        customers.repaint();
    }

    private static void setInventoryPane() {
        String[] list = { "Store", "Genre", "Number of Movies" };
        Vector<String> headings = new Vector<String>(Arrays.asList(list));

        Vector<Vector<String>> data = Database.getInstance().getInventoryData();

        if (data == null) {
            showDatabaseErrorMessage(inventory);
            return;
        }

        JTable table = new JTable(data, headings);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        inventory.setLayout(new GridLayout(1, 1));
        inventory.add(scrollPane);
    }

    private static void setCustomersPane() {
        String[] list = {
                "ID", "Name", "Phone", "Email", "Address", 
                "City", "Country", "Zip Code", "Active", "Store ID"
        };

        Vector<String> headings = new Vector<String>(Arrays.asList(list));
        Vector<Vector<String>> data = Database.getInstance().getCustomerData();

        if (data == null) {
            showDatabaseErrorMessage(customers);
            return;
        }

        JTable table = new JTable(data, headings);
        styleTable(table);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        // Form Buttons
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new FlowLayout());
        JButton createCustomerButton = new JButton("Insert Customer");
        JButton updateCustomerButton = new JButton("Update Customer");
        JButton deleteCustomerButton = new JButton("Delete Customer");
        createCustomerButton.setPreferredSize(new Dimension(150, 40));
        updateCustomerButton.setPreferredSize(new Dimension(150, 40));
        deleteCustomerButton.setPreferredSize(new Dimension(150, 40));
        createCustomerButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        updateCustomerButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        deleteCustomerButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        buttonContainer.add(createCustomerButton);
        buttonContainer.add(updateCustomerButton);
        buttonContainer.add(deleteCustomerButton);

        // Table panel
        JScrollPane scrollPane = new JScrollPane(table);
        customers.setLayout(new BorderLayout());
        customers.add(scrollPane, BorderLayout.CENTER);
        customers.add(buttonContainer, BorderLayout.PAGE_END);


        /*------------------------------BUTTON EVENT LISTENERS------------------------------*/
        createCustomerButton.addActionListener(e -> createNewCustomer());
        updateCustomerButton.addActionListener(e -> updateNewCustomer());
        deleteCustomerButton.addActionListener(e -> deleteNewCustomer());


        customers.revalidate();
        customers.repaint();
    }

    // ======================================================================================
    // User Input Handlers
    // ======================================================================================

    private static void createNewFilm() {
        /*--------------------------------CREATE INPUT FORM--------------------------------*/
        JFrame formFrame = new JFrame("New Film");
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Title");
        gbc.gridy = 0;
        panel.add(title, gbc);
        JTextField titleField = new JTextField(17);
        // If you want label and field next to each other
        // gbc.gridx = 2;
        // gbc.gridy = 0;
        gbc.gridy = 1;
        panel.add(titleField, gbc);

        JLabel description = new JLabel("Description");
        gbc.gridy = 2;
        panel.add(description, gbc);
        JTextField descriptionField = new JTextField(17);
        gbc.gridy = 3;
        panel.add(descriptionField, gbc);

        JLabel releaseYear = new JLabel("Release Year");
        gbc.gridy = 4;
        panel.add(releaseYear, gbc);
        JTextField releaseYearField = new JTextField(17);
        gbc.gridy = 5;
        panel.add(releaseYearField, gbc);

        JLabel language = new JLabel("Language");
        gbc.gridy = 6;
        panel.add(language, gbc);
        JTextField languageField = new JTextField(17);
        gbc.gridy = 7;
        panel.add(languageField, gbc);

        JLabel category = new JLabel("Category");
        gbc.gridy = 8;
        panel.add(category, gbc);
        JTextField categoryField = new JTextField(17);
        gbc.gridy = 9;
        panel.add(categoryField, gbc);

        JLabel rentalDuration = new JLabel("Rental Duration");
        gbc.gridy = 10;
        panel.add(rentalDuration, gbc);
        JTextField rentalDurationField = new JTextField(17);
        gbc.gridy = 11;
        panel.add(rentalDurationField, gbc);

        JLabel rentalRate = new JLabel("Rental Rate");
        gbc.gridy = 12;
        panel.add(rentalRate, gbc);
        JTextField rentalRateField = new JTextField(17);
        gbc.gridy = 13;
        panel.add(rentalRateField, gbc);

        JLabel replacementCost = new JLabel("Replacement Cost");
        gbc.gridy = 16;
        panel.add(replacementCost, gbc);
        JTextField replacementCostField = new JTextField(17);
        gbc.gridy = 17;
        panel.add(replacementCostField, gbc);

        JLabel length = new JLabel("Length");
        gbc.gridy = 14;
        panel.add(length, gbc);
        JTextField lengthField = new JTextField(17);
        gbc.gridy = 15;
        panel.add(lengthField, gbc);

        JLabel rating = new JLabel("Rating");
        gbc.gridy = 18;
        panel.add(rating, gbc);
        JTextField ratingField = new JTextField(17);
        gbc.gridy = 19;
        panel.add(ratingField, gbc);

        JLabel specialFeatures = new JLabel("Special Features");
        gbc.gridy = 20;
        panel.add(specialFeatures, gbc);
        JTextField specialFeaturesField = new JTextField(17);
        gbc.gridy = 21;
        panel.add(specialFeaturesField, gbc);

        for (Component component : panel.getComponents()) {
            if (component instanceof JLabel) {
                ((JLabel) component).setFont(new Font("SansSerif", Font.BOLD, 12));
            }
        }

        // Submit and Cancel Buttons
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new FlowLayout());
        JButton filterButton = new JButton("Add Film");
        JButton removeFilterButton = new JButton("Cancel");
        filterButton.setPreferredSize(new Dimension(100, 35));
        removeFilterButton.setPreferredSize(new Dimension(100, 35));
        filterButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        removeFilterButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        buttonContainer.add(removeFilterButton);
        buttonContainer.add(filterButton);

        // Frame Layout and Size
        formFrame.add(panel, BorderLayout.CENTER);
        formFrame.add(buttonContainer, BorderLayout.PAGE_END);

        formFrame.setPreferredSize(new Dimension(300, 600));
        buttonContainer.setPreferredSize(new Dimension(300, 60));
        panel.setPreferredSize(new Dimension(300, 540));
        formFrame.pack();
        formFrame.setLocationRelativeTo(null);
        formFrame.setVisible(true);
        formFrame.setResizable(false);

        /*-----------------------------HANDLE FORM INPUT-----------------------------*/
        removeFilterButton.addActionListener(e -> {
            formFrame.setVisible(false);
            formFrame.dispose();
        });

        filterButton.addActionListener(e -> {
            LinkedHashMap<String, String> newFilmData = new LinkedHashMap<>();
            Component[] components = panel.getComponents();
            String label;
            String fieldText;

            for (int i = 0; i < components.length - 1; i += 2) {
                label = ((JLabel) components[i]).getText();
                fieldText = ((JTextField) components[i + 1]).getText().trim();
                newFilmData.put(label, fieldText);
            }

            if (checkIfFilmIsValid(formFrame, newFilmData)) {
                formFrame.setVisible(false);
                formFrame.dispose();

                if (Database.getInstance().addNewFilm(newFilmData)) {
                    showSuccessMessage("Film added successfully");
                    films.removeAll();
                    setFilmsPane();
                } else {
                    showErrorMessage("Something went wrong while adding the film. Try again later.");
                }
            }
        });
    }

    private static void createNewCustomer() {

    }

    private static void updateNewCustomer() {

    }

    private static void deleteNewCustomer() {
        /*--------------------------------CREATE DELETE FORM--------------------------------*/
        JFrame formFrame = new JFrame("Delete Customer");
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel idLabel = new JLabel("Customer ID");
        idLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridy = 0;
        panel.add(idLabel, gbc);
        JTextField idField = new JTextField(17);
        gbc.gridy = 1;
        panel.add(idField, gbc);

        // Submit and Cancel Buttons
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new FlowLayout());
        JButton deleteButton = new JButton("Delete Customer");
        JButton cancelButton = new JButton("Cancel");
        deleteButton.setPreferredSize(new Dimension(150, 35));
        cancelButton.setPreferredSize(new Dimension(150, 35));
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        buttonContainer.add(cancelButton);
        buttonContainer.add(deleteButton);

        // Frame Layout and Size
        formFrame.add(panel, BorderLayout.CENTER);
        formFrame.add(buttonContainer, BorderLayout.PAGE_END);

        formFrame.setPreferredSize(new Dimension(350, 180));
        buttonContainer.setPreferredSize(new Dimension(350, 50));
        panel.setPreferredSize(new Dimension(350, 130));
        formFrame.pack();
        formFrame.setLocationRelativeTo(null);
        formFrame.setVisible(true);
        formFrame.setResizable(false);

        /*-----------------------------HANDLE FORM INPUT-----------------------------*/
        cancelButton.addActionListener(e -> {
            formFrame.setVisible(false);
            formFrame.dispose();
        });

        deleteButton.addActionListener(e -> {
            String customerID = idField.getText();

            if (!customerID.matches("^[0-9]+$")) {
                showErrorMessage("Invalid Customer ID!");
            } else {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this customer?", "Warning", JOptionPane.YES_NO_OPTION);
                
                if (option == 0) {
                    if (Database.getInstance().deleteCustomer(customerID)) {
                        showSuccessMessage("Customer Deleted!");
                        customers.removeAll();
                        setCustomersPane();
                    } else {
                        showErrorMessage("Could not delete customer. Make sure customer ID is correct or try again later.");
                    }
                }

                formFrame.setVisible(false);
                formFrame.dispose();
            }
        });
    }

    // ======================================================================================
    // General Data Validation Functions
    // ======================================================================================

    private static boolean checkIfEmpty(String fieldText) {
        return fieldText.equals("");
    }

    // ======================================================================================
    // New Film Data Validation Functions
    // ======================================================================================

    /*
     * Data validation is 'hard coded' here to make the app feel a bit faster
     * and to cut down on complex logic of having to display database errors to
     * the user. It was also assumed that the schema for films is unlikely to
     * change.
     */

    private static boolean checkIfFilmIsValid(JFrame frame, LinkedHashMap<String, String> newFilmData) {
        boolean valid = true;

        if (!checkIfTitleIsValid(newFilmData.get("Title")) ||
                !checkIfReleaseYearIsValid(newFilmData.get("Release Year")) ||
                !checkIfLanguageIsValid(newFilmData.get("Language")) ||
                !checkIfCategoryIsValid(newFilmData.get("Category")) ||
                !checkIfRentalDurationIsValid(newFilmData.get("Rental Duration")) ||
                !checkIfRentalRateIsValid(newFilmData.get("Rental Rate")) ||
                !checkIfReplacementCostIsValid(newFilmData.get("Replacement Cost")) ||
                !checkIfLengthIsValid(newFilmData.get("Length")) ||
                !checkIfRatingIsValid(newFilmData.get("Rating")) ||
                !checkIfSpecialFeaturesIsValid(newFilmData.get("Special Features"))) {

            valid = false;
        }

        if (!valid) {
            return valid;
        }

        return true;
    }

    private static boolean checkIfTitleIsValid(String title) {
        boolean valid = true;

        if (checkIfEmpty(title)) {
            showErrorMessage("Title is required!");
            valid = false;
        } else if (title.length() > 128) {
            showErrorMessage("Title is too long!");
            valid = false;
        }

        return valid;
    }

    private static boolean checkIfReleaseYearIsValid(String releaseYear) {
        boolean valid = true;

        if (!releaseYear.matches("^(([0-9]{4})|(^$))$")) {
            showErrorMessage("Release Year is not valid!");
            valid = false;
        }

        return valid;
    }

    private static boolean checkIfLanguageIsValid(String language) {
        boolean valid = false;

        if (checkIfEmpty(language)) {
            showErrorMessage("Language is required!");
        } else {
            String[] validLanguages = {
                    "English", "Italian", "Japanese",
                    "Mandarin", "French", "German"
            };

            for (String validLanguage : validLanguages) {
                if (language.equals(validLanguage)) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                showErrorMessage("Language is not valid!");
            }
        }

        return valid;
    }

    private static boolean checkIfCategoryIsValid(String category) {
        boolean valid = false;

        if (checkIfEmpty(category)) {
            showErrorMessage("Category is required!");
        } else {
            String[] validCategories = {
                    "Action", "Animation", "Children", "Classics", "Comedy",
                    "Documentary", "Drama", "Family", "Foreign", "Games",
                    "Horror", "Music", "New", "Sci-Fi", "Sports", "Travel"
            };

            for (String validCategory : validCategories) {
                if (category.equals(validCategory)) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                showErrorMessage("Category is not valid!");
            }
        }

        return valid;
    }

    private static boolean checkIfRentalDurationIsValid(String rentalDuration) {
        boolean valid = true;

        if (checkIfEmpty(rentalDuration)) {
            showErrorMessage("Rental Duration is required!");
            valid = false;
        } else if (!rentalDuration.matches("^([1-9][0-9]{0,2})$")) {
            showErrorMessage("Rental Duration is not valid!");
            valid = false;
        }

        return valid;
    }

    private static boolean checkIfRentalRateIsValid(String rentalRate) {
        boolean valid = true;

        if (checkIfEmpty(rentalRate)) {
            showErrorMessage("Rental Rate is required!");
            valid = false;
        } else if (!rentalRate.matches("^([1-9][0-9]{0,3}.[0-9]{2})|(0.[0-9]{2})$")) {
            showErrorMessage("Rental Rate is not valid!");
            valid = false;
        }

        return valid;
    }

    private static boolean checkIfReplacementCostIsValid(String replacementCost) {
        boolean valid = true;

        if (checkIfEmpty(replacementCost)) {
            showErrorMessage("Replacement Cost is required!");
            valid = false;
        } else if (!replacementCost.matches("^([1-9][0-9]{0,4}.[0-9]{2})|(0.[0-9]{2})$")) {
            showErrorMessage("Replacement Cost is not valid!");
            valid = false;
        }

        return valid;
    }

    private static boolean checkIfLengthIsValid(String length) {
        boolean valid = true;

        if (!length.matches("^(([1-9][0-9]{0,4})|(^$))$")) {
            showErrorMessage("Length is not valid!");
            valid = false;
        }

        return valid;
    }

    private static boolean checkIfRatingIsValid(String rating) {
        boolean valid = false;

        String[] validRatings = { "G", "PG", "PG-13", "R", "NC-17" };

        if (checkIfEmpty(rating)) {
            showErrorMessage("Rating is required!");
        } else {
            for (String validRating : validRatings) {
                if (rating.equals(validRating)) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                showErrorMessage("Rating is not valid!");
            }
        }

        return valid;
    }

    private static boolean checkIfSpecialFeaturesIsValid(String specialFeatures) {
        boolean valid = true;

        if (!checkIfEmpty(specialFeatures)) {
            String[] features = specialFeatures.split(",");
            String[] validSpecialFeatures = {
                    "Trailers", "Commentaries",
                    "Deleted Scenes", "Behind the Scenes"
            };

            valid = Arrays.asList(validSpecialFeatures).containsAll(Arrays.asList(features));

            if (!valid) {
                showErrorMessage("Special features are not valid!");
            }
        }

        return valid;
    }

    // ======================================================================================
    // General Helper Functions
    // ======================================================================================

    private static void styleTable(JTable table) {
        styleTableHeader(table);
        centreTableCellValues(table);

        table.setGridColor(Color.BLACK);

        // Disable editing of cell values (also disables cell selection unfortunately)
        table.setEnabled(false);

        // Auto-fit columns
        TableColumnModel columnModel = table.getColumnModel();
        TableModel model = table.getModel();
        int numCols = columnModel.getColumnCount();
        int numRows;
        int width1;
        int width2;

        for (int i = 0; i < numCols; i++) {
            width1 = 0;
            numRows = model.getRowCount();

            for (int j = 0; j < numRows; j++) {
                if (model.getValueAt(j, i) != null) {
                    width2 = model.getValueAt(j, i).toString().length() * 6;

                    if (width2 > width1)  {
                        width1 = width2;
                    }

                    columnModel.getColumn(i).setPreferredWidth(width1);
                }
            }
        }
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
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    private static void showErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static void showDatabaseErrorMessage(JPanel panel) {
        JLabel label = new JLabel("Data could not be retrieved");
        label.setFont(new Font("Verdana", 1, 16));
        panel.add(label);
    }

    private static void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(null, message,
        "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}