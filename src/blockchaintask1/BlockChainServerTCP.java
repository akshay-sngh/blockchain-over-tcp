package blockchaintask1;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * BlockChainServerTCP uses TCP to listen to a {@link BlockChainClientTCP} and serve its requests
 * It abstracts the blockchain functionality from the client side
 * It receives a JSON String message, deserializes it to a {@link JsonObject} and extracts the relevant values from it
 *
 * References
 *  1. https://stackoverflow.com/questions/4252294/sending-objects-across-network-using-udp-in-java
 *  2. https://stackoverflow.com/questions/21073024/receive-an-object-over-tcp-ip
 *  3. https://github.com/CMU-Heinz-95702/Project-2-Client-Server#task-4-use-the-intellij-project-name-project2task4
 *  https://www.baeldung.com/gson-string-to-jsonobject
*/
public class BlockChainServerTCP {
    private static ServerModel sm;  // Server's model class that holds the blockchain data

    /**
     * This method reads incoming streams from the client and serves requests
     */
    private static void processNewClientRequest() {
        int serverPort = 7777;
        Socket clientSocket;
        try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            clientSocket = listenSocket.accept();
            // Set up "in" to read from the client socket
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            // Set up "out" to read from the client socket
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            String messageFromClientJSON;

            while ((messageFromClientJSON = (String) ois.readObject()) != null) {
                System.out.println("JSON Message from the client:\n" + messageFromClientJSON);
                JsonParser parser = new JsonParser();
                JsonObject messageObject = (JsonObject) parser.parse(messageFromClientJSON);
                // Delegate message processing concern to the model
                String result = sm.processClientMessage(messageObject);
                // Return result back to client
                System.out.println("Returning result:\n" + result);
                oos.writeObject(result);
                oos.flush();
            }

        } catch (SocketException e) {
            System.out.println("Client disconnected: " + e.getMessage());
            processNewClientRequest();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Deserialization: " + e.getMessage());
        }
    }
    // Initialize model and listen for requests upon startup
    public static void main(String[] args) {
        sm = new ServerModel();
        System.out.println("Server started.");
        processNewClientRequest();
    }
}
