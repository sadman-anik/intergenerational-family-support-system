import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// Lab-style query class for member-related database operations.
public class MemberQueries {
    private PreparedStatement insertMember;
    private PreparedStatement insertChild;
    private PreparedStatement getAllMembers;
    private PreparedStatement getMemberByFidn;
    private PreparedStatement getChildrenByFidn;
    private PreparedStatement loginMember;

    public MemberQueries(Connection connect) throws SQLException {
        insertMember = connect.prepareStatement(
                "INSERT INTO MEMBERS "
                        + "(memberType, spouse1Name, spouse2Name, spouse1Phone, spouse2Phone, "
                        + "spouse1Email, spouse2Email, address, password, yearsMarried) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

        insertChild = connect.prepareStatement(
                "INSERT INTO CHILDREN (fidn, gender, age) VALUES (?, ?, ?)");

        getAllMembers = connect.prepareStatement(
                "SELECT fidn, memberType, spouse1Name, spouse2Name FROM MEMBERS ORDER BY fidn");

        getMemberByFidn = connect.prepareStatement(
                "SELECT * FROM MEMBERS WHERE fidn = ?");

        getChildrenByFidn = connect.prepareStatement(
                "SELECT * FROM CHILDREN WHERE fidn = ? ORDER BY childId");

        loginMember = connect.prepareStatement(
                "SELECT * FROM MEMBERS WHERE spouse1Email = ? AND password = ?");
    }

    public synchronized int addOlderCouple(OlderCouple olderCouple) throws SQLException {
        insertMember.setString(1, "Older Couple");
        insertMember.setString(2, olderCouple.getSpouse1Name());
        insertMember.setString(3, olderCouple.getSpouse2Name());
        insertMember.setString(4, olderCouple.getSpouse1Phone());
        insertMember.setString(5, olderCouple.getSpouse2Phone());
        insertMember.setString(6, olderCouple.getSpouse1Email());
        insertMember.setString(7, olderCouple.getSpouse2Email());
        insertMember.setString(8, olderCouple.getAddress());
        insertMember.setString(9, olderCouple.getPassword());
        insertMember.setInt(10, olderCouple.getYearsMarried());

        insertMember.executeUpdate();

        ResultSet keys = insertMember.getGeneratedKeys();
        if (keys.next()) {
            return keys.getInt(1);
        }
        return -1;
    }

    public synchronized int addYoungFamily(YoungFamily youngFamily) throws SQLException {
        insertMember.setString(1, "Young Family");
        insertMember.setString(2, youngFamily.getSpouse1Name());
        insertMember.setString(3, youngFamily.getSpouse2Name());
        insertMember.setString(4, youngFamily.getSpouse1Phone());
        insertMember.setString(5, youngFamily.getSpouse2Phone());
        insertMember.setString(6, youngFamily.getSpouse1Email());
        insertMember.setString(7, youngFamily.getSpouse2Email());
        insertMember.setString(8, youngFamily.getAddress());
        insertMember.setString(9, youngFamily.getPassword());
        insertMember.setNull(10, java.sql.Types.INTEGER);

        insertMember.executeUpdate();

        ResultSet keys = insertMember.getGeneratedKeys();
        int generatedFidn = -1;
        if (keys.next()) {
            generatedFidn = keys.getInt(1);
        }

        for (Child child : youngFamily.getChildren()) {
            insertChild.setInt(1, generatedFidn);
            insertChild.setString(2, child.getGender());
            insertChild.setInt(3, child.getAge());
            insertChild.executeUpdate();
        }

        return generatedFidn;
    }

    public synchronized boolean verifyLogin(String email, String password) throws SQLException {
        loginMember.setString(1, email);
        loginMember.setString(2, password);

        ResultSet results = loginMember.executeQuery();
        return results.next();
    }

    public synchronized int getFidnByLogin(String email, String password) throws SQLException {
        loginMember.setString(1, email);
        loginMember.setString(2, password);

        ResultSet results = loginMember.executeQuery();
        if (results.next()) {
            return results.getInt("fidn");
        }
        return -1;
    }

    public synchronized ArrayList<String> getAllMemberSummaries() throws SQLException {
        ArrayList<String> summaries = new ArrayList<>();
        ResultSet results = getAllMembers.executeQuery();

        while (results.next()) {
            summaries.add(results.getInt("fidn") + " - "
                    + results.getString("memberType") + " - "
                    + results.getString("spouse1Name") + " & "
                    + results.getString("spouse2Name"));
        }

        return summaries;
    }

    public synchronized Member getMemberByFidn(int fidn) throws SQLException {
        getMemberByFidn.setInt(1, fidn);
        ResultSet results = getMemberByFidn.executeQuery();

        if (!results.next()) {
            return null;
        }

        String memberType = results.getString("memberType");
        Member member;

        if ("Older Couple".equals(memberType)) {
            OlderCouple olderCouple = new OlderCouple();
            olderCouple.setYearsMarried(results.getInt("yearsMarried"));
            member = olderCouple;
        } else {
            YoungFamily youngFamily = new YoungFamily();
            loadChildren(youngFamily, fidn);
            member = youngFamily;
        }

        member.setFidn(results.getInt("fidn"));
        member.setSpouse1Name(results.getString("spouse1Name"));
        member.setSpouse2Name(results.getString("spouse2Name"));
        member.setSpouse1Phone(results.getString("spouse1Phone"));
        member.setSpouse2Phone(results.getString("spouse2Phone"));
        member.setSpouse1Email(results.getString("spouse1Email"));
        member.setSpouse2Email(results.getString("spouse2Email"));
        member.setAddress(results.getString("address"));

        return member;
    }

    private void loadChildren(YoungFamily youngFamily, int fidn) throws SQLException {
        getChildrenByFidn.setInt(1, fidn);
        ResultSet results = getChildrenByFidn.executeQuery();

        while (results.next()) {
            Child child = new Child();
            child.setGender(results.getString("gender"));
            child.setAge(results.getInt("age"));
            youngFamily.addChild(child);
        }
    }
}
