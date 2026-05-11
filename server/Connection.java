import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Connection extends Thread {
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private static final DatabaseUtility dbUtil = new DatabaseUtility();
    private static MemberQueries memberQueries;
    private static EventQueries eventQueries;

    static {
        try {
            memberQueries = new MemberQueries(dbUtil.getDbConnection());
            eventQueries = new EventQueries(dbUtil.getDbConnection());
        } catch (SQLException e) {
            System.out.println("Query preparation failed: " + e.getMessage());
        }
    }

    public Connection(Socket aClientSocket) {
        this.clientSocket = aClientSocket;

        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection: " + e.getMessage());
            closeResources();
        }
    }

    @Override
    public void run() {
        try {
            MemberRequest request = (MemberRequest) in.readObject();
            Object response = processRequest(request);
            out.writeObject(response);
            out.flush();
        } catch (EOFException e) {
            System.out.println("EOF: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            writeError("Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + e.getMessage());
            writeError("Error: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private Object processRequest(MemberRequest request) {
        try {
            String command = request.getCommand();

            if (MemberRequest.GET_PUBLIC_KEY.equals(command)) {
                HashMap pubKeys = SecurityUtil.ReadinKeys("IGFSSPub.ser");
                return (PublicKey) pubKeys.get("IGFSS");
            }

            if (MemberRequest.REGISTER_OC.equals(command)) {
                OlderCouple olderCouple = (OlderCouple) request.getData();
                olderCouple.setPassword(decryptPassword(olderCouple.getPassword()));
                int fidn = memberQueries.addOlderCouple(olderCouple);
                return "Older couple registered successfully. Your FIDN is " + fidn
                        + ". Use " + olderCouple.getSpouse1Email() + " as username for login.";
            }

            if (MemberRequest.REGISTER_YF.equals(command)) {
                YoungFamily youngFamily = (YoungFamily) request.getData();
                youngFamily.setPassword(decryptPassword(youngFamily.getPassword()));
                int fidn = memberQueries.addYoungFamily(youngFamily);
                return "Young family registered successfully. Your FIDN is " + fidn
                        + ". Use " + youngFamily.getSpouse1Email() + " as username for login.";
            }

            if (MemberRequest.LOGIN.equals(command)) {
                LoginData loginData = (LoginData) request.getData();
                String decodedPassword = decryptPassword(loginData.getEncryptedPassword());
                int fidn = memberQueries.getFidnByLogin(loginData.getEmail(), decodedPassword);

                if (fidn > 0) {
                    return "LOGIN_SUCCESS:" + fidn;
                }
                return "LOGIN_FAILED";
            }

            if (MemberRequest.GET_MEMBER_SUMMARIES.equals(command)) {
                return memberQueries.getAllMemberSummaries();
            }

            if (MemberRequest.GET_MEMBER.equals(command)) {
                Integer fidn = (Integer) request.getData();
                return memberQueries.getMemberByFidn(fidn);
            }

            if (MemberRequest.CREATE_EVENT.equals(command)) {
                Event event = (Event) request.getData();
                int eventId = eventQueries.addNewEvent(event);
                return "Event created successfully. Event ID is " + eventId + ".";
            }

            if (MemberRequest.GET_EVENTS.equals(command)) {
                return eventQueries.getEventsList();
            }

            return "Invalid request.";
        } catch (SQLException e) {
            return "Database Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String decryptPassword(String encryptedPassword) {
        HashMap priKeys = SecurityUtil.ReadinKeys("IGFSSPri.ser");
        PrivateKey privateKey = (PrivateKey) priKeys.get("IGFSS");
        return SecurityUtil.asyDecrypt(encryptedPassword, privateKey);
    }

    private void writeError(String message) {
        try {
            if (out != null) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException ignored) {
        }
    }

    private void closeResources() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ignored) {
        }

        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ignored) {
        }

        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Close: " + e.getMessage());
        }
    }
}
