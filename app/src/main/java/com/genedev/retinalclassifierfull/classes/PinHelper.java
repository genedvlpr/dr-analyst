package com.genedev.retinalclassifierfull.classes;

/**
 * Created by Gene on 4/29/2018.
 */

public class PinHelper {
    String pin;
    public String id;
    private String pushId;
    public PinHelper() {

    }

    public PinHelper(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }
    public void setPin(String fname) {
        this.pin = pin;
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