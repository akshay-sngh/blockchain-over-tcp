package blockchaintask1;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a simple BlockChain.
 * It holds a collection of {@link Block } instances and allows the user to perform operations in them.
 * It also maintains an instance variable holding the approximate number of hashes per second on this computer.
 * BlockChain has exactly three instance members:
 * 1. An ArrayList to hold Blocks
 * 2. A chain hash to hold a SHA256 hash of the most recently added Block
 * 3. Estimated average number of hashes per second
 * 4. https://mkyong.com/java/java-how-to-convert-system-nanotime-to-seconds/
 * 5. https://howtodoinjava.com/gson/gson-serializedname/
 */
public class BlockChain {

    @SerializedName(value =  "ds_chain")
    private List<Block> blocks; //holds Blocks

    private String chainHash;   //holds SHA-256 hash of the most recently added Block
    private int hashesPerSecond;

    /**
     * This constructor creates an empty ArrayList for Block storage.
     * It also sets the chain hash to the empty string and sets hashes per second to 0.
     */
    public BlockChain() {
        blocks = new ArrayList<>();
        chainHash = "";
        hashesPerSecond = 0;
    }

    public String getChainHash() {
        return chainHash;
    }

    /**
     * This method fetches the current time
     * @return the current system time
     */
    public Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    public Block getLatestBlock() {
        return blocks.get(blocks.size() - 1);
    }

    public int getChainSize() {
        return blocks.size();
    }

    /**
     * This method computes exactly 1 million hashes and times how long that process takes.
     * So, hashes per second is approximated as (1 million / number of seconds).
     * It is run on start up and sets the instance variable hashesPerSecond.
     * It uses a simple string - "00000000" to hash.
     */
    public void computeHashesPerSecond() {
        long start = System.nanoTime();
        String simpleString = "00000000";
        for (int i = 0; i < 1000000; i++) {
            Utils.computeSimpleSHA256Hash(simpleString);
        }
        long end = System.nanoTime();
        // Convert nanoseconds to seconds and divide the value by one million
        hashesPerSecond = (int) (1000000 / ((double) (end - start) / 1_000_000_000));
    }

    /**
     * Fetches hashesPerSecond
     *
     * @return the instance variable approximating the number of hashes per second.
     */
    public int getHashesPerSecond() {
        return hashesPerSecond;
    }

    /**
     * This method adds a new block to the blockchain
     * This new block's previous hash must hold the hash of the most recently added block.
     * After this call on addBlock, the new block becomes the most recently added block on the BlockChain.
     *
     * @param newBlock newBlock - is added to the BlockChain as the most recent block
     */
    public void addBlock(Block newBlock) {
        String previousHash = chainHash;
        newBlock.setPreviousHash(previousHash);
        chainHash = newBlock.proofOfWork();
        blocks.add(newBlock);
    }

    /**
     * Overrides toString() and creates a JSON string representation of the entire blockchain
     *
     * @return a String representation of the entire chain is returned.
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /**
     * Used to get the block for a given index
     *
     * @param i index of the block to be fetched
     * @return Instance of Block in the index i
     */
    public Block getBlock(int i) {
        return blocks.get(i);
    }

    /**
     * Compute and return the total difficulty of all blocks on the chain. Each block knows its own difficulty
     *
     * @return totalDifficulty
     */
    public int getTotalDifficulty() {
        int totalDifficulty = 0;
        for (Block block : blocks)
            totalDifficulty += block.getDifficulty();
        return totalDifficulty;
    }

    /**
     * Compute and return the expected number of hashes required for the entire chain.
     *
     * @return totalExpectedHashes
     */
    public double getTotalExpectedHashes() {
        double totalExpectedHashes = 0;
        // expected number of hashes for one block = 16 ^ (difficulty of that block)
        for (Block block : blocks)
            totalExpectedHashes += Math.pow(16, block.getDifficulty());
        return totalExpectedHashes;
    }

    /**
     * If the chain only contains one block, the genesis block at position 0, this routine computes the hash of the
     * block and checks that the hash has the requisite number of leftmost 0's (proof of work) as specified in the difficulty field.
     * It also checks that the chain hash is equal to this computed hash. If either check fails, return false.
     * Otherwise, return true. If the chain has more blocks than one, begin checking from block one.
     * The first check will involve a computation of a hash in Block 0 and a comparison with the hash pointer in Block 1.
     * If they match and if the proof of work is correct, go and visit the next block in the chain. At the end, check that the chain hash is also correct.
     *
     * @return true if and only if the chain is valid
     */
    public boolean  isChainValid() {
        int chainSize = getChainSize();
        if (chainSize == 0)
            return true;

        boolean validityFlag = true;
        Block genesis = getBlock(0);
        String currentBlockHash = getComputedHashForBlock(genesis);

        // Check only the leading zeroes for the genesis block
        if (!Utils.hasLeadingZeroes(currentBlockHash, genesis.getDifficulty())) {
            System.out.print("Improper hash on node 0, does not begin with ");
            for (int i = 0; i < genesis.getDifficulty(); i++)
                System.out.print("0");
            System.out.println();
            validityFlag = false;
        }

        for (int i = 1; i < chainSize; i++) {
            Block nextBlock = getBlock(i);
            // Check if previous hash matches the previousHash field of next block
            if (!nextBlock.getPreviousHash().equals(currentBlockHash))
                validityFlag = false;

            // Check if current block shows correct proof of work
            currentBlockHash = getComputedHashForBlock(nextBlock);
            if (!Utils.hasLeadingZeroes(currentBlockHash, nextBlock.getDifficulty())) {
                System.out.printf("Improper hash on node %d, does not begin with ", i);
                for (int j = 0; j < nextBlock.getDifficulty(); j++)
                    System.out.print("0");
                System.out.println();
                validityFlag = false;
            }
        }
        return validityFlag;
    }

    /**
     * This routine repairs the chain. It checks the hashes of each block and ensures that any illegal hashes are recomputed.
     * After this routine is run, the chain will be valid. The routine does not modify any difficulty values
     * It computes new proof of work based on the difficulty specified in the Block.
     */
    public void repairChain() {
        // don't check previous hash for genesis block
        Block previousBlock = getBlock(0);
        String previousHash = getComputedHashForBlock(previousBlock);
        if (!Utils.hasLeadingZeroes(previousHash, previousBlock.getDifficulty()))
            previousBlock.proofOfWork();

        for (int i = 1; i < getChainSize(); i++) {
            Block currentBlock = getBlock(i);
            currentBlock.setPreviousHash(previousHash);
            String currentHash = getComputedHashForBlock(currentBlock);
            if (!Utils.hasLeadingZeroes(currentHash, currentBlock.getDifficulty())) {
                computeValidProofOfWork(currentBlock);
            }
            // Keep track of the valid previous hash
            previousHash = getComputedHashForBlock(currentBlock);
        }
    }

    /**
     * Recomputes the nonce for which the object transaction data was modified.
     * Called by repairChain
     */
    private void computeValidProofOfWork(Block b) {
        b.proofOfWork();
    }

    /**
     * Computes proof of work the same way a block would - using the nonce
     * @param b Block whose proof of work needs to be computed
     * @return Hex string of the block's hash for its given nonce
     */
    private String getComputedHashForBlock(Block b) {
        String concatenatedString;
        concatenatedString = b.getIndex() + b.getTimestamp().toString()
                + b.getData() + b.getPreviousHash()
                + b.getNonce() + b.getDifficulty();
        return Utils.getSHA256Hash(concatenatedString);
    }
}
