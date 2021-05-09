
package hr.algebra.multicast;

import hr.algebra.controller.StartGameController;
import hr.algebra.utils.ByteUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import hr.algebra.model.Player;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.LinkedBlockingDeque;


public class ClientThread extends Thread {

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

    public ClientThread(StartGameController controller) {
        this.controller = controller;
    }

    private final LinkedBlockingDeque<Player> players = new LinkedBlockingDeque<>();

    public void trigger(Player player) {
        players.add(player);
    }

    @Override
    public void run() {

        try (MulticastSocket client = new MulticastSocket(CLIENT_PORT);) {

            InetAddress groupAddress = InetAddress.getByName(GROUP);
            //System.err.println(controller.hashCode() + " joining group");
            client.joinGroup(groupAddress);

            while (true) {
                if (!players.isEmpty()) {

                    byte[] playerBytes;
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                        oos.writeObject(players.getFirst());
                        players.clear();
                        oos.flush();
                        playerBytes = baos.toByteArray();
                    }

                    byte[] numberOfPlayerBytes = ByteUtils.intToByteArray(playerBytes.length);
                    DatagramPacket packet = new DatagramPacket(numberOfPlayerBytes, numberOfPlayerBytes.length, InetAddress.getByName("localhost"), 12345);
                    client.send(packet);

                    packet = new DatagramPacket(playerBytes, playerBytes.length, InetAddress.getByName("localhost"), 12345);
                    client.send(packet);

                }

            }

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
