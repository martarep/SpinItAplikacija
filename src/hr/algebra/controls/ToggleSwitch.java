
package hr.algebra.controls;

import hr.algebra.model.Game;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class ToggleSwitch extends Parent {

        private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

        private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
        private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));

        private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

        public BooleanProperty switchedOnProperty() {
            return switchedOn;
        }

        public ToggleSwitch(Game game) {
            Rectangle background = new Rectangle(60, 30);
            background.setArcWidth(30);
            background.setArcHeight(30);
            background.setFill(Color.web("#c9c7d7"));
            background.setStroke(Color.LIGHTGRAY);

            Circle trigger = new Circle(15);
            trigger.setCenterX(15);
            trigger.setCenterY(15);
            trigger.setFill(Color.WHITE);
            trigger.setStroke(Color.LIGHTGRAY);

            DropShadow shadow = new DropShadow();
            shadow.setRadius(2);
            trigger.setEffect(shadow);

            translateAnimation.setNode(trigger);
            fillAnimation.setShape(background);

            getChildren().addAll(background, trigger);

            switchedOn.addListener((obs, oldState, newState) -> {
                boolean isOn = newState.booleanValue();
                translateAnimation.setToX(isOn ? 60 - 30 : 0);
                fillAnimation.setFromValue(isOn ? Color.web("#c9c7d7") : Color.web("#7e9fc7"));
                fillAnimation.setToValue(isOn ? Color.web("#7e9fc7") : Color.web("#c9c7d7"));

                animation.play();
            });

            setOnMouseClicked(event -> {
                switchedOn.set(!switchedOn.get());
              //  game.setIsOn(switchedOn.get());
            });
        }
    }
