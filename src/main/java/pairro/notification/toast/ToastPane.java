package pairro.notification.toast;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class ToastPane extends StackPane {
  public static final double WIDTH = 360;
  public static final double HEIGHT = 100;

  private static final double CORNER_RADIUS = 18;
  private static final double PROGRESS_HEIGHT = 3;

  private final Rectangle progressBar;

  public ToastPane(ToastContent content, Image icon) {
    getStylesheets().add(getClass().getResource("/styles/toast.css").toExternalForm());
    getStyleClass().add("toast-pane");

    HBox strip = new HBox();
    strip.getStyleClass().add("toast-strip");
    strip.setAlignment(Pos.CENTER_LEFT);

    Label captionLabel = new Label("PAIRRO");
    captionLabel.getStyleClass().add("toast-caption");
    strip.getChildren().add(captionLabel);

    HBox body = new HBox(14);
    body.getStyleClass().add("toast-body");
    body.setAlignment(Pos.CENTER_LEFT);
    VBox.setVgrow(body, Priority.ALWAYS);

    StackPane iconTile = new StackPane();
    iconTile.getStyleClass().add("toast-icon-tile");
    if (icon != null) {
      ImageView imageView = new ImageView(icon);
      imageView.setFitWidth(30);
      imageView.setFitHeight(30);
      iconTile.getChildren().add(imageView);
    }

    VBox textVBox = new VBox(3);
    Label titleLabel = new Label(content.title());
    titleLabel.getStyleClass().add("toast-title");

    Label messageLabel = new Label(content.message());
    messageLabel.getStyleClass().add("toast-message");
    textVBox.getChildren().addAll(titleLabel, messageLabel);

    body.getChildren().addAll(iconTile, textVBox);

    progressBar = new Rectangle(WIDTH, PROGRESS_HEIGHT);
    progressBar.getStyleClass().add("toast-progress-bar");
    progressBar.setFill(content.accent());

    StackPane progressTrack = new StackPane(progressBar);
    progressTrack.getStyleClass().add("toast-progress-track");
    progressTrack.setAlignment(Pos.CENTER_LEFT);
    StackPane.setAlignment(progressTrack, Pos.BOTTOM_LEFT);

    Rectangle trackClip = new Rectangle(0, PROGRESS_HEIGHT - HEIGHT, WIDTH, HEIGHT);
    trackClip.setArcWidth(CORNER_RADIUS * 2);
    trackClip.setArcHeight(CORNER_RADIUS * 2);
    progressTrack.setClip(trackClip);

    VBox card = new VBox(strip, body);

    Region borderOverlay = new Region();
    borderOverlay.getStyleClass().add("toast-border-overlay");
    borderOverlay.setMouseTransparent(true);

    getChildren().addAll(card, progressTrack, borderOverlay);
  }

  public DoubleProperty progressWidthProperty() {
    return progressBar.widthProperty();
  }
}
