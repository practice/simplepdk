package com.bpnr.portal.devtools.actions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

public class StreamUtil {
	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(in);
		BufferedOutputStream bos = new BufferedOutputStream(out);
		byte[] buf = new byte[10000];
		int count = 0;
		while (-1 != (count = bis.read(buf))) {
			bos.write(buf, 0, count);
		}
		bos.flush();
	}

	public static void copyAndCloseStream(InputStream in, OutputStream out) throws IOException {
		copyStream(in, out);
		in.close();
		out.close();
	}

	public static String createStringOfStreamContents(InputStream in) throws IOException {
		return createStringOfReaderContents(new InputStreamReader(in));
	}

	private static String createStringOfReaderContents(InputStreamReader inputStreamReader) throws IOException {
		BufferedReader br = new BufferedReader(inputStreamReader);
		char[] buf = new char[10000];
		StringWriter result = new StringWriter();
		int count = 0;
		while (-1 != (count = br.read(buf))) {
			result.write(buf, 0, count);
		}
		result.flush();
		result.close();
		return result.toString();
	}

	public static void writeStreamToFile(String pathname, InputStream inputStream) throws IOException {
		File file = new File(pathname);
		file.getParentFile().mkdirs();
		if (!file.exists())
			file.createNewFile();
		copyAndCloseStream(inputStream, new FileOutputStream(file));
	}
}