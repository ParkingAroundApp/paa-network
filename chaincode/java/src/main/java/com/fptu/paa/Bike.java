package com.fptu.paa;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType
public class Bike {
    @Property()
    private String customerID;
    @Property
    private String plateNumber;
    @Property
    private String color;
    @Property
    private String modelID;
    @Property
    private String chassisNumber;
    @Property
    private String status;

    public Bike() {
    }

    public Bike(@JsonProperty("customerID") String customerID, @JsonProperty("plateNumber") String plateNumber, @JsonProperty("color") String color,
                @JsonProperty("modelID") String modelID, @JsonProperty("chassisNumber") String chassisNumber, @JsonProperty("status") String status) {
        this.customerID = customerID;
        this.plateNumber = plateNumber;
        this.color = color;
        this.modelID = modelID;
        this.chassisNumber = chassisNumber;
        this.status = status;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getModelID() {
        return modelID;
    }

    public void setModelID(String modelID) {
        this.modelID = modelID;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
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
        Bike bike = (Bike) o;
        return Objects.equals(getCustomerID(), bike.getCustomerID()) &&
                Objects.equals(getPlateNumber(), bike.getPlateNumber()) &&
                Objects.equals(getColor(), bike.getColor()) &&
                Objects.equals(getModelID(), bike.getModelID()) &&
                Objects.equals(getChassisNumber(), bike.getChassisNumber()) &&
                Objects.equals(getStatus(), bike.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCustomerID(), getPlateNumber(), getColor(), getModelID(), getChassisNumber(), getStatus());
    }

    @Override
    public String toString() {
        return "Bike{" +
                "customerID='" + customerID + '\'' +
                ", plateNumber='" + plateNumber + '\'' +
                ", color='" + color + '\'' +
                ", modelID='" + modelID + '\'' +
                ", capacity='" + chassisNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
