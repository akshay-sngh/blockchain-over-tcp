package blockchaintask1;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * This class represents a simple Block.
 * Each Block object has an index - the position of the block on the chain. The first block (the so called Genesis block) has an index of 0.
 * Each block has a timestamp - a Java Timestamp object, it holds the time of the block's creation.
 * Each block has a field named data - a String holding the block's single transaction details.
 * Each block has a String field named previousHash - the SHA256 hash of a block's parent.
 * This is also called a hash pointer. Each block holds a nonce - a BigInteger value determined by a proof of work routine.
 * This has to be found by the proof of work logic. It has to be found so that this block has a hash of the proper difficulty.
 * The difficulty is specified by a small integer representing the minimum number of leading hex zeroes the hash must have.
 * Each block has a field named difficulty - it is an int that specifies the minimum number of left most hex digits needed by a proper hash.
 * The hash is represented in hexadecimal.
 * If, for example, the difficulty is 3, the hash must have at least three leading hex 0's (or,1 and 1/2 bytes).
 * Each hex digit represents 4 bits.
 *
 * References:
 * 1. https://www.andrew.cmu.edu/course/95-702/examples/javadoc/blockchaintask0/Block.html
 * 2. https://stackoverflow.com/questions/23068676/how-to-get-current-timestamp-in-string-format-in-java-yyyy-mm-dd-hh-mm-ss
 * 3. http://www.java2s.com/Code/Jar/g/Downloadgson222jar.htm
 * 4. https://www.java67.com/2016/10/3-ways-to-convert-string-to-json-object-in-java.html
 * 5. https://howtodoinjava.com/gson/gson-serializedname/
 */
public class Block {
    private int index;
    // SerializedName changes the JSON key name to the 'value' field on serialization
    @SerializedName(value = "time stamp ")
    private Timestamp timestamp;

    @SerializedName(value = "Tx ")
    private String data;

    private int difficulty;

    @SerializedName(value = "PrevHash")
    private String previousHash;
    private BigInteger nonce;

    /**
     * This the Block constructor.
     * @param index Position within the chain. Genesis is at 0
     * @param timestamp  Time this block was added
     * @param data Transaction to be included on the blockchain
     * @param difficulty This is the number of leftmost nibbles that need to be 0
     */
    Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
        previousHash = "";
    }

    /**
     * This method computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
     * @return a String holding Hexadecimal characters
     */
    public String calculateHash() {
        String concatenatedString = index + timestamp.toString() + data + previousHash + nonce + difficulty;
        // Call utility method to compute hash
        return Utils.getSHA256Hash(concatenatedString);
    }

    /**
     * The proof of work methods finds a good hash. It increments the nonce until it produces a good hash.
     * This method calls calculateHash() to compute a hash of the concatenation of the index, timestamp, data,
     * previousHash, nonce, and difficulty.
     * If the hash has the appropriate number of leading hex zeroes, it is done and returns that proper hash.
     * If the hash does not have the appropriate number of leading hex zeroes, it increments the nonce by 1 and tries again.
     * It continues this process, burning electricity and CPU cycles, until it gets lucky and finds a good hash.
     * @return a String with a hash that has the appropriate number of leading hex zeroes.
     * The difficulty value is already in the block.
     * This is the minimum number of hex 0's a proper hash must have.
     */
    public String proofOfWork() {
        nonce = new BigInteger("-1");
        String hash;
        do {
            nonce = nonce.add(BigInteger.ONE);
            hash = calculateHash();
        } while (!Utils.hasLeadingZeroes(hash, difficulty));

        return hash;
    }

    // Getters and setters for attributes

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    /**
     * Override Java's toString method
     * @return JSON string representation of the entire Block
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}