package org.n3r.nio.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class LoginClientHandler extends IoHandlerAdapter {

	@Override
	public void messageReceived(IoSession arg0, Object message)
			throws Exception {
		String msgFServer = message.toString();
		System.out.println("client received msg :=" + msgFServer);

	}

}
