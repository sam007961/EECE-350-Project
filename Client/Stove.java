package project350client;

import java.io.IOException;

public class Stove extends Appliance {
    public Stove() {
        super();
        file = User.StoveFile;
    }

    @Override
    protected void handleUserInput() {//Read Files and send requests
            if(file.getStatus().equals("BAD")){//Status check
                if(!User.Orders.checkItem("MaintS")){
                    try {
                        out.write(("MAINT Stove\n").getBytes());
                        User.Orders.append("MaintS");
                    } catch (IOException ex) {
                        System.out.println("IO Exception while sending requests");
                    }
                }
            }      
            checkItems();
        try {
            Thread.sleep(1);//Do not overload with synchronized calls
        } catch (InterruptedException ex) {
            System.out.println("Client Interrupted");
        }
    }
    
    @Override
     protected void checkItems() {
        if(file.getQuantity("Gas") < file.getThreshold("Gas"))
            if(!User.Orders.checkItem("Gas")) {
            try {
                out.write(("ORDER Gas_Tank 1" + "\n").getBytes());
                User.Orders.append("Gas");
            } catch (IOException ex) {
                System.out.println("IO Exception while sending requests");
            }
            }
    }
    
}
