package javaproject350.JavaProject350;

public class server {

    public static void main(String[] args) {
        MultithreadedServer Server = new MultithreadedServer(9000);
        new Thread(Server).start();
    }

}
