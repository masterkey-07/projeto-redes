import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        final int port = 5000;

        try {
            ServerSocket server = new ServerSocket(port);

            System.out.println("Server is running on port " + port);

            while (true) {
                new ClientHandler(server.accept()).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter myWriter;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                myWriter = new PrintWriter(socket.getOutputStream(), true);

                // it runs in a thread safe way
                synchronized (clientWriters) {
                    clientWriters.add(myWriter);
                }

                InputStream inputSream = socket.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputSream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String message;

                while ((message = bufferedReader.readLine()) != null) {
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {

                            if (myWriter != writer)
                                writer.println(message);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // it runs in a thread safe way
                synchronized (clientWriters) {
                    clientWriters.remove(myWriter);
                }

                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
