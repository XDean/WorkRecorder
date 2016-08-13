package org.wenzhe.scrcap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author liuwenzhe2008@qq.com
 *
 */
public class ScrCapProps {
	
	private static final String EXPORTED_DIR = "exportedDir";

	private static final String AUTO_CAPTURE = "autoCapture";
	
	private static final String HOT_KEY = "hotKey";

	private static final String CLIP_KEY = "clipKey";

	private static final String MAX_ALLOW_DIFF_FOR_IMAGECOMPARE = "maxAllowDiffForImageCompare";
	
	private static final String SWITCH_AUTO_CAPTURE_KEY = "switchAutoCaptureKey";

	private static ScrCapSetting setting;
	
	public static ScrCapSetting getSetting() {
		if (setting == null) {
			setting = load();
		}
		return setting;
	}

	private static ScrCapSetting load() {
		ScrCapSetting setting = new ScrCapSetting();
		File f = getSettingPropertiesFile();
		if (!f.exists()) {
			setting.setExportedDir(getDefaultExportedDir());
			return setting;
		}
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(f));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setting.setExportedDir(getDefaultExportedDir());
			return setting;
		}
		try {
			setting.setAutoCaptureInteval(Integer.parseInt(
				prop.getProperty(AUTO_CAPTURE, "5")));
		} catch (NumberFormatException e) {
			setting.setAutoCaptureInteval(5);
		}
		
		setting.setExportedDir(new File(
				prop.getProperty(EXPORTED_DIR, getDefaultExportedDir().getPath())));
		String hotKey = prop.getProperty(HOT_KEY, "A");
		if (hotKey.length() == 0) {
			hotKey = "A";
		}
		setting.setHotKey(hotKey.charAt(0));
		
		String clipKey = prop.getProperty(CLIP_KEY, "Q");
		if (clipKey.length() == 0) {
			clipKey = "Q";
		}
		setting.setClipKey(clipKey.charAt(0));

		String switchAutoCaptKey = prop.getProperty(SWITCH_AUTO_CAPTURE_KEY, "W");
		if (switchAutoCaptKey.length() == 0) {
			switchAutoCaptKey = "W";
		}
		setting.setSwitchAutoCaptureKey(switchAutoCaptKey.charAt(0));
		
		String maxAllowDiffForImageCompare = prop.getProperty(MAX_ALLOW_DIFF_FOR_IMAGECOMPARE, "100");
		try {
			setting.setMaxAllowDiffForImageCompare(Integer.parseInt(maxAllowDiffForImageCompare));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return setting;
	}

	private static File getSettingPropertiesFile() {
		return new File(ScrCapSetting.getWorkDir(), "setting.properties");
	}

	private static File getDefaultExportedDir() {
		return new File(ScrCapSetting.getWorkDir(), "exported");
	}

	public static void save() {
		if (setting == null) {
			return;
		}
		Properties prop = new Properties();
		prop.setProperty(EXPORTED_DIR, setting.getExportedDir().getPath());
		prop.setProperty(AUTO_CAPTURE, String.valueOf(setting.getAutoCaptureInteval()));
		prop.setProperty(SWITCH_AUTO_CAPTURE_KEY, String.valueOf(setting.getSwitchAutoCaptureKey()));
		prop.setProperty(HOT_KEY, String.valueOf(setting.getHotKey()));
		prop.setProperty(CLIP_KEY, String.valueOf(setting.getClipKey()));
		prop.setProperty(MAX_ALLOW_DIFF_FOR_IMAGECOMPARE, String.valueOf(setting.getMaxAllowDiffForImageCompare()));
		
		File f = getSettingPropertiesFile();
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			prop.store(new FileOutputStream(f), "screen capture settings");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
