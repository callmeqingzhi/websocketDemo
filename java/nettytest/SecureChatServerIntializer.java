package nettytest;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * 扩展 ChatServerInitializer 来实现加密
 * Created by chenz on 2017/11/19.
 */
public class SecureChatServerIntializer extends ChatServerInitializer {
    private final SslContext context;

    public SecureChatServerIntializer(ChannelGroup group, SslContext context) {
        super(group);
        this.context = context;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        super.initChannel(ch);
        SSLEngine engine = context.newEngine(ch.alloc());
        engine.setUseClientMode(false);
        //向 ChannelPipeline 中添加SslHandler
        ch.pipeline().addFirst(new SslHandler(engine)); //2
    }
}
