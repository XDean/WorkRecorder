package org.wenzhe.scrcap;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.wenzhe.scrcap.hotkey.HotKey;

/**
 * @author liuwenzhe2008@qq.com
 * @author XDean@github.com
 *
 */
public class ScrCapSetting {

  private File exportedDir;

  private int autoCaptureInteval = 5; // stop, unit is second

  private boolean enableAutoCapture = false;

  private String switchAutoCaptureKey = "WIN + W";

  private String captureKey = "WIN + A";

  private String clipKey = "WIN + Q";

  private Timer timer;

  private int maxAllowDiffForImageCompare = 100;

  public static File getWorkDir() {
    File workDir = new File(System.getProperty("user.home"), ".workrecorder");
    if (!workDir.exists()) {
      workDir.mkdirs();
    }
    return workDir;
  }

  public File getExportedDir() {
    return exportedDir;
  }

  public void setExportedDir(File exportedDir) {
    this.exportedDir = exportedDir;
  }

  public String getCaptureKey() {
    return captureKey;
  }

  public void setCaptureKey(String hotKey) {
    this.captureKey = hotKey;
    HotKey.registerCaptureHotKey(HotKey.CAPTURE, hotKey);
    ScrCapProps.save();
  }

  public int getAutoCaptureInteval() {
    return autoCaptureInteval;
  }

  public void setAutoCaptureInteval(int autoCaptureInteval) {
    this.autoCaptureInteval = autoCaptureInteval;
    updateTimer();
  }

  private void updateTimer() {
    if (enableAutoCapture && autoCaptureInteval > 0) {
      if (timer != null) {
        timer.cancel();
      }
      timer = new Timer(true);
      timer.schedule(new TimerTask() {

        @Override
        public void run() {
          ScreenCapture.getInstance().captScreen(false);
        }
      }, autoCaptureInteval * 1000, autoCaptureInteval * 1000);
    } else if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }

  public boolean isAutoCapture() {
    return enableAutoCapture && autoCaptureInteval > 0;
  }

  public String getClipKey() {
    return clipKey;
  }

  public void setClipKey(String clipKey) {
    this.clipKey = clipKey;
    HotKey.registerCaptureHotKey(HotKey.CLIP_CAPTURE, clipKey);
    ScrCapProps.save();
  }

  public int getMaxAllowDiffForImageCompare() {
    return maxAllowDiffForImageCompare;
  }

  public void setMaxAllowDiffForImageCompare(int maxAllowDiffForImageCompare) {
    this.maxAllowDiffForImageCompare = maxAllowDiffForImageCompare;
  }

  public boolean isEnableAutoCapture() {
    return enableAutoCapture;
  }

  public void setEnableAutoCapture(boolean enableAutoCapture) {
    this.enableAutoCapture = enableAutoCapture;
    updateTimer();
  }

  public String getSwitchAutoCaptureKey() {
    return switchAutoCaptureKey;
  }

  public void setSwitchAutoCaptureKey(String newKey) {
    this.switchAutoCaptureKey = newKey;
    updateTimer();
    HotKey.registerCaptureHotKey(HotKey.SWITCH_AUTO_CAPTURE_KEY, newKey);
    ScrCapProps.save();
  }
}
