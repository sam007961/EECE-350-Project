package project350client;

import java.io.IOException;

public class ClientApp {
    public static void main(String[] args) throws IOException {
        
        new Thread(new User()).start(); //Start main Thread
    }
}
