
/**
 *
 * -Xms30m -Xmx30m -XX:+UseConcMarkSweepGC -XX:-UseCompressedOops
 * -Xss1m
 *
 * @author Jack_Ou  created on 2021/1/21.
 */
public class ObjectInJvm {

    public final static String MAN_TYPE = "man";
    public static String WOMAN_TYPE = "woman";

    public static void main(String[] args) throws Exception {
        Student T1 = new Student();
        T1.setName("DY");
        T1.setSexType(WOMAN_TYPE);
        T1.setAge(26);
        for (int i = 0; i < 15; i++) {
            System.gc();
        }
        Student T2 = new Student();
        T2.setName("JackOu");
        T2.setSexType(MAN_TYPE);
        T2.setAge(29);
        Thread.sleep(Integer.MAX_VALUE);
    }
}

class Student {

    String name;
    String sexType;
    int age;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSexType() {
        return sexType;
    }
    public void setSexType(String sexType) {
        this.sexType = sexType;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
}