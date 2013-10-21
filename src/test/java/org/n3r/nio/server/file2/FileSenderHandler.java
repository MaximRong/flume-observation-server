package org.n3r.nio.server.file2;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class FileSenderHandler extends IoHandlerAdapter {

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		IoBuffer receive = (IoBuffer) message;
		String string = receive
				.getString(Charset.forName("UTF-8").newDecoder());
		System.out.println(string);

		try {
			File f = new File("e:/TEMP2/test.txt");
			// System.out.println(f.length());
			FileInputStream fin = new FileInputStream(f);
			FileChannel fc = fin.getChannel();
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
			// session.close(true);
			System.out.println(fin);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		session.close(true);
	}

	// @Override
	// public void sessionCreated(IoSession session) throws Exception {
	// try {
	// File f = new File("e:/TEMP2/test.txt");
	// // System.out.println(f.length());
	// FileInputStream fin = new FileInputStream(f);
	// FileChannel fc = fin.getChannel();
	// ByteBuffer bb = ByteBuffer.allocate(2048 * 1000);
	// while (true) {
	// // 不间断发送会导致buffer异常
	// Thread.sleep(5);
	// bb.clear();
	// int i = fc.read(bb);
	// if (i == -1) {
	// System.out.println("exit");
	// break;
	// }
	// IoBuffer ib = IoBuffer.wrap(bb);
	// bb.flip();
	// session.write(ib);
	// }
	// // session.close(true);
	// System.out.println(fin);
	//
	// } catch (Throwable e) {
	// e.printStackTrace();
	// }
	// }
}
