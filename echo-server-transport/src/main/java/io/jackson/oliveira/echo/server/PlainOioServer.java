package io.jackson.oliveira.echo.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class PlainOioServer {

	public static void main(String[] args) throws IOException {
		new PlainOioServer().serve(7001);
	}
	
	public void serve(int port) throws IOException {
		final ServerSocket serverSocket = new ServerSocket(port);
		for (;;) {
			final Socket clientSocket = serverSocket.accept();
			System.out.println("Accepted connection from " + clientSocket);

			new Thread(() -> {

				try {
					OutputStream out = clientSocket.getOutputStream();
					out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();

				} finally {
					try {
						clientSocket.close();

					} catch (Exception e) {
						throw new RuntimeException(e);
					}

				}

			});
		}

	}

}
