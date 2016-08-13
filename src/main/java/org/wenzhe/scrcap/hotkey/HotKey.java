package org.wenzhe.scrcap.hotkey;

import org.wenzhe.scrcap.ScrCapProps;
import org.wenzhe.scrcap.ScrCapSetting;
import org.wenzhe.scrcap.ScreenCapture;
import org.wenzhe.scrcap.tray.ScrCapTray;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

/**
 * @author liuwenzhe2008@qq.com
 *
 */
public class HotKey {
	
	public static final int CAPTURE = 1;
	
	public static final int CLIP_CAPTURE = 2;

	public static final int SWITCH_AUTO_CAPTURE_KEY = 3;

	public static void registerCaptureHotKey(int type, char hotKey) {
		JIntellitype.getInstance().unregisterHotKey(type);
		JIntellitype.getInstance().registerHotKey(type, JIntellitype.MOD_WIN, hotKey);
	}
	
	public static void dispose() {
		JIntellitype.getInstance().cleanUp();
	}
	
	public static void start() {
		JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {

			@Override
			public void onHotKey(int identifier) {
				switch (identifier) {
				case CAPTURE:
					ScreenCapture.getInstance().captScreen(true);
					break;
				case CLIP_CAPTURE:
					ScreenCapture.getInstance().clipCaptScreen();
					break;
				case SWITCH_AUTO_CAPTURE_KEY:
					ScrCapSetting setting = ScrCapProps.getSetting();
					setting.setEnableAutoCapture(!setting.isEnableAutoCapture());
					ScrCapTray.updateAutoCaptureItemLabel();
					break;
				default:
					break;
				}
			}		
		});
	}
}
