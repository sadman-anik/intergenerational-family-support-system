import java.io.Serializable;

public class Child implements Serializable {
    private static final long serialVersionUID = 1L;

    private String gender;
    private int age;

    public Child() {
    }

    public Child(String gender, int age) {
        this.gender = gender;
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return gender + " - " + age;
    }
}
