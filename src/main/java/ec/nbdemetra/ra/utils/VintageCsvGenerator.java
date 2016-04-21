package ec.nbdemetra.ra.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import org.openide.util.Exceptions;

/**
 * @author bennouha
 * @date Apr 30, 2013
 */
public class VintageCsvGenerator {

    private Connection connection = null;
    private static final String URL = "jdbc:oracle:thin:peei_hist/peei_hist@10.2.0.74:1521:ora10db1";
    private String sql = " SELECT CL_FREQ, TIME, CL_REVDATE, OBS_VALUE FROM PEEI_HIST.EI_BPCA_M WHERE CL_GEO = 'EA16' "
            + " AND TO_DATE(CL_REVDATE, 'yyyy/mm/dd') >= TO_DATE('2009/02/20', 'yyyy/mm/dd')  AND TO_DATE(CL_REVDATE, 'yyyy/mm/dd') <= sysdate "
            + " AND SERNAME = 'MIO-EUR_NSA_EXT_EA16_NET_BP-010_EA16' ORDER BY TIME, CL_REVDATE ";
    private String pattern = "yyyy/MM/dd";
    private SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    private PreparedStatement statement;
    private String oldRevdate = "";
    private TreeMap<String, Integer> headers = new TreeMap<String, Integer>(new Comparator<String>() {
        public int compare(String o1, String o2) {
            try {
                return sdf.parse(o1).before(sdf.parse(o2)) ? -1 : (sdf.parse(o1).after(sdf.parse(o2)) ? 1 : 0);
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
            return 0;
        }
    });

    public String start(String filePath) throws SQLException, IOException {
        FileWriter fw = null;
        PrintWriter pw = null;
        statement = getConnection().prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = statement.executeQuery();
        try {
            File csv = new File(filePath);
            if (csv.exists()) {
                csv.delete();
                csv.createNewFile();
            }
            fw = new FileWriter(csv);
            pw = new PrintWriter(fw);
            if (resultSet != null) {
                resultSet.beforeFirst();
                int index = 1;
                while (resultSet.next()) {
                    if (!headers.containsKey(resultSet.getString("CL_REVDATE"))) {
                        headers.put(resultSet.getString("CL_REVDATE"), index++);
                    }
                }
                printCell(pw, "");
                index = 1;
                for (Map.Entry<String, Integer> entry : headers.entrySet()) {
                    entry.setValue(index++);
                    printCell(pw, entry.getKey());
                }
                printLastCell(pw);
                resultSet.beforeFirst();
                String time = "";
                while (resultSet.next()) {
                    if (!time.equals(resultSet.getString("TIME"))) {
                        if (!time.isEmpty()) {
                            printLastCell(pw);
                        }
                        printCell(pw, time = resultSet.getString("TIME"));
                    }
                    printCellByHeader(pw, resultSet.getString("OBS_VALUE"),
                            resultSet.getString("CL_REVDATE"));
                }
            } else {
                printCell(pw, "No data found for query : ");
                printCell(pw, sql);
                printLastCell(pw);
                System.err.println("No data found for query : ");
                System.err.println(sql);
            }
        } finally {
            if (pw != null) {
                pw.flush();
                pw.close();
            }
            if (fw != null) {
                fw.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return filePath;
    }

    /**
     * @date May 28, 2013
     * @author bennouha
     *
     * @param pw
     * @param string
     * @param string2
     */
    private void printCellByHeader(PrintWriter pw, String obsValue,
            String revdate) {
        int column = headers.get(revdate);
        int i = 1;
        try {
            i = sdf.parse(revdate).after(sdf.parse(oldRevdate)) ? (headers.get(oldRevdate) > 0 ? headers.get(oldRevdate) + 1 : 1) : 1;
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        while (i++ < column) {
            printCell(pw, "");
        }
        printCell(pw, obsValue);
        oldRevdate = revdate;
    }

    private void printCell(PrintWriter pw, String value) {
        pw.print(value);
        pw.print(",");
    }

    private void printLastCell(PrintWriter pw) {
        pw.println("");
    }

    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    public static void main(String[] args) {
        try {
            new VintageCsvGenerator()
                    .start("C:\\Users\\bennouha\\Downloads\\vintageQuery.csv");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
