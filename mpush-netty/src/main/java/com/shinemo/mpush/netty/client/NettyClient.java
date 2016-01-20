package com.shinemo.mpush.netty.client;

import java.util.concurrent.TimeUnit;

import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.mpush.api.Client;
import com.shinemo.mpush.api.connection.Connection;
import com.shinemo.mpush.api.protocol.Packet;
import com.shinemo.mpush.netty.util.NettySharedHolder;
import com.shinemo.mpush.tools.config.ConfigCenter;

public  class NettyClient implements Client {
	
	private static final String format = "%s:%s";
	
    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    private final String host;
    private final int port;
    private Channel channel;
    private int hbTimes;
    private Connection connection;
    
    public NettyClient(final String host, final int port, Channel channel) {
        this.host = host;
        this.port = port;
        this.channel = channel;
    }
    
    @Override
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void initConnection(Connection connection){
    	this.connection = connection;
    }
    
    @Override
	public void close(String cause) {
		if (!StringUtils.isBlank(cause) && !"null".equals(cause.trim())) {
			log.error("close channel:"+cause);
		}
		this.channel.close();
	}

	@Override
	public boolean isEnabled() {
		return channel.isWritable();
	}

	@Override
	public boolean isConnected() {
		return channel.isActive();
	}

	@Override
	public void resetHbTimes() {
		hbTimes = 0;
	}

	@Override
	public int inceaseAndGetHbTimes() {
		return ++hbTimes;
	}

	@Override
	public void startHeartBeat() throws Exception {
		NettySharedHolder.HASHED_WHEEL_TIMER.newTimeout(new TimerTask() {
			@Override
			public void run(Timeout timeout) throws Exception {
				try {
					ChannelFuture channelFuture = channel.writeAndFlush(Packet.getHBPacket());
					channelFuture.addListener(new ChannelFutureListener() {

						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							if (!future.isSuccess()) {
								if (!channel.isActive()) {
									log.warn("client send hb msg false:" + channel.remoteAddress().toString()  + ",channel is not active");
								}
								log.warn("client send msg hb false:" + channel.remoteAddress().toString());
							} else {
								log.warn("client send msg hb success:" + channel.remoteAddress().toString());
							}
						}
					});
				} finally {
					if (channel.isActive()) {
						NettySharedHolder.HASHED_WHEEL_TIMER.newTimeout(this, ConfigCenter.holder.scanConnTaskCycle()/1000, TimeUnit.SECONDS);
					}
				}
			}
		}, ConfigCenter.holder.scanConnTaskCycle()/1000, TimeUnit.SECONDS);
	}


	@Override
	public String getUrl() {
		return String.format(format, host, port);
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public int getPort() {
		return port;
	}
	
	@Override
	public void stop(){
		
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

}
