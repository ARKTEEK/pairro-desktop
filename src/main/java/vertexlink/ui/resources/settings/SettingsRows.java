package vertexlink.ui.resources.settings;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import vertexlink.ui.resources.global.ToggleSwitch;

public final class SettingsRows {
  public static HBox switchRow(String title, String subtitle, boolean initial) {
    return switchRow(title, subtitle, initial, null);
  }

  public static HBox switchRow(String title, String subtitle, boolean initial, Consumer<Boolean> onToggle) {
    ToggleSwitch toggle = new ToggleSwitch(initial);

    HBox row = new HBox(12, textBlock(title, subtitle), toggle);
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().addAll("settings-row", "settings-row-clickable");
    row.setPadding(new Insets(12, 14, 12, 14));

    row.setOnMouseClicked(e -> {
      toggle.toggle();

      if (onToggle != null) {
        onToggle.accept(toggle.isSelected());
      }
    });

    return row;
  }

  public static VBox textFieldRow(String title, String subtitle, String value, String prompt) {
    TextField field = new TextField(value);
    field.setPromptText(prompt);
    field.getStyleClass().add("settings-field");

    VBox row = new VBox(10, textBlock(title, subtitle), field);
    row.getStyleClass().add("settings-row");
    row.setPadding(new Insets(12, 14, 14, 14));

    return row;
  }

  private static VBox textBlock(String title, String subtitle) {
    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("settings-title-text");

    Label subtitleLabel = new Label(subtitle);
    subtitleLabel.getStyleClass().add("settings-subtitle-text");
    subtitleLabel.setWrapText(true);

    VBox block = new VBox(2, titleLabel, subtitleLabel);
    block.setAlignment(Pos.CENTER_LEFT);
    block.setMinWidth(0);
    HBox.setHgrow(block, Priority.ALWAYS);

    return block;
  }
}
