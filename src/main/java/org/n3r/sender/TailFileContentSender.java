package org.n3r.sender;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.n3r.common.Config;
import org.n3r.common.ProcessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TailFileContentSender implements ClientSender {

	private static Logger log = LoggerFactory
			.getLogger(TailFileContentSender.class);
	private Process process;
	private Thread thread;

	@Override
	public void send(IoSession session, String comand, String param) {
		thread = new Thread(new FileContentWork(session, param));
		thread.start();
	}

	class FileContentWork implements Runnable {

		private final IoSession session;
		private final String fileName;

		public FileContentWork(IoSession session, String fileName) {
			this.session = session;
			this.fileName = fileName;
		}

		@Override
		public void run() {

			String flumeFolderStr = Config.getString("flumeFolder");
			String filePath = StringUtils.endsWith(flumeFolderStr, "/") ? flumeFolderStr
					+ fileName
					: flumeFolderStr + "/" + fileName;
			log.info("the tail flume file path is {}", filePath);
			String[] cmd = new String[] { "tail", "-200", filePath };

			try {
				process = new ProcessBuilder(cmd).start();
				ProcessHelper.process(process, cmd, session);

			} catch (Exception e) {
				log.error("process work error!", e);
			} finally {
				process = null;
			}

		}

	}

	@Override
	public void close() {
		if (null != process) {
			thread.interrupt();

			log.info("thread process has been interrupted!");
		}
	}
}
