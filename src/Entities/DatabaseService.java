package  Entities;

import Entities.Client.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseService {
    private static DatabaseService instance = null;
    Connection connection;
    private String url = "jdbc:mysql://localhost:3306/bank";
    private String user = "root";
    private String password = "";

    private DatabaseService() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
    }

    public static DatabaseService getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    public ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public void executeUpdate(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public void readResources(String table) throws SQLException{
        ResultSet resultSet = executeQuery("select * from " + table);
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int noColumns = rsmd.getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= noColumns; i++) {
                if (i > 1) {
                    System.out.print(",  ");
                }
                String value = resultSet.getString(i);
                System.out.print(rsmd.getColumnName(i) + ": " + value);
            }
            System.out.println("");
        }
    }

    public ArrayList<Client> readClients() throws SQLException {
        ArrayList<Client> clients = new ArrayList<>();
        ResultSet resultSet = executeQuery("select * from Client");
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int noColumns = rsmd.getColumnCount();
        while (resultSet.next()) {
            ArrayList<String> params = new ArrayList<>();
            for (int i = 1; i <= noColumns; i++) {
                String value = resultSet.getString(i);
                params.add(value);
            }
            clients.add(new Client(params.get(0), params.get(1), params.get(2), params.get(3)));
        }
        return clients;
    }

    public void editResource(String table, HashMap<String, String> fields, String primaryKey) throws SQLException {
        String primaryKeyName;
        switch(table) {
            case "Client":
                primaryKeyName = "clientId";
                break;
            case "Card":
                primaryKeyName = "cardId";
                break;
            case "Account":
                primaryKeyName = "accountId";
                break;
            case "Transaction":
                primaryKeyName = "transactionId";
                break;
            default:
                System.out.println("No such table.");
                return;
        }
        String query = "update " + table + " set";
        for(String fieldName: fields.keySet()) {
            query = query + " " + fieldName + " = " + fields.get(fieldName) + ',';
        }
        query = query.substring(0, query.length() - 1);
        query = query + " where " + primaryKeyName + " = " + primaryKey;
        executeUpdate(query);
    }

    public void deleteResource(String table, String primaryKey) throws SQLException {
        String primaryKeyName;
        switch(table) {
            case "Client":
                primaryKeyName = "clientId";
                break;
            case "Card":
                primaryKeyName = "cardId";
                break;
            case "Account":
                primaryKeyName = "accountId";
                break;
            case "Transaction":
                primaryKeyName = "transactionId";
                break;
            default:
                System.out.println("No such table.");
                return;
        }
        String query =  "delete from " + table + " where " + primaryKeyName +  " = " + primaryKey;
        System.out.println(query);
        executeUpdate(query);
    }

}