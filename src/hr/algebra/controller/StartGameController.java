
package hr.algebra.controller;

import hr.algebra.SpinIt;
import hr.algebra.model.Challenge;
import hr.algebra.model.Game;
import hr.algebra.model.Player;
import hr.algebra.multicast.ClientThread;
import hr.algebra.multicast.ClientThreadReceive;
import hr.algebra.repository.GamesRepository;
import hr.algebra.rmi.ChatClient;
import hr.algebra.utils.DOMUtils;
import hr.algebra.utils.MessageUtils;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;


public class StartGameController implements Initializable {

    private final String TIME_FORMAT = "HH:mm:ss";
    private static final String MESSAGE_FORMAT = "%s [%s]: %s";
    private static final int MESSAGE_LENGTH = 60;

    @FXML
    private Circle c1;
    @FXML
    private Circle c2;
    @FXML
    private Circle c3;
    @FXML
    private Label lblGameSelection;
    @FXML
    private Button btnSpinIt;
    @FXML
    private Label lblChallengeSelection;
    @FXML
    private Button btnDone;
    @FXML
    private Button btnFailed;
    @FXML
    private Label lblTimer;
    @FXML
    private ScrollPane spContainer;
    @FXML
    private VBox vbMessages;
    @FXML
    private Button btnSend;
    @FXML
    private TextField tfMessage;

