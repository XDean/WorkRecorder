package org.wenzhe.scrcap.tool;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import javafx.util.StringConverter;
import xdean.tool.api.impl.AbstractToolItem;

public class InputItem<T> extends AbstractToolItem {
  private final String text;
  private final Supplier<T> getter;
  private final Consumer<T> setter;
  private final StringConverter<T> converter;

  public InputItem(String text, Supplier<T> getter, Consumer<T> setter, StringConverter<T> converter) {
    super();
    this.text = text;
    this.getter = getter;
    this.setter = setter;
    this.converter = converter;
    this.textProperty().set(formatText(text, converter.toString(getter.get())));
  }

  @Override
  public void onClick() {
    Platform.runLater(() -> {
      TextInputDialog in = new TextInputDialog(converter.toString(getter.get()));
      in.setHeaderText("Set value");
      in.setContentText("Set " + text);
      in.showAndWait().ifPresent(s -> {
        setter.accept(converter.fromString(s));
        textProperty().set(formatText(text, s));
      });
    });
  }

  protected String formatText(String text, String value) {
    return text + "(" + value + ")";
  }
}