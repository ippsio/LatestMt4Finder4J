package com.hoge.fx.finder;

import java.text.SimpleDateFormat;
import java.util.TreeMap;

import org.eclipse.swt.internal.win32.OS;

import com.hoge.fx.constant.Const;
import com.hoge.fx.exception.NotifyNotExistException;
import com.hoge.fx.util.GCJHelper;
import com.hoge.fx.util.Reg;
import com.hoge.fx.vo.KeyWithTimestamp;

@SuppressWarnings("unchecked")
public class LatestMt4Finder implements Const {

	public static String findLatestMt4Folder(String broakerName, boolean showKey, boolean showDate) {

		@SuppressWarnings("rawtypes")
		TreeMap mt4Keys = searchMt4Keys(broakerName, OS.HKEY_LOCAL_MACHINE, UNINSTALL_32,
				UNINSTALL_64);

		if (mt4Keys != null && mt4Keys.size() > 0) {
			Long latestInstallDateMillis = (Long) mt4Keys.lastKey();
			String latestInstallKey = (String) mt4Keys
					.get(latestInstallDateMillis);
			String installLocation = getInstallFolder(latestInstallKey);
			installLocation = GCJHelper.yen2slash(installLocation);

			StringBuffer sb = new StringBuffer();
			if (showKey) {
				sb.append(latestInstallKey).append(",");
			}
			if (showDate) {
				sb.append(
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(new java.util.Date(
										latestInstallDateMillis))).append(",");
			}
			sb.append(installLocation);
			return sb.toString();
		} else {
			throw new NotifyNotExistException();
		}
	}

	private static String getInstallFolder(String keyName) {
		int hKey = Reg.regOpenKey(OS.HKEY_LOCAL_MACHINE, keyName);
		String value = Reg.regQueryValue(hKey, "InstallLocation");
		return value;
	}

	private static class Condition {
		public String valueName;
		public String expectedValue;
		public Condition(String valueName, String expectedValue) {
			this.valueName = valueName;
			this.expectedValue = expectedValue;
		}
	}

	private static boolean findRegistory(int hRootKey, String keyName, Condition[] conds) {

		int hKey = Reg.regOpenKey(hRootKey, keyName);
		boolean result = true;
		for (int i=0; i<conds.length; i++) {
			Condition cond = conds[i];
			String value = Reg.regQueryValue(hKey, cond.valueName);
			result = result && (cond.expectedValue.equals(value));
		}
		Reg.regCloseKey(hKey);
		return result;
	}

	@SuppressWarnings("rawtypes")
	private static TreeMap searchMt4Keys(String broakerName, int hRootKey, String key32bit, String key64bit) {
		TreeMap mt4Keys = new TreeMap();
		String[] perBitKeys = new String[] { key32bit, key64bit };
		for (int i = 0; i < perBitKeys.length; i++) {
			String perBitKey = perBitKeys[i];
			int hKey = 0;
			try {
				hKey = Reg.regOpenKey(hRootKey, perBitKey);
				for (int dwIndex = 0;; dwIndex++) {
					KeyWithTimestamp keyWithTs = Reg.regEnumKey(hKey, dwIndex);
					String subKey = perBitKey + "\\" + keyWithTs.key;
					Condition[] conds = null;
					if (broakerName != null && broakerName.length() > 0) {
						conds = new Condition[] {
								new Condition(DISPLAYNAME, broakerName),
								new Condition(PUBLISHER, META_QUOTES),
						};
					} else {
						conds = new Condition[] {
								new Condition(PUBLISHER, META_QUOTES),
						};
					}
					if (findRegistory(hRootKey, subKey, conds)) {
						mt4Keys.put(keyWithTs.date.getTime(), subKey);
					}
				}
			} catch (NotifyNotExistException e) {
				// nope
			}
			Reg.regCloseKey(hKey);
		}

		return mt4Keys;
	}
}
