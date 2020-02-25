/*--------------------------------------------------------

1. Willy Moore / 01.26.2020:

2. Java version used, if not the official version for the class:

Java 8 Update 241

3. Precise command-line compilation examples / instructions:

>java JokeClientAdmin <Server IP>

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

import static java.lang.System.exit;

public class JokeClientAdmin {

    private static void establishConnection(String host) {

        try{
            String messageToServer;

            Socket sock = new Socket(host, 5050);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter outputStream = new PrintWriter(sock.getOutputStream(), true);

            BufferedReader adminInput = new BufferedReader(new InputStreamReader(System.in));

            /*In this while block, we want to send something to the server to notify it to switch from
            * joke mode to proverb mode, or vice versa.*/

            while((inputStream.readLine()) != null) {

                messageToServer = adminInput.readLine();
                if (!messageToServer.equals("q")) {

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

    public static void main(String[] args) {

        /*This code block is taken directly form InetClient.java
         *this is how we get the connection to the server, defaults
         *to localhost if no IP address is given*/

        String serverIP;

        if(args.length < 1) {
            serverIP = "localhost";
        } else {
            serverIP = args[0];
        }

        //Welcome message

        System.out.println("Willy Moore's Administrative Client\nServer: " + serverIP + "\nPort: 5050\n");

        BufferedReader adminInput = new BufferedReader(new InputStreamReader(System.in));

        try {
            String line;

            do {
                System.out.println("Press 'Enter' to switch mode\nNote: Type 'q' at any point to quit");

                line = adminInput.readLine();

                if(!line.equals("q")) {

                    /*establishConnection() creates a new socket and connects it at the specified port on the given host,
                    * same functionality as in JokeClient.java*/

                    establishConnection(serverIP);

                }

            } while(!line.equals("q"));

            System.out.println("Shutting down...");
        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
