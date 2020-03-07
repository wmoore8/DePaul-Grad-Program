/* Blockchain.java

1. Willy Moore

2. Java 8 Update 241

3.

To compile and run on WINDOWS:
    1. In terminal, cd to Blockchain folder

    2. javac -cp ".;gson-2.8.2.jar" Blockchain.java

        (On three separate terminal windows)
    3. java -cp ".;gson-2.8.2.jar" Blockchain 0
    4. java -cp ".;gson-2.8.2.jar" Blockchain 1
    5. java -cp ".;gson-2.8.2.jar" Blockchain 2


To compile and run on MAC:
    1. In terminal, cd to Blockchain folder

    2. javac -cp ".:gson-2.8.2.jar" Blockchain.java

        (On three separate terminal windows)
    3. java -cp ".:gson-2.8.2.jar" -Djava.net.preferIPv4Stack=true Blockchain 0
    4. java -cp ".:gson-2.8.2.jar" -Djava.net.preferIPv4Stack=true Blockchain 1
    5. java -cp ".:gson-2.8.2.jar" -Djava.net.preferIPv4Stack=true Blockchain 2

4) Files needed:
Blockchain.java
BlockInput0.txt
BlockInput1.txt
BlockInput2.txt


5. Notes:

SOURCES:

(The following was provided by Clark Elliott)

    Taken from https://condor.depaul.edu/~elliott/435/hw/programs/Blockchain/BlockJ.java with slight edits
    and my own comments explaining each step in detail

    Reading lines and tokens from a file:
    http://www.fredosaurus.com/notes-java/data/strings/96string_examples/example_stringToArray.html
    Good explanation of linked lists:
    https://beginnersbook.com/2013/12/linkedlist-in-java-with-example/
    Priority Queue:
    https://www.javacodegeeks.com/2013/07/java-priority-queue-priorityqueue-example.html

(The following I found and used as sources for learning to help with this assignment. No code was copied, though
fragments were used as inspiration into my own code)

https://dzone.com/articles/a-simplest-block-chain-in-java
    |
    For a basis on how blockchain works at it's most simplest level. This is where I started

https://www.geeksforgeeks.org/sha-256-hash-in-java/
    |
    Learning about SHA-256 hashing as opposed to a regular hash in Java

https://www.developer.com/java/data/how-to-multicast-using-java-sockets.html
    |
    Reference for learning about multi-casting.

DISCLAIMER: I really didn't get this to totally work :( and I'm really upset about it.

What I have:

    -It's like I have all the working components of the assignment (except for the simplified "Work" puzzle), but not
    working together.

    - Working blockchain with verification and validation methods (see validate()). createBlock() also dynamically
    adds blocks based on the blocks previous hash, concatenating 2 pieces of data to create a current hash (I know it
    should concatenate 3, but, as I'll soon go over, my biggest roadblock with this assignment is synchronizing information
    across the 3 processes.

    -Block class that is complete, allowing for the blockchain to be made

    -Public/private key generation, as well as commented code from Elliott's source on the three methods having to
    work with it (verifySig(), signData(), writeKey(), readKey()-Sort of, this method is the one major area I struggled
    with.

    -A bunch of methods for reading and writing to JSON, most work except the readKey() because Gson HATES ME and
    I have no clue how to read in an object that isn't a class I made.

    -Multicast server class that is multithreaded, actually works very well and I like what I coded and organized. I was
    thinking about making separate methods in it that specialized what type of listener port (KeyPair Listener, Blockchain
    Listener, etc.) and specifying which one to use in the constructor before executing the thread.

The issues that made me choke up and almost cry:

    - Present in the readKey() method, I can't return objects that aren't defined in this file. Specifically, this line:

        String skey = gson.fromJson(reader, String.class);
        ----------OR--------(what I had before trying this)--------
        KeyPair key = gson.fromJson(reader, KeyPair.class);

        It didn't like the latter because PrivateKey and PublicKey are interfaces that KeyPair implements, so I guess gson
        didn't like that. The first didn't work for a reason I am yet to find out. When I tried:

        Block block = gson.fromJson(reader, Block.class);

        That worked! But without the public/private key, I felt stuck on that before going into the next part of the assignment
        where I'm reading and writing from the BlockchainLedger.

    - The biggest issue was not being able to figure out how to instantiate an object or data structure that was shared
    between the three processes. Originally, I had it in the StartProcess class, which I realized later that if I am making
    threads of that class, obviously the blockchain arraylist isn't going to be the same. But even when I made it static
    and part of the Blockchain class (essentially the main() class), it still wouldn't individually add the blocks from
    the 3 processes to it.

    This same problem persisted when trying to get the public keys of the three processes. Maybe I was confused on the
    assignment, but was I supposed to write all the information to BlockchainLedger.json and then read it back to whatever
    object/method/class that needed the information?

    Regardless, the major stump of the project was definitely just not being able to share information effectively. I can
    multicast messages and strings from each process to any process, effectively shutting off the listener and re-starting it
    back up whenever I need to. But the complication from where to read in data and not being able to get gson to
    convert objects back from the json ruined me.

What I dont' have:

    -Work puzzle

    -Processes competing to solve the block and write it to JSON where the information is updated to the other processes

    -Sharing public key information

    -Basically the essence of the assignment which is so sad because I feel like I am so close with all the separate
    components, but just not being able to weave it all together.

If I had more time, I would definitely re-structure this so that each process was it's own class instead of one
incredibly, annoyingly large class (StartProcess) that holds pretty much everything. This way, the specific functions
of P0 and P2 can be specifically dispersed (ie. key pair method is only multi-casted is only in P2 class). I also don't
think I needed to multithread the processes, just the Multicast listeners. Maybe re-writing this code in a different
organization would turn on some lightbulbs in my head.

-----------------------------------------------------------------------*/
//JSON/Gson Imports

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.Reader;

