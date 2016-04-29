package net.println.easydroid.file;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;

public class IO {

	private static final int BUF_SIZE = 4096;

	public static final void safeClose(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
			}
		}
	}

	public static final byte[] toBytes(InputStream is) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			byte[] buf = new byte[BUF_SIZE];
			int count;
			while ((count = is.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, count);
			}
			baos.flush();
			return baos.toByteArray();

		} catch (IOException e) {
		} finally {
			safeClose(baos);
		}
		return null;
	}

	public static final long pipe(InputStream is, OutputStream os) {
		long total = -1;
		if (is == null || os == null) {
			return total;
		}
		try {
			byte[] buf = new byte[BUF_SIZE];
			int count = 0;
			total = 0;
			while ((count = is.read(buf)) > 0) {
				os.write(buf, 0, count);
				total += count;
			}
		} catch (IOException e) {

		}
		return total;
	}

	public static int writeToFile(byte[] data, String fileName, boolean append) {
		if (TextUtils.isEmpty(fileName)) {
			return 0;
		}
		return writeToFile(data, new File(fileName), append);
	}

	public static int writeToFile(byte[] data, File file, boolean append) {
		if(data == null || data.length == 0 || file == null || file.isDirectory()) {
			return 0;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file, append);
			fos.write(data);
			fos.flush();
			return data.length;
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			safeClose(fos);
		}
		return 0;
	}
	
	/**
	 * 删除指定目录在的所有文件
	 * @param rootPath
	 */
	public static boolean deleteFileOrFolder(String rootPath) {
		if(TextUtils.isEmpty(rootPath)) {
			return false;
		}
		
		Stack<String> stack = new Stack<String>();
		stack.push(rootPath);
		while(!stack.isEmpty()) {
			String current = stack.peek();
			File f = new File(current);
			if(f.exists()) {
				if(f.isDirectory()) {
					File[] files = f.listFiles();
					if(files == null || files.length == 0) {
						// 如果这个目录是空，则可以删除了。
						f.delete();
						stack.pop();
					}else {
						for(File file: files) {
							if(file.isDirectory()) {
								stack.push(file.getAbsolutePath());
							}else {
								file.delete();
							}
						}
					}
				}else {
					// 如果是文件，一般只有rootPath是个文件的时候会出现这种情况。
					f.delete();
					stack.pop();
				}
			}else {
				// 如果这个路径文件不存在，则移除
				stack.pop();
			}
		}
		return true;
	}
	
	/**
	 * 保证这个目录存在
	 * @param path
	 * @return
	 */
	public static boolean ensureDir(String path) {
		if(TextUtils.isEmpty(path)) {
			return false;
		}
		try {
			File f = new File(path);
			if(f.exists() && f.isDirectory()) {
				return true;
			}else if(f.exists() && f.isFile()) {
				f.delete();
			}
			return f.mkdirs();
		}catch(Exception e) {
			
		}
		return false;
	}
	
	public static final InputStream getInputStream(String fileName) {
		if(TextUtils.isEmpty(fileName)) {
			return null;
		}
		try {
			File f = new File(fileName);
			if(f.exists() && f.isFile() && f.canRead()) {
				return new FileInputStream(f);
			}
		}catch(IOException e) {
			
		}
		return null;
	}
}
