package com.example.igfss_client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;

public class IGFSSClient {
    private static final String SERVER_NAME = "localhost";
    private static final int SERVER_PORT = 8888;

    public Object sendRequest(MemberRequest request) throws IOException, ClassNotFoundException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            socket = new Socket(SERVER_NAME, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(request);
            out.flush();

            return in.readObject();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
    }

    public PublicKey getServerPublicKey() throws IOException, ClassNotFoundException {
        return (PublicKey) sendRequest(new MemberRequest(MemberRequest.GET_PUBLIC_KEY, null));
    }

    public String registerOlderCouple(OlderCouple olderCouple) throws IOException, ClassNotFoundException {
        PublicKey publicKey = getServerPublicKey();
        olderCouple.setPassword(SecurityUtil.asyEncrypt(olderCouple.getPassword(), publicKey));
        return (String) sendRequest(new MemberRequest(MemberRequest.REGISTER_OC, olderCouple));
    }

    public String registerYoungFamily(YoungFamily youngFamily) throws IOException, ClassNotFoundException {
        PublicKey publicKey = getServerPublicKey();
        youngFamily.setPassword(SecurityUtil.asyEncrypt(youngFamily.getPassword(), publicKey));
        return (String) sendRequest(new MemberRequest(MemberRequest.REGISTER_YF, youngFamily));
    }

    public String login(String email, String password) throws IOException, ClassNotFoundException {
        PublicKey publicKey = getServerPublicKey();
        String encryptedPassword = SecurityUtil.asyEncrypt(password, publicKey);
        LoginData loginData = new LoginData(email, encryptedPassword);
        return (String) sendRequest(new MemberRequest(MemberRequest.LOGIN, loginData));
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> getAllMemberSummaries() throws IOException, ClassNotFoundException {
        return (ArrayList<String>) sendRequest(new MemberRequest(MemberRequest.GET_MEMBER_SUMMARIES, null));
    }

    public Member getMemberByFidn(int fidn) throws IOException, ClassNotFoundException {
        return (Member) sendRequest(new MemberRequest(MemberRequest.GET_MEMBER, fidn));
    }

    public String createEvent(Event event) throws IOException, ClassNotFoundException {
        return (String) sendRequest(new MemberRequest(MemberRequest.CREATE_EVENT, event));
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Event> getAllEvents() throws IOException, ClassNotFoundException {
        return (ArrayList<Event>) sendRequest(new MemberRequest(MemberRequest.GET_EVENTS, null));
    }
}
