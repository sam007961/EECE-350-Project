package project350client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

//Worker Thread for client for non-blocking read
public class clientReader implements Runnable{
    private final Socket serverSocket;
    public BufferedReader in;
    
    public clientReader(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader
                                                (serverSocket.getInputStream()));
            while(true) {//Infinite loop
            String data = in.readLine();//Wait for server input
                    if (data.equals("EXIT")) {//Server Sent EXIT to Terminate Connection
                        System.out.println("Server Terminated Connection");
                        return;
                    }
                    
                    handleServerInput(data);//Classes that extend clientReader handle input appropriately
                    
            }
        } catch (IOException ex) {
            System.out.println("IO Exception while reading server input");
        }
    }
    
    public void handleServerInput(String data) {
        //TO DO
    }
    
}
