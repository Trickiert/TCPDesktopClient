import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Description
 *
 */
public class MainScreen extends JFrame {

    private JTextArea messagesArea;
    private JButton sendButton;
    private JTextField message;
    private JButton startServer;
    private JButton stopServer;
    private JButton ScreenshotButt;
    private TcpServer mServer;

    public MainScreen() {

        super("MainScreen");

        JPanel panelFields = new JPanel();
        panelFields.setLayout(new BoxLayout(panelFields, BoxLayout.X_AXIS));

        JPanel panelFields2 = new JPanel();
        panelFields2.setLayout(new BoxLayout(panelFields2, BoxLayout.X_AXIS));
        
        JPanel panelFields3 = new JPanel();
        panelFields3.setLayout(new BoxLayout(panelFields3, BoxLayout.X_AXIS));

        //here we will have the text messages screen
        messagesArea = new JTextArea();
        messagesArea.setColumns(50);
        messagesArea.setRows(10);
        messagesArea.setEditable(false);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the message from the text view
                String messageText = message.getText();
                // add message to the message area
                messagesArea.append("\n" + messageText);
                if (mServer != null) {
                    // send the message to the client
                    mServer.sendMessage(messageText);
                }
                // clear text
                message.setText("");
            }
        });

        startServer = new JButton("Start");
        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //creates the object OnMessageReceived asked by the TCPServer constructor
                mServer = new TcpServer(new TcpServer.OnMessageReceived() {
                    @Override
                    //this method declared in the interface from TCPServer class is implemented here
                    //this method is actually a callback method, because it will run every time when it will be called from
                    //TCPServer class (at while)
                    public void messageReceived(String message) {
                        messagesArea.append("\n " + message);
                    }
                });
                mServer.start();

                // disable the start button and enable the stop one
                startServer.setEnabled(false);
                stopServer.setEnabled(true);

            }
        });

        stopServer = new JButton("Stop");
        stopServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (mServer != null) {
                    mServer.close();
                }

                // disable the stop button and enable the start one
                startServer.setEnabled(true);
                stopServer.setEnabled(false);

            }
        });
        
        //Screenshot Functionality 
        ScreenshotButt = new JButton("Screenshot");
        ScreenshotButt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                    try {
                        Robot robot = new Robot();
                        String format = "jpg"; //Low bandwidth
                        String fileName = "screenshot." + format;
                         
                        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                        BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
                        ImageIO.write(screenFullImage, format, new File(fileName));
                         
                        //System.out.println("A full screenshot saved!");
                    } catch (AWTException | IOException ex) {
                        System.err.println(ex);
                    }
                }
            
        });

        //the box where the user enters the text (EditText is called in Android)
        message = new JTextField();
        message.setSize(200, 20);

        //add the buttons and the text fields to the panel
        //Top
        panelFields.add(messagesArea);
        panelFields.add(startServer);
        panelFields.add(stopServer);
        panelFields.add(ScreenshotButt);

        //Bottom
        panelFields2.add(message);
        panelFields2.add(sendButton);

        getContentPane().add(panelFields);
        getContentPane().add(panelFields2);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        setSize(300, 170);
        setVisible(true);
    }

}