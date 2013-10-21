package org.n3r.nio.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class LoginServerHandler extends IoHandlerAdapter {

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String resultMsg = message.toString();
		System.out.println("server received msg = " + resultMsg);

		if ("bye".equals(resultMsg)) {
			session.close(true);
		}

		// 简单模拟业务逻辑处理
		String[] userInfo = resultMsg.split(",");
		String username = userInfo[0];
		String password = userInfo[1];

		if (username.equals("admin") && password.equals("3")) {
			session.write("login success...");
		} else {
			session.write("login error...");
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// longlived connection
		System.out.println("message sent success...");
	}
}
