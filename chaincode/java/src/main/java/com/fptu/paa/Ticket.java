package com.fptu.paa;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.List;
import java.util.Objects;

@DataType
public class Ticket {
    @Property
    private String bikeID;
    @Property
    private String createTime;
    @Property
    private List checkinImages;
    @Property
    private List checkoutImages;
    @Property
    private String status;
    @Property
    private String ownerID;

    public Ticket() {
    }

    public Ticket(@JsonProperty("bikeID") String bikeID,@JsonProperty("createTime") String createTime,@JsonProperty("checkinImages") List checkinImages,
                  @JsonProperty("checkoutImages")List checkoutImages,@JsonProperty("status") String status,@JsonProperty("ownerID") String ownerID) {
        this.bikeID = bikeID;
        this.createTime = createTime;
        this.checkinImages = checkinImages;
        this.checkoutImages = checkoutImages;
        this.status = status;
        this.ownerID = ownerID;
    }

    public String getBikeID() {
        return bikeID;
    }

    public void setBikeID(String bikeID) {
        this.bikeID = bikeID;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List getCheckinImages() {
        return checkinImages;
    }

    public void setCheckinImages(List checkinImages) {
        this.checkinImages = checkinImages;
    }

    public List getCheckoutImages() {
        return checkoutImages;
    }

    public void setCheckoutImages(List checkoutImages) {
        this.checkoutImages = checkoutImages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(getBikeID(), ticket.getBikeID()) &&
                Objects.equals(getCreateTime(), ticket.getCreateTime()) &&
                Objects.equals(getCheckinImages(), ticket.getCheckinImages()) &&
                Objects.equals(getCheckoutImages(), ticket.getCheckoutImages()) &&
                Objects.equals(getStatus(), ticket.getStatus()) &&
                Objects.equals(getOwnerID(), ticket.getOwnerID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBikeID(), getCreateTime(), getCheckinImages(), getCheckoutImages(), getStatus(), getOwnerID());
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "bikeID='" + bikeID + '\'' +
                ", createTime='" + createTime + '\'' +
                ", checkinImages=" + checkinImages +
                ", checkoutImages=" + checkoutImages +
                ", status='" + status + '\'' +
                ", ownerID='" + ownerID + '\'' +
                '}';
    }
}
