package com.jean.nettyRpc;

import com.jean.model.NettyRpcRequest;
import com.jean.annotation.RpcService;
import com.jean.zkRegistry.IRegister;
import com.jean.zkRegistry.RegisterWithZk;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcBean implements ApplicationContextAware, InitializingBean {
    private final int port;
    private static final Map<String, Object> instanceMap = new ConcurrentHashMap<String, Object>();
    private IRegister register = new RegisterWithZk();

    public RpcBean(int port) {
        this.port = port;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!beansWithAnnotation.isEmpty()) {
            for (Object servcieBean : beansWithAnnotation.values()) {
                RpcService rpcService = servcieBean.getClass().getAnnotation((RpcService.class));
                String serviceName = rpcService.value().getName();//拿到接口类定义
//                String version=rpcService.version(); //拿到版本号
//                if(!StringUtils.isEmpty(version)){
//                    serviceName+="-"+version;
//                }
                instanceMap.put(serviceName,servcieBean);
                register.register(serviceName, getAddress() + ":" + port);
            }
        }
    }

    private static String getAddress(){
        InetAddress inetAddress=null;
        try {
            inetAddress=InetAddress.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inetAddress.getHostAddress();
    }

    public void afterPropertiesSet() throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, work).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    //自定义协议编码器
                    pipeline.addLast(new LengthFieldPrepender(4));
                    //对象参数类型编码器
                    pipeline.addLast("encoder",new ObjectEncoder());
                    //对象参数类型解码器
                    pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                    pipeline.addLast(new RegistryHandler(instanceMap));
                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("RPC Registry start listen at " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
