package zuaskoneksi;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class MatakuliahFrame extends JFrame {
    private JTextField txtIDMk, txtNamaMk;
    private JButton btnTambah, btnUbah, btnHapus, btnCari;
    private JTable table;
    private Connection conn;

    public MatakuliahFrame() {
        setTitle("CRUD Matakuliah");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        connectDatabase();
        loadTable();
    }

    private void initComponents() {
        txtIDMk = new JTextField(5);
        txtNamaMk = new JTextField(15);

        btnTambah = new JButton("Tambah");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnCari = new JButton("Cari");

        table = new JTable();

        JPanel panel = new JPanel();
        panel.add(new JLabel("ID MK:"));
        panel.add(txtIDMk);
        panel.add(new JLabel("Nama MK:"));
        panel.add(txtNamaMk);
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
            ResultSet rs = stmt.executeQuery("SELECT * FROM matakuliah");

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
            String sql = "INSERT INTO matakuliah (nama) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtNamaMk.getText());
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

            String idmk = table.getValueAt(selectedRow, 0).toString();
            String sql = "UPDATE matakuliah SET nama=? WHERE idmk=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtNamaMk.getText());
            stmt.setString(2, idmk);
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

            String idmk = table.getValueAt(selectedRow, 0).toString();
            String sql = "DELETE FROM matakuliah WHERE idmk=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, idmk);
            stmt.executeUpdate();
            loadTable(); // Refresh the table after deleting data
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void cariData() {
        try {
            String sql = "SELECT * FROM matakuliah WHERE nama LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + txtNamaMk.getText() + "%");
            ResultSet rs = stmt.executeQuery();
            table.setModel(new ResultSetTableModel(rs)); // Set the result set to the table
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MatakuliahFrame().setVisible(true));
    }
}
