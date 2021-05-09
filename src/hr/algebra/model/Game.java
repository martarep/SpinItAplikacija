
package hr.algebra.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Game implements Serializable {

    private static final long serialVersionUID = 1L;
    private IntegerProperty id;
    private StringProperty name;
    private ListProperty<Challenge> challenges;
    private BooleanProperty isOn;
    private StringProperty picturePath;

    public Game(int id, String name, List<Challenge> challenges, boolean isOn, String picturePath) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.challenges = new SimpleListProperty<>((ObservableList<Challenge>) challenges);
        this.isOn = new SimpleBooleanProperty(isOn);
        this.picturePath = new SimpleStringProperty(picturePath);
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

    public List<Challenge> getChallenges() {
        return challenges.get();
    }

    public void setChallenges(ObservableList<Challenge> challenges) {
        this.challenges.set(challenges);
    }

    public boolean getIsOn() {
        return isOn.get();
    }

    public void setIsOn(boolean isOn) {
        this.isOn.set(isOn);
    }

    public String getPicturePath() {
        return picturePath.get();
    }

    public void setPicturePath(String picturePath) {
        this.picturePath.set(picturePath);
    }

    public BooleanProperty isOnProperty() {
        return isOn;
    }

    @Override
    public String toString() {
        return "Game " + name;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(id.get());
        oos.writeUTF(name.get());
        oos.writeObject(new ArrayList<>(challenges.get()));
        oos.writeBoolean(isOn.get());
        oos.writeUTF(picturePath.get());
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        int serializedId = ois.readInt();
        String serializedName = ois.readUTF();
        List<Challenge> serializedChallenges = (List<Challenge>) ois.readObject();
        Boolean serializedIsOn = ois.readBoolean();
        String serializedPicturePath = ois.readUTF();

        id = new SimpleIntegerProperty(serializedId);
        name = new SimpleStringProperty(serializedName);
        challenges = new SimpleListProperty<> (FXCollections.observableArrayList(serializedChallenges));
        isOn = new SimpleBooleanProperty(serializedIsOn);
        picturePath = new SimpleStringProperty(serializedPicturePath);

    }
}
