

import java.io.*;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public class Client {
    static Inet4Address ip_addr = null;
    static int port_number = 0;

    static Socket socket;
    static BufferedReader server_reader;
    static PrintWriter server_writer;

    static long timeLimit = 60; // in seconds

    public static void main(String[] args) {
        try {
            read_connection_configuration();
            System.out.println(ip_addr + " " + port_number);
            connect_to_server();
            startQuiz();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (server_reader != null) server_reader.close();
                if (server_writer != null) server_writer.close();
                if (socket != null) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void read_connection_configuration() throws IOException {
        File config = new File("src/ConnectionConfiguration.txt");
        BufferedReader reader = new BufferedReader(new FileReader(config));
        ip_addr = (Inet4Address) Inet4Address.getByName(reader.readLine());
        port_number = Integer.parseInt(reader.readLine());
        reader.close();
    }

    public static void connect_to_server() throws Exception {
        if (ip_addr == null || port_number == 0) {
            throw new Exception("failed to connet to server");
        }

        try {

            socket = new Socket(ip_addr, port_number);
            server_writer = new PrintWriter(socket.getOutputStream(), true);
            server_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (ConnectException ex) {
            System.out.println(ASCII.RED + ex.getMessage() + ASCII.RESET);
        }
    }

    public static String readMessage() throws IOException {
        StringBuilder msg = new StringBuilder();
        String m;

        while (true) {
            m = server_reader.readLine();
            if (Objects.equals(m, "/message::end")) {
                break;
            }
            msg.append(m);
        }
        return msg.toString();
    }

    public static void sendMessage(String choice) {
        server_writer.println(choice);

    }

    public static String readChoice() throws InterruptedException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        long endTime = System.currentTimeMillis() + timeLimit * 1000L;
        String input = "";

        while (System.currentTimeMillis() < endTime) {
            if (System.in.available() > 0) { // Non-blocking reading
                input = reader.readLine();
                break;
            } else {
                Thread.sleep(100);
            }
        }

        if (input.isEmpty()) {
            System.out.println(ASCII.RED + "\nTime's up!" + ASCII.RESET);
        }

        System.out.println();

        return input;
    }

    public static void startQuiz() throws IOException, InterruptedException {
        if (socket != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Wpisz imie i nazwisko:");
            String name = reader.readLine();
            sendMessage(name);

            System.out.println(ASCII.CYAN + "(You have " + timeLimit + " seconds)" + ASCII.RESET);
            String message;
            String choice;


            do {
                message = readMessage();
                System.out.println(message);
                if (message.equals("Test zakonczony")) break;

                choice = readChoice();
                sendMessage(choice);

                message = readMessage();
                System.out.println(message);

            } while (true);
            message = readMessage();
            System.out.println("Wynik:" + message);
        }
    }
}
