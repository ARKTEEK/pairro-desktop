package pairro.notification.toast;

import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public final class ToastStage {
  private static final Duration FADE_IN_DURATION = Duration.millis(180);
  private static final Duration FADE_OUT_DURATION = Duration.millis(260);
  private static final Duration VISIBLE_DURATION = Duration.millis(6000);

  private final Stage stage;
  private final Timeline fadeInTimeline;
  private final Timeline countdownTimeline;
  private final Consumer<ToastStage> onClosed;
  private double targetX;
  private double targetY;
  private boolean dismissed;

  public ToastStage(ToastContent content, Image icon, Runnable onClick, Consumer<ToastStage> onClosed) {
    this.onClosed = onClosed;
    this.stage = new Stage(StageStyle.TRANSPARENT);

    ToastPane pane = new ToastPane(content, icon);
    pane.setOnMouseClicked(event -> {
      onClick.run();
      dismiss();
    });

    Scene scene = new Scene(pane);
    scene.setFill(Color.TRANSPARENT);

    stage.setScene(scene);
    stage.setAlwaysOnTop(true);
    stage.setWidth(ToastPane.WIDTH);
    stage.setHeight(ToastPane.HEIGHT);

    ChangeListener<Number> positionPin = (observable, oldValue, newValue) -> applyPosition();
    stage.xProperty().addListener(positionPin);
    stage.yProperty().addListener(positionPin);

    fadeInTimeline = new Timeline(
        new KeyFrame(FADE_IN_DURATION, new KeyValue(stage.opacityProperty(), 1.0))
    );
    countdownTimeline = new Timeline(
        new KeyFrame(VISIBLE_DURATION, new KeyValue(pane.progressWidthProperty(), 0.0))
    );
    countdownTimeline.setOnFinished(event -> dismiss());
  }

  public void display() {
    stage.setOpacity(0.0);
    stage.setOnShown(event -> Platform.runLater(this::applyPosition));
    stage.show();
    fadeInTimeline.play();
    countdownTimeline.play();
  }

  public void moveTo(double x, double y) {
    targetX = x;
    targetY = y;

    applyPosition();
  }

  private void applyPosition() {
    if (stage.getX() != targetX) {
      stage.setX(targetX);
    }

    if (stage.getY() != targetY) {
      stage.setY(targetY);
    }
  }

  public void dismiss() {
    if (dismissed) {
      return;
    }

    dismissed = true;
    countdownTimeline.stop();
    fadeInTimeline.stop();

    Timeline fadeOutTimeline = new Timeline(
        new KeyFrame(FADE_OUT_DURATION, new KeyValue(stage.opacityProperty(), 0.0))
    );
    fadeOutTimeline.setOnFinished(event -> {
      stage.close();
      onClosed.accept(this);
    });
    fadeOutTimeline.play();
  }
}
