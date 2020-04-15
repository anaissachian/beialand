package com.beia.solomon.networkPackets;

import java.io.Serializable;

public class Campaign implements Serializable
{
    private String idCampaign;
    private String idCompany;
    private String companyName;
    private byte[] companyImage;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String photoPath;
    private byte[] image;

    public Campaign(String idCampaign, String companyName, String title, String description, String startDate, String endDate, byte[] image)
    {
        this.idCampaign = idCampaign;
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
    }
    //constructor for campaigns data sent to users
    public Campaign(String idCampaign, String companyName, String title, String description, String startDate, String endDate)
    {
        this.idCampaign = idCampaign;
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    //constructor for server campaigns
    public Campaign(String idCampaign, String idCompany, String title, String description, String startDate, String endDate, String photoPath)
    {
        this.idCampaign = idCampaign;
        this.idCompany = idCompany;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.photoPath = photoPath;
    }
    public String getId() { return this.idCampaign; }
    public String getIdCompany() { return this.idCompany; }
    public String getCompanyName() { return this.companyName; }
    public byte[] getCompanyImage() { return this.companyImage; }
    public String getTitle() { return this.title; }
    public String getDescription() { return this.description; }
    public String getStartDate() { return this.startDate; }
    public String getEndDate() { return this.endDate; }
    public String getPhotoPath() { return this.photoPath; }
    public byte[] getImage() { return this.image; }
    public void setTitle(String title) { this.title = title; }
    public void setCompanyImage(byte[] image) { this.companyImage = image; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setImage(byte[] image) { this.image = image; }
    public void update(String title, String description, String startDate, String endDate)
    {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}

