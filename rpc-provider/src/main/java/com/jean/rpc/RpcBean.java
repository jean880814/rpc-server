package com.jean.rpc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcBean implements ApplicationContextAware, InitializingBean {
    private final int port;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<String, Object> instanceMap = new ConcurrentHashMap<String, Object>();

    public RpcBean(int port) {
        this.port = port;
    }

    public void afterPropertiesSet() throws Exception {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(new RpcPublisherHandlerByMap(socket, instanceMap));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(!beansWithAnnotation.isEmpty()){
            for(Object servcieBean:beansWithAnnotation.values()){
                RpcService rpcService=servcieBean.getClass().getAnnotation((RpcService.class));
                String serviceName=rpcService.value().getName();//拿到接口类定义
                String version=rpcService.version(); //拿到版本号
                if(!StringUtils.isEmpty(version)){
                    serviceName+="-"+version;
                }
                instanceMap.put(serviceName,servcieBean);
            }
        }
    }
}
