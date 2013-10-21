package org.n3r.server;

public class RequestCommand {

	public static final int LOG_FILE = 0;

	public static final int GZ_FILE = 1;

	public static final int FILE_CONTENT = 2;

	public static final int SEARCH_FILE_CONTENT = 3;

	public static final int QUIT = -999;

	private final int num;

	private RequestCommand(int num) {
		this.num = num;
	}

	public int toInt() {
		return num;
	}

	public static RequestCommand valueOf(String command) {
		command = command.toUpperCase();
		if ("LOG_FILE".equals(command)) {
			return new RequestCommand(LOG_FILE);
		}
		if ("GZ_FILE".equals(command)) {
			return new RequestCommand(GZ_FILE);
		}
		if ("SEARCH_FILE_CONTENT".equals(command)) {
			return new RequestCommand(SEARCH_FILE_CONTENT);
		}
		if ("FILE_CONTENT".equals(command)) {
			return new RequestCommand(FILE_CONTENT);
		}
		if ("QUIT".equals(command)) {
			return new RequestCommand(QUIT);
		}
		;

		throw new IllegalArgumentException("Unrecognized command: " + command);
	}
}
