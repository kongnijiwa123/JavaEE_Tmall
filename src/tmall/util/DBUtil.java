package tmall.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    /*private static String ip = "127.0.0.1";
    private static int port =3306;
    private static String database = "tmall";
    private static String encoding = "UTF-8";
    private static String loginName = "root";
    private static String password = "1230.+";

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = String.format("jdbc:mysql://%s:%d/%s?characterEncoding=%s"
                , ip, port, database, encoding);
        Connection conn = DriverManager.getConnection(url, loginName, password);
        return conn;
    }


*/
    private static DataSource pool;

    static {
        Context env = null;
        try {
            env = (Context) new InitialContext().lookup("java:comp/env");
            pool = (DataSource) env.lookup("jdbc/dbpool");
            if (pool == null) {
                System.err.println("'DBPool' is an unknown DataSource");
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

/*
    public static DataSource getPool() {
        return pool;
    }
*/

    public static Connection getConnection() {
        Connection conn=null;
        try {
            conn= pool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

}
