package vertexlink.ui.component.settings;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class ToggleSwitch extends StackPane {
  private static final double WIDTH = 46;
  private static final double HEIGHT = 26;
  private static final double PADDING = 3;

  private final BooleanProperty selected = new SimpleBooleanProperty(false);
  private final Circle thumb = new Circle((HEIGHT / 2) - PADDING);

  public ToggleSwitch(boolean initial) {
    getStyleClass().add("toggle-switch");
    setMinSize(WIDTH, HEIGHT);
    setPrefSize(WIDTH, HEIGHT);
    setMaxSize(WIDTH, HEIGHT);

    thumb.getStyleClass().add("toggle-thumb");
    StackPane.setAlignment(thumb, Pos.CENTER_LEFT);
    StackPane.setMargin(thumb, new Insets(0, 0, 0, PADDING));
    getChildren().add(thumb);

    selected.addListener((obs, was, now) -> apply(now, true));

    selected.set(initial);
    apply(initial, false);

    setMouseTransparent(true);
  }

  public boolean isSelected() {
    return selected.get();
  }

  public void setSelected(boolean value) {
    selected.set(value);
  }

  public void toggle() {
    selected.set(!selected.get());
  }

  public BooleanProperty selectedProperty() {
    return selected;
  }

  private void apply(boolean on, boolean animate) {
    if (on) {
      if (!getStyleClass().contains("on")) {
        getStyleClass().add("on");
      }
    } else {
      getStyleClass().remove("on");
    }

    double targetX = on ? WIDTH - HEIGHT : 0;

    if (animate) {
      TranslateTransition slide = new TranslateTransition(Duration.millis(140), thumb);
      slide.setToX(targetX);
      slide.setInterpolator(Interpolator.EASE_BOTH);
      slide.play();
    } else {
      thumb.setTranslateX(targetX);
    }
  }
}
