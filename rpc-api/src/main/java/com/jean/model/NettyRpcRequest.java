package com.jean.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class NettyRpcRequest implements Serializable {
    private String classname;
    private String method;
    private Object[] args;
    private Class<?>[] types;

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
