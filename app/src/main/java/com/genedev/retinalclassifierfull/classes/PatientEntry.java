package com.genedev.retinalclassifierfull.classes;

/**
 * Created by Gene on 4/29/2018.
 */

public class PatientEntry {
    String fullName;
    String address;
    String age;
    String gender;
    String birthday;
    String checkupDate;
    String diagnosis;
    String type;
    String phone;
    public String id;
    private String pushId;
    public PatientEntry() {

    }

    public PatientEntry(String fullName, String address, String age, String birthday,String checkupDate, String gender, String diagnosis,String type,String phone) {
        this.fullName = fullName;
        this.address = address;
        this.checkupDate = checkupDate;
        this.age = age;
        this.birthday = birthday;
        this.gender = gender;
        this.diagnosis = diagnosis;
        this.type = type;
        this.phone = phone;
    }

    public String getName() {
        return fullName;
    }
    public void setName(String fullName) {
        this.fullName = fullName;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getCheckupDate() {
        return checkupDate;
    }
    public void setCheckupDate(String checkupDate) {
        this.checkupDate = checkupDate;
    }

    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
    public void setId(String id) {
        this.id = id;
    }
}