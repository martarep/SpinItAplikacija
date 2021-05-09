
package hr.algebra.repository;

import hr.algebra.model.Challenge;
import hr.algebra.model.Player;
import hr.algebra.model.Game;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class GamesRepository implements Serializable {
    
    private static final long serialVersionUID = 4L;

    private GamesRepository() {}
    
    private static final GamesRepository INSTANCE = new GamesRepository();

    public static GamesRepository getInstance() {
        return INSTANCE;
    }
     private int numberOfPlayers = 0;
     
    private ObservableList<Game> games = FXCollections.observableArrayList(
            new Game(0, "Truth", FXCollections.observableArrayList(
                    new Challenge("What's your biggest fear?", false),
                    new Challenge("Do you have a hidden talent?", false),
                    new Challenge("Have you ever broken the law?", false)),
                    false, "/hr/algebra/resources/truth.png"),
            new Game(0, "Dare", FXCollections.observableArrayList(
                    new Challenge("Do 20 push-ups", false),
                    new Challenge("Jump into a dumpster", false),
                    new Challenge("Scream for 20 seconds", false)), true, "/hr/algebra/resources/dare.png"),
            new Game(0, "Alias", FXCollections.observableArrayList(
                    new Challenge("Lion", false),
                    new Challenge("Friends", false),
                    new Challenge("Night", false)), true, "/hr/algebra/resources/alias.png"),
            new Game(0, "Pantomime", FXCollections.observableArrayList(
                    new Challenge("Washing a dog", false),
                    new Challenge("Putting on shoes and socks", false),
                    new Challenge("Walking in the rain or snow", false)), true, "/hr/algebra/resources/pantomime.png"),
            new Game(0, "NeverHaveI", FXCollections.observableArrayList(
                    new Challenge("Used a fake ID", false),
                    new Challenge("Had to go to court", false),
                    new Challenge("Lied to a boss", false)), true, "/hr/algebra/resources/NeverHaveI.png"));

    public  ObservableList<Game> getGames() {
        return games;
    }
    
    public void setNumberOfPlayers(int numberOfPlayers) {
        INSTANCE.numberOfPlayers =numberOfPlayers;
    }
    
     public int getNumberOfPlayers() {
        return INSTANCE.numberOfPlayers;
    }

    private final  ObservableList<Player> players = FXCollections.observableArrayList(
    );

    public  ObservableList<Player> getPlayers() {
        return INSTANCE.players;
    }

    public void setPlayers(ObservableList<Player> players){
    INSTANCE.players.clear();
    INSTANCE.players.addAll(players);
    }
     public void addPlayer(Player player){
    INSTANCE.players.add(player);
    }

       private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(new ArrayList<>(games));
        oos.writeObject(new ArrayList<>(players));
    }


    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        List<Game> serializedGames = (List<Game>) ois.readObject();
        List<Player> serializedPlayers = (List<Player>)ois.readObject();
        INSTANCE.games.clear();
        INSTANCE.players.clear();
        INSTANCE.games.addAll(serializedGames);
        INSTANCE.players.addAll(serializedPlayers);        
    }
      
      private Object readResolve() {

        return INSTANCE;
    }

      
}
