package project350client;

import javax.swing.JFrame;

public class GUIThread implements Runnable {
    JFrame frame;
    //files
    public GUIThread() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// X button closes application
        frame.getContentPane().add(new ClientUI());//Add panel
        frame.pack();//Sizes frame so that all its contents are at preferred sizes
    }
    
    @Override
    public void run() {
        //Files are updated on button presses
        frame.setVisible(true);//Visible
    }
}
