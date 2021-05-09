
package hr.algebra;

import hr.algebra.model.Player;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import hr.algebra.repository.GamesRepository;
import java.util.Map;


public class SpinIt extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parameters params = getParameters();
        Map<String, String> namedParams = params.getNamed();
        int idPlayer = Integer.parseInt(namedParams.get("idPlayer"));
        String name = namedParams.get("name");
        int numberOfPlayers = Integer.parseInt(namedParams.get("numberOfPlayers"));

        Player player = new Player(idPlayer, name, 0, "", "");
        GamesRepository.getInstance().addPlayer(player);
        GamesRepository.getInstance().setNumberOfPlayers(numberOfPlayers);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/StartScreen.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Game");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.show();
    }

    public static void main(int idPlayer, String name) {

        launch();
    }

}
