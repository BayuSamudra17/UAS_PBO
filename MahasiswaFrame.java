package zuaskoneksi;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class MahasiswaFrame extends JFrame {
    private JTextField txtNIM, txtNama;
    private JButton btnTambah, btnUbah, btnHapus, btnCari;
    private JTable table;
    private Connection conn;

    public MahasiswaFrame() {
        setTitle("CRUD Mahasiswa");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        connectDatabase();
        loadTable();
    }

    private void initComponents() {
        txtNIM = new JTextField(15);
        txtNama = new JTextField(15);

        btnTambah = new JButton("Tambah");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnCari = new JButton("Cari");

        table = new JTable();

        JPanel panel = new JPanel();
        panel.add(new JLabel("NIM:"));
        panel.add(txtNIM);
        panel.add(new JLabel("Nama:"));
        panel.add(txtNama);
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
        }
    }

    private void loadTable() {
        try {
            // Use the existing connection (conn)
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("SELECT * FROM mahasiswa");

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
            String sql = "INSERT INTO mahasiswa (nim, nama) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtNIM.getText());
            stmt.setString(2, txtNama.getText());
            stmt.executeUpdate();
            loadTable(); // Refresh the table after adding data
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void ubahData() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah!");
                return;
            }

            String idmhs = table.getValueAt(selectedRow, 0).toString();
            String sql = "UPDATE mahasiswa SET nim=?, nama=? WHERE idmhs=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtNIM.getText());
            stmt.setString(2, txtNama.getText());
            stmt.setString(3, idmhs);
            stmt.executeUpdate();
            loadTable(); // Refresh the table after updating data
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void hapusData() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
                return;
            }

            String idmhs = table.getValueAt(selectedRow, 0).toString();
            String sql = "DELETE FROM mahasiswa WHERE idmhs=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, idmhs);
            stmt.executeUpdate();
            loadTable(); // Refresh the table after deleting data
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void cariData() {
        try {
            String sql = "SELECT * FROM mahasiswa WHERE nim LIKE ? OR nama LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + txtNIM.getText() + "%");
            stmt.setString(2, "%" + txtNama.getText() + "%");
            ResultSet rs = stmt.executeQuery();
            table.setModel(new ResultSetTableModel(rs)); // Set the result set to the table
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MahasiswaFrame().setVisible(true));
    }
}
