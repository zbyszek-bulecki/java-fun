import java.io.IOException;
import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3000;
    private static final int BACKLOG = 8;

    public static void main(String[] args) throws IOException {

        final Logger logger = Logger.getLogger(App.class.getName()); //java.util.logging.Logger
        List<Socket> socketList = Collections.synchronizedList(new ArrayList<>()); //rozwiązanie 1, alternatywą może być concurrent list

        // jeżeli używamy kolekcji na wielu wątkach to trzeba zadbać o bezpieczeństwo dostępu współbierznego
        ServerSocket serverSocket = new ServerSocket(PORT);
        logger.log(Level.INFO, "Listening on port {0} : {1} with a connection backlog of {2}", new Object[]{HOST, PORT, BACKLOG});

        new Thread(() -> {
            while (true) {
                try {
                    socketList.add(serverSocket.accept());// daj mi deskryptor połączenia przychodzącego
                    logger.log(Level.INFO, "Socket added to list.");
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Unable to obtain socket descriptor. {0}", e);
                    throw new RuntimeException(e);
                }
            }
        }).start();

        while (true) {

            try {
                socketList.stream().filter(socket -> {
                            try {
                                return socket.getInputStream().available() > 0;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .map(socket -> {
                            try {
                                return socket.getInputStream().read();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            socketList.forEach((socket -> {

                InputStreamReader inputStreamReader;
                BufferedReader bufferedReader;

                try {
                    inputStreamReader = new InputStreamReader(socket.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                while (true) {
                    try {
                        if (!(socket.getInputStream().available() > 0)) break;
                        System.out.println(bufferedReader.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
        }
    }
}
