package com.example.sprintleague;

import android.net.Uri;

public class Sponsor {

    private String sponsorName;
    private String sponsorImgLink;
    private Uri sponsorImgUri;


    public Sponsor() {
    }

    public Sponsor(String sponsorName, String sponsorImgLink) {
        this.sponsorName = sponsorName;
        this.sponsorImgLink = sponsorImgLink;
    }


    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getSponsorImgLink() {
        return sponsorImgLink;
    }

    public void setSponsorImgLink(String sponsorImgLink) {
        this.sponsorImgLink = sponsorImgLink;
    }

    public Uri getSponsorImgUri() {
        return sponsorImgUri;
    }

    public void setSponsorImgUri(Uri sponsorImgUri) {
        this.sponsorImgUri = sponsorImgUri;
    }
}
