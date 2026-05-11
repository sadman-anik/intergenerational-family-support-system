package com.example.igfss_client;
import java.io.Serializable;

public class MemberRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String GET_PUBLIC_KEY = "GET_PUBLIC_KEY";
    public static final String REGISTER_OC = "REGISTER_OC";
    public static final String REGISTER_YF = "REGISTER_YF";
    public static final String LOGIN = "LOGIN";
    public static final String GET_MEMBER_SUMMARIES = "GET_MEMBER_SUMMARIES";
    public static final String GET_MEMBER = "GET_MEMBER";
    public static final String CREATE_EVENT = "CREATE_EVENT";
    public static final String GET_EVENTS = "GET_EVENTS";

    private String command;
    private Object data;

    public MemberRequest() {
    }

    public MemberRequest(String command, Object data) {
        this.command = command;
        this.data = data;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