//Hashing Related Imports

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

//General Imports

import java.util.*;
import java.security.*;

//Server Imports

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.InetAddress;

public class Blockchain {

    public static ArrayList<Block> blockchain = new ArrayList<>();

    public static void main(String[] args) {

        if (args.length != 1) {

            System.err.println("Please specify exactly one process: 0, 1, or 2\nPlease remember to start 2 last!");

        } else {

            String processToRun = args[0];

            switch(processToRun) {
                case "0":
                    new StartProcess("0").start();
                    break;
                case "1":
                    new StartProcess("1").start();
                    break;
                case "2":
                    new StartProcess("2").start();
                    break;
                default:
                    System.err.println("Please specify exactly one process: 0, 1, or 2\nPlease remember to start 2 last!");
                    break;
            }
        }
    }

}

class StartProcess extends Thread {

    private String processNumber;
    private String fileName;

    /*static blockChain ArrayList is the initial list we add our blocks to. Once that is ready
     we will add them to the blockPriorityQueue I THINK*/

    Queue<Block> blockPriorityQueue = new PriorityQueue<>(4, BlockTSComparator);

    public StartProcess(String process) {
        this.processNumber = process;
    }

    //Starts the process based off the argument given in main()
    @Override
    public void run() {
        switch(processNumber) {
            case "0":
                startP0();
                break;
            case "1":
                startP1();
                break;
            case "2":
                startP2();
                break;
            default:
                System.err.println("Please specify exactly one process: 0, 1, or 2\nPlease remember to start 2 last!");
                break;
        }
    }

    /*This process is in charge of writing the information to BlockchainLedger.json. */

    public void startP0() {

        PublicKey publicKey;
        PrivateKey privateKey;
        int keyPort = 4710;

        fileName = "BlockInput0.txt";

        System.out.println("Hello from Process " + processNumber + "! :-)");

        try {

            KeyPair temp = generateKeyPair(990);
            publicKey = temp.getPublic();
            privateKey = temp.getPrivate();

            /*Receive public key multicast*/

            MulticastServer mskeys = new MulticastServer(keyPort);
            mskeys.start();

            //Just to prove something on the checklist, I opened another port on the same process

            MulticastServer msblocks = new MulticastServer(4810);
            msblocks.start();

            //Only continue once Process 2 has started and multicasted the public keys

            while(mskeys.isAlive()) {

            }

            readBlockInputFile(fileName, processNumber, Blockchain.blockchain);



            //writeBlockOutputFile(blockchain);

        } catch (Exception e) {
            System.err.println("Process 0 failed:");
            e.printStackTrace();
        }

    }

    public void startP1() {

        PublicKey publicKey;
        PrivateKey privateKey;
        int keyPort = 4711;

        fileName = "BlockInput1.txt";

        System.out.println("Hello from Process " + processNumber + "! :-)");

        try {

            KeyPair temp = generateKeyPair(991);
            publicKey = temp.getPublic();
            privateKey = temp.getPrivate();

            /*Receive public key multicast*/

            MulticastServer mskeys = new MulticastServer(keyPort);
            mskeys.start();

            writeKey(publicKey);

            //Only continue once Process 2 has started and multicasted the public keys

            while(mskeys.isAlive()) {

            }

            readBlockInputFile(fileName, processNumber, Blockchain.blockchain);


        } catch (Exception e) {
            System.err.println("Process 1 failed:");
            e.printStackTrace();
        }
    }

    /*This process is unique in that it will trigger the multicast of the public keys. Once these public keys of the
    * other processes are read and verified by this process, P2 will then multicast a message to start the action. */

    public void startP2() {

        PublicKey publicKey;
        PrivateKey privateKey;
        int keyPort = 4712;

        fileName = "BlockInput2.txt";

        System.out.println("Hello from Process " + processNumber + "! :-)");

        try {

            /*We can now multicast this public key array in JSON String format to the other processes.*/

            KeyPair temp = generateKeyPair(992);
            publicKey = temp.getPublic();
            privateKey = temp.getPrivate();

            MulticastServer mskeys = new MulticastServer(keyPort);
            mskeys.start();

            MulticastServer.multicastMessage("sup Process 2", 4712);
            MulticastServer.multicastMessage("break", 4712);

            while(mskeys.isAlive()) {

            }

            MulticastServer.multicastMessage("hi Process 0", 4710);
            MulticastServer.multicastMessage("break", 4710);

            MulticastServer.multicastMessage("hey Process 1", 4711);
            MulticastServer.multicastMessage("break", 4711);

            readBlockInputFile(fileName, processNumber, Blockchain.blockchain);
            writeBlockOutputFile(Blockchain.blockchain);

            Thread.sleep(2000);

            MulticastServer.multicastMessage("Ok, this is all I have really!! Thanks for tuning in :~)", 4810);
            MulticastServer.multicastMessage("break", 4810);



        } catch (Exception e) {
            System.err.println("Process 2 failed:");
            e.printStackTrace();
        }

    }

    /*Originally, I had a createBlockchain() method here where it would create the entire chain. This wasn't dynamic
    * and couldn't handle more than a set amount of Blocks on the chain, so I got (just a little) smarter and made a
    * createBlock() method that returns a new block based on the hash of the previous block. It also has logic in place
    * that decides if it's the first block in the chain, although this isn't very effective because of the pitfall I
    * encountered stated above. */

    private Block createBlock(Block previousBlock, ArrayList<Block> blockchain) {

        String prevHash;
        int count;

        /*Decide what the previous hash is, sort of a base case for an empty blockchain*/

        if(blockchain.size() == 0 || previousBlock == null) {
            prevHash = "0";
        } else {
            prevHash = previousBlock.getCurrentHash();
        }

        Block blockToReturn = new Block(prevHash);
        blockchain.add(blockToReturn);

        return blockToReturn;
    }

    /*Inspired from the comparator from Clark Elliott
     *https://condor.depaul.edu/~elliott/435/hw/programs/Blockchain/BlockInputG.java
     * This is currently unused in my project because the priority queue is unused, but I'll leave it in
     * here if it warrants anything.*/

