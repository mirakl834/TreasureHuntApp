package com.findit.findit;

public class Target {
    String sTitle = "default Title";
    String sDescription = "default description";
    String sTargetMsg = "default target msg";
    String sLocation = "default location";
    Boolean isSolved = false;

    public Target(){

    }

    public Target(String sTitle, String sDescription, String sTargetMsg, String sLocation) {
        this.sTitle = sTitle;
        this.sDescription = sDescription;
        this.sTargetMsg = sTargetMsg;
        this.sLocation = sLocation;
    }

    public Target(String sTitle, String sDescription, String sTargetMsg, String sLocation, Boolean isSolved) {
        this.sTitle = sTitle;
        this.sDescription = sDescription;
        this.sTargetMsg = sTargetMsg;
        this.sLocation = sLocation;
        this.isSolved = isSolved;
    }

    public Boolean getSolved() {
        return isSolved;
    }

    public void setSolved(Boolean solved) {
        isSolved = solved;
    }

    public String getsTitle() {
        return sTitle;
    }

    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }

    public String getsDescription() {
        return sDescription;
    }

    public void setsDescription(String sDescription) {
        this.sDescription = sDescription;
    }

    public String getsTargetMsg() {
        return sTargetMsg;
    }

    public void setsTargetMsg(String sTargetMsg) {
        this.sTargetMsg = sTargetMsg;
    }

    public String getsLocation() {
        return sLocation;
    }

    public void setsLocation(String sLocation) {
        this.sLocation = sLocation;
    }
}
