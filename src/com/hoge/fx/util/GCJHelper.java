package com.hoge.fx.util;

public class GCJHelper {
	public static String yen2slash(String s) {
		if (s == null) {
			return null;
		}
		return s.replace('\\',  '/');
	}
}
