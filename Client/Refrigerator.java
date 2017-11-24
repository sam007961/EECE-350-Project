package project350client;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Refrigerator extends Appliance {

    public Refrigerator() {
        super();
        file = User.RefrigeratorFile;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                        out.write(("ORDER Electricity 10\n").getBytes());
                    } catch (IOException ex) {
                        System.out.println("IO Exception while sending requests");
                    }
            }
        }, 15000, 15000);//Consume electricity every 15 seconds 
    }

    @Override
    protected void handleUserInput() {//Read Files and send requests
            checkItems();
            if(file.getStatus().equals("BAD")){//Status check
                if(!User.Orders.checkItem("MaintR")){
                    try {
                        out.write(("MAINT Refrigerator\n").getBytes());
                        User.Orders.append("MaintR");
                    } catch (IOException ex) {
                        System.out.println("IO Exception while sending requests");
                    }
                }
            }      
        try {
            Thread.sleep(1);//Do not overload with synchronized calls
        } catch (InterruptedException ex) {
            System.out.println("Client Interrupted");
        }
    }
    
}
