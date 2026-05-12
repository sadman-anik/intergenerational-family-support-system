import java.util.ArrayList;

public class YoungFamily extends Member {
    private static final long serialVersionUID = 1L;

    private ArrayList<Child> children;

    public YoungFamily() {
        children = new ArrayList<>();
    }

    public ArrayList<Child> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Child> children) {
        this.children = children;
    }

    public void addChild(Child child) {
        children.add(child);
    }
}
