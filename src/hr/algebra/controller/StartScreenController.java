
package hr.algebra.controller;

import hr.algebra.SpinIt;
import hr.algebra.model.Game;
import hr.algebra.model.Player;
import hr.algebra.repository.GamesRepository;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class StartScreenController implements Initializable {

    @FXML
    private Button btnStart;
    @FXML
    private Button btnSettings;

    private ObservableList<Player> players;
    private ObservableList<Game> games;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initSettings();
    }

    @FXML
    private void startGame(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/algebra/view/StartGame.fxml"));
        Parent root = loader.load();
        Stage previousStage = (Stage) btnStart.getScene().getWindow();

        Stage stage = new Stage();
        stage.setTitle("Game start");
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(e -> System.exit(0));

        stage.show();
        previousStage.close();
    }

    @FXML
    private void showSettings(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/algebra/view/Settings.fxml"));
        Parent root = loader.load();
        Stage previousStage = (Stage) btnStart.getScene().getWindow();

        Stage stage = new Stage();
        stage.setTitle("Settings");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/hr/algebra/style/main.css");
        stage.setScene(scene);

        stage.show();
        previousStage.close();
    }

    private void initSettings() {

        try {
            
            games = GamesRepository.getInstance().getGames();  
            players = GamesRepository.getInstance().getPlayers();



        } catch (Exception ex) {
            Logger.getLogger(SpinIt.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


}
