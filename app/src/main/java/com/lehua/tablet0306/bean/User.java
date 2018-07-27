package com.lehua.tablet0306.bean;

import java.io.Serializable;

/**
 * Created by lehua on 2018/2/3.
 */

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String account;   //账号
    private String pwd;       //密码
    private String name;      //名字
    private String sex;        //性别
    private int age;        // 年龄
    private String parentNum;    //家长电话

    private String province;    // 省
    private String city;        //市
    private String area;       //区

    private String school;  //学校名字
    private String schoolClass;  //班级


    public User() {
        super();
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getParentNum() {
        return parentNum;
    }

    public void setParentNum(String parentNum) {
        this.parentNum = parentNum;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(String schoolClass) {
        this.schoolClass = schoolClass;
    }

    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", pwd='" + pwd + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", parentNum='" + parentNum + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", school='" + school + '\'' +
                ", schoolClass='" + schoolClass + '\'' +
                '}';
    }
}
