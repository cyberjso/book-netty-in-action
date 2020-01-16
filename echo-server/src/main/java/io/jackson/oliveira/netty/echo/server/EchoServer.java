package io.jackson.oliveira.netty.echo.server;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {
	private int port;

	public EchoServer(int port) {
		this.port = port;
	}
	
	public static void main(String[] args) throws InterruptedException {
		int port  = (args.length == 0) ? 7001 : Integer.valueOf(args[0]);
		new EchoServer(port).start();
	}

	private void start() throws InterruptedException {
		final EchoServerHandler  handler = new EchoServerHandler();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstraper =  new ServerBootstrap();
			
			bootstraper.group(group)
			.channel(NioServerSocketChannel.class)
			.localAddress(new InetSocketAddress(port))
			.childHandler(new ChannelInitializer<SocketChannel>() {
				public void initChannel(SocketChannel ch) {
					ch.pipeline().addLast(handler);
				}
			});
			
			ChannelFuture future = bootstraper.bind().sync();
			
			System.out.println("**** SERVER INITIALIZED  ****");
			
			future.channel().closeFuture().sync();
			
		} finally {
			group.shutdownGracefully().sync();
		}
		
	}
	
}
