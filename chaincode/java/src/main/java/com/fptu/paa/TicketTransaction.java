package com.fptu.paa;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public class TicketTransaction {
    @Property
    private String ticketID;

    @Property
    private String walletID;

    @Property
    private String createTime;

    @Property
    private String type;

    @Property
    private String amount;

    @Property
    private String status;
}
