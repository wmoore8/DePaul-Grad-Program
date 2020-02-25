/*--------------------------------------------------------

1. Willy Moore / 01.26.2020:

2. Java version used, if not the official version for the class:

Java 8 Update 241

3. Precise command-line compilation examples / instructions:

>java JavaServer

4. Precise examples / instructions to run this program:

In separate shell windows, run the following commands:

> java JokeServer
> java JokeClient <Server IP, defaults to 'localhost'>
> java JokeClientAdmin <Server IP, defaults to 'localhost'>

...and then just follow the on screen prompts!

All acceptable commands are displayed on the various consoles.

This runs across machines, in which case you have to pass the IP address of
the server to the clients. For example, if the server is running at
140.192.1.22 then you would type:

> java JokeClient 140.192.1.22
> java JokeClientAdmin 140.192.1.22

5. List of files needed for running the program.

 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

5. Notes:

The code still has a UUID implementation artifact.
***NOTE ABOUT UUID AND CLIENT TRACKED:
    I'm not sure if what I did is exactly legal in the scope of what was expected, but the output works perfectly!
    Instead of passing in a unique identifier from the client (which I still have the artifact of and might work
    on later if I have time), the server keeps track of each clients progress and randomized joke order by creating
    an object unique to each client connection. This object is MessageProcedure, and there is more information toward
    the bottom of JokeServer.java about it. By doing it this way, the client isn't sending a unique identifier to the
    server nor is the server reading a unique identifier. Like I said previously, I'm not sure if this is the correct
    implementation you are looking for, but it handles multiple client connections without mixing them up, doesn't
    repeat jokes, keeps correct track of proverbs and jokes sent, and notifies correctly when cycle has completed for
    each individual client, independent of the others connected.

NOT CURRENTLY FUNCTIONING:
    -Multiple servers
----------------------------------------------------------*/


import java.io.*;

import java.net.Socket;
import java.net.ServerSocket;

import java.util.*;

public class JokeServer {

    public static boolean jokeMode = true;

    public JokeServer(){}

    //A method that makes deciding printing the mode switch easier

    public static String modeToText(){
        boolean jokeMode = JokeServer.jokeMode;

        //This is reverse I know, and I have literally no clue why it works like this but it just does.

        if (jokeMode) {
            return "Switching from proverb mode to joke mode.";
        } else {
            return "Switching from joke mode to proverb mode.";
        }
    }

