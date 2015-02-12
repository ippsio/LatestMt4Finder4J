package com.hoge.fx;

import com.hoge.fx.constant.Const;
import com.hoge.fx.exception.NotifyNotExistException;
import com.hoge.fx.finder.LatestMt4Finder;
import com.hoge.fx.finder.TerminalDataFolderFinder;

public class FindLatestMt4 implements Const {

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		int exitCd = Integer.MIN_VALUE;

		try {
			String mt4InstallFolder = null;
			String terminalDataFolder = null;
			try {
				// arguments
				String broakerName = obtainBrokerName(args);
				boolean showKey = obtainArguments(args, "-showKey");
				boolean showDate = obtainArguments(args, "-showDate");

				// MT4フォルダがあるかどうかを調べる
				mt4InstallFolder = LatestMt4Finder.findLatestMt4Folder(broakerName, showKey, showDate);
				sb.append("mt4_install_folder=").append(mt4InstallFolder).append("\r\n");

			} catch (NotifyNotExistException e) {
				sb.append("mt4_install_folder=not_found").append("\n");
				exitCd = (WARN_NO_AVAILABLE_MT4_FOUND);
				return;
			}

			// データフォルダの存在有無をチェック
			terminalDataFolder = TerminalDataFolderFinder.getTerminalDataFolder(mt4InstallFolder);
			if (terminalDataFolder.length() > 0) {
				sb.append("terminal_data_folder=").append(terminalDataFolder).append("\r\n");
				exitCd = GOOD_MT4_FOUND_DATAFOLDER_FOUND;
			} else {
				sb.append("terminal_data_folder=").append("").append("\r\n");
				exitCd = (GOOD_MT4_FOUND_DATAFOLDER_NOT_FOUND);
				return;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			exitCd = (ERROR_SOMETHING_WRONG);
			return;

		} finally {
			System.out.println("[result]");
			System.out.println("exitCd=" + exitCd);
			System.out.println(sb.toString());
			System.exit(exitCd);
		}
	}

	private static String obtainBrokerName(String[] args) {
		if (args == null || args.length == 0) {
			return null;
		}

		String result = null;
		for (int i=0; i<args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("-broaker=")) {
				result = arg.replace("-broaker=", "");
				result = result.replace("^\"", "");
				result = result.replace("\".*", "");
				break;
			}
		}
		return result;
	}

	private static boolean obtainArguments(String[] args, String findIt) {
		if (args == null || args.length == 0) {
			return false;
		}

		for (int i=0; i<args.length; i++) {
			if(args[i].equals(findIt)) {
				return true;
			}
		}
		return false;
	}
}
