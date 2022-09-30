package com.example.websocket.netty.demo.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	/**
	 * 与客户端建立连接
	 */
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端连接：" + ctx.channel().id());
        //添加到channelGroup通道组
        ChannelHandlerPool.channelGroup.add(ctx.channel());
    }

	/**
	 * 与客户端断开连接
	 */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端断开连接：" + ctx.channel().id());
        //从channelGroup通道组删除
        ChannelHandlerPool.channelGroup.remove(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
      ctx.channel().flush();
    }
    
    @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	//接收的消息
    	if (null != msg && msg instanceof FullHttpRequest) {
			FullHttpRequest request = (FullHttpRequest) msg;
			System.out.println("uri:" + request.uri());
			String uri = request.uri();
			if (null != uri && uri.contains("/") && uri.contains("?")) {
				String[] uriArray = uri.split("\\?");
				if (null != uriArray && uriArray.length > 1) {
					String[] paramsArray = uriArray[1].split("=");
					if (null != paramsArray && paramsArray.length > 1) {
						//TODO
						
					}
				}
				request.setUri(getBasePath(request.uri()));
			} else {
				System.out.println("传参格式有问题");
				ctx.close();
			}
    	}
    	
    	//重新调用父类方法，父类该方法会让ByteBuf	计数清零。从而达到数据被GC回收。如果没有调用该方法内存必会泄露。同时注意：该父类会判断出如果是TextWebSocketFrame 数据类型会调用你重写的channelRead0；具体可以参考源码
		super.channelRead(ctx, msg);   
    }
    
    /**
     * 客户端发送消息处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
    	//接收的消息
        System.out.println(String.format("收到客户端%s的数据：%s" ,ctx.channel().id(), msg.text()));

        // 单独发消息
        // sendMessage(ctx);
        // 群发消息
        sendAllMessage(msg.text());
    }

    private void sendMessage(ChannelHandlerContext ctx){
        String message = "消息";
        ctx.writeAndFlush(new TextWebSocketFrame(message));
    }

    private void sendAllMessage(String msg){
        String message = "我是服务器，这是群发消息: " + msg;
        ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(message));
    }
    
    private static String getBasePath(String uri) {
        if (uri == null || uri.isEmpty())
          return null;

        int idx = uri.indexOf("?");
        if (idx == -1)
          return uri;

        return uri.substring(0, idx);
      }

}
