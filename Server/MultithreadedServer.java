package javaproject350.JavaProject350;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.HashMap;

public class MultithreadedServer implements Runnable {
    
    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    
    public static FileHandler Prices = new FileHandler("Prices");//Price list
    public static HashMap<String, FileHandler> Bills = new HashMap<>();//Organize files for synchronous use
    
    public MultithreadedServer(int port){
        this.serverPort = port;
    }

    
    @Override
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(!isStopped()){
            Socket clientSocket = null;
            try{
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            //Create Thread
            new Thread(
                new WorkerRunnable(
                    clientSocket,"Multithreaded Server")
            ).start();
        }
        System.out.println("Server Stopped");
    }
    
    private synchronized boolean isStopped() {
        return this.isStopped;
    }
    
    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
    
    public void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + serverPort, e);
        }
    }
}
