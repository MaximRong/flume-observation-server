package org.n3r.nio.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class DownloadFileHandler implements IoHandler {

	@Override
	public void sessionCreated(IoSession session) throws Exception {

	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {

	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {

	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		IoBuffer receive = (IoBuffer) message;
		String fileName = receive
				.getString(Charset.forName("UTF-8").newDecoder());

		InputStream openStream = null;
		FileInputStream fin = null;
		FileChannel fc = null;
		try {
			URL resource = this.getClass().getClassLoader()
					.getResource("flume.properties");
			openStream = resource.openStream();
			Properties properties = new Properties();
			properties.load(openStream);

			String flumeHistoryFolderStr = properties
					.getProperty("flumeHistoryFolder");

			// System.out.println(f.length());
			fin = new FileInputStream(new File(flumeHistoryFolderStr + '/' + fileName));
			fc = fin.getChannel();
			ByteBuffer bb = ByteBuffer.allocate(2048 * 1000);
			while (true) {
				// 不间断发送会导致buffer异常
				Thread.sleep(5);
				bb.clear();
				int i = fc.read(bb);
				if (i == -1) {
					System.out.println("exit");
					break;
				}
				IoBuffer ib = IoBuffer.wrap(bb);
				bb.flip();
				session.write(ib);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			IOUtils.closeQuietly(fc);
			IOUtils.closeQuietly(fin);
			IOUtils.closeQuietly(openStream);
		}

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {

	}

}