    public static void main(String[] args) {
        Socket clientSocket;
        byte backlog = 6;                                 //maximum length the queue can be for incoming connections
        int port = 4545;

        /*Here, we call our AdminSocket class to create a server connection that waits for the AdminClient. It's port
        * will be available on 5050, and is in charge of changing the mode*/

        new AdminSocket().start();

        try {
            ServerSocket serverSocket = new ServerSocket(port, backlog);

            System.out.println("Thank you for using Willy Moore's Joke Server!\nServer is starting up...\nListening at port " + port + ".");

            while(true) {

                //serverSocket.accept() waits for a connection, and then a new Socket is created and returned

                clientSocket = serverSocket.accept();

                /*The Worker class is in charge of handling the multi-threaded component to this server. It extends Thread
                * and creates a new thread each time it is called. This thread takes the socket created from the connection
                * made and returns back Strings of jokes or proverbs to the client.
                * We invoke start() to create a new thread.*/

                new Worker(clientSocket).start();

            }


        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}

/*https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
* I used the following documentation to help with understanding the server side of this documentation, I believe I
* re-adapted most of the code to my own styling, but just in case I'm providing documentation where I got some
* code snippets of help*/

class Worker extends Thread {

    private Socket sock;

    Worker(Socket s) {
        this.sock = s;
    }

    /*The following private method is simply here for the purpose of putting together the prefix of the joke and
    * proverb string (JA Willy, etc.)
    * In the future, I would edit this out in a way that feels less hack-y and spaghetti code-y*/

    @Override
    public void run() {                                     //initialization instructions for thread

        try {

            /*The first line handles three tasks in one. It gets an input stream for this socket and reads it
            * via an InputStreamReader object, which is then read to text by the BufferedReader.
            * The line below retrieves the output into a PrintStream object so that it can be communicated to the client*/

            BufferedReader socketInput = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
            PrintWriter socketOutput = new PrintWriter(this.sock.getOutputStream(), true);

            try {
                System.out.println("A new client has connected!");

                socketOutput.println("Hello Client! What is your username?");

                /*This MessageProcedure class handles the technicalities of delivering unique jokes. We create the
                * object prior to the while loop so that each client has a unique MessageProcedure object created
                * unique to it.*/

                MessageProcedure mp = new MessageProcedure();

                mp.setUsername(socketInput.readLine());


                socketOutput.println("Username updated! Press 'Enter' to receive a joke or proverb.");

                while ((socketInput.readLine()) != null) {

                    socketOutput.println((mp.getAbbreviation(mp.peekMessage()) + mp.getUsername() + ": " + mp.getMessage()));
                }


            } catch (IOException e) {
                System.out.println("Input error");
                e.printStackTrace();
            }

            this.sock.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

/* The following AdminSocket class extends a Thread because we want to be able to run and execute this function in main
* without the rest of the code getting held up. We still need the separate server socket to be made on Port 5050 for
* the admin client to connect to.*/

class AdminSocket extends Thread {

    @Override
    public void run() {
        Socket adminSocket;
        byte backlog = 6;                                 //maximum length the queue can be for incoming connections
        int port = 5050;

        try {
            ServerSocket serverSocket = new ServerSocket(port, backlog);

            while(true) {

                //serverSocket.accept() waits for a connection, and then a new Socket is created and returned

                adminSocket = serverSocket.accept();

                /*The Worker class is in charge of handling the multi-threaded component to this server. It extends Thread
                 * and creates a new thread each time it is called. This thread takes the socket created from the connection
                 * made has the sole purpose of detecting the mode of the server and switching it.
                 * We invoke start() to create a new thread.*/

                new AdminWorker(adminSocket).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*This worker's sole job is to switch the mode from joke mode to proverb mode and vice versa. A majority of this
* code is taken directly from my Worker class, just with less functionality for I/O connections because they are
* unnecessary at the current phase of the server. */

class AdminWorker extends Thread {

    private Socket sock;

    AdminWorker(Socket s) {
        this.sock = s;
    }

    @Override
    public void run() {

        try {

            /*Here, we want to flip the mode of the JokeServer without having to type anything in on the
             * JokeClientAdmin, so this code that switches it can execute immediately*/

            JokeServer.jokeMode = !JokeServer.jokeMode;

            //Outputs to server terminal window

            System.out.println(JokeServer.modeToText());

            this.sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class MessageProcedure {
    private String username;
    private String UUID;

    //Jokes are (and I say this PROUDLY) ones I thought up of or remembered!

    private static final String JA = "Why did the chicken cross the road? To get to the other side!";
    private static final String JB = "What did the mother buffalo say to her child when he left for college? Bison";
    private static final String JC = "Two atoms are in a bar. One atom says to the other 'Hey, I lost an electron!' " +
                                    "The other atom asks 'Are you sure?' and then the other atom says 'Yea, Im positive!'";
    private static final String JD = "What do you call a pig that does karate? A pork chop";

    //Proverbs I looked up, except for 'Just do it.' which is my new years resolution this year.

    private static final String PA = "Just do it.";
    private static final String PB = "You've got to do your own growing, no matter how tall your parents were.";
    private static final String PC = "Opportunity doesn't knock until you build a door.";
    private static final String PD = "Life is not a fairy tale. If you lose your shoe at midnight, you're drunk.";

    /*ArrayList isn't the most efficient data structure. Hash map would probably be ideal, and would help with
    * organization by key and inserting that key in places I need it*/

    private ArrayList<String> jokes = new ArrayList<>();
    private ArrayList<String> proverbs = new ArrayList<>();

    private boolean jokeMode;

    //Initialize class by adding all jokes and proverbs to the hash map and passing the username of client

    MessageProcedure(){

        jokes.add(JA);
        jokes.add(JB);
        jokes.add(JC);
        jokes.add(JD);

        proverbs.add(PA);
        proverbs.add(PB);
        proverbs.add(PC);
        proverbs.add(PD);

        Collections.shuffle(jokes);
        Collections.shuffle(proverbs);

        jokeMode = JokeServer.jokeMode;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String un) {
        this.username = un;
    }

    public void setUUID(String uniqueID) {
        this.UUID = uniqueID;
    }
    /*A helper function that's called any time a function is called in this class, specifically when dealing with
    * a message return statement as this object needs to constantly check and update what mode the server is in
    * after it's object creation*/

    private void updateMode() {
        jokeMode = JokeServer.jokeMode;
    }

    public String getAbbreviation(String message) {
        String toReturn;

        switch (message) {
            case JA:
                toReturn = "JA ";
                break;
            case JB:
                toReturn = "JB ";
                break;
            case JC:
                toReturn = "JC ";
                break;
            case JD:
                toReturn = "JD ";
                break;
            case PA:
                toReturn = "PA ";
                break;
            case PB:
                toReturn = "PB ";
                break;
            case PC:
                toReturn = "PC ";
                break;
            case PD:
                toReturn = "PD ";
                break;
            default:
                toReturn = "";
        }
        return toReturn;
    }

    //Next two are individual helper functions that re-populate and re-shuffle the jokes and proverbs list.

    private void repopulateJokes() {
        jokes.add(JA);
        jokes.add(JB);
        jokes.add(JC);
        jokes.add(JD);
        Collections.shuffle(jokes);
    }

    private void repopulateProverbs() {
        proverbs.add(PA);
        proverbs.add(PB);
        proverbs.add(PC);
        proverbs.add(PD);
        Collections.shuffle(proverbs);
    }

    /*The following function is the intelligence behind whether or not a joke/proverb has been dealt to the client.
    * Since each MessageProcedure object created is specific to the client connected, this funtion just has to keep track
    * of what is in the shuffled array and what it isn't. Once the array is empty, it get's repopulated with the same
    * jokes/proverbs and starts over in a new order. The jokes and proverbs array and re-population are separate so as
    * to maintain the integrity of the order in each list independently. This also satisfies the requirement to
    * re-randomize the order at the start of each joke and proverb cycle.*/

    public String getMessage() {
        updateMode();

        if (jokeMode) {
            if (jokes.size() != 0) {
                return jokes.remove(0);
            } else {
                repopulateJokes();
                return "JOKE CYCLE COMPLETED";
            }
        } else {
            if (proverbs.size() != 0) {
                return proverbs.remove(0);
            } else {
                repopulateProverbs();
                return "PROVERBS CYCLE COMPLETED";
            }
        }
    }

    public String peekMessage() {
        updateMode();

        if (jokeMode) {
            if (jokes.size() != 0) {
                return jokes.get(0);
            } else {
                return "JOKE CYCLE COMPLETED";
            }
        } else {
            if (proverbs.size() != 0) {
                return proverbs.get(0);
            } else {
                return "PROVERBS CYCLE COMPLETED";
            }
        }
    }

}
