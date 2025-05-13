import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    private List<String[]> questions;
    private String clientId;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.questions = new ArrayList<>();
        this.clientId = "1";
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            int score = 0;
            loadQuestionsFromFile("bazaPytan.txt");


            for (String[] q : questions) {
                String question = q[0];
                String correctAnswer = q[1];

                sendMessage(question);
                String userAnswer = in.readLine();

                System.out.println("Odpowiedz: " + userAnswer);
                Server.writeResponsesToFile(clientId, userAnswer);


                if (userAnswer == null || !userAnswer.trim().equalsIgnoreCase(correctAnswer)) {
                    sendMessage("Zle, poprawna opdowiedz: " + correctAnswer);
                } else {
                	score++;
                    sendMessage("Dobrze");
                }
            }

            Server.writeResultToFile(clientId, score);
            sendMessage("Test zakonczony");

            clientSocket.close();
            System.out.println("Zakonczono polaczenie");

        } catch (IOException e) {
            System.err.println("Client error" + e.getMessage());
        } finally {
            Server.decrementClient();
        }
    }
    
    private void loadQuestionsFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String question = line;
                String correctAnswer = reader.readLine();

                if (question != null && correctAnswer != null) {
                    questions.add(new String[]{question, correctAnswer});
                }
            }
        } catch (IOException e) {
            System.err.println("Blad odczytu" + e.getMessage());
        }
    }

    private void sendMessage(String msg) {

        out.println(msg);
        out.println("/message::end");
    }
}