    private ObservableList<Node> messages;
    private ChatClient chatClient;
    private ClientThread clientThread;
    private final ClientThreadReceive clientThreadReceive = new ClientThreadReceive(this);
    private Game randomGame;
    private Challenge randomChallenge;
    private final Random rand = new Random();
    private ObservableList<Player> players;
    private List<Player> playersMoves;
    private ObservableList<Game> games;
    public Player currentPlayer;
    private boolean allGameChallengesFinished;
    private boolean allGamesChallengesFinished;
    public boolean alreadySpinned = false;
    public boolean timerStarted;
    private Integer seconds, minutes;
    private int numberOfPlayers;
    private int idTurnCounter;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initSettings();
        initChat();
    }

    @FXML
    private void spinIt(ActionEvent event) {

        if ("Spin".equals(btnSpinIt.getText())) {
            clearForm();
            lblGameSelection.visibleProperty().set(false);
            setRotation(c1, true, 200, 4);
            setRotation(c2, true, 180, 4);
            setRotation(c3, true, 160, 3);
            btnSpinIt.setDisable(true);
            timerStarted = true;
            Thread th = new Thread(() -> {
                synchronized (clientThreadReceive) {

                    while (idTurnCounter != currentPlayer.getId()) {

                        try {
                            alreadySpinned = true;
                            clientThreadReceive.wait();

                        } catch (InterruptedException e) {
                            Logger.getLogger(StartGameController.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }

                    randomGame();

                }
            });

            th.start();

        } else {
            try {

                games.forEach(game -> {
                    game.getChallenges().forEach(challenge -> challenge.setFinished(false));
                });
                clearForm();

            } catch (Exception ex) {
                Logger.getLogger(SpinIt.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @FXML
    private void showPoints(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/algebra/view/PlayersPoints.fxml"));
        Parent root = loader.load();
        PlayersPointsController playersPointsController = loader.getController();

        Stage stage = new Stage();
        stage.setTitle("Points");
        stage.setScene(new Scene(root, 400, 200));
        playersPointsController.initPoints(players.get(0));
        stage.show();
    }

    private void showSettings(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/algebra/view/Settings.fxml"));
        Parent root = loader.load();
        Stage previousStage = (Stage) c1.getScene().getWindow();

        Stage stage = new Stage();
        stage.setTitle("Settings");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/hr/algebra/style/main.css");
        stage.setScene(scene);

        stage.show();
        previousStage.close();
    }

    private void setRotation(Circle c, boolean reverse, int angle, int duration) {

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(duration), c);
        rotateTransition.setAutoReverse(reverse);
        rotateTransition.setByAngle(angle);
        rotateTransition.setDelay(Duration.seconds(0));
        rotateTransition.setRate(3);
        rotateTransition.setCycleCount(3);
        rotateTransition.play();

    }

    private void randomGame() {

        checkAllGamesChallengesFinished();

        if (!allGamesChallengesFinished) {
            boolean gameOn;

            do {
                randomGame = games.get(rand.nextInt(games.size()));
                gameOn = randomGame.getIsOn();
                checkGameChallengesFinished(randomGame);
            } while (!gameOn || allGameChallengesFinished);

            try {

                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(4),
                                new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                                lblGameSelection.setText(randomGame.getName());
                                lblGameSelection.visibleProperty().set(true);
                                getRandomChallenge();
                            }
                        }));
                timeline.setCycleCount(1);
                timeline.play();
                currentPlayer.setGame(randomGame.getName());
            } catch (Exception ex) {
                Logger.getLogger(SpinIt.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            clearForm();
            c1.setVisible(false);
            c2.setVisible(false);
            c3.setVisible(false);
            lblGameSelection.setVisible(false);
            lblChallengeSelection.setVisible(true);
            lblChallengeSelection.setText("You have finished all tasks!");
            btnSpinIt.setText("Play again");
        }

    }

    private void initSettings() {

        clientThreadReceive.setDaemon(true);
        clientThreadReceive.start();

        clientThread = new ClientThread(this);
        clientThread.setDaemon(true);
        clientThread.start();

        games = GamesRepository.getInstance().getGames();
        players = GamesRepository.getInstance().getPlayers();
        playersMoves = new ArrayList<Player>();
        timerStarted = false;
        numberOfPlayers = GamesRepository.getInstance().getNumberOfPlayers();
        currentPlayer = players.get(0);
        idTurnCounter = 1;

        if (currentPlayer.getId() == idTurnCounter) {
            btnSpinIt.setDisable(false);
        } else {
            btnSpinIt.setDisable(true);
        }
        lblTimer.setVisible(false);
    }

    private void getRandomChallenge() {

        boolean challengeFinished;
        do {
            randomChallenge = randomGame.getChallenges().get(rand.nextInt(randomGame.getChallenges().size()));
            challengeFinished = randomChallenge.getFinished();
        } while (challengeFinished);

        try {

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(2),
                            new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            c2.setVisible(false);
                            c3.setVisible(false);
                            lblGameSelection.setVisible(false);
                            lblChallengeSelection.setVisible(true);
                            lblChallengeSelection.setText(randomChallenge.getTask());
                            btnSpinIt.setVisible(false);
                            btnDone.setVisible(true);
                            btnFailed.setVisible(true);
                            randomChallenge.setFinished(true);
                            startTimer();
                        }
                    }));
            timeline.setCycleCount(1);
            timeline.play();
            currentPlayer.setChallenge(randomChallenge.getTask());
            sendData();
        } catch (Exception ex) {
            Logger.getLogger(SpinIt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void clearForm() {
        lblChallengeSelection.setVisible(false);
        lblGameSelection.setText("?");
        lblGameSelection.setVisible(true);
        c1.setVisible(true);
        c2.setVisible(true);
        c3.setVisible(true);
        btnDone.setVisible(false);
        btnFailed.setVisible(false);
        btnSpinIt.setVisible(true);
        btnSpinIt.setText("Spin");
        lblTimer.setVisible(false);
      

    }

    @FXML
    private void setPoints(ActionEvent event) {

        int points = currentPlayer.getPoints();
        currentPlayer.setPoints(points + 1);
        clearForm();

    }

    private void checkGameChallengesFinished(Game game) {

        allGameChallengesFinished = true;
        game.getChallenges().forEach(c -> {
            if (allGameChallengesFinished) {
                allGameChallengesFinished = c.getFinished();
            }
        });
    }

    private void checkAllGamesChallengesFinished() {
        allGamesChallengesFinished = true;

        for (Game game : games) {
            checkGameChallengesFinished(game);
            if (allGamesChallengesFinished && game.getIsOn()) {
                allGamesChallengesFinished = allGameChallengesFinished;
            }
        }

    }

    public void showOtherPlayersChallenge(Player player) {

        if (currentPlayer.getId() == player.getId() && !timerStarted) {
            c2.setVisible(false);
            c3.setVisible(false);
            lblGameSelection.setVisible(false);
            lblChallengeSelection.setVisible(true);
            lblChallengeSelection.setText(player.getChallenge());
            btnSpinIt.setVisible(false);
            btnDone.setVisible(true);
            btnFailed.setVisible(true);
            // randomChallenge.setFinished(true);
            startTimer();
        }

        changeTurn();
        if (currentPlayer.getId() != player.getId()) {
            lblGameSelection.setVisible(true);
            lblGameSelection.setText(player.getName() + " is doing " + player.getGame() + " challenge!");
            btnSpinIt.setDisable(true);
            startTimer();
        }

       
            playersMoves.add(player);
      

    }

    private void changeTurn() {
        if (idTurnCounter != numberOfPlayers) {
            idTurnCounter++;
        } else {
            idTurnCounter = 1;
        }
    }

    private void sendData() {

        clientThread.trigger(currentPlayer);

    }

    private void startTimer() {

        minutes = 0;
        seconds = 15;
        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        clockEvent -> {

                            seconds--;

                            if (seconds < 0) {
                                minutes--;
                                seconds = 59;
                            }

                            String secondsString = seconds.toString();
                            String minutesString = minutes.toString();

                            if (secondsString.length() < 2) {
                                secondsString = "0" + secondsString;
                            }

                            if (minutesString.length() < 2) {
                                minutesString = "0" + minutesString;
                            }

                            lblTimer.setText(minutesString + ":" + secondsString);
                            if (minutes == 0 && seconds == 0) {
                                clearForm();
                                timerStarted = false;

                                if (currentPlayer.getId() == idTurnCounter && !alreadySpinned) {
                                    btnSpinIt.setDisable(false);
                                }
                                if (currentPlayer.getId() == idTurnCounter && alreadySpinned) {
                                    alreadySpinned = false;
                                }

                            }
                        }
                )
        );
        timeline.setCycleCount(60 * minutes + seconds);
        timeline.play();
        lblTimer.setText("00:15");
        lblTimer.setVisible(true);

    }

    @FXML
    private void send(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

    @FXML
    private void sendMessage() {
        if (tfMessage.getText().trim().length() > 0) {
            chatClient.sendMessage(tfMessage.getText().trim());
            addMessage(tfMessage.getText().trim(), currentPlayer.getName());
            tfMessage.clear();
        }
    }

    private void moveScrollPane() {
        spContainer.applyCss();
        spContainer.layout();
        spContainer.setVvalue(1D);
    }

    private void initChat() {
        chatClient = new ChatClient(this);
        messages = FXCollections.observableArrayList();
        Bindings.bindContentBidirectional(messages, vbMessages.getChildren());
        tfMessage.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.length() >= MESSAGE_LENGTH) {
                        ((StringProperty) observable).setValue(oldValue);
                    }
                }
        );
    }

    public void postMessage(String message, String name) {
        if (!name.equals(currentPlayer.getName())) {
            Platform.runLater(() -> addMessage(message, name));
        }
    }

    private void addMessage(String message, String name) {
        Label label = new Label();
        label.setText(String.format(MESSAGE_FORMAT, LocalTime.now().format(DateTimeFormatter.ofPattern(TIME_FORMAT)), name, message));
        label.setId("message");
        label.wrapTextProperty().setValue(true);
        label.setTextOverrun(OverrunStyle.ELLIPSIS);
        messages.add(label);
        moveScrollPane();
    }

    private void sendDataFromXML(int playerIndex) {

        clientThread.trigger(playersMoves.get(playerIndex));

    }

    @FXML
    private void loadXML(ActionEvent event) {
        
        playersMoves = DOMUtils.loadPlayers();
        int size = playersMoves.size();
        for (int i = 0; i < playersMoves.size(); i++) {
            final int j = i;
            int seconds = (int) (16 *(i*1.5));
           
            if (i == 0) {
                sendDataFromXML(i);
            } 
            else {

                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(()-> {
                 sendDataFromXML(j);
                }, seconds, TimeUnit.SECONDS);
              
            }

        }
    }

    @FXML
    private void saveXML(ActionEvent event) {
        DOMUtils.savePlayers(playersMoves);
        MessageUtils.showInfoMessage("DOM", "XML documents saved", "Saved with no failures");

    }

}
