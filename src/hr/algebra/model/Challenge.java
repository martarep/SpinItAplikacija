
package hr.algebra.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Challenge implements Serializable {

    private static final long serialVersionUID = 3L;

    private StringProperty task;
    private BooleanProperty finished;

    public Challenge(String task, Boolean done) {
        this.task = new SimpleStringProperty(task);
        this.finished = new SimpleBooleanProperty(done);
    }

    public String getTask() {
        return task.get();
    }

    public void setTask(String task) {
        this.task.set(task);
    }

    public boolean getFinished() {
        return finished.get();
    }

    public void setFinished(boolean isOn) {
        this.finished.set(isOn);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeUTF(task.get());
        oos.writeBoolean(finished.get());
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        String serializedTask = ois.readUTF();
        Boolean serializedFinished = ois.readBoolean();

        task = new SimpleStringProperty(serializedTask);
        finished = new SimpleBooleanProperty(serializedFinished);

    }

}
