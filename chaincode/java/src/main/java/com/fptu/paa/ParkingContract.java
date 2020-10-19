package com.fptu.paa;


import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Contract(name = "ParkingContract", info = @Info(
        title = "Parking Contract",
        description = "The parking bike contract",
        version = "0.0.1-SNAPSHOT",
        contact = @Contact(
                email = "paa@gmail.com",
                name = "PAA",
                url = "https://hyperledger.example.com"
        )))
@Default
public class ParkingContract implements ContractInterface {
    private final Genson genson = new Genson();
    private final String DEFAULT_VALUE = "100111010010000010";

    private enum IndexName {
        TYPE,
        BIKE_TICKET,
        BIKE_STATUS_TICKET,
        NFC_TICKET,
        NFC_STATUS_TICKET,
        OWNER_TICKET,
        TICKET_TRANSACTION;
    }

    private enum ParkingContractError {
        KEY_NOT_FOUND,
        TICKET_NOT_FOUND;
    }

    private enum TicketStatus {
        KEEPING,
        CLAIMING,
        FINISH;
    }

    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        System.out.println("Calling Init Function");
        //Init sample data
        String[] ticketData = {
                "{ \"bikeID\": \"1\", \"ownerCheckInID\": \"1\", \"checkinTime\": \"17/10/2020-08:58:51:958\", \"nfcNumber\": \"\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"2\", \"ownerCheckInID\": \"1\", \"checkinTime\": \"17/10/2020-09:58:51:958\", \"nfcNumber\": \"\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"3\", \"ownerCheckInID\": \"1\", \"checkinTime\": \"17/10/2020-10:58:51:958\", \"nfcNumber\": \"\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"4\", \"ownerCheckInID\": \"2\", \"checkinTime\": \"17/10/2020-11:58:51:958\", \"nfcNumber\": \"\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"5\", \"ownerCheckInID\": \"2\", \"checkinTime\": \"17/10/2020-12:58:51:958\", \"nfcNumber\": \"\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"\", \"ownerCheckInID\": \"1\", \"checkinTime\": \"17/10/2020-13:58:51:958\", \"nfcNumber\": \"123456789\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"\", \"ownerCheckInID\": \"1\", \"checkinTime\": \"17/10/2020-14:58:51:958\", \"nfcNumber\": \"123456788\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"\", \"ownerCheckInID\": \"1\", \"checkinTime\": \"17/10/2020-15:58:51:958\", \"nfcNumber\": \"123456786\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"\", \"ownerCheckInID\": \"2\", \"checkinTime\": \"17/10/2020-16:58:51:958\", \"nfcNumber\": \"123456785\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"\", \"ownerCheckInID\": \"2\", \"checkinTime\": \"17/10/2020-17:58:51:958\", \"nfcNumber\": \"123456782\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}"
        };
//        int i = 0;
        for (String data : ticketData) {
            Ticket ticket = genson.deserialize(data, Ticket.class);
//            String ticketKey = String.format("TICKET%d", ++i);
            createTicket(ctx, ticket.getBikeID(), ticket.getNfcNumber(), ticket.getOwnerCheckInID(), ticket.getCheckinTime(), ticket.getCheckinImages()[0], ticket.getCheckinImages()[1]);
        }
    }

    @Transaction()
    public String queryByKey(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String state = stub.getStringState(key);
        if (state.isEmpty()) {
            String errorMessage = String.format("Asset %s not found", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ParkingContractError.KEY_NOT_FOUND.toString());
        }
        return state;
    }

    @Transaction()
    public Ticket createTicket(final Context ctx, final String bikeID, final String nfcSerial, final String ownerCheckInID, final String checkinTime, final String checkinBikeImage, final String checkInFaceImage) {
        ArrayList<String> args = new ArrayList<>();
        boolean isNFC = false;
        if ((bikeID.isEmpty() == true)) {
            args.add(nfcSerial);
            isNFC = true;
        } else {
            args.add(bikeID);
        }
        args.add(checkinTime);
        args.add(checkinBikeImage);
        args.add(checkInFaceImage);
        //If args not empty
        Ticket result = null;
        System.out.println("NFC Ticket:" + isNFC + " bikeID:" + bikeID);
        if (checkArgs(args)) {
            //Create chaincode stub
            ChaincodeStub stub = ctx.getStub();
            //Create key and value
            String ticketKey = "TICKET" + checkinTime;
            String[] checkInImages = {checkinBikeImage, checkInFaceImage};
            Ticket newTicket = new Ticket(bikeID, ownerCheckInID, checkinTime, nfcSerial, checkInImages, TicketStatus.KEEPING.name());
            //Store new ticket
            stub.putStringState(ticketKey, genson.serialize(newTicket));
            result = newTicket;
            //Create composite key for new ticket
            if (!isNFC) {
                //QUERY by BIKE ID
                String bikeTicketIndexKey = stub.createCompositeKey(IndexName.BIKE_TICKET.name(), bikeID, ticketKey).toString();
                System.out.println("bikeTicketIndexKey - " + bikeTicketIndexKey);
                stub.putStringState(bikeTicketIndexKey, DEFAULT_VALUE);
                //QUERY by BIKE ID + STATUS
                String bikeStatusTicketIndexKey = stub.createCompositeKey(IndexName.BIKE_STATUS_TICKET.name(), TicketStatus.KEEPING.name(), bikeID, ticketKey).toString();
                System.out.println("bikeStatusTicketIndexKey - " + bikeStatusTicketIndexKey);
                stub.putStringState(bikeStatusTicketIndexKey, DEFAULT_VALUE);
            } else {
                //QUERY by NFC SERIAL
                String nfcTicketIndexKey = stub.createCompositeKey(IndexName.NFC_TICKET.name(), nfcSerial, ticketKey).toString();
                System.out.println("nfcTicketIndexKey - " + nfcTicketIndexKey);
                stub.putStringState(nfcTicketIndexKey, DEFAULT_VALUE);
                //QUERY by NFC SERIAL + STATUS
                String nfcStatusTicketIndexKey = stub.createCompositeKey(IndexName.NFC_STATUS_TICKET.name(), TicketStatus.KEEPING.name(), nfcSerial, ticketKey).toString();
                System.out.println("nfcStatusTicketIndexKey - " + nfcStatusTicketIndexKey);
                stub.putStringState(nfcStatusTicketIndexKey, DEFAULT_VALUE);
            }
            String ticketIndexKey = stub.createCompositeKey(IndexName.TYPE.name(), "ticket", ticketKey).toString();
            System.out.println("ticketIndexKey - " + ticketIndexKey);
            stub.putStringState(ticketIndexKey, DEFAULT_VALUE);
        }
        return result;
    }

    @Transaction()
    public String checkOutByBike(final Context ctx, final String ticketKey, final String ownerCheckOutID, final String checkOutTime, final String checkOutBikeImage, final String checkOutFaceImage, final String paymentType) {
        String result = "Error";
        ArrayList<String> args = new ArrayList<>();
        args.add(ticketKey);
        args.add(ownerCheckOutID);
        args.add(paymentType);
        args.add(checkOutTime);
        args.add(checkOutBikeImage);
        args.add(checkOutFaceImage);
        if (checkArgs(args)) {
            System.out.println("CHECKOUT BIKE:" + ticketKey);
            //Create stub
            ChaincodeStub stub = ctx.getStub();
            //Get ticket
            String ticketState = stub.getStringState(ticketKey);
            Ticket ticket = genson.deserialize(ticketState, Ticket.class);
            //Begin checkout
            String[] checkOutImages = {checkOutBikeImage, checkOutFaceImage};
            Ticket newTicket = new Ticket(ticket.getBikeID(), ticket.getOwnerCheckInID(), ticket.getCheckinTime(), ownerCheckOutID,
                    checkOutTime, ticket.getNfcNumber(), paymentType,
                    ticket.getCheckinImages(), checkOutImages, TicketStatus.FINISH.name());
            //Store check out ticket
            String newTicketState = genson.serialize(newTicket);
            System.out.println("NEW State: " + newTicketState);
            stub.putStringState(ticketKey, newTicketState);

            //Begin update index key
            boolean isNFC = false;
            if (newTicket.getNfcNumber() != null && !newTicket.getNfcNumber().isEmpty()) {
                isNFC = true;
            }
            //step 1: remove old key
            if (!isNFC) {
                String bikeStatusTicketIndexKey = stub.createCompositeKey(IndexName.BIKE_STATUS_TICKET.name(), TicketStatus.KEEPING.name(), newTicket.getBikeID(), ticketKey).toString();
                System.out.println("Checkin Bike Old Key - " + bikeStatusTicketIndexKey);
                stub.putStringState(bikeStatusTicketIndexKey, "");
                //step 2: insert new key
                bikeStatusTicketIndexKey = stub.createCompositeKey(IndexName.BIKE_STATUS_TICKET.name(), TicketStatus.FINISH.name(), newTicket.getBikeID(), ticketKey).toString();
                System.out.println("Checkout Bike New Key - " + bikeStatusTicketIndexKey);
                stub.putStringState(bikeStatusTicketIndexKey, DEFAULT_VALUE);
            } else {
                String nfcStatusTicketIndexKey = stub.createCompositeKey(IndexName.NFC_STATUS_TICKET.name(), TicketStatus.KEEPING.name(), newTicket.getNfcNumber(), ticketKey).toString();
                System.out.println("Checkin NFC Old Key - " + nfcStatusTicketIndexKey);
                stub.putStringState(nfcStatusTicketIndexKey, "");
                //step 2: insert new key
                nfcStatusTicketIndexKey = stub.createCompositeKey(IndexName.NFC_STATUS_TICKET.name(), TicketStatus.FINISH.name(), newTicket.getNfcNumber(), ticketKey).toString();
                System.out.println("Checkout NFC New Key - " + nfcStatusTicketIndexKey);
                stub.putStringState(nfcStatusTicketIndexKey, DEFAULT_VALUE);
            }
            //Return checkout ticket
            result = newTicketState;
        }
        System.out.println("FINISH CHECKOUT");
        return result;
    }

    @Transaction()
    public String getCheckoutTicket(final Context ctx, final String bikeID, final String nfcSerial) {
        String result = "Error";
        //Create stub
        ChaincodeStub stub = ctx.getStub();
        QueryResultsIterator<KeyValue> queryResultsIterator;
        if (nfcSerial != null && !nfcSerial.isEmpty()) {//CHECK NFC FIELD NOT EMPTY
            queryResultsIterator = stub.getStateByPartialCompositeKey(IndexName.NFC_STATUS_TICKET.name(), TicketStatus.KEEPING.name(), nfcSerial);
        } else {
            queryResultsIterator = stub.getStateByPartialCompositeKey(IndexName.BIKE_STATUS_TICKET.name(), TicketStatus.KEEPING.name(), bikeID);
        }
        Iterator<KeyValue> iterator = queryResultsIterator.iterator();
        if (iterator.hasNext()) {
            KeyValue keyValue = iterator.next();
            //Split composite key to get ticket key
            //Step 1: Get composite key
            CompositeKey compositeKey = stub.splitCompositeKey(keyValue.getKey());
            System.out.println("getCheckoutTicketByBikeID :" + compositeKey.toString());
            //Step 2: Get ticket key
            String ticketKey = compositeKey.getAttributes().get(2);
            //Get ticket
            String ticketState = stub.getStringState(ticketKey);
            if (ticketState.isEmpty()) {
                String errorMessage = String.format("Ticket %s not found", ticketKey);
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, ParkingContractError.TICKET_NOT_FOUND.toString());
            }
            result = ticketState;
        }
        return result;
    }

    @Transaction()
    public String queryTicketById(final Context ctx, final String nfcSerial, final String bikeID) {
        String result = "Empty";
        //Create chaincode stub
        ChaincodeStub stub = ctx.getStub();
        QueryResultsIterator<KeyValue> resultsIterator;
        if (nfcSerial != null && !nfcSerial.isEmpty()) {//CHECK NFC FIELD NOT EMPTY
            resultsIterator = stub.getStateByPartialCompositeKey(IndexName.NFC_TICKET.name(), nfcSerial);
        } else {
            resultsIterator = stub.getStateByPartialCompositeKey(IndexName.BIKE_TICKET.name(), bikeID);
        }
        if (resultsIterator != null) {
            List<Ticket> ticketList = new ArrayList<>();
            for (KeyValue keyValue : resultsIterator) {
                //Split composite key to get ticket key
                //Step 1: Get composite key
                CompositeKey compositeKey = stub.splitCompositeKey(keyValue.getKey());
                //Step 2: Get ticket key
                String ticketKey = compositeKey.getAttributes().get(1);
                //Query ticket and add to list
                String ticketState = stub.getStringState(ticketKey);
                Ticket ticket = genson.deserialize(ticketState, Ticket.class);
                ticketList.add(ticket);
            }
            result = genson.serialize(ticketList);
        }
        return result;
    }

    @Transaction()
    public String queryAllTicket(final Context ctx) {
        String result = "Empty";
        //Create chaincode stub
        ChaincodeStub stub = ctx.getStub();
        QueryResultsIterator<KeyValue> resultsIterator = stub.getStateByPartialCompositeKey(IndexName.TYPE.name(), "ticket");
        if (resultsIterator != null) {
            List<Ticket> ticketList = new ArrayList<>();
            for (KeyValue keyValue : resultsIterator) {
                //Split composite key to get ticket key
                //Step 1: Get composite key
                CompositeKey compositeKey = stub.splitCompositeKey(keyValue.getKey());
                //Step 2: Get ticket key
                String ticketKey = compositeKey.getAttributes().get(1);
                //Query ticket and add to list
                String ticketState = stub.getStringState(ticketKey);
                Ticket ticket = genson.deserialize(ticketState, Ticket.class);
                ticketList.add(ticket);
            }
            result = genson.serialize(ticketList);
        }
        return result;
    }

    /*#################################TRANSACTION#################################*/
    private boolean checkArgs(List<String> args) {
        for (String arg : args) {
            if (arg != null) {
                if (arg.isEmpty()) return false;
            }
        }
        return true;
    }
}
