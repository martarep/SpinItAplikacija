
package hr.algebra.controller;

import hr.algebra.model.Player;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;


public class PlayersPointsController implements Initializable {

    @FXML
    private Label lblPlayersPoints;

 
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    void initPoints(Player player) {
        lblPlayersPoints.setText(player.getName() + " scored " + player.getPoints() + " !");
    }

}
