package org.n3r.server;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.n3r.common.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDownloadHandler extends IoHandlerAdapter {
	private static Logger log = LoggerFactory
			.getLogger(FileDownloadHandler.class);

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.error("download file server error...", cause);
		session.close(true);

	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		FileInputStream fin = null;
		FileChannel fc = null;
		try {

			IoBuffer receive = (IoBuffer) message;
			String fileName = receive.getString(Charset.forName("UTF-8")
					.newDecoder());

			String flumeHistoryFolderStr = Config
					.getString("flumeHistoryFolder");

			fin = new FileInputStream(new File(flumeHistoryFolderStr + '/'
					+ fileName));
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

			log.info("file {} download finish!",  fileName);
		} catch (Exception ex) {
			log.error("download file error...", ex);
		} finally {
			IOUtils.closeQuietly(fc);
			IOUtils.closeQuietly(fin);
		}

	}

}
