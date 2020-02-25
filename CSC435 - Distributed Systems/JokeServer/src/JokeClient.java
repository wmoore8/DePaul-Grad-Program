/*--------------------------------------------------------

1. Willy Moore / 01.26.2020:

2. Java version used, if not the official version for the class:

Java 8 Update 241

3. Precise command-line compilation examples / instructions:

>java JokeClient <Server IP>

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

import java.util.UUID;

import java.net.Socket;

import static java.lang.System.exit;


public class JokeClient {

    private static UUID uniqueID;

    public UUID getUniqueID(){
        return uniqueID;
    }

    public JokeClient(){}

    private static void establishConnection(String host, String UUID) {

        try {
            String messageFromServer;
            String messageToServer;

            //Open a socket and enable I/O connections

            Socket sock = new Socket(host, 4545);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter outputStream = new PrintWriter(sock.getOutputStream(), true);

            //Create a buffered reader for user input in client end

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            //Server talks first, so we listen to what the server says first.

            while((messageFromServer = inputStream.readLine()) != null) {
                System.out.println(messageFromServer);

                /*The Server asks the client for their username, thus we
                * assign our message to the server in the username data field here*/

                messageToServer = userInput.readLine();

                if (!messageToServer.equals("q")) {

                    //Sends messageToServer string to server

                    outputStream.println(messageToServer);

                } else {

                    sock.close();
                    System.out.println("Shutting down...");
                    exit(0);

                }
            }

            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){

        /*This code block is taken from InetClient.java
         *this is how we get the connection to the server, defaults
         *to localhost if no IP address is given*/

        String serverIP;

        if(args.length < 1) {
            serverIP = "localhost";
        } else {
            serverIP = args[0];
        }

        //The following creates a unique ID associated to this particular client

        uniqueID = UUID.randomUUID();

        //Welcome message

        System.out.println("Willy Moore's Joke Client\nRunning on...\nServer: " + serverIP + "\nPort: 4545" +
                            "\nUnique ID: " + uniqueID.toString() +"\n");

        /*This BufferedReader object initializes by taking an InputStreamReader that initializes with the user's
         *keyboard input in the console window. InputStreamReader takes these bytes and reads them into characters,
         *where BufferedReader reads text from the input stream, buffering the characters.*/

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        try {
            String line;

            /*The following do-while block asks user to input a username, and then establishes a connection to the
             * server using the listed port (4545 in this scenario) as long as the user doesn't input "q"*/

            do {

                //Waits for user input before proceeding to connection

                System.out.println("Thank you for connecting!\nPress 'Enter' to begin\nNote: Type 'q' at any point to quit");

                line = userInput.readLine();

                if (!line.equals("q")) {

                    //establishConnection() creates a new socket and connects it at the specified port on the given host

                    establishConnection(serverIP, uniqueID.toString());
                }

            } while (!line.equals("q"));

            System.out.println("Shutting down...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
