package blockchaintask1;

import com.google.gson.JsonObject;
import java.sql.Timestamp;

/**
 * This class acts as the model for {@link BlockChainServerTCP}
 * It does the requested operation per the given user choice and returns the result
 * It holds the blockchain and the methods required for its operation
 */
public class ServerModel {
    BlockChain blockChain;

    /**
     * Constructor initializes the blockchain, adds the genesis block of difficulty level as 2 and computes the required nonce.
     * It also computes the estimates hashes per second for the server machine
     */
    ServerModel() {
        System.out.println("Initializing blockchain..");
        blockChain = new BlockChain();
        Block genesis = new Block(0, new Timestamp(System.currentTimeMillis()), "", 2);
        genesis.proofOfWork();
        blockChain.computeHashesPerSecond();
        blockChain.addBlock(genesis);
    }

    /**
     * This method is called by the server
     * @param message {@link JsonObject} JSON object that contains client's message
     * corresponding id.
     */
    public String processClientMessage(JsonObject message) {
        int choice = message.get("choice").getAsInt();
            // Return basic details when choice is 0
            if (choice == 0) {
                return "Number of blocks on the chain: " + blockChain.getChainSize() + "\n" +
                        "Difficulty of most recent block: " + blockChain.getLatestBlock().getDifficulty() + "\n" +
                        "Total difficulty for all blocks: " + blockChain.getTotalDifficulty() + "\n" +
                        "Approximate hashes per second on this machine: " + blockChain.getHashesPerSecond() + "\n" +
                        "Expected total hashes required for the whole chain: " + blockChain.getTotalExpectedHashes() + "\n" +
                        "Nonce for most recent block: " + blockChain.getLatestBlock().getNonce() + "\n" +
                        "Chain hash: " + blockChain.getChainHash() + "\n";
            }
            // Add block to the blockchain if choice is 1
            else if (choice == 1) {
                int difficulty = message.get("difficulty").getAsInt();
                String data = message.get("data").getAsString();
                long startTime = System.nanoTime();
                Block b = new Block(blockChain.getChainSize(), new Timestamp(System.currentTimeMillis()), data, difficulty);
                blockChain.addBlock(b);
                long estimatedTime = (System.nanoTime() - startTime)/1000000;
                return String.format("Total execution time to add this block was %d milliseconds\n\n",estimatedTime);
            }

            // Verify chain if choice is 2
            else if (choice == 2) {
                StringBuilder sb = new StringBuilder();
                System.out.println("Verifying entire chain");
                System.out.println();
                long startTime = System.nanoTime();
                sb.append("Chain verification: ").append(blockChain.isChainValid()).append("\n");
                long estimatedTime = (System.nanoTime() - startTime)/1000000;
                sb.append("Total execution time required to verify the chain was ").append(estimatedTime).append(" milliseconds\n");
                return sb.toString();
            }
            // Return chain as a string if choice is 3
            else if (choice == 3)  {
                return blockChain.toString() + "\n";
            }
            // Corrupt the chain if choice is 4
            else if (choice == 4) {
                int id = message.get("id").getAsInt();
                String newData = message.get("newData").getAsString();
                blockChain.getBlock(id).setData(newData);
                return String.format("Block %d now holds %s\n\n", id, newData);
            }
            // Repair corrupt blocks if choice is 5
            else if (choice == 5) {
                System.out.println("Repairing the entire chain");
                long startTime = System.nanoTime();
                blockChain.repairChain();
                long estimatedTime = (System.nanoTime() - startTime)/1000000;
                return "Total execution time required to repair the chain was " + estimatedTime + " milliseconds\n";
            }
            return "Invalid choice!";
        }
}

