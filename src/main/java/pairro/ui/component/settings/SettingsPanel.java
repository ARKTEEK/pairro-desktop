package pairro.ui.component.settings;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pairro.device.DeviceName;
import pairro.store.SettingsStore;
import pairro.ui.component.common.ComponentFactory;
import pairro.ui.icon.IconPaths;

public class SettingsPanel extends VBox {

  public SettingsPanel(Runnable onBack, SettingsStore settingsStore, Consumer<String> onDeviceNameChanged) {
    super(0);
    getStyleClass().add("settings-view");

    Button backBtn = ComponentFactory.createIconButton(IconPaths.ARROW_BACK, "settings-back-brn");
    ComponentFactory.setTooltip(backBtn, "Back");
    backBtn.setOnAction(e -> {
      if (onBack != null) {
        onBack.run();
      }
    });

    Label title = new Label("Settings");
    title.getStyleClass().add("settings-title");

    HBox topBar = new HBox(14, backBtn, title);
    topBar.setAlignment(Pos.CENTER_LEFT);
    topBar.getStyleClass().add("settings-top-bar");

    HBox closeToTrayRow = SettingsRows.switchRow(
        "Close minimizes to tray",
        "Closing the window keeps Pairro running in the tray",
        settingsStore.isCloseToTray());

    VBox cards = new VBox(12,
        new SettingsCard("Startup",
            SettingsRows.switchRow(
                "Open on Windows startup",
                "Launch Pairro when you sign in to Windows",
                settingsStore.isOpenOnStartup()),

            SettingsRows.switchRow(
                "Open in tray",
                "Start minimized in the system tray instead of showing the window",
                settingsStore.isOpenInTray()),
            closeToTrayRow),

        new SettingsCard("Device",
            SettingsRows.textFieldRow(
                "Device name",
                "How this PC appears to other devices",
                settingsStore.getDeviceName(),
                DeviceName.DEFAULT,
                raw -> {
                  String name = DeviceName.normalize(raw);

                  if (!name.equals(settingsStore.getDeviceName())) {
                    settingsStore.setDeviceName(name);
                    onDeviceNameChanged.accept(name);
                  }

                  return name;
                }),

            SettingsRows.switchRow(
                "Auto-start discoverability",
                "Become visible to phones as soon as the app opens",
                settingsStore.isAutoStartDiscoverability(),
                settingsStore::setAutoStartDiscoverability)),

        new SettingsCard("Behavior",
            SettingsRows.switchRow(
                "Show notifications",
                "Notify when a device connects or disconnects",
                settingsStore.isShowNotifications())),

        new SettingsCard("Safety",
            SettingsRows.switchRow(
                "Confirm before unpairing",
                "Ask first when removing a paired device",
                settingsStore.isConfirmBeforeUnpairing())));

    Label version = new Label("Pairro 1.0");
    version.getStyleClass().add("settings-version");
    version.setMaxWidth(Double.MAX_VALUE);
    version.setAlignment(Pos.CENTER);

    VBox content = new VBox(12, cards, version);
    content.getStyleClass().add("settings-content");

    ScrollPane scrollPane = new ScrollPane(content);
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.getStyleClass().add("groups-scroll");
    VBox.setVgrow(scrollPane, Priority.ALWAYS);

    VBox sheet = new VBox(0, scrollPane);
    sheet.getStyleClass().add("settings-sheet");
    VBox.setVgrow(sheet, Priority.ALWAYS);

    getChildren().addAll(topBar, sheet);
  }

}
