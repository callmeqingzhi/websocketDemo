package nettytest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * 处理文本类型的webSocket帧
 * 扩展 SimpleChannelInboundHandler 用于处理 TextWebSocketFrame 信息
 * Created by chenz on 2017/11/19.
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    /**
     * 覆写userEventTriggered() 方法来处理自定义事件
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {    //2
       // 如果接收的事件表明握手成功,就从 ChannelPipeline 中删除HttpRequestHandler ，因为接下来不会接受 HTTP 消息了
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {

            ctx.pipeline().remove(HttpRequestHandler.class);    //3
            //写一条消息给所有的已连接 WebSocket 客户端，通知它们建立了一个新的 Channel 连接
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));//4
            //添加新连接的 WebSocket Channel 到 ChannelGroup 中，这样它就能收到所有的信息
            group.add(ctx.channel());    //5
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //增加消息的引用计数,通过 writeAndFlush() 传递给所有连接的客户端。
        group.writeAndFlush(msg.retain());    //6
    }
}
