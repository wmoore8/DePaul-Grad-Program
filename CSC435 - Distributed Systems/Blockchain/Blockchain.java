/* Blockchain.java

Version 2.1 2020-02-10, Fixed compile warning.

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

-----------------------------------------------------------------------*/
//JSON/Gson Imports

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.BufferedReader;

//Hashing Related Imports

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//General Imports

import java.util.*;
import java.text.*;

//Server Imports

import java.net.Socket;
import java.net.ServerSocket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.InetAddress;

/*TODO:
        1. Multicast a message from each process to each other
     */

public class Blockchain {

    public static void main(String[] args) {

        if (args.length != 1) {

            System.err.println("Please specify exactly one process: 0, 1, or 2\nPlease remember to start 2 last!");

        } else {

            String processToRun = args[0];

            switch(processToRun) {
                case "0":
                    new startProcess("0").start();
                    break;
                case "1":
                    new startProcess("1").start();
                    break;
                case "2":
                    new startProcess("2").start();
                    break;
                default:
                    System.err.println("Please specify exactly one process: 0, 1, or 2\nPlease remember to start 2 last!");
                    break;
            }
        }
    }

}

class startProcess extends Thread {

    private String processNumber;
    private String fileName;

    private String ipAddress = "239.0.0.0";

    /*blockChain ArrayList is the initial list we add our blocks to. Once that is ready
     we will add them to the blockPriorityQueue */

    Queue<Block> blockPriorityQueue = new PriorityQueue<>(4, BlockTSComparator);
    static ArrayList<Block> blockchain = new ArrayList<>();

    public startProcess(String process) {
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

        fileName = "BlockInput0.txt";

        System.out.println("Hello from Process " + processNumber + "! :-)");

        try {
            readBlockInputFile(fileName, processNumber, blockchain);

            Thread.sleep(2000);

            //writeBlockOutputFile(blockchain);

            MulticastServer.multicastMessage("*** *** *** HI FROM PROCESS " + processNumber + "! *** *** ***",5040);
            MulticastServer.multicastMessage("end", 5040);

            Thread.sleep(2000);

            receiveMulticastMessage(5040, "end");

            Thread.sleep(2000);

            receiveMulticastMessage(5040, "end");


        } catch (Exception e) {
            System.err.println("Process 0 failed: " + e);
        }

    }

    public void startP1() {

        fileName = "BlockInput1.txt";

        System.out.println("Hello from Process " + processNumber + "! :-)");

        try {
            readBlockInputFile(fileName, processNumber, blockchain);

            receiveMulticastMessage(5040, "end");

            Thread.sleep(2000);

            MulticastServer.multicastMessage("*** *** *** HI FROM PROCESS " + processNumber + "! *** *** ***",5040);
            MulticastServer.multicastMessage("end", 5040);

            Thread.sleep(2000);

            receiveMulticastMessage(5040, "end");

        } catch (Exception e) {
            System.err.println("Process 1 failed: " + e);
        }
//        for (int i = 0; i < blockchain.size(); i++) {
//            Block block = blockchain.get(i);
//            System.out.println("Block " + block.title +
//                                ":\nPrevious Hash: " + block.getPreviousHash() +
//                                "\nCurrent Hash: " + block.getCurrentHash() +
//                                "\nRandom String R: " + block.getRandomStringR() +
//                                "\nUUID: " + block.getBlockUUID() +
//                                "\nTime Stamp: " + block.getTimeStamp() +
//                                "\nFirst Name: " + block.getFirstName() +
//                                "\nLast Name: " + block.getLastName() +
//                                "\nDOB: " + block.getDOB() +
//                                "\nSocial Security: " + block.getSocialSecurity() +
//                                "\nDiagnosis: " + block.getDiagnosis() +
//                                "\nTreatment: " + block.getTreatment() +
//                                "\nPrescription: " + block.getPrescription() +
//                                "\nValidation Check: " + validate(blockchain));
//        }
    }

    public void startP2() {

        fileName = "BlockInput2.txt";

        System.out.println("Hello from Process " + processNumber + "! :-)");

        try {
            readBlockInputFile(fileName, processNumber, blockchain);

            receiveMulticastMessage(5040, "end");

            Thread.sleep(2000);

            receiveMulticastMessage(5040, "end");

            Thread.sleep(2000);

            MulticastServer.multicastMessage("*** *** *** HI FROM PROCESS " + processNumber + "! *** *** ***",5040);
            MulticastServer.multicastMessage("end", 5040);


        } catch (Exception e) {
            System.err.println("Process 2 failed: " + e);
        }

    }

