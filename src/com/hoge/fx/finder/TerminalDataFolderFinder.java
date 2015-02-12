package com.hoge.fx.finder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.hoge.fx.constant.Const;
import com.hoge.fx.util.GCJHelper;

public class TerminalDataFolderFinder implements Const {

	@SuppressWarnings("unchecked")
	public static String getTerminalDataFolder(String mt4InstallFolder) {
		try {
			String terminalsFolder = System.getenv("APPDATA") + "\\MetaQuotes\\Terminal";
			terminalsFolder = GCJHelper.yen2slash(terminalsFolder);

			File fTerminals = new File(terminalsFolder);
			if (!fTerminals.exists()) {
				throw new RuntimeException("omg_folder_not_found");
			}

			File[] listFiles = fTerminals.listFiles();
			@SuppressWarnings("rawtypes")
			List originTxtList = new ArrayList();
			for (int i = 0; i < listFiles.length; i++) {
				File eachFolder = listFiles[i];
				File[] originTxts = eachFolder.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return (name.equals("origin.txt"));
					}
				});
				if (originTxts != null && originTxts.length > 0) {
					// あったとしても１フォルダに１こだけなので、配列の0番だけ処理
					originTxtList.add(originTxts[0]);
				}
			}

			String hiddenDatafolder = "";
			for (int i = 0; i < originTxtList.size(); i++) {
				File originTxt = (File) originTxtList.get(i);
				String content = readOneLine(originTxt, mt4InstallFolder);
				content = GCJHelper.yen2slash(content);

				if (mt4InstallFolder.toLowerCase().equals(content.toLowerCase())) {
					hiddenDatafolder = originTxt.getParent();
					hiddenDatafolder = GCJHelper.yen2slash(hiddenDatafolder);
					break;
				}
			}
			return hiddenDatafolder;

		} catch (Exception e) {
			System.out.println(e);
			throw new RuntimeException(e);
		} catch (Throwable t) {
			System.out.println("koreka-");
			throw new RuntimeException(t);
		}
	}

	private static String readOneLine(File f, String mt4InstallFolder) {
		BufferedReader br = null;
		String line = null;
		String[] encodes1 = new String[]{"utf-16"};
		String[] encodes2 = new String[]{"utf-16"};
		String[] encodes3 = new String[]{"utf-16"};
		try {
			for (int i=0; i<encodes1.length; i++) {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(f), encodes1[i]));
				// 1行だけ読めば十分
				line = excludeBOM(br.readLine());
				line = GCJHelper.yen2slash(line);

				for (int j=0; j<encodes2.length; j++) {
					for (int k=0; k<encodes3.length; k++) {
						StringBuffer sb = new StringBuffer();
						sb.append("["+encodes1[i]).append("]-");
						sb.append("["+encodes2[j]).append("]-");
						sb.append("["+encodes3[k]).append("] ");

						String content = "";
						try {
							content = new String(line.getBytes(encodes2[j]), encodes3[k]);
							content = GCJHelper.yen2slash(content);
						} catch (Throwable t) {
							t.printStackTrace();
						}
						sb.append("'").append(content).append("' ");
						boolean compare = (mt4InstallFolder.equals(content));
						sb.append(">>>>>>>>>>>>>>>>>>>>>>>").append(compare);
//						System.out.println(sb.toString());

						line = content;
					}
				}
			}

			return new String(line);

		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				// nope
			}
		}
	}

	private static String excludeBOM(String original_str) {
		if (original_str != null) {
			char c = original_str.charAt(0);
			if (Integer.toHexString(c).equals("feff")) {
				StringBuilder sb = new StringBuilder();
				for (int i=1; i < original_str.length(); i++) {
					sb.append(original_str.charAt(i));
				}
				return sb.toString();
			} else {
				return original_str;
			}
		} else {
			return "";
		}
	}
}
