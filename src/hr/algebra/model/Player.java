
package hr.algebra.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Player implements Serializable {

    private static final long serialVersionUID = 2L;
    private IntegerProperty id;
    private StringProperty name;
    private IntegerProperty points;
    private StringProperty challenge;
    private StringProperty game;

    public Player(int id, String name, int points, String challenge, String game) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.points = new SimpleIntegerProperty(points);
        this.challenge = new SimpleStringProperty(challenge);
        this.game = new SimpleStringProperty(game);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getPoints() {
        return points.get();
    }

    public void setPoints(int points) {
        this.points.set(points);
    }

    public String getChallenge() {
        return challenge.get();
    }

    public void setChallenge(String challenge) {
        this.challenge.set(challenge);
    }

    public String getGame() {
        return game.get();
    }

    public void setGame(String game) {
        this.game.set(game);
    }

    @Override
    public String toString() {
        return "Player" + name + "scored " + points + " points!";
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(id.get());
        oos.writeUTF(name.get());
        oos.writeInt(points.get());
        oos.writeUTF(challenge.get());
        oos.writeUTF(game.get());
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        int serializedId = ois.readInt();
        String serializedName = ois.readUTF();
        int serializedPoints = ois.readInt();
        String serializedChallenge = ois.readUTF();
        String serializedGame = ois.readUTF();

        id = new SimpleIntegerProperty(serializedId);
        name = new SimpleStringProperty(serializedName);
        points = new SimpleIntegerProperty(serializedPoints);
        challenge = new SimpleStringProperty(serializedChallenge);
        game = new SimpleStringProperty(serializedGame);

    }
}
