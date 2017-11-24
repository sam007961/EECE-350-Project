package project350client;
import java.io.IOException;
import java.util.Timer;

public class Appliance extends Client{
    //public boolean Status;
    protected FileHandler file;
    
    public Appliance() {
        Timer timer = new Timer(); //Generate malfunctions
        timer.schedule(new malfunction(this), 10000, 10000);
    }
    protected synchronized void generateMalfunction() {
        //System.out.println("10 seconds");
        if(file.getStatus().equals("BAD")) return;
        file.setStatus(false);
    }
    
    
    @Override
    protected void init() {
        try {
            out.write((User.UserID + "\n").getBytes());
            //Start Worker Thread to Read from Server
            Reader = new clientReader(serverSocket) {
                @Override
                public void handleServerInput(String data) {

                }
            };
        } catch (IOException ex) {
            System.out.println("IO Exception while initializing Refrigerator");
        }
    }
    
    protected void checkItems() {
        String[] itemList = file.listItems();//Item check
            for(String item : itemList) {
                int t = file.getThreshold(item);
                if(file.getQuantity(item) < t){
                    if(!User.Orders.checkItem(item)) {
                        try {
                            out.write(("ORDER " + item + " " + Integer.toString(t)
                                    + "\n").getBytes());
                            User.Orders.append(item);
                        } catch (IOException ex) {
                            System.out.println("IO Exception while sending requests");
                        }
                    }
                }
            }
    }
}

