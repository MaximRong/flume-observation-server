package org.n3r.nio.server.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class ClientMain {
	public static void main(String[] args) throws InterruptedException,
			IOException {
		try {
			NioSocketConnector connector = new NioSocketConnector();
			DefaultIoFilterChainBuilder chain = connector.getFilterChain();
			connector.setHandler(new FileSenderHandler());
			ConnectFuture connectFuture = connector
					.connect(new InetSocketAddress("127.0.0.1", 3333));

			connectFuture.awaitUninterruptibly(); //
			IoSession session = connectFuture.getSession(); //

//			IoSession session = null;
//			for (;;) {
//				try {
//					ConnectFuture future = connector
//							.connect(new InetSocketAddress("localhost", 3333));
//					future.awaitUninterruptibly();
//					session = future.getSession();
//					break;
//				} catch (RuntimeIoException e) {
//					System.err.println("Failed to connect.");
//					e.printStackTrace();
//					Thread.sleep(5000);
//				}
//			}
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
			session.close(true);
			// System.out.println(fin);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
