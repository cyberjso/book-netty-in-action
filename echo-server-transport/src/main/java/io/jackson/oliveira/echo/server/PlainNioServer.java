package io.jackson.oliveira.echo.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class PlainNioServer {

	public static void main(String[] args) throws IOException {
		new PlainNioServer().serve(7002);
	}
	
	public void serve(int port) throws IOException {
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		ServerSocket  sSocket = serverChannel.socket();
		
		InetSocketAddress address =  new InetSocketAddress(port);
		sSocket.bind(address);
		
		
		Selector selector = Selector.open();
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		final ByteBuffer  msg = ByteBuffer.wrap("Hi!\r\n".getBytes());
		
		for (;;) {
			try {
				selector.select();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			
			Set<SelectionKey> readKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator=   readKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();
				
				try {
					if (key.isAcceptable()) {
						ServerSocketChannel server   =  (ServerSocketChannel) key.channel();
						SocketChannel  client = server.accept();
						client.configureBlocking(false);
						client.register(selector, SelectionKey.OP_WRITE);
						System.out.println("Accepted connection from " + client);
						
						if (key.isWritable()) {
							SocketChannel clientSocket  =  (SocketChannel) key.channel();
							ByteBuffer buffer = (ByteBuffer) key.attachment();
							
							while (buffer.hasRemaining()) {
								if (client.write(buffer) == 0) break;
							}
							
							clientSocket.close();
						}
						
					}
				} catch (Exception e) {
					key.cancel();
					try {
						key.channel().close();
					} catch (Exception e2) {
						e2.printStackTrace();
						
					}
				}
			}
			
		}
	}
}
