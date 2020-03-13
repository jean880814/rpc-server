package com.jean.rpc;

import com.jean.model.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

public class RpcPublisherHandler implements Runnable {
    private final Socket socket;
    private final Object instance;

    public RpcPublisherHandler(Object instance, Socket socket) {
        this.instance = instance;
        this.socket = socket;
    }

    public void run() {
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object o = invoke(rpcRequest);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Object invoke(RpcRequest rpcRequest) throws Exception {
        String classname = rpcRequest.getClassname();
        Object[] args = rpcRequest.getArgs();
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        Class clazz = Class.forName(classname);
        Method method = clazz.getMethod(rpcRequest.getMethod(), types);
        return method.invoke(instance, args);
    }
}
