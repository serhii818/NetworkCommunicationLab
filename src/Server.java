import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.concurrent.locks.ReentrantLock;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class Server {
	
    private static final int MAX_CLIENTS = 250;
    private static int currentClients = 0;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final int port_number = 8000;

    private static final String database_server = "jdbc:mysql://localhost:3306/";
    private static final String database_name = "baza1";
    private static Connection database = null;

    public static void main(String[] args) {
        System.out.println("Uruchamianie serwera");

        // database initialization
        if (chackDriver()) {
            connectToDB();
        }



        try (ServerSocket serverSocket = new ServerSocket(port_number)) {
            System.out.println("Serwer nasluchuje na porcie " + port_number);

            while (true) {
            	
            	lock.lock();
            	try {
            		if (currentClients >= MAX_CLIENTS) {
                        System.out.println("Osiagnieto limit");
                        continue;
            		}
            	} finally {
                    lock.unlock();
                }
            
                Socket clientSocket = serverSocket.accept();
                
                lock.lock();
                try {
                    currentClients++;
                } finally {
                    lock.unlock();
                }
                System.out.println("Nowe polaczenie: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(handler);
                clientThread.start();
            }

        } catch (IOException e) {
            System.err.println("Blad serwera: " + e.getMessage());
        }
    }
    
    public static void decrementClient() {
        lock.lock();
        try {
            currentClients--;
        } finally {
            lock.unlock();
        }
    }
    
    public static void writeResultToFile(String clientId, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/wyniki.txt", true))) {
            writer.write(clientId + ": " + score + " punktów");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeResponsesToFile(String clientId, String answer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/bazaOdpowiedzi.txt", true))) {
            writer.write(clientId + ": " + answer);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean chackDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println(ASCII.GREEN + "MYSQL DATABASE DRIVER OK" + ASCII.RESET);
            return true;
        } catch (ClassNotFoundException ex) {
            System.out.println(ASCII.RED + "Database driver ERROR: " + ex.getMessage() + ASCII.RESET);
            return false;
        }
    }

    public static void connectToDB() {
        try {
            createDBIfNotExists();
            database = DriverManager.getConnection(database_server + database_name, "root", "");
        } catch (SQLException ex) {
            System.out.println(ASCII.RED + "database connection error: " + ex.getMessage() + ASCII.RESET);
        }
    }

    public static void createDBIfNotExists() {
        try {
            Connection conn = DriverManager.getConnection(database_server, "root", "");

            String checkQuery = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, database_name);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                createDB(conn);
                createTables(conn);

                System.out.println(ASCII.YELLOW + "Database created, database name: " + ASCII.BLUE + database_name + ASCII.RESET);
            } else {
                System.out.println(ASCII.GREEN + "Database exists, database name: " + ASCII.BLUE + database_name + ASCII.RESET);
            }

            rs.close();
            checkStmt.close();
            conn.close();

        } catch (SQLException ex) {
            System.out.println(ASCII.RED + "database error: " + ex.getMessage() + ASCII.RESET);
        }
    }

    public static void createDB(Connection conn) throws SQLException {
        String query = "CREATE DATABASE " + database_name;
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }

    public static void createTables(Connection conn) throws SQLException {
        String query1 = "USE baza1;";
        String query2 = "CREATE TABLE pytanie (\n" +
                "    nrPytania INT(4) PRIMARY KEY,\n" +
                "    tresc TEXT NOT NULL,\n" +
                "    opcje TEXT NOT NULL,\n" +
                "    odpowiedz CHAR(1) NOT NULL\n" +
                ");";
        String query3 = "CREATE TABLE odpowiedz_studenta (\n" +
                "    imie VARCHAR(20),\n" +
                "    nrPytania INT(4),\n" +
                "    odpowiedz CHAR(1),\n" +
                "    PRIMARY KEY(imie, nrPytania),\n" +
                "    FOREIGN KEY(nrPytania) REFERENCES pytanie(nrPytania)\n" +
                ");";
        String query4 = "CREATE TABLE wynik (\n" +
                "    idWyniku INT(4) PRIMARY KEY,\n" +
                "    imie VARCHAR(20) NOT NULL,\n" +
                "    punkty INT(4) NOT NULL\n" +
                ");";

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query1);
        stmt.executeUpdate(query2);
        stmt.executeUpdate(query3);
        stmt.executeUpdate(query4);
        stmt.close();
    }

    public static void deleteDB(String database_name2) {
        try {
            Connection conn = DriverManager.getConnection(database_server, "root", "");
            String query = "DROP DATABASE " + database_name2;
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            System.out.println(ASCII.YELLOW + "database " + ASCII.BLUE + database_name2 + ASCII.YELLOW + " was deleted" + ASCII.RESET);
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ASCII.RED + "database error: " + ex.getMessage() + ASCII.RESET);
        }
    }
}
