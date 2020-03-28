package com.jean.zkRegistry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class RegisterWithZk implements IRegister {
    private static String CONNECTION_STR = "192.168.126.128:2181,192.168.126.129:2181,192.168.126.130:2181";
    private static CuratorFramework curatorFramework;

    static {
        curatorFramework = CuratorFrameworkFactory.builder().connectString(CONNECTION_STR).sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).namespace("registry").build();
        curatorFramework.start();
    }

    public void register(String serviceName, String addr) {
        String servicePath = "/" + serviceName;
        try {
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }
            String addressPath = servicePath + "/" + addr;
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath);
            System.out.println(serviceName + "服务注册成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
