package com.hoge.fx.util;
import org.eclipse.swt.internal.win32.FILETIME;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.TCHAR;

import com.hoge.fx.exception.NotifyNotExistException;
import com.hoge.fx.vo.KeyWithTimestamp;


public class Reg {
	public static KeyWithTimestamp regEnumKey(int hKey, int dwIndex) {
		TCHAR lpName = new TCHAR(OS.CP_INSTALLED, 256);
		int[] lpcName = new int[] { 256 };
		FILETIME ft = new FILETIME();
		int hresult = OS.RegEnumKeyEx(hKey, dwIndex, lpName, lpcName, null,
				null, null, ft);
		java.util.Date javaDate = filetimeToDate(ft.dwHighDateTime,
				ft.dwLowDateTime);

		if (hresult == OS.S_OK) {
			return new KeyWithTimestamp(lpName.toString(0, lpName.strlen()),
					javaDate);
		}
		throw new NotifyNotExistException();
	}

	private static String toBinaryString(int value) {
		String str = Integer.toBinaryString(value);
		int len = str.length();
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<32-len; i++) {
			sb.append("0");
		}
		sb.append(str);
		return sb.toString();
	}

	public static java.util.Date filetimeToDate(int high, int low) {
		String highByBits = toBinaryString(high);
		String lowByBits = toBinaryString(low);
		String fullByBits = highByBits + lowByBits;
		long filetime = Long.parseLong(fullByBits, 2);

		long ms_since_16010101 = filetime / (1000L * 10L);
		long ms_since_19700101 = ms_since_16010101 - 11644473600000L;
		java.util.Date result = new java.util.Date(ms_since_19700101);
		return result;
	}

	public static int regOpenKey(long hKey, String subKey) {
		TCHAR lpSubKey = new TCHAR(OS.CP_INSTALLED, subKey, true);
		int[] phkResult = new int[1];
		int hresult = OS.RegOpenKeyEx((int) hKey, lpSubKey, 0,
				OS.KEY_ENUMERATE_SUB_KEYS | OS.KEY_QUERY_VALUE, phkResult);
		if ((hresult == OS.S_OK)) {
			return (int) phkResult[0];
		}
		throw new NotifyNotExistException();
	}

	public static int regCloseKey(int hKey) {
		return OS.RegCloseKey(hKey);
	}

	public static String regQueryValue(int hKey, String valueName) {
		TCHAR lpValueName = new TCHAR(OS.CP_INSTALLED, valueName, true);
		TCHAR lpData = new TCHAR(OS.CP_INSTALLED, 256);
		int[] lpcbData = new int[] { 256 };
		int hresult = OS.RegQueryValueEx(hKey, lpValueName, 0, null, lpData,
				lpcbData);
		return (hresult == OS.S_OK) ? lpData.toString(0, lpData.strlen())
				: null;
	}
}
