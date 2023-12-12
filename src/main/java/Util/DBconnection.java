package Util;
import java.sql.*;

public class DBconnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/VotingApp";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";
    private Connection connection;

    public DBconnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        if (connection != null) {
            System.out.println("Connection Established");
        } else {
            System.out.println("Connection Failed");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public ResultSet executeQuery(String query, Object... parameters) throws SQLException {
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            setParameters(preparedStatement, parameters);
            return preparedStatement.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public int executeUpdate(String query, Object... parameters) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, parameters);
            return preparedStatement.executeUpdate();
        }
    }

    private void setParameters(PreparedStatement preparedStatement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setObject(i + 1, parameters[i]);
        }
    }
}
