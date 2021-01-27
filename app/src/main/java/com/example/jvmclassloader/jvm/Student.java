package com.example.jvmclassloader.jvm;

/**
 * @author Jack_Ou  created on 2021/1/21.
 */
public class Student {

    String name;
    String sexType;
    int age;

    public Student(String name, String sexType, int age) {
        this.name = name;
        this.sexType = sexType;
        this.age = age;
    }

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

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", sexType='" + sexType + '\'' +
                ", age=" + age +
                '}';
    }
}
