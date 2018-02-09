package org.wenzhe.scrcap.tool;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.wenzhe.scrcap.ScrCapProps;
import org.wenzhe.scrcap.ScrCapSetting;
import org.wenzhe.scrcap.ScreenCapture;
import org.wenzhe.scrcap.gif.GifConvertion;
import org.wenzhe.scrcap.hotkey.HotKey;

import com.sun.javafx.application.PlatformImpl;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.util.converter.IntegerStringConverter;
import xdean.tool.api.IToolGetter;
import xdean.tool.api.Tool;
import xdean.tool.api.impl.SimpleToolItem;
import xdean.tool.sys.other.CommandTool;

@Tool(path = "ScreenRecorder")
public interface ScreenRecorderTool {
  Void init = init();
  ScrCapSetting setting = ScrCapProps.getSetting();

  @Tool
  IToolGetter OPEN_EXPORTED_FORLDER = () -> CommandTool.create("Open Exported Folder", "explorer",
      ScreenCapture.getInstance().getLastCaptureFolder().getPath());

  @Tool
  IToolGetter CHANGE_EXPORTED_DIR = () -> new SimpleToolItem("Set Exported Folder", () -> changeExportedDir());

  @Tool
  IToolGetter CHANGE_HOTKEY = () -> new HotkeyItem("Set Hotkey to Capture Full Screen", setting::getCaptureKey,
      setting::setCaptureKey);

  @Tool
  IToolGetter CLIP_HOTKEY = () -> new HotkeyItem("Set Hotkey to Clip Screen", setting::getClipKey,
      setting::setClipKey);

  @Tool
  IToolGetter AUTO_HOTKEY = () -> new HotkeyItem("Set Hotkey to Auto Capture Screen", setting::getSwitchAutoCaptureKey,
      setting::setSwitchAutoCaptureKey);

  @Tool
  IToolGetter AUTO_CAPTURE_INTERVAL = () -> new InputItem<>("Interval for Screen Recorder", setting::getAutoCaptureInteval,
      setting::setAutoCaptureInteval, new IntegerStringConverter());

  @Tool
  IToolGetter MAX_DIFF = () -> new InputItem<>("Error Threshold pixel", setting::getMaxAllowDiffForImageCompare,
      setting::setMaxAllowDiffForImageCompare, new IntegerStringConverter());

  @Tool
  IToolGetter EXPORT_GIF = () -> new SimpleToolItem("Export Gif", () -> exportGif());

  @Tool
  IToolGetter ABOUT = () -> new SimpleToolItem("About", () -> Platform.runLater(
      () -> new Alert(AlertType.INFORMATION, "Author: Wenzhe Liu\\nContact: liuwenzhe2008@qq.com", ButtonType.OK).showAndWait()));

  static void changeExportedDir() {
    ScrCapSetting setting = ScrCapProps.getSetting();
    File oldExportedDir = setting.getExportedDir();
    if (!oldExportedDir.isDirectory()) {
      oldExportedDir.mkdirs();
    }
    JFileChooser fc = new JFileChooser(oldExportedDir);
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fc.showOpenDialog(new JLabel());
    File newDir = fc.getSelectedFile();
    if (newDir == null || !newDir.isDirectory()
        || newDir.equals(setting.getExportedDir())) {
      return;
    }
    setting.setExportedDir(newDir);
    ScrCapProps.save();
  }

  static Void init() {
    PlatformImpl.startup(() -> {
      PlatformImpl.setTaskbarApplication(false);
      Platform.setImplicitExit(false);
    });
    HotKey.start();
    return null;
  }

  static void exportGif() {
    GifConvertion currentConvertion = GifConvertion.getCurrentConvertion();
    if (currentConvertion != null) {
      String progress = currentConvertion.getProgress();
      String exportedGif = currentConvertion.getExportedGif().toString();
      int r = JOptionPane.showConfirmDialog(null,
          "Now Export to GIF Video (" + progress + " for " + exportedGif + ")\n"
              + "Would you want to stop current GIF convertion?");
      if (JOptionPane.YES_OPTION == r) {
        currentConvertion.stopConvertion();
      }
      return;
    }
    JFileChooser c = new JFileChooser();
    c.setFileSelectionMode(JFileChooser.FILES_ONLY);
    c.addChoosableFileFilter(new FileNameExtensionFilter("GIF Image", "gif"));
    c.setAcceptAllFileFilterUsed(true);
    int rVal = c.showSaveDialog(null);
    if (rVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    Path fileToSave = c.getSelectedFile().toPath();
    String gifFile = fileToSave.toString();
    if (!gifFile.endsWith(".gif")) {
      fileToSave = Paths.get(gifFile + ".gif");
    }
    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String param = JOptionPane.showInputDialog(
        "Start time: yyyy-MM-dd hh:mm:ss, End time: yyyy-MM-dd hh:mm:ss, Interval Between Frames (ms): 500",
        now + ", " + now + ", " + 500);
    if (param == null || param.isEmpty()) {
      return;
    }
    GifConvertion.convertToGif(param, fileToSave);
  }
}
