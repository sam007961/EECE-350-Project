package project350client;

import java.io.*;
import java.net.*;


public class Client implements Runnable {
    protected OutputStream out;
    protected Socket serverSocket;
    protected clientReader Reader;
    
    @Override
    public void run() {
        String hostName = "127.0.0.1";
        int portNumber = 9000;
        try {
            serverSocket = new Socket(hostName, portNumber);
            out = serverSocket.getOutputStream();//Output to server
            init();//Classes that extend client initialize connection appropriately
            new Thread(Reader).start();//Start worker thread defined in init()
            while (true) {//Infinite Loop
                handleUserInput();//Classes that extend client handle user input appropriately
            }
        } catch (UnknownHostException e) {
            System.out.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Could not get I/O for the connection to " 
                                                                    + hostName);
            System.exit(1);
        }
    }


    protected void handleUserInput() {
        //TO DO
    }
    
    protected void init() {
        //TO DO
    }
}