    private static Comparator<Block> BlockTSComparator = new Comparator<Block>() {
        @Override
        public int compare(Block b1, Block b2){
            if (b1.getTimeStamp().equals(b2.getTimeStamp())) {return 0;}
            if (b1.getTimeStamp() == null) {return -1;}
            if (b2.getTimeStamp() == null) {return 1;}
            return b1.getTimeStamp().compareTo(b2.getTimeStamp());

        }
    };

    /*This validation check is based off the validate function from this website. I added my own variations to it
      based off the assignment requirements.
      https://dzone.com/articles/a-simplest-block-chain-in-java

      This really doesn't do much aside from error checking in the blockchain to make sure that the hash of a previous
      block is correctly recorded in the current block. Was going to be used toward the end of the assignment.*/

    private boolean validate(ArrayList<Block> chainToCheck) {

        Block currentBlock;
        Block previousBlock = null;
        boolean toReturn = true;

        //Loop through blockchain "backwards" since blocks are prepended

        for (int i = chainToCheck.size() - 1; i >= 0; i--) {

            if (previousBlock == null) {
                previousBlock = chainToCheck.get(i);

            } else {
                currentBlock = chainToCheck.get(i);

                /*This is the part that checks that our previous hash is equals to the current hash */

                if (!previousBlock.getPreviousHash().equals(currentBlock.getCurrentHash())) {
                    toReturn = false;
                    break;
                }

                previousBlock = currentBlock;

            }
        }

        return toReturn;

    }

    /*Adapted from https://condor.depaul.edu/~elliott/435/hw/programs/Blockchain/BlockJ.java*/

    private KeyPair generateKeyPair(long seed) throws Exception {
        //Singleton class, acquiring instance of it with the algorithm specified (being "RSA" in this case)
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        //Another singleton class where we can get a random instance based on SHA1PRNG algorithm.
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        //Supplements current seed of rng to create randomized hash.
        rng.setSeed(seed);
        //Initialize kpg with a keysize of 1024 and the randomized instance
        kpg.initialize(1024, rng);

        return kpg.generateKeyPair();
    }

    /*Adapted from https://condor.depaul.edu/~elliott/435/hw/programs/Blockchain/BlockJ.java
     * Would've been used to sign data with a processes private key to be later read by another process with the
     * signing processes' public key that was to be multicasted to the other processes.*/

    private byte[] signData(byte[] data, PrivateKey key) throws Exception {
        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initSign(key);
        signer.update(data);
        return (signer.sign());
    }

