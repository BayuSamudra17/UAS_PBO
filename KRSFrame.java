package zuaskoneksi;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class KRSFrame extends JFrame {
    private JTextField txtIDKRS, txtIDMhs, txtIDMk, txtSemester, txtTahunAjaran;
    private JButton btnTambah, btnUbah, btnHapus, btnCari;
    private JTable table;
    private Connection conn;

    public KRSFrame() {
        setTitle("CRUD KRS");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        connectDatabase();
        loadTable();
    }

    private void initComponents() {
        txtIDKRS = new JTextField(5);
        txtIDMhs = new JTextField(5);
        txtIDMk = new JTextField(5);
        txtSemester = new JTextField(10);
        txtTahunAjaran = new JTextField(10);

        btnTambah = new JButton("Tambah");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnCari = new JButton("Cari");

        table = new JTable();

        JPanel panel = new JPanel();
        panel.add(new JLabel("ID KRS:"));
        panel.add(txtIDKRS);
        panel.add(new JLabel("ID Mhs:"));
        panel.add(txtIDMhs);
        panel.add(new JLabel("ID MK:"));
        panel.add(txtIDMk);
        panel.add(new JLabel("Semester:"));
        panel.add(txtSemester);
        panel.add(new JLabel("Tahun Ajaran:"));
        panel.add(txtTahunAjaran);
        panel.add(btnTambah);
        panel.add(btnUbah);
        panel.add(btnHapus);
        panel.add(btnCari);

        add(panel, "North");
        add(new JScrollPane(table), "Center");

        btnTambah.addActionListener(e -> tambahData());
        btnUbah.addActionListener(e -> ubahData());
        btnHapus.addActionListener(e -> hapusData());
        btnCari.addActionListener(e -> cariData());
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/akademik", "root", "");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + ex.getMessage());
        }
    }

    private void loadTable() {
        try {
            // Use the existing connection (conn)
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("SELECT * FROM krs");

            // Load the data into the table model
            ResultSetTableModel model = new ResultSetTableModel(rs);
            table.setModel(model); // Set model to the table

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void tambahData() {
        try {
            String sql = "INSERT INTO krs (idmhs, idmk, semester, tahunajaran) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(txtIDMhs.getText()));
            stmt.setInt(2, Integer.parseInt(txtIDMk.getText()));
            stmt.setString(3, txtSemester.getText());
            stmt.setString(4, txtTahunAjaran.getText());
            stmt.executeUpdate();
            loadTable(); // Refresh the table after adding data
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding data: " + ex.getMessage());
        }
    }

    private void ubahData() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah!");
                return;
            }

            String idkrs = table.getValueAt(selectedRow, 0).toString();
            String sql = "UPDATE krs SET idmhs=?, idmk=?, semester=?, tahunajaran=? WHERE idkrs=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(txtIDMhs.getText()));
            stmt.setInt(2, Integer.parseInt(txtIDMk.getText()));
            stmt.setString(3, txtSemester.getText());
            stmt.setString(4, txtTahunAjaran.getText());
            stmt.setString(5, idkrs);
            stmt.executeUpdate();
            loadTable(); // Refresh the table after updating data
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating data: " + ex.getMessage());
        }
    }

    private void hapusData() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
                return;
            }

            String idkrs = table.getValueAt(selectedRow, 0).toString();
            String sql = "DELETE FROM krs WHERE idkrs=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, idkrs);
            stmt.executeUpdate();
            loadTable(); // Refresh the table after deleting data
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting data: " + ex.getMessage());
        }
    }

    private void cariData() {
        try {
            String sql = "SELECT * FROM krs WHERE semester LIKE ? OR tahunajaran LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + txtSemester.getText() + "%");
            stmt.setString(2, "%" + txtTahunAjaran.getText() + "%");
            ResultSet rs = stmt.executeQuery();
            table.setModel(new ResultSetTableModel(rs)); // Set the result set to the table
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching data: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KRSFrame().setVisible(true));
    }
}

