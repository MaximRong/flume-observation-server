package org.n3r.sender;

import org.n3r.server.RequestCommand;

public class ClientSenderFactory {

	public static ClientSender make(String comandStr) {
		RequestCommand comandInt = RequestCommand.valueOf(comandStr);
		ClientSender clientSender = null;

		switch (comandInt.toInt()) {
		case RequestCommand.LOG_FILE:

			clientSender = new LogFileStructureSender();
			break;

		case RequestCommand.FILE_CONTENT:
			clientSender = new TailFileContentSender();
			break;

		case RequestCommand.SEARCH_FILE_CONTENT:
			clientSender = new SearchFileContentSender();
			break;

		case RequestCommand.GZ_FILE:
			clientSender = new GzFileStructureSender();
			break;

		default:
			break;
		}

		return clientSender;
	}

}
