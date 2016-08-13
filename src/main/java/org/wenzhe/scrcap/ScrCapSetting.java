package org.wenzhe.scrcap;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author liuwenzhe2008@qq.com
 *
 */
public class ScrCapSetting {

	private File exportedDir;
	
	private int autoCaptureInteval = 5;  // stop, unit is second
	
	private boolean enableAutoCapture = false;
	
	private char switchAutoCaptureKey = 'W';
	
	private char hotKey = 'A';  // capture
	
	private char clipKey = 'Q';
	
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

	public char getHotKey() {
		return hotKey;
	}

	public void setHotKey(char hotKey) {
		this.hotKey = hotKey;
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

	public char getClipKey() {
		return clipKey;
	}

	public void setClipKey(char clipKey) {
		this.clipKey = clipKey;
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

	public char getSwitchAutoCaptureKey() {
		return switchAutoCaptureKey;
	}

	public void setSwitchAutoCaptureKey(char newKey) {
		this.switchAutoCaptureKey = newKey;
		updateTimer();
	}
}
