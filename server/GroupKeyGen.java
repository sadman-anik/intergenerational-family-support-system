import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

// Lab-style RSA key generator.
// Run once before running the server if key files are missing.
public class GroupKeyGen {

    private KeyPairGenerator keyPairGen;

    public GroupKeyGen() throws NoSuchAlgorithmException {
        keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
    }

    public KeyPair KeyPairGen() {
        return keyPairGen.genKeyPair();
    }

    public static void main(String[] args) throws Exception {
        HashMap<String, PublicKey> serverPubMap = new HashMap<>();
        HashMap<String, PrivateKey> serverPriMap = new HashMap<>();

        GroupKeyGen gkg = new GroupKeyGen();
        KeyPair kp = gkg.KeyPairGen();

        serverPubMap.put("IGFSS", kp.getPublic());
        serverPriMap.put("IGFSS", kp.getPrivate());

        FileOutputStream fout = new FileOutputStream("IGFSSPub.ser");
        ObjectOutputStream oout = new ObjectOutputStream(fout);
        oout.writeObject(serverPubMap);
        oout.close();
        fout.close();

        fout = new FileOutputStream("IGFSSPri.ser");
        oout = new ObjectOutputStream(fout);
        oout.writeObject(serverPriMap);
        oout.close();
        fout.close();

        System.out.println("IGFSS RSA key files created successfully.");
    }
}
