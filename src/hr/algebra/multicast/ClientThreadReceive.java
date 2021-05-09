
package hr.algebra.multicast;

import hr.algebra.controller.StartGameController;
import hr.algebra.model.Player;
import hr.algebra.utils.ByteUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;


public class ClientThreadReceive extends Thread {

    public static int CLIENT_PORT;
    public static String GROUP;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File("socket.properties")));
            CLIENT_PORT = Integer.valueOf(properties.getProperty("CLIENT_PORT"));
            GROUP = properties.getProperty("GROUP");
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private final StartGameController controller;

    public ClientThreadReceive(StartGameController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {

        try (MulticastSocket client = new MulticastSocket(CLIENT_PORT);) {

            InetAddress groupAddress = InetAddress.getByName(GROUP);
         
            client.joinGroup(groupAddress);

            while (true) {

               
                byte[] numberOfPlayerBytes = new byte[4];
                DatagramPacket packet = new DatagramPacket(numberOfPlayerBytes, numberOfPlayerBytes.length);
                client.receive(packet);
                int length = ByteUtils.byteArrayToInt(numberOfPlayerBytes);

                byte[] playerBytes = new byte[length];
                packet = new DatagramPacket(playerBytes, playerBytes.length);
                client.receive(packet);
                try (ByteArrayInputStream baos = new ByteArrayInputStream(playerBytes);
                        ObjectInputStream oos = new ObjectInputStream(baos)) {
                    Player player = (Player) oos.readObject();
                    try {

                        Thread.sleep(2000);

                    } catch (InterruptedException ex) {

                        Logger.getLogger(ClientThreadReceive.class.getName()).log(Level.SEVERE, null, ex);

                    }

                    synchronized (this) {
                      Platform.runLater(() -> {
                            controller.showOtherPlayersChallenge(player);     
                       });
                        if (controller.alreadySpinned) {
                      try {
                        Thread.sleep(14000);
                    } catch (InterruptedException ex) {

                        Logger.getLogger(ClientThreadReceive.class.getName()).log(Level.SEVERE, null, ex);
                    }
                            notify();
                        }

                    }
                }

            }

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
