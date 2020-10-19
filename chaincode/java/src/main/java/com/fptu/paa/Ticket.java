package com.fptu.paa;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@DataType
public class Ticket {
    @Property
    private String bikeID;

    @Property
    private String ownerCheckInID;

    @Property
    private String checkinTime;

    @Property
    private String ownerCheckOutID;

    @Property
    private String checkoutTime;

    @Property
    private String nfcNumber;

    @Property
    private String paymentType;

    @Property
    private String[] checkinImages;

    @Property
    private String[] checkoutImages;

    @Property
    private String status;

    public Ticket() {
    }

    public Ticket(@JsonProperty("bikeID") String bikeID, @JsonProperty("ownerCheckInID") String ownerCheckInID, @JsonProperty("checkinTime") String checkinTime,
                  @JsonProperty("nfcNumber") String nfcNumber, @JsonProperty("checkinImages") String[] checkinImages, @JsonProperty("status") String status) {
        this.bikeID = bikeID;
        this.ownerCheckInID = ownerCheckInID;
        this.checkinTime = checkinTime;
        this.nfcNumber = nfcNumber;
        this.checkinImages = checkinImages;
        this.status = status;
    }

    public Ticket(@JsonProperty("bikeID") String bikeID, @JsonProperty("ownerCheckInID") String ownerCheckInID, @JsonProperty("checkinTime") String checkinTime,
                  @JsonProperty("ownerCheckOutID") String ownerCheckOutID, @JsonProperty("checkoutTime") String checkoutTime, @JsonProperty("nfcNumber") String nfcNumber,
                  @JsonProperty("paymentType") String paymentType, @JsonProperty("checkinImages") String[] checkinImages,
                  @JsonProperty("checkoutImages") String[] checkoutImages, @JsonProperty("status") String status) {
        this.bikeID = bikeID;
        this.ownerCheckInID = ownerCheckInID;
        this.checkinTime = checkinTime;
        this.ownerCheckOutID = ownerCheckOutID;
        this.checkoutTime = checkoutTime;
        this.nfcNumber = nfcNumber;
        this.paymentType = paymentType;
        this.checkinImages = checkinImages;
        this.checkoutImages = checkoutImages;
        this.status = status;
    }

    public String getBikeID() {
        return bikeID;
    }

    public void setBikeID(String bikeID) {
        this.bikeID = bikeID;
    }

    public String getOwnerCheckInID() {
        return ownerCheckInID;
    }

    public void setOwnerCheckInID(String ownerCheckInID) {
        this.ownerCheckInID = ownerCheckInID;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getOwnerCheckOutID() {
        return ownerCheckOutID;
    }

    public void setOwnerCheckOutID(String ownerCheckOutID) {
        this.ownerCheckOutID = ownerCheckOutID;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public String getNfcNumber() {
        return nfcNumber;
    }

    public void setNfcNumber(String nfcNumber) {
        this.nfcNumber = nfcNumber;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String[] getCheckinImages() {
        return checkinImages;
    }

    public void setCheckinImages(String[] checkinImages) {
        this.checkinImages = checkinImages;
    }

    public String[] getCheckoutImages() {
        return checkoutImages;
    }

    public void setCheckoutImages(String[] checkoutImages) {
        this.checkoutImages = checkoutImages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return getBikeID().equals(ticket.getBikeID()) &&
                getOwnerCheckInID().equals(ticket.getOwnerCheckInID()) &&
                getCheckinTime().equals(ticket.getCheckinTime()) &&
                Objects.equals(getOwnerCheckOutID(), ticket.getOwnerCheckOutID()) &&
                Objects.equals(getCheckoutTime(), ticket.getCheckoutTime()) &&
                getNfcNumber().equals(ticket.getNfcNumber()) &&
                Objects.equals(getPaymentType(), ticket.getPaymentType()) &&
                Arrays.equals(getCheckinImages(), ticket.getCheckinImages()) &&
                Arrays.equals(getCheckoutImages(), ticket.getCheckoutImages()) &&
                getStatus().equals(ticket.getStatus());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getBikeID(), getOwnerCheckInID(), getCheckinTime(), getOwnerCheckOutID(), getCheckoutTime(), getNfcNumber(), getPaymentType(), getStatus());
        result = 31 * result + Arrays.hashCode(getCheckinImages());
        result = 31 * result + Arrays.hashCode(getCheckoutImages());
        return result;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "bikeID='" + bikeID + '\'' +
                ", ownerCheckInID='" + ownerCheckInID + '\'' +
                ", checkinTime='" + checkinTime + '\'' +
                ", ownerCheckOutID='" + ownerCheckOutID + '\'' +
                ", checkoutTime='" + checkoutTime + '\'' +
                ", nfcNumber='" + nfcNumber + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", checkinImages=" + Arrays.toString(checkinImages) +
                ", checkoutImages=" + Arrays.toString(checkoutImages) +
                ", status='" + status + '\'' +
                '}';
    }
}
