
package hr.algebra.controller;

import hr.algebra.controls.ToggleSwitch;
import hr.algebra.model.Game;
import hr.algebra.model.Player;
import hr.algebra.repository.GamesRepository;
import hr.algebra.utils.FileUtils;
import hr.algebra.utils.ReflectionUtils;
import hr.algebra.utils.SerializationUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class SettingsController implements Initializable {

    @FXML
    private TextField tfPlayersNumber;
    @FXML
    private VBox vbLeft;
    @FXML
    private VBox vbRight;

    private ObservableList<Player> players;
    private ObservableList<Game> games;

  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init();
    }

    @FXML
    private void startGame(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/algebra/view/StartGame.fxml"));
        Parent root = loader.load();

        Stage previousStage = (Stage) vbLeft.getScene().getWindow();

        Stage stage = new Stage();
        stage.setTitle("Game start");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/hr/algebra/style/main.css");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
        previousStage.close();

    }

    private void init() {
        games = GamesRepository.getInstance().getGames();
        players = GamesRepository.getInstance().getPlayers();
        tfPlayersNumber.setText(String.valueOf(GamesRepository.getInstance().getNumberOfPlayers()));
        tfPlayersNumber.setFocusTraversable(false);

        int half = games.size() / 2;

        fillVBox(vbLeft, 0, half);
        fillVBox(vbRight, half, games.size());
    }

    private void fillVBox(VBox vb, int start, int stop) {

        vb.getChildren().clear();
        for (int i = start; i < stop; i++) {
            Game game = games.get(i);
            Pane pane = new Pane();
            pane.setMinSize(vb.getMaxWidth(), 138.5);
            pane.getStyleClass().add("bg-black");
            String path = game.getPicturePath();
            Image img = new Image(getClass().getResourceAsStream(path), 140, 135, true, false);
            ImageView iv = new ImageView(img);
            iv.setId(iv + game.getName());
            iv.setLayoutX(0);
            iv.setLayoutY(0);
            iv.setFitHeight(135);
            iv.setFitWidth(140);
            Label label = new Label(game.getName());
            label.setLayoutX(150);
            label.setLayoutY(10);

            ToggleSwitch toggle = new ToggleSwitch(game);
            toggle.setTranslateX(50);
            toggle.setTranslateY(50);
            Label text = new Label();
            text.setFont(Font.font(18));
            text.setTranslateX(50);
            text.setTranslateY(70);
            toggle.switchedOnProperty().setValue(game.getIsOn());
            text.textProperty().bind(Bindings.when(toggle.switchedOnProperty()).then("ON").otherwise("OFF"));
            game.isOnProperty().bindBidirectional(toggle.switchedOnProperty());
            text.getStyleClass().add("switch");
            text.setLayoutX(168);
            text.setLayoutY(1);
            toggle.setLayoutX(100);
            toggle.setLayoutY(20);
            pane.getChildren().addAll(iv, label, toggle, text);
            vb.getChildren().add(pane);

        }
    }

    @FXML
    private void deserialize(ActionEvent event) {
        File file = FileUtils.uploadFileDialog(vbLeft.getScene().getWindow(), "ser");
        if (file != null) {
            try {

                SerializationUtils.read(file.getAbsolutePath());
                init();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void serialize(ActionEvent event) {
        try {
            File file = FileUtils.saveFileDialog(vbLeft.getScene().getWindow(), "ser");
            if (file != null) {
                SerializationUtils.write(GamesRepository.getInstance(), file.getAbsolutePath());
            }
        } catch (IOException ex) {
            Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void createDocumentation() {

        try (FileWriter htmlWriter = new FileWriter("htmlDocumentation.html")) {

            String absoluteClassesPath = Paths.get(FileUtils.GetDirPath(vbLeft.getScene().getWindow())).toString();
            String classesPath = absoluteClassesPath.substring(absoluteClassesPath.indexOf("src\\"));
            String classesPackage
                    = classesPath.substring(classesPath.indexOf("\\") + 1).replace("\\", ".").concat(".");

            htmlWriter.write("<!DOCTYPE html>");
            htmlWriter.write("<html>");
            htmlWriter.write("<head>");
            htmlWriter.write("<title>Class documentation</title>");
            htmlWriter.write("<style>\n"
                    + "body {background-color: #eacdc2;}\n"
                    + "h1   {color: #372549;}\n"
                    + "h3   {color: #b75d69;}\n"
                    + "</style>");
            htmlWriter.write("</head>");
            htmlWriter.write("<body>");

            DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(absoluteClassesPath));

            StringBuilder classAndMembersInfo = new StringBuilder();
            stream.forEach(f -> {
                String filename = f.getFileName().toString();
                String className = filename.substring(0, filename.indexOf("."));

                classAndMembersInfo
                        .append("<h1>")
                        .append(className)
                        .append("</h1>");

                try {
                    Class<?> clazz = Class.forName(classesPackage.concat(className));
                    ReflectionUtils.readClassAndMembersInfo(clazz, classAndMembersInfo);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
                }

                classAndMembersInfo
                        .append(System.lineSeparator())
                        .append(System.lineSeparator());

            });

            htmlWriter.write(classAndMembersInfo.toString());
            htmlWriter.write("</body>");
            htmlWriter.write("</html>");

            htmlWriter.flush();

        } catch (IOException ex) {
            Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
