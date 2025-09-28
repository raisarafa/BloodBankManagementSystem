import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BloodBankManagement extends JFrame {
    private JTextField nameField, bloodGroupField, ageField, contactField;
    private JTable table;
    private DefaultTableModel model;
    private Connection conn;

    public BloodBankManagement() {
// Connect to database
        connectDatabase();

// GUI setup
        setTitle("Blood Bank Management System");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

// Top panel (input fields)
        JPanel inputPanel = new JPanel(new GridLayout(2, 5));
        nameField = new JTextField();
        bloodGroupField = new JTextField();
        ageField = new JTextField();
        contactField = new JTextField();
        JButton addButton = new JButton("Add Donor");

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(new JLabel("Blood Group:"));
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(new JLabel("Contact:"));
        inputPanel.add(new JLabel(""));
        inputPanel.add(nameField);
        inputPanel.add(bloodGroupField);
        inputPanel.add(ageField);
        inputPanel.add(contactField);
        inputPanel.add(addButton);

        add(inputPanel, BorderLayout.NORTH);

// Table
        model = new DefaultTableModel(new String[]{"ID", "Name", "Blood Group", "Age", "Contact"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

// Delete button
        JButton deleteButton = new JButton("Delete Donor");
        add(deleteButton, BorderLayout.SOUTH);

// Load donors
        loadDonors();

// Button listeners
        addButton.addActionListener(e -> addDonor());
        deleteButton.addActionListener(e -> deleteDonor());
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bloodBankDB",
                    "root",
                    "" // blank password for XAMPP default
            );
            System.out.println("Connected to database!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDonors() {
        try {
            model.setRowCount(0);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM donors");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("blood_group"),
                        rs.getInt("age"),
                        rs.getString("contact")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading donors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addDonor() {
        try {
            String sql = "INSERT INTO donors (name, blood_group, age, contact) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nameField.getText());
            ps.setString(2, bloodGroupField.getText());
            ps.setInt(3, Integer.parseInt(ageField.getText()));
            ps.setString(4, contactField.getText());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Donor Added!");
            loadDonors();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding donor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteDonor() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a donor to delete.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        try {
            String sql = "DELETE FROM donors WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Donor Deleted!");
            loadDonors();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting donor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BloodBankManagement().setVisible(true));
    }
}