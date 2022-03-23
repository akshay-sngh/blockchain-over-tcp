# blockchain-over-tcp
Simulation of a Proof Of Work consesus blockchain over a TCP network of nodes.

 * This project simulates a simple BlockChain over a network in a client-server setting.
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
