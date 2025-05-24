import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    private List<Question> questions;
    private String clientId;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.questions = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            int score = 0;
            loadQuestionsFromDB();

            clientId = in.readLine();

            for (Question q : questions) {
                sendMessage(q.tresc);
                sendMessage(q.opcje);
                String userAnswer = in.readLine();

                System.out.println("Odpowiedz: " + userAnswer);
                Server.writeResponsesToDB(clientId, q.nrPytania, userAnswer);


                if (userAnswer == null || !userAnswer.trim().equalsIgnoreCase(q.odpowiedz)) {
                    sendMessage("Zle, poprawna opdowiedz: " + q.odpowiedz);
                } else {
                	score++;
                    sendMessage("Dobrze");
                }
            }

            Server.writeResultToDB(clientId, score, Date());
            sendMessage("Test zakonczony");
            sendMessage("" + score);

            clientSocket.close();
            System.out.println("Zakonczono polaczenie");

        } catch (IOException e) {
            System.err.println("Client error" + e.getMessage());
        } finally {
            Server.decrementClient();
        }
    }
    
    private void loadQuestionsFromDB() {
        String query = "SELECT nrPytania, tresc, odpowiedz FROM pytanie";
        try (Statement stmt = Server.getConnection().createStatement();
             ResultSet r = stmt.executeQuery(query)) {

            while (r.next()) {
                int nr = r.getInt("nrPytania");
                String tresc = r.getString("tresc");
                String opcje = r.getString("opcje");
                String odp = r.getString("odpowiedz");
                questions.add(new Question(nr, tresc, opcje, odp));
            }

        } catch (SQLException e) {
            System.err.println("Błąd pobierania pytań z DB: " + e.getMessage());
        }
    }
    
    private static class Question {
        int nrPytania;
        String tresc;
        String opcje;
        String odpowiedz;

        Question(int nr, String tresc, String opcje, String odpowiedz) {
            this.nrPytania = nr;
            this.tresc = tresc;
            this.opcje = opcje;
            this.odpowiedz = odpowiedz;
        }
    }
    
    public static int Date() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = now.format(f);
        return Integer.parseInt(formattedDateTime.substring(0, 9));
    }

    private void sendMessage(String msg) {

        out.println(msg);
        out.println("/message::end");
    }


}
