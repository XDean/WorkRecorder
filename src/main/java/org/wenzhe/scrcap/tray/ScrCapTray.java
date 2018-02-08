//package org.wenzhe.scrcap.tray;
//
//import java.awt.AWTException;
//import java.awt.MenuItem;
//import java.awt.PopupMenu;
//import java.awt.SystemTray;
//import java.awt.Toolkit;
//import java.awt.TrayIcon;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.text.MessageFormat;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//import javax.swing.ImageIcon;
//import javax.swing.JFileChooser;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.filechooser.FileNameExtensionFilter;
//
//import org.wenzhe.scrcap.ScrCapProps;
//import org.wenzhe.scrcap.ScrCapSetting;
//import org.wenzhe.scrcap.ScreenCapture;
//import org.wenzhe.scrcap.gif.GifConvertion;
//import org.wenzhe.scrcap.hotkey.HotKey;
//
///**
// * @author liuwenzhe2008@qq.com
// *
// */
//public class ScrCapTray {  
//  private static MenuItem autoCaptureItem;
//
//  public static void main(String[] args) {
//    if (SystemTray.isSupported()){
//      TrayIcon icon =
//          new TrayIcon(Toolkit.getDefaultToolkit().getImage(ScrCapTray.class.getResource("/icons/Hello_Kitty_Panda_16px.png")));
//
//      PopupMenu menu = new PopupMenu();  
//      menu.add(createOpenExportedFolderItem());
//      menu.add(createChangeExportedDirItem());
//      menu.add(createChangeHotKeyItem());
//      menu.add(createChangeClipKeyItem());
//      menu.add(createAutoChangeHotKeyItem());
//      
//      menu.add(createAutoCaptureItem());
//      menu.add(createMaxAllowDiffForImageCompareItem());
//      menu.add(createExportToGifItem());
//      menu.add(createAboutItem());
//      menu.add(createExitItem());
//
//      icon.setPopupMenu(menu);
//
//      SystemTray tray = SystemTray.getSystemTray();
//      try {
//        tray.add(icon);  
//      } catch (AWTException e1) {  
//        // TODO Auto-generated catch block  
//        e1.printStackTrace();  
//      }
//
//      HotKey.registerCaptureHotKey(HotKey.CAPTURE, ScrCapProps.getSetting().getCaptureKey());
//      HotKey.registerCaptureHotKey(HotKey.CLIP_CAPTURE, ScrCapProps.getSetting().getClipKey());
//      HotKey.registerCaptureHotKey(HotKey.SWITCH_AUTO_CAPTURE_KEY, ScrCapProps.getSetting().getSwitchAutoCaptureKey());
//      HotKey.start();
//    }  
//  }
//
//  private static MenuItem createExportToGifItem() {
//    MenuItem item = new MenuItem("Export to GIF Video");
//    item.addActionListener(e -> {
//      exportGif();
//    });
//    return item;
//  }
//
//  public static void exportGif() {
//    GifConvertion currentConvertion = GifConvertion.getCurrentConvertion();
//    if (currentConvertion != null) {
//      String progress = currentConvertion.getProgress();
//      String exportedGif = currentConvertion.getExportedGif().toString();
//      int r = JOptionPane.showConfirmDialog(null, 
//          "Now Export to GIF Video (" + progress + " for " + exportedGif + ")\n" 
//        + "Would you want to stop current GIF convertion?");
//      if (JOptionPane.YES_OPTION == r) {
//        currentConvertion.stopConvertion();
//      }
//      return;
//    }
//    JFileChooser c = new JFileChooser();
//    c.setFileSelectionMode(JFileChooser.FILES_ONLY);
//    c.addChoosableFileFilter(new FileNameExtensionFilter("GIF Image", "gif"));
//    c.setAcceptAllFileFilterUsed(true);
//    int rVal = c.showSaveDialog(null);
//    if (rVal != JFileChooser.APPROVE_OPTION) {
//      return;
//    }
//    Path fileToSave = c.getSelectedFile().toPath();
//    String gifFile = fileToSave.toString();
//    if (!gifFile.endsWith(".gif")) {
//      fileToSave = Paths.get(gifFile + ".gif");
//    }
//    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//    String param = JOptionPane.showInputDialog(
//        "Start time: yyyy-MM-dd hh:mm:ss, End time: yyyy-MM-dd hh:mm:ss, Interval Between Frames (ms): 500", 
//        now + ", " + now + ", " + 500
//        );
//    if (param == null || param.isEmpty()) {
//      return;
//    }
//    GifConvertion.convertToGif(param, fileToSave);
//  }
//
//  private static MenuItem createMaxAllowDiffForImageCompareItem() {
//    final ScrCapSetting setting = ScrCapProps.getSetting();
//    final String label = "Set Error Threshold ({0} pixels)";
//    final MenuItem item = new MenuItem(MessageFormat.format(label, setting.getMaxAllowDiffForImageCompare()));  
//    item.addActionListener(e -> {
//      int old = setting.getMaxAllowDiffForImageCompare();
//      String newStr = JOptionPane.showInputDialog("Set the maximum count of allowed different pixels", 
//          String.valueOf(old));
//      int newValue = old;
//      try {
//        newValue = Integer.parseInt(newStr);
//      } catch (NumberFormatException ex) {
//        return;
//      }
//      if (old == newValue) {
//        return;
//      }
//      setting.setMaxAllowDiffForImageCompare(newValue);
//      ScrCapProps.save();
//      item.setLabel(MessageFormat.format(label, setting.getMaxAllowDiffForImageCompare()));
//    });
//    return item;
//  }
//
//  private static MenuItem createAutoChangeHotKeyItem() {
//    final ScrCapSetting setting = ScrCapProps.getSetting();
//    final String label = "Set Hotkey to Switch Screen Recorder (Win + {0})";
//    final MenuItem item = new MenuItem(MessageFormat.format(label, setting.getSwitchAutoCaptureKey()));  
//    item.addActionListener(e -> {
//      String oldKey = setting.getSwitchAutoCaptureKey();
//      String newKeyStr = JOptionPane.showInputDialog(
//          "Set the hotkey for switching screen recorder (Window + Charactor)", Character.toString(oldKey));
//      if (newKeyStr == null || newKeyStr.length() == 0) {
//        return;
//      }
//      char newKey = Character.toUpperCase(newKeyStr.charAt(0));
//      if (oldKey == newKey) {
//        return;
//      }
//      setting.setSwitchAutoCaptureKey(newKey);
//      HotKey.registerCaptureHotKey(HotKey.SWITCH_AUTO_CAPTURE_KEY, newKey);
//      ScrCapProps.save();
//      item.setLabel(MessageFormat.format(label, setting.getSwitchAutoCaptureKey()));
//    });
//    return item;
//  }
//
//  private static MenuItem createOpenExportedFolderItem() {
//    MenuItem item = new MenuItem("Open Exported Folder");  
//    item.addActionListener(e -> {  
//      ProcessBuilder pb = new ProcessBuilder("explorer", 
//          ScreenCapture.getInstance().getLastCaptureFolder().getPath()); 
//      try {
//        pb.start();
//      } catch (IOException e1) {
//        // TODO Auto-generated catch block
//        e1.printStackTrace();
//      }
//    });
//    return item;
//  }
//
//  private static MenuItem createAutoCaptureItem() {
//    final ScrCapSetting setting = ScrCapProps.getSetting();
//    final String labelFormat = "Set Interval for Screen Recorder (every {0} seconds, {1})";
//    String label = MessageFormat.format(labelFormat, setting.getAutoCaptureInteval(),
//        setting.isAutoCapture() ? "opened" : "closed");
//    final MenuItem item = new MenuItem(label);  
//    //final CheckboxMenuItem item = new CheckboxMenuItem(label);  
//    item.addActionListener(e -> {
//      int oldInteval = setting.getAutoCaptureInteval();
//      String newIntevalStr = JOptionPane.showInputDialog(
//          "Set Interval for Screen Recorder (unit: second, 0 for closing)", 
//          String.valueOf(oldInteval));
//      if (newIntevalStr == null) {
//        return;
//      }
//      int newInteval;
//      try {
//        newInteval = Integer.parseInt(newIntevalStr);
//      } catch (NumberFormatException e1) {
//        e1.printStackTrace();
//        return;
//      }
//      if (newInteval == oldInteval) {
//        return;
//      }
//      setting.setAutoCaptureInteval(newInteval);
//      ScrCapProps.save();
//      String label1 = MessageFormat.format(labelFormat, setting.getAutoCaptureInteval(),
//          setting.isAutoCapture() ? "opened" : "closed");
//      item.setLabel(label1); 
//    });
//    autoCaptureItem = item;
//    return item;
//  }
//
//  public static void updateAutoCaptureItemLabel() {
//    if (autoCaptureItem == null) {
//      return;
//    }
//    final ScrCapSetting setting = ScrCapProps.getSetting();
//    final String labelFormat = "Set Interval for Screen Recorder (every {0} seconds, {1})";
//    String label = MessageFormat.format(labelFormat, setting.getAutoCaptureInteval(),
//        setting.isAutoCapture() ? "opened" : "closed");
//    autoCaptureItem.setLabel(label); 
//  }
//
//  private static MenuItem createChangeHotKeyItem() {
//    final ScrCapSetting setting = ScrCapProps.getSetting();
//    final String label = "Set Hotkey to Capture Full Screen (Win + {0})";
//    final MenuItem item = new MenuItem(MessageFormat.format(label, setting.getCaptureKey()));  
//    item.addActionListener(e -> {
//      char oldKey = setting.getCaptureKey();
//      String newKeyStr = JOptionPane.showInputDialog(
//          "Set Hotkey to Capture Full Screen (Window + Charactor)", 
//          Character.toString(oldKey));
//      if (newKeyStr == null || newKeyStr.length() == 0) {
//        return;
//      }
//      char newKey = Character.toUpperCase(newKeyStr.charAt(0));
//      if (oldKey == newKey) {
//        return;
//      }
//      setting.setCaptureKey(newKey);
//      HotKey.registerCaptureHotKey(HotKey.CAPTURE, newKey);
//      ScrCapProps.save();
//      item.setLabel(MessageFormat.format(label, setting.getCaptureKey()));
//    });
//    return item;
//  }
//
//  private static MenuItem createChangeClipKeyItem() {
//    final ScrCapSetting setting = ScrCapProps.getSetting();
//    final String label = "Set Hotkey to clip screen to capture (Win + {0})";
//    final MenuItem item = new MenuItem(MessageFormat.format(label, setting.getClipKey()));  
//    item.addActionListener(e -> {
//      char oldKey = setting.getClipKey();
//      String newKeyStr = JOptionPane.showInputDialog(
//          "Set Hotkey to clip screen to capture (Window + Charactor)", 
//          Character.toString(oldKey));
//      if (newKeyStr == null || newKeyStr.length() == 0) {
//        return;
//      }
//      char newKey = Character.toUpperCase(newKeyStr.charAt(0));
//      if (oldKey == newKey) {
//        return;
//      }
//      setting.setClipKey(newKey);
//      HotKey.registerCaptureHotKey(HotKey.CLIP_CAPTURE, newKey);
//      ScrCapProps.save();
//      item.setLabel(MessageFormat.format(label, setting.getClipKey()));
//    });
//    return item;
//  }
//
//  private static MenuItem createChangeExportedDirItem() {
//    MenuItem item = new MenuItem("Set Exported Folder");  
//    item.addActionListener(e -> {
//      ScrCapSetting setting = ScrCapProps.getSetting();
//      File oldExportedDir = setting.getExportedDir();
//      if (!oldExportedDir.isDirectory()) {
//        oldExportedDir.mkdirs();
//      }
//      JFileChooser fc = new JFileChooser(oldExportedDir);
//      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//      fc.showOpenDialog(new JLabel());
//      File newDir = fc.getSelectedFile();  
//      if (newDir == null || !newDir.isDirectory()
//          || newDir.equals(setting.getExportedDir())) {
//        return;
//      }
//      setting.setExportedDir(newDir);
//      ScrCapProps.save();
//    });
//    return item;
//  }
//
//  private static MenuItem createExitItem() {
//    MenuItem item = new MenuItem("Exit");  
//    item.addActionListener(e -> {  
//      HotKey.dispose();
//      System.exit(0);  
//    });
//    return item;
//  }
//  
//  private static MenuItem createAboutItem() {
//    MenuItem item = new MenuItem("About");  
//    item.addActionListener(e -> {
//      ImageIcon icon = new ImageIcon(ScrCapTray.class.getResource(
//          "/icons/Hello_Kitty_Panda_16px.png"));
//      JOptionPane.showMessageDialog(null, 
//          "Author: Wenzhe Liu\nContact: liuwenzhe2008@qq.com", 
//          "About", JOptionPane.OK_OPTION, icon);
//    });
//    return item;
//  }
//}   
