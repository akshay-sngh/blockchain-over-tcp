package blockchaintask1;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * BlockChainClientTCP uses TCP to establish connections with a {@link BlockChainServerTCP}
 * It makes calls to the server in order to perform blockchain operations
 * The underlying procedure concerns is seperated from the client
 * It passes the message to the server in a JSON String, which is deserialized to a {@link JsonObject}
 *
 * References:
 * 1. https://stackoverflow.com/questions/15687146/objectinputstream
 * 2. https://stackoverflow.com/questions/4252294/sending-objects-across-network-using-udp-in-java
 * 3. http://www.java2s.com/Code/Java/Network-Protocol/ObjectInputStreamandObjectOutputStreamfromSocket.htm
 * 4. https://www.tabnine.com/code/java/methods/com.google.gson.JsonObject/add
 */
public class BlockChainClientTCP {
    private static final int SERVER_PORT = 7777;
    private static String hostName;
    private static Socket clientSocket;
    private static ObjectOutputStream oos;  // Stream for sending objects
    private static ObjectInputStream ois;   // Stream for receiving objects
    private static Scanner input;
    /**
     * This method initiates the menu for the user to interact with
     */
    private static void startClient() {
        boolean receivedExitCommand = false;
        int choice = -1;
        input = new Scanner(System.in);
        while (true) {
            System.out.println("0. View basic blockchain status.");
            System.out.println("1. Add a transaction to the blockchain.");
            System.out.println("2. Verify the blockchain.");
            System.out.println("3. View the blockchain.");
            System.out.println("4. Corrupt the chain.");
            System.out.println("5. Hide the corruption by repairing the chain.");
            System.out.println("6. Exit.");
            choice = input.nextInt();
            if (choice == 6) {
                System.out.println("Exit command received, client side quitting. Server is still running.");
                break;
            }
            else {
                String requestMessage = constructRequestMessage(choice);
                System.out.println(getResponseFromServer(requestMessage));
            }
        }
    }

    /**
     * This method sends the JSON string to the server.
     * It also returns the server's response to the client request
     * @param requestMessage Message to be sent to the client
     * @return String returned by the server
     */
    private static String getResponseFromServer(String requestMessage) {
        try {
            // Write the Message object to the stream
            oos.writeObject(requestMessage);
            oos.flush();

            // Wait for server's response and return once it is received.
            return (String) ois.readObject();

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Failed to get a valid response from server: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method creates a JSONObject and populates it with the relevant data.
     * This may include transaction value, difficulty, or new data to be replaced.
     * @param choice The choice entered by the user
     * @return JSON String of the request message to be sent to the server
     */
    private static String constructRequestMessage(int choice) {

        // Handling invalid choices
        if (choice < 0 || choice > 5) {
            System.out.println("Invalid command");
            return null;
        }
        JsonObject message = new JsonObject();
        message.addProperty("choice", choice);
        // Adding a transaction
        if (choice == 1) {
            System.out.println("Enter difficulty > 0");
            message.addProperty("difficulty", input.nextInt());
            input.nextLine();
            System.out.println("Enter transaction");
            message.addProperty("data", input.nextLine());
        }
        else if (choice == 2) {
            System.out.println("Server verifying the chain");
        }
        // Corrupt the chain with a new transaction data
        else if (choice == 4) {
            System.out.println("corrupt the Blockchain");
            System.out.println("Enter block ID of block to corrupt");
            int id = input.nextInt();
            message.addProperty("id", id);
            input.nextLine();
            System.out.println("Enter new data for block " + id);
            message.addProperty("newData", input.nextLine());
        }
        return message.toString();
    }

    /**
     * This method instantiates the client socket and retrieves its input and output object streams.
     */
    private static void setupConnections() {
        try {
            clientSocket = new Socket(hostName, SERVER_PORT);
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ois = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Unable to establish connection with the server");
            e.printStackTrace();
        }
    }

    /**
     * Driver program for running the client
     * @param args hardcoded as "localhost"
     */
    public static void main(String[] args) {
        System.out.println("The client is running.");
        hostName = "localhost";
        setupConnections();
        startClient();
    }
}
