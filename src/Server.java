import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class Server {
	
    private static final int MAX_CLIENTS = 250;
    private static int currentClients = 0;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final int port_number = 8000;

    public static void main(String[] args) {
    	

        System.out.println("Uruchamianie serwera");

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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("wyniki.txt", true))) {
            writer.write(clientId + ": " + score + " punktów");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeResponsesToFile(String clientId, String answer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bazaOdpowiedzi.txt", true))) {
            writer.write(clientId + ": " + answer);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
