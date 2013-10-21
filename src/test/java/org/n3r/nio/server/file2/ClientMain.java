package org.n3r.nio.server.file2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

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
//			chain.addLast("codec", new ProtocolCodecFilter(
//					new TextLineCodecFactory(Charset.forName("UTF-8"))));
			connector.setHandler(new FileReceiveHandler());
			ConnectFuture connectFuture = connector
					.connect(new InetSocketAddress("127.0.0.1", 3333));

			connectFuture.awaitUninterruptibly();
			IoSession session = connectFuture.getSession();

			IoBuffer buffer = IoBuffer.allocate(12, false);
			CharSequence cs = "msg";
			buffer.putString(cs, Charset.forName("UTF-8").newEncoder());
			buffer.flip();
			session.write(buffer);
			// String str = "msg";
			// InputStream inputStream = new
			// ByteArrayInputStream(str.getBytes());
			// session.write(inputStream);

			session.getCloseFuture().awaitUninterruptibly();
			connector.dispose(true);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
