package com.genedev.retinalclassifierfull.classes;

/**
 * Created by Gene on 4/29/2018.
 */

public class AccountEntry {
    String fname;
    String email;
    String hostname;
    String hostadd;
    public String id;
    private String pushId;
    public AccountEntry() {

    }

    public AccountEntry(String fname,String email,String hostname,String hostadd) {
        this.fname = fname;
        this.email = email;
        this.hostname = hostname;
        this.hostadd = hostadd;
    }

    public String getName() {
        return fname;
    }
    public void setName(String fname) {
        this.fname = fname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    public String getHostadd() {
        return hostadd;
    }
    public void setHostadd(String hostadd) {
        this.hostadd = hostadd;
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