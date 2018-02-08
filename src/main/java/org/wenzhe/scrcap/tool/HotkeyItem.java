package org.wenzhe.scrcap.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import xdean.tool.api.impl.AbstractToolItem;

public class HotkeyItem extends AbstractToolItem {
  private final String text;
  private final Supplier<String> getKey;
  private final Consumer<String> setKey;

  public HotkeyItem(String text, Supplier<String> getKey, Consumer<String> setKey) {
    super(text);
    this.text = text;
    this.getKey = getKey;
    this.setKey = setKey;
  }

  @Override
  public void onClick() {
    Platform.runLater(() -> {
      TextInputDialog in = new TextInputDialog(getKey.get());
      in.setHeaderText("Set hotkey");
      in.setContentText("Select the input field and press the new hotkey. Only ALT/SHIFT/CTRL + A~Z is legal.");
      TextField editor = in.getEditor();
      editor.setEditable(false);
      editor.setOnKeyPressed(e -> {
        List<String> list = new ArrayList<>();
        if (e.isAltDown()) {
          list.add("ALT");
        }
        if (e.isShiftDown()) {
          list.add("SHIFT");
        }
        if (e.isControlDown()) {
          list.add("CTRL");
        }
        if (e.getCode().isLetterKey()) {
          list.add(e.getCode().toString());
        } else {
          list.add("");
        }
        editor.setText(list.stream().collect(Collectors.joining(" + ")));
        e.consume();
      });
      in.showAndWait().ifPresent(s -> {
        setKey.accept(Arrays.stream(s.split(" \\+ ")).collect(Collectors.joining("+")));
        textProperty().set(formatText(text, s));
      });
    });
  }

  protected String formatText(String text, String key) {
    return text + "(" + key + ")";
  }
}