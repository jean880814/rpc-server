package com.jean.nettyRpc;

import com.jean.model.NettyRpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryHandler extends ChannelInboundHandlerAdapter {
    private Map<String, Object> registerMap = new ConcurrentHashMap<String, Object>();
    public RegistryHandler(Map registerMap){
        this.registerMap = registerMap;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyRpcRequest rpcRequest = (NettyRpcRequest) msg;
       if(registerMap.containsKey(rpcRequest.getClassname())){
            Object clazz = registerMap.get(rpcRequest.getClassname());
            Method method = clazz.getClass().getMethod(rpcRequest.getMethod(), rpcRequest.getTypes());
            Object result = method.invoke(clazz, rpcRequest.getArgs());
            if (result != null) {
                ctx.write(result);
                ctx.flush();
            }
           ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
