package blockchaintask1;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class that undergoes used operations
 */
public class Utils {

    /**
     * Computes SHA-256 hash of a given input string
     * @param inputString String whose hash needs to be computed
     * @return Hex representation of SHA256 hash
     */
    public static String getSHA256Hash(String inputString) {
        byte[] encodedBytes;
        String hexString = null;
        try {
            // Create a message digest and use it to get encoded bytes
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Get bytes from the digest
            encodedBytes = md.digest(inputString.getBytes(StandardCharsets.UTF_8));
            hexString = convertByteToHexadecimal(encodedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    /**
     * Method that converts an array of bytes to its hexadecimal representation
     * @param byteArray array bytes to be encoded
     * @return hexadecimal value of byteArray
     */
    private static String convertByteToHexadecimal(byte[] byteArray) {
        // Iterating through each byte in the array
        StringBuilder sb = new StringBuilder();
        for (byte i : byteArray) {
            sb.append(String.format("%02X", i));
        }
        return sb.toString();
    }

    /**
     * This method is different from convertByteToHexadecimal because it does not convert bytes to
     * hex string.
     * It is simply called to compute the hash one million times to give an estimate of hashes per second
     * @param simpleString Fixed string value, typically "0000000000"
     */
    public static void computeSimpleSHA256Hash(String simpleString) {
        try {
            // Create a message digest and use it to get encoded bytes
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.digest(simpleString.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the given hex hash has specified number (n) of 0s in the front
     * @param hash hex representation hash to be verified
     * @param n number of leading 0s the hash must have
     * @return boolean representing whether hash has n leading 0s
     */
    public static boolean hasLeadingZeroes(String hash, int n) {
        for (int i = 0; i < n; i++) {
            if (hash.charAt(i) != '0')
                return false;
        }
        return true;
    }
}