    private void writeKey(PublicKey key) throws Exception {

        //Create Gson object for writing
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //Turn public key into string for reading later on
        String skey = key.toString().concat(processNumber);
        //Turn into JSON string
        String json = gson.toJson(skey);

        //Print statement check
        System.out.println("JSON Public Key Record ==> " + json);


        try (FileWriter writer = new FileWriter("publicKeys.json")) {
            gson.toJson(key, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readKey(String fileName) throws Exception {

        Gson gson = new Gson();

        try (Reader reader = new FileReader(fileName)) {

            String skey = gson.fromJson(reader, String.class);
            System.out.println(skey);
        }

    }

    /*Adapted from https://condor.depaul.edu/~elliott/435/hw/programs/Blockchain/BlockJ.java
     * Would've been used to verify data with the signing processes public key that was to be multicasted to the other processes.*/

    private boolean verifySig(byte[] data, PublicKey key, byte[] sig) throws Exception {
        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initVerify(key);
        signer.update(data);

        return (signer.verify(sig));
    }

    /*Adapted from https://condor.depaul.edu/~elliott/435/hw/programs/Blockchain/BlockInputG.java
    * This method was the bread and butter of reading in the text files and creating blocks using my Block class with all
    * the information they need in them about patient data. */

    private synchronized void readBlockInputFile(String fileToRead, String processNumber, ArrayList<Block> myBlockchain) throws Exception {

        StringWriter sw = new StringWriter();
        String[] tokens = new String[10];
        String line;

        //Print statement check

        System.out.println("Using file: " + fileToRead);

        try {

            //Keep track of our position in the blockchain when reading information into blocks

            int blockFromChainIndex = 0;
            BufferedReader br = new BufferedReader(new FileReader(fileToRead));

            while((line = br.readLine()) != null) {

                /*Here, createBlock() is called, passing in null if it's expected to be the first block in the blockchain*/

                Block myBlock;

                if (blockFromChainIndex == 0) {
                    myBlock = createBlock(null, myBlockchain);
                } else {
                    myBlock = createBlock(myBlockchain.get(blockFromChainIndex - 1), myBlockchain);
                }

                /*Waits a second in between each line of the text file being read. Why? In order to try and ensure
                 * different time stamps between each block.
                 *
                 * Lines 320 - 362 took inspiration from Clark Elliott
                 * https://condor.depaul.edu/~elliott/435/hw/programs/Blockchain/BlockInputG.java
                 *
                 * Can probably be omitted when we do "work" */

                try {
                    Thread.sleep(1001);
                } catch (InterruptedException e) {
                    System.err.println("Thread couldn't sleep due to Interrupted Exception: " + e);
                }

                /*This part has been sectioned of to create a timestamp because of it's complexity relative to the
                 * rest of the readable information*/

                Date date = new Date();
                String timeStamp = String.format("%1$s %2$tF.%2$tT", "", date);
                /*Avoids timestamp collisions in the miraculous case that the timestamps are the exact same.
                  Doesn't this mean I don't need the equals operator in the comparator? */
                myBlock.setTimeStamp(timeStamp.concat("." + processNumber));

                //Print statement check

                System.out.println("Timestamp: " + timeStamp);

                /*This section creates a UUID for each block, and then adds all fields of important data to the block*/

                String uuid = UUID.randomUUID().toString();
                myBlock.setBlockUUID(uuid);

                /*Rest of the fields (firstName, lastName, etc.) using the tokens array initialized earlier
                 * I did it with the actual integer value in the array instead of instantiating integers above
                 * as to what index each piece of information is. Not very flexible coding style BUT it works for
                 * this assignment and I want to omit unnecessary information, this file is larger enough as it is.*/

                tokens = line.split(" +");

                myBlock.setFirstName(tokens[0]);
                myBlock.setLastName(tokens[1]);
                myBlock.setDOB(tokens[2]);
                myBlock.setSocialSecurity(tokens[3]);
                myBlock.setDiagnosis(tokens[4]);
                myBlock.setTreatment(tokens[5]);
                myBlock.setPrescription(tokens[6]);

                blockFromChainIndex++;
            }
        } catch (Exception e) {
            System.err.println("JSON read failed: " + e);
        }

    }

    /*Adapted from https://condor.depaul.edu/~elliott/435/hw/programs/Blockchain/BlockInputG.java
    * Nothing special, just writes the blockchain passed in to BlockchainLedger.json
    * I couldn't get all three processes to unite to a single blockchain for this.*/

    private void writeBlockOutputFile(ArrayList<Block> myBlockchain) throws Exception {

        //Now we write information to the JSON

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(myBlockchain);

        //Print check statement

        System.out.println("String list is: " + json);

        /*If we needed to write to different JSON's, we'd just have to let this function accept it as a parameter
        * and then pass the parameter into FileWriter. However, for the scope of this assignment, that is unnecessary.*/

        try (FileWriter writer = new FileWriter("BlockchainLedger.json")){
            gson.toJson(myBlockchain, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

/*This class is called whenever I needed to multicast something. It has two methods,
    multicastMessage() ==> sends message
    receiveMulticastMessage() ==> receives message

    The latter is executed whenever I make a thread of this class so that I can have listeners running while the process
    continues on. This was especially useful when trying to synchronize the processes to wait for process 2 before continuing on

   https://www.developer.com/java/data/how-to-multicast-using-java-sockets.html*/

class MulticastServer extends Thread {

    int port;

    public MulticastServer(int port) {
        this.port = port;
    }

    @Override
    public void run(){
        try {
            receiveMulticastMessage(port, "break");
        } catch (Exception e) {
            System.out.println("Server thread execution failed:");
            e.printStackTrace();
        }
    }

    //Send a message over the specified port, not much else!

    public static void multicastMessage(String message, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        //230.0.0.0 is one of the IP addresses built for multicasting
        InetAddress group = InetAddress.getByName("230.0.0.0");

        //Turn string passed in to bytes so that it can be turned into a packet object to send over socket.
        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, group, port);

        //Send data and close socket
        socket.send(packet);
        socket.close();

    }

    /*This method is called every time a thread of a multicast server is made. This is because this method creates a
    * "listener" on a specified port that runs until the finalMessage parameter is sent by a process, which then terminates
    * the listener by breaking the while loop.*/

    private void receiveMulticastMessage(int port, String finalMessage) throws IOException {

        //Create buffer for incoming messages, initialize socket, and join IP Address
        byte[] buffer = new byte[1024];
        MulticastSocket socket = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName("230.0.0.0");
        socket.joinGroup(group);

        try {

            //Receive messages until key string is typed in finalMessage parameter, breaking the listening port.
            while (true) {

                //Print statement check

                System.out.println("Listening...");

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());

                if (finalMessage.equals(msg)) {
                    System.out.println("Exiting...");
                    break;
                }

                //Print statement check

                System.out.println("Multicast message received! -->" + msg);


            }

        } catch (IOException e) {
            System.err.println("Multicast receive interrupted");
            e.printStackTrace();
        }

        socket.leaveGroup(group);
        socket.close();
    }

}

/*This class is complete and done, minus the work solving which I don't think I was planning on doing in here anyways.
* It has all the necessary data fields for each block. I also used the same SHA256 hashing method to make a random String
* R to solve puzzles. I was planning on changing this eventually when I did the solving work part, but I was also thinking
* of ways that I could make R in any way a winning guess with my own silly puzzle. */

class Block {

    //Data fields for public information relating to block

    private String blockUUID;

    //Data fields for blockchain-specific information

    private String currentHash;
    private String previousHash;
    private String randomStringR;
    private String timeStamp;

    //Data fields for JSON information of patient

    private String firstName;
    private String lastName;
    private String DOB;
    private String socialSecurity;
    private String diagnosis;
    private String treatment;
    private String prescription;

    /*Constructor that requires information about the previous block and the data in this specific block

        This constructor automatically generates:
        1. Random String R needed to solve the puzzle
        2. Current (winning) hash S of the block

     */

    public Block(String prevHash) {
        this.previousHash = prevHash;

        //Uses same SHA-256 hash to make a random string to solve puzzles! Might change later but see no reason to yet.

        try {
            this.randomStringR = hashSHA256(new Random(5).toString());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Creating SHA-256 hash failed: " + e);
        }

        /*Automatically create hash for block using SHA-256 hashing by concatenating the three necessary pieces of information
        *NOTE: I use blockUUID temporarily as the "data" component for hashing 3 elements together requirement.*/

        try {
            this.currentHash = hashSHA256((new Integer(Arrays.hashCode(new String[]{previousHash, randomStringR, blockUUID}))).toString());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Creating SHA-256 hash failed: " + e);
        }

    }

    //Getters

    public String getBlockUUID() {
        return blockUUID;
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getRandomStringR() {
        return randomStringR;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDOB() {
        return DOB;
    }

    public String getSocialSecurity() {
        return socialSecurity;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getPrescription() {
        return prescription;
    }

    //Setters

    public void setBlockUUID(String blockUUID) {
        this.blockUUID = blockUUID;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public void setSocialSecurity(String socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    //Creates a SHA256 hash, necessary for constructor of this class

    private String hashSHA256(String toHash) throws NoSuchAlgorithmException {
        byte[] mdHash;

        MessageDigest mdInstance = MessageDigest.getInstance("SHA-256");

        mdHash = mdInstance.digest(toHash.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder((new BigInteger(1, mdHash)).toString(16));
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }



}