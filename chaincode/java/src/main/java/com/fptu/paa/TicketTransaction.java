package com.fptu.paa;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public class TicketTransaction {
    @Property
    private String ticketID;
    @Property
    private String createTime;
}
