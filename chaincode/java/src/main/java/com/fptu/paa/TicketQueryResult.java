package com.fptu.paa;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.List;
import java.util.Objects;

@DataType
public class TicketQueryResult {
    @Property
    private String bookmark;
    @Property
    private String data;

    public TicketQueryResult() {
    }

    public TicketQueryResult(@JsonProperty("bookmark") String bookmark,@JsonProperty("data") String data) {
        this.bookmark = bookmark;
        this.data = data;
    }

    public String getBookmark() {
        return bookmark;
    }

    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketQueryResult that = (TicketQueryResult) o;
        return getBookmark().equals(that.getBookmark()) &&
                getData().equals(that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBookmark(), getData());
    }

    @Override
    public String toString() {
        return "TicketQueryResult{" +
                "bookmark='" + bookmark + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}

