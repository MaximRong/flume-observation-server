package org.n3r.nio.server.file2;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class FileReceiveHandler extends IoHandlerAdapter {
	private static FileChannel fc;

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		System.out.println("receive");
		IoBuffer ib = (IoBuffer) message;
		if (fc == null) {
			fc = new FileOutputStream("e:/TEMP2/test_copy.txt").getChannel();
		}
		fc.write(ib.buf());
		session.close(true);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		System.out.println("over");
		fc.close();
		session.close(true);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		System.out.println("session idle");
	}
}
