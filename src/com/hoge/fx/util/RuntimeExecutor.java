package com.hoge.fx.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RuntimeExecutor {
	public static final int STDOUT = 0;
	public static final int STDERR = 1;
	public static final int RETURN_CD = 2;

	/**
	 * �O���R�}���h�����s���܂��B �܂����^���[���l�ŁA�W���o�́A�G���[�o�� �A���^�[���R�[�h���擾���܂��B ��F
	 * execCommand("notepad.exe");
	 *
	 * @see execCommand(String[] cmds) �����s����R�}���h�Ɉ����i�p�����[�^�j������ꍇ�́A �ȉ����g�p���Ă��������B
	 * @param cmd
	 *            ���s����R�}���h
	 * @return �R�}���h���s���ʏ���ێ�����String�z�� �z��[0] �� �W���o�� �z��[1] �� �G���[�o�� �z��[2] ��
	 *         ���^�[���R�[�h
	 * @throws IOException
	 *             ���o�̓G���[�����������ꍇ
	 */
	public static String[] execCommand(String cmd) throws IOException,
			InterruptedException {
		return execCommand(new String[] { cmd });
	}

	/**
	 * �O���R�}���h�������i�p�����[�^�j���w�肵�Ď��s���܂��B �܂����^���[���l�ŁA�W���o�́A�G���[�o�� �A���^�[���R�[�h���擾���܂��B ��F
	 * execCommand(new String[]{"notepad.exe","C:\test.txt"});
	 *
	 * Process.waitFor()�����s���Ă��܂��̂ŁA�O���R�}���h�̎��s�� �I������܂ł��̃��\�b�h�͑ҋ@���܂��B
	 *
	 * @see execCommand(String cmd) �����s����R�}���h�Ɉ������Ȃ��ꍇ�͊ȈՓI�ɂ������ �g�p���Ă��������B
	 * @param cmds
	 *            ���s����R�}���h�ƈ������܂ޔz��
	 * @return �R�}���h���s���ʏ���ێ�����String�z�� �z��[0] �� �W���o�� �z��[1] �� �G���[�o�� �z��[2] ��
	 *         ���^�[���R�[�h
	 * @throws IOException
	 *             ���o�̓G���[�����������ꍇ
	 */
	public static String[] execCommand(String[] cmds) throws IOException,
			InterruptedException {
		String[] returns = new String[3];
		String LINE_SEPA = System.getProperty("line.separator");
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(cmds);
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = p.getInputStream();
			StringBuffer out = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(in));
			String line;
			int lineNo = 1;
			while ((line = br.readLine()) != null) {
				if (lineNo != 1) {
					out.append(LINE_SEPA);
					lineNo++;
				}
				out.append(line);
			}
			returns[0] = out.toString();
			br.close();
			in.close();
			in = p.getErrorStream();
			StringBuffer err = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(in));

			lineNo = 1;
			while ((line = br.readLine()) != null) {
				if (lineNo != 1) {
					err.append(LINE_SEPA);
					lineNo++;
				}
				err.append(line);
			}
			returns[1] = err.toString();
			returns[2] = Integer.toString(p.waitFor());
			return returns;
		} finally {
			if (br != null) {
				br.close();
			}
			if (in != null) {
				in.close();
			}
		}
	}

}
