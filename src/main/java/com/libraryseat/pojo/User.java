package com.libraryseat.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
@JsonIgnoreProperties({"password","salt"})
public class User {
    private int uid;
    private String username,password,truename,gender,phone;
    private int salt;
    private short role;
//    private boolean valid = true; //这个可能不需要

    public User() {}
    public User(String username, String password, String truename, String gender, String phone, short role) {
        this.username = username;
        this.password = password;
        this.truename = truename;
        this.gender = gender;
        this.phone = phone;
        this.role = role;
    }

    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getTruename() {
        return truename;
    }
    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public short getRole() {
        return role;
    }
    public void setRole(short role) {
        if (role < 0)
            throw new IllegalArgumentException();
        this.role = role;
    }

    public int getSalt() {
        return salt;
    }

    public void setSalt(int salt) {
        this.salt = salt;
    }
    //    public boolean isValid() {
//        return valid;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (uid != user.uid) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (truename != null ? !truename.equals(user.truename) : user.truename != null) return false;
        return phone != null ? phone.equals(user.phone) : user.phone == null;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{uid,username,password,truename,phone});
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", truename='" + truename + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