    /*TODO COMMENT: Detailed comments, change variable names, and link source */

    private void receiveMulticastMessage(int port, String finalMessage) throws IOException {
        byte[] buffer = new byte[1024];
        MulticastSocket socket = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName("230.0.0.0");
        socket.joinGroup(group);

        try {
            while (true) {

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());

                if (finalMessage.equals(msg)) {
                    System.out.println("Exiting...");
                    break;
                }

                //Print statement check

                System.out.println("Waiting to receive multicast message...");

                //Print statement check

                System.out.println("Multicast message received! -->" + msg);

                /*TODO: We can change the statement in this if to break out of this loop whenever we want. */

            }

        } catch (IOException e) {
            System.err.println("Multicast receive interrupted");
            e.printStackTrace();
        }

        socket.leaveGroup(group);
        socket.close();
    }

    /*TODO COMMENT: What does this function do, and why is it better than what I had? */

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

    /*TODO COMMENT: How I got this code? Link source? Explain it in depth*/

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
      */

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

                /*This is the part that checks that our previous hash is equals to the current hash
                 TODO: Add validity checking for random string R here */

                if (!previousBlock.getPreviousHash().equals(currentBlock.getCurrentHash())) {
                    toReturn = false;
                    break;
                }

                previousBlock = currentBlock;

            }
        }

        return toReturn;

    }

    /*This class handles everything about reading in JSON information and writing it out.
     * Code utilized from https://condor.depaul.edu/~elliott/435/hw/programs/Blockchain/BlockInputG.java
     * with my own comments and alterations
     *
     * I've altered this function to take each block in the blockchain ArrayList and populate the blocks with the
     * necessary information about the patient. As of right now, it has only been tested when the json and the blockchain
     * match in size (that size being 4 currently).
     * TODO: Investigate ways to make the chain dynamic in regards to JSON size
     * TODO: This information will then write to disk in JSON format*/

    private void readBlockInputFile(String fileToRead, String processNumber, ArrayList<Block> myBlockchain) throws Exception {

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

                /*TODO COMMENT: What createBlock() does in this scenario*/

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
                 * TODO: Can probably be omitted when we do "work" */

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

    private void writeBlockOutputFile(ArrayList<Block> myBlockchain) throws Exception {

        //Now we write information to the JSON

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(blockchain);

        //Print check statement

        System.out.println("String list is: " + json);

        /*If we needed to write to different JSON's, we'd just have to let this function accept it as a parameter
        * and then pass the parameter into FileWriter. However, for the scope of this assignment, that is unnecessary.*/

        try (FileWriter writer = new FileWriter("BlockchainLedger.json")){
            gson.toJson(blockchain, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

/*TODO COMMENT: What parts of the source did I use and not use? What did I change (InetAddress.getLocalHost())
    Remember to heavily comment each part, explaining what's going on

   https://www.developer.com/java/data/how-to-multicast-using-java-sockets.html*/

class MulticastServer {

    int port;

    public MulticastServer(int port) {
        this.port = port;
    }

    public static void multicastMessage(String message, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        //I changed this code to getLocalHost() for simplicity. I think it should work?
        InetAddress group = InetAddress.getByName("230.0.0.0");
        byte[] byteMessage = message.getBytes();

        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, group, port);
        socket.send(packet);
        socket.close();

    }
}

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

        //Automatically create hash for block using SHA-256 hashing by concatenating the three necessary pieces of information

        try {
            this.currentHash = hashSHA256((new Integer(Arrays.hashCode(new String[]{previousHash, randomStringR}))).toString());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Creating SHA-256 hash failed: " + e);
        }

        //Uses same SHA-256 hash to make a random string to solve puzzles! Might change later but see no reason to yet.

        try {
            this.randomStringR = hashSHA256(new Random(5).toString());
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

    //TODO COMMENT: what does this function do? what are my sources for it? why the BigInteger part?

    private String hashSHA256 (String toHash) throws NoSuchAlgorithmException {
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