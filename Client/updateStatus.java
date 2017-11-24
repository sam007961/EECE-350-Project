
package project350client;

import java.awt.Color;
import java.util.TimerTask;


//Timer Task to update GUI component content
public final class updateStatus extends TimerTask{
    
    @Override
    public void run() {
        
        ClientUI.jLabel3.setText(ClientUI.file.getStatus());//Update Status Label
        if (ClientUI.jLabel3.getText().equals("GOOD"))
            ClientUI.jLabel3.setForeground(Color.green);
        else
            ClientUI.jLabel3.setForeground(Color.red);
        if(!ClientUI.jTextArea1.getText().equals("Updating..."))//Update Bill Text Area
            ClientUI.jTextArea1.setText(String.format("%-22s%-5s%-8s%n",
                    "Item", "Qty", "Price($)") + 
                    User.Bill.getTextFormat().replace('_', ' '));
        if(ClientUI.jRadioButton4.isSelected()){//Update Power Label
            if(ClientUI.acPower){
                ClientUI.jLabel7.setText("ON");
                ClientUI.jLabel7.setForeground(Color.green);
            } else {
                ClientUI.jLabel7.setText("OFF");
                ClientUI.jLabel7.setForeground(Color.red);
            }
        }
        if(ClientUI.jRadioButton2.isSelected()){
            if(ClientUI.washing){
                ClientUI.jLabel7.setText("ON");
                ClientUI.jLabel7.setForeground(Color.green);
            } else {
                ClientUI.jLabel7.setText("OFF");
                ClientUI.jLabel7.setForeground(Color.red);
            }
        }
        if(ClientUI.jRadioButton3.isSelected()) {//Update Progress Bar for Stove
            ClientUI.jProgressBar1.setValue(User.StoveFile.getQuantity(("Gas")));
        }
    }
    
}