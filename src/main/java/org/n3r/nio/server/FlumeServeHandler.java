package org.n3r.nio.server;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FlumeServeHandler extends IoHandlerAdapter {

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String clientRequest = StringUtils.split(message.toString(), "^")[0];
//		switch (clientRequest) {
//		case "fileTree":
//			sendToClientFileTree(session);
//			break;
//
//		case "zipFiles":
//			sendToClientZipFiles(session);
//			break;
//
//		case "download":
//			sendToClientFile(session,
//					StringUtils.split(message.toString(), "^")[1]);
//			break;
//
//		case "tail":
//			sendToClientLogContent(session,
//					StringUtils.split(message.toString(), "^")[1]);
//			break;
//
//		default:
//			sendToClientFileTree(session);
//			break;
//		}

	}

	private void sendToClientLogContent(IoSession session, String fileName) {
		new Thread(new TailLogContent(session, fileName)).start();
	}

	class TailLogContent extends Thread {

		private final IoSession session;
		private final String fileName;

		public TailLogContent(IoSession session, String fileName) {
			this.session = session;
			this.fileName = fileName;
		}

		@Override
		public void run() {
			Properties properties = getProperties();
			String property = properties.getProperty("flumeFolder");
			File f = new File(property + "/" + fileName);
			List<String> readLines;
			try {
				readLines = FileUtils.readLines(f);
				String content = Joiner.on("\n").skipNulls().join(readLines);
				session.write(content);

				String lastLine = readLines.get((readLines.size() - 1));
				while (!session.isClosing()) {

					readLines = FileUtils.readLines(f);
					String endLine = readLines.get((readLines.size() - 1));
					if (!endLine.equals(lastLine)) {
						lastLine = endLine;
						session.write(lastLine);
					}

					sleepMillis(500);

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("end the tail....");

			// try {
			// String[] cmd = new String[] { "tail", "-100f", reqText };
			//
			// Process process = new ProcessBuilder(cmd).start();
			// new Thread(new ListenerThread(Thread.currentThread(),
			// process)).start();
			// BufferedReader infoBr = null;
			// BufferedReader errorBr = null;
			//
			// session.write(ctx, process, infoBr, NormalStream);
			//
			// printProcessInfo(ctx, process, errorBr, ErrorStream);
			//
			// int val = process.waitFor();
			// process.destroy();
			//
			// if (val != 0) {
			//
			// }
			// }
			// catch (Exception e) {
			// e.printStackTrace();
			// }

			// FileUtils.readLines(file)

		}

		private void sleepMillis(int millis) {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private Properties getProperties() {
		InputStream openStream = null;
		try {
			URL resource = this.getClass().getClassLoader()
					.getResource("flume.properties");
			openStream = resource.openStream();
			Properties properties = new Properties();
			properties.load(openStream);

			return properties;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
				IOUtils.closeQuietly(openStream);
		}
	}

	private void sendToClientFile(IoSession session, String fileName)
			throws IOException {
		InputStream openStream = null;
		try {
			URL resource = this.getClass().getClassLoader()
					.getResource("flume.properties");
			openStream = resource.openStream();
			Properties properties = new Properties();
			properties.load(openStream);

			String flumeHistoryFolderStr = properties
					.getProperty("flumeHistoryFolder");
			File zipFile = new File(flumeHistoryFolderStr + "/" + fileName);
			if (!zipFile.exists()) {
				throw new RuntimeException("can't not found the flumeFolder :"
						+ flumeHistoryFolderStr);
			}

			if (!zipFile.isFile()) {
				throw new RuntimeException("flumeFolder '"
						+ flumeHistoryFolderStr + "' is not a file!");
			}

			FileInputStream fin = null;
			FileChannel fc = null;
			try {
				fin = new FileInputStream(zipFile);
				fc = fin.getChannel();
				ByteBuffer bb = ByteBuffer.allocate(2048 * 1000);
				while (true) {
					// 不间断发送会导致buffer异常
					Thread.sleep(5);
					bb.clear();
					int i = fc.read(bb);
					if (i == -1) {
						break;
					}
					IoBuffer ib = IoBuffer.wrap(bb);
					bb.flip();
					session.write(ib);
				}
			} finally {
				if (null != fc) {
					fc.close();
				}

				if (null != fin) {
					fin.close();
				}
			}

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			if (null != openStream) {
				openStream.close();
			}

		}
	}

	private void sendToClientZipFiles(IoSession session) throws IOException {
		InputStream openStream = null;
		try {
			URL resource = this.getClass().getClassLoader()
					.getResource("flume.properties");
			openStream = resource.openStream();
			Properties properties = new Properties();
			properties.load(openStream);

			String flumeHistoryFolderStr = properties
					.getProperty("flumeHistoryFolder");
			File flumeHistoryFolder = new File(flumeHistoryFolderStr);
			if (!flumeHistoryFolder.exists()) {
				throw new RuntimeException("can't not found the flumeFolder :"
						+ flumeHistoryFolderStr);
			}

			if (!flumeHistoryFolder.isDirectory()) {
				throw new RuntimeException("flumeFolder '"
						+ flumeHistoryFolderStr + "' is not a folder!");
			}

			ArrayList<String> list = Lists.newArrayList();
			for (File file : flumeHistoryFolder.listFiles()) {
				list.add(file.getName());
				// list.add(StringUtils.split(file.getName(), '.')[0]);
			}
			String treeFileJsonStr = JSONArray.toJSONString(list);
			session.write(treeFileJsonStr);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			if (null != openStream) {
				openStream.close();
			}

		}
	}

	private void sendToClientFileTree(IoSession session) throws IOException {
		InputStream openStream = null;
		try {
			URL resource = this.getClass().getClassLoader()
					.getResource("flume.properties");
			openStream = resource.openStream();
			Properties properties = new Properties();
			properties.load(openStream);

			String flumeFolderStr = properties.getProperty("flumeFolder");

			File flumeFolder = new File(flumeFolderStr);
			if (!flumeFolder.exists()) {
				throw new RuntimeException("can't not found the flumeFolder :"
						+ flumeFolderStr);
			}

			if (!flumeFolder.isDirectory()) {
				throw new RuntimeException("flumeFolder '" + flumeFolderStr
						+ "' is not a folder!");
			}

			List<Object> folderList = addChildFiles(flumeFolder);
			String treeFileJsonStr = JSONArray.toJSONString(folderList);
			session.write(treeFileJsonStr);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			if (null != openStream) {
				openStream.close();
			}

		}
	}

	private List<Object> addChildFiles(File flumeFolder) {
		List<Object> folderList = Lists.newArrayList();
		for (File file : flumeFolder.listFiles(new FlumeLogFileFilter())) {
			Map<String, Object> fileMap = Maps.newHashMap();
			fileMap.put("name", StringUtils.split(file.getName(), '.')[0]);
			folderList.add(fileMap);
		}

		return folderList;
	}

	class FlumeLogFileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			String fileName = file.getName();
			return StringUtils.endsWith(fileName, "log");
		}

	}

	public static void main(String[] args) throws IOException {

		// System.out.println(string);
	}
}
