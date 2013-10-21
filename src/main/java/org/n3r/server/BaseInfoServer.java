package org.n3r.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.n3r.server.base.FlumeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseInfoServer implements FlumeServer {

	private static Logger log = LoggerFactory.getLogger(BaseInfoServer.class);
	private static final int PORT = 26699;

	@Override
	public void startUp() {
		try {
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			TextLineCodecFactory lineCodec = new TextLineCodecFactory(
					Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(),
					LineDelimiter.WINDOWS.getValue());
			lineCodec.setDecoderMaxLineLength(1024 * 1024); // 1M
			lineCodec.setEncoderMaxLineLength(1024 * 1024);

			acceptor.getFilterChain().addLast("codec",
					new ProtocolCodecFilter(lineCodec));
			acceptor.getSessionConfig().setReadBufferSize(2048);
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
			acceptor.setHandler(new BaseInfoServerHandler());

			acceptor.bind(new InetSocketAddress(PORT));
			log.info("flume base info server has bean startup!");
		} catch (Exception ex) {
			log.error("flume base info server run with error...", ex);
		}

	}
}
