package org.n3r.sender;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.n3r.common.Config;
import org.n3r.common.ProcessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class SearchFileContentSender implements ClientSender {

	private static Logger log = LoggerFactory
			.getLogger(SearchFileContentSender.class);
	private Process process;
	private Thread thread;

	@Override
	public void send(IoSession session, String comand, String param) {
		thread = new Thread(new SearchFileContentWork(session, param));
		thread.start();
	}

	class SearchFileContentWork implements Runnable {

		private final IoSession session;
		private final String fileName;
		private final String content;

		public SearchFileContentWork(IoSession session, String param) {
			this.session = session;
			JSONObject json = JSONObject.parseObject(param);
			this.fileName = json.getString("fileName");
			this.content = json.getString("content");
		}

		@Override
		public void run() {
			String flumeFolderStr = Config.getString("flumeFolder");
			String filePath = StringUtils.endsWith(flumeFolderStr, "/") ? flumeFolderStr
					+ fileName
					: flumeFolderStr + "/" + fileName;
			log.info("the search flume file path is {}, and the content is {}",
					filePath, content);
			String[] cmd = new String[] { "grep", content, filePath };
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
