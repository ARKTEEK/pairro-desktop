package vertexlink.ui.resources.settings;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SettingsCard extends VBox {

  public SettingsCard(String title, Node... rows) {
    super(0);
    getStyleClass().add("settings-card");

    Label caption = new Label(title.toUpperCase());
    caption.getStyleClass().add("header-card-caption");

    HBox captionBar = new HBox(caption);
    captionBar.getStyleClass().add("header-card-caption-bar");

    getChildren().add(captionBar);

    for (int i = 0; i < rows.length; i++) {
      if (i > 0) {
        Region divider = new Region();
        divider.getStyleClass().add("settings-divider");

        VBox.setMargin(divider, new Insets(0, 14, 0, 14));
        getChildren().add(divider);
      }

      getChildren().add(rows[i]);
    }
  }
}
