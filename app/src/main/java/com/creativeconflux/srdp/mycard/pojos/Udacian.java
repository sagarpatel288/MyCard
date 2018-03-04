package com.creativeconflux.srdp.mycard.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by srdpatel on 3/2/2018.
 */

public class Udacian implements Parcelable {
    private String fullName;
    private String userName;
    private String emailId;
    private String contactNumber;
    private String scholarshipTrack;
    private String githubProfile;
    private String idea;
    private String ideaResourceLink;
    private String experience;
    private String probAndSols;
    private String about;
    private String address;

    public Udacian(String fullName, String emailId, String contactNumber, String address) {
        this.fullName = fullName;
        this.emailId = emailId;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    public Udacian() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getScholarshipTrack() {
        return scholarshipTrack;
    }

    public void setScholarshipTrack(String scholarshipTrack) {
        this.scholarshipTrack = scholarshipTrack;
    }

    public String getGithubProfile() {
        return githubProfile;
    }

    public void setGithubProfile(String githubProfile) {
        this.githubProfile = githubProfile;
    }

    public String getIdea() {
        return idea;
    }

    public void setIdea(String idea) {
        this.idea = idea;
    }

    public String getIdeaResourceLink() {
        return ideaResourceLink;
    }

    public void setIdeaResourceLink(String ideaResourceLink) {
        this.ideaResourceLink = ideaResourceLink;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getProbAndSols() {
        return probAndSols;
    }

    public void setProbAndSols(String probAndSols) {
        this.probAndSols = probAndSols;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public static Creator<Udacian> getCREATOR() {
        return CREATOR;
    }

    public Udacian(String fullName, String userName, String emailId, String contactNumber, String scholarshipTrack, String githubProfile, String idea, String ideaResourceLink, String experience, String probAndSols, String about) {
        this.fullName = fullName;
        this.userName = userName;
        this.emailId = emailId;
        this.contactNumber = contactNumber;
        this.scholarshipTrack = scholarshipTrack;
        this.githubProfile = githubProfile;
        this.idea = idea;
        this.ideaResourceLink = ideaResourceLink;
        this.experience = experience;
        this.probAndSols = probAndSols;
        this.about = about;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fullName);
        dest.writeString(this.userName);
        dest.writeString(this.emailId);
        dest.writeString(this.contactNumber);
        dest.writeString(this.scholarshipTrack);
        dest.writeString(this.githubProfile);
        dest.writeString(this.idea);
        dest.writeString(this.ideaResourceLink);
        dest.writeString(this.experience);
        dest.writeString(this.probAndSols);
        dest.writeString(this.about);
        dest.writeString(this.address);
    }

    protected Udacian(Parcel in) {
        this.fullName = in.readString();
        this.userName = in.readString();
        this.emailId = in.readString();
        this.contactNumber = in.readString();
        this.scholarshipTrack = in.readString();
        this.githubProfile = in.readString();
        this.idea = in.readString();
        this.ideaResourceLink = in.readString();
        this.experience = in.readString();
        this.probAndSols = in.readString();
        this.about = in.readString();
        this.address = in.readString();
    }

    public static final Creator<Udacian> CREATOR = new Creator<Udacian>() {
        @Override
        public Udacian createFromParcel(Parcel source) {
            return new Udacian(source);
        }

        @Override
        public Udacian[] newArray(int size) {
            return new Udacian[size];
        }
    };
}
