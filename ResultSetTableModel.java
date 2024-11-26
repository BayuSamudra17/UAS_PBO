package zuaskoneksi;

import javax.swing.table.AbstractTableModel;
import java.sql.*;

public class ResultSetTableModel extends AbstractTableModel {
    private ResultSet rs;
    private ResultSetMetaData metaData;
    private int numberOfColumns;

    public ResultSetTableModel(ResultSet rs) throws SQLException {
        this.rs = rs;
        this.metaData = rs.getMetaData();
        this.numberOfColumns = metaData.getColumnCount();
        rs.last(); // Menggerakkan cursor ke baris terakhir untuk mendapatkan jumlah baris
    }

    @Override
    public int getRowCount() {
        try {
            rs.last(); // Memastikan kita bergerak ke baris terakhir untuk menghitung jumlah baris
            return rs.getRow();  // Mendapatkan jumlah baris
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        return numberOfColumns;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            if (!rs.absolute(rowIndex + 1)) { // Pindah ke baris yang diinginkan
                return null;
            }
            return rs.getObject(columnIndex + 1); // Ambil nilai kolom dari ResultSet
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        try {
            return metaData.getColumnName(column + 1); // Mengambil nama kolom
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }
}
