package com.fptu.paa;


import com.google.gson.JsonObject;
import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.*;
import org.json.JSONObject;

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
        YMD_TICKET,
        CUSTOMER_TICKET,
    }

    private enum ParkingContractError {
        KEY_NOT_FOUND,
        TICKET_NOT_FOUND
    }

    private enum TicketStatus {
        KEEPING,
        CLAIMING,
        FINISH
    }

    @Transaction()
    public void initLedger(final Context ctx) {
        System.out.println("Calling Init Function");
        //Init sample data
        String[] ticketData = {
                "{ \"bikeID\": \"1\",\"licensePlate\": \"59P2-81240\", \"ownerCheckInID\": \"2\", \"checkinTime\": \"17/10/2020-08:58:51:958\", \"nfcNumber\": \"\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"1\",\"licensePlate\": \"59P2-81241\", \"ownerCheckInID\": \"2\", \"checkinTime\": \"18/10/2020-09:58:51:958\", \"nfcNumber\": \"\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"1\",\"licensePlate\": \"59P2-81242\", \"ownerCheckInID\": \"2\", \"checkinTime\": \"19/10/2020-10:58:51:958\", \"nfcNumber\": \"\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"1\",\"licensePlate\": \"59P2-81243\", \"ownerCheckInID\": \"2\", \"checkinTime\": \"20/10/2020-11:58:51:958\", \"nfcNumber\": \"\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"1\",\"licensePlate\": \"59P2-81250\", \"ownerCheckInID\": \"2\", \"checkinTime\": \"21/10/2020-12:58:51:958\", \"nfcNumber\": \"\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"\",\"licensePlate\": \"59P2-81251\", \"ownerCheckInID\": \"1\", \"checkinTime\": \"17/10/2020-13:58:51:958\", \"nfcNumber\": \"89:20:a7:c2:02\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"\",\"licensePlate\": \"59P2-81252\", \"ownerCheckInID\": \"1\", \"checkinTime\": \"17/10/2020-14:58:51:958\", \"nfcNumber\": \"89:20:a7:c2:03\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"\",\"licensePlate\": \"59P2-81255\", \"ownerCheckInID\": \"1\", \"checkinTime\": \"17/10/2020-15:58:51:958\", \"nfcNumber\": \"89:20:a7:c2:04\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"\",\"licensePlate\": \"59P2-81256\", \"ownerCheckInID\": \"2\", \"checkinTime\": \"17/10/2020-16:58:51:958\", \"nfcNumber\": \"89:20:a7:c2:05\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}",
                "{ \"bikeID\": \"\",\"licensePlate\": \"59P2-81260\", \"ownerCheckInID\": \"2\", \"checkinTime\": \"17/10/2020-17:58:51:958\", \"nfcNumber\": \"89:20:a7:c2:06\",\"checkinImages\":[\"idPlateImage\",\"idFaceImage\"]}"
        };
        for (String data : ticketData) {
            Ticket ticket = genson.deserialize(data, Ticket.class);
            createTicket(ctx, ticket.getLicensePlate(), ticket.getBikeID(), ticket.getNfcNumber(), "9", ticket.getOwnerCheckInID(), ticket.getCheckinTime(), ticket.getCheckinImages()[0], ticket.getCheckinImages()[1]);
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
    public Ticket createTicket(final Context ctx, final String licensePlate, final String bikeID, final String nfcSerial,
                               final String customerID, final String ownerCheckInID, final String checkinTime,
                               final String checkinBikeImage, final String checkInFaceImage) {
        ArrayList<String> args = new ArrayList<>();
        boolean isNFC = false;
        if (bikeID.isEmpty()) {
            args.add(nfcSerial);
            isNFC = true;
        } else {
            args.add(bikeID);
            args.add(customerID);
        }
        args.add(ownerCheckInID);
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
            String ticketKey = isNFC ? ("TICKET" + "_" + checkinTime + "_" + nfcSerial) : ("TICKET" + "_" + checkinTime + "_" + bikeID);
            String[] checkInImages = {checkinBikeImage, checkInFaceImage};
            Ticket newTicket = new Ticket(licensePlate, bikeID, ownerCheckInID, checkinTime, nfcSerial, checkInImages, TicketStatus.KEEPING.name());
            //Store new ticket
            stub.putStringState(ticketKey, genson.serialize(newTicket));
            //Create composite key for new ticket
            if (!isNFC) {
                //QUERY by CUSTOMER ID (List)
                String date = checkinTime.split("-")[0];
                String[] data = date.split("/");
                String year = data[2];
                String month = data[1];
                String customerTicketIndexKey = stub.createCompositeKey(IndexName.CUSTOMER_TICKET.name(), customerID, year, month, ticketKey).toString();
                System.out.println("customerTicketIndexKey - " + customerTicketIndexKey);
                stub.putStringState(customerTicketIndexKey, DEFAULT_VALUE);
            }
            createDateTimeKey(stub, checkinTime, ticketKey);
            result = newTicket;
        }
        return result;
    }

    @Transaction()
    public String checkOut(final Context ctx, final String ticketKey, final String ownerCheckOutID, final String checkOutTime, final String checkOutBikeImage, final String checkOutFaceImage, final String paymentType) {
        String result = null;
        ArrayList<String> args = new ArrayList<>();
        args.add(ticketKey);
        args.add(ownerCheckOutID);
        args.add(paymentType);
        args.add(checkOutTime);
        args.add(checkOutBikeImage);
        args.add(checkOutFaceImage);
        if (checkArgs(args)) {
            //Create stub
            ChaincodeStub stub = ctx.getStub();
            //Get ticket
            String ticketState = stub.getStringState(ticketKey);
            Ticket ticket = genson.deserialize(ticketState, Ticket.class);
            //Verified if this bike is parking
            if (ticket.getStatus().equals(TicketStatus.KEEPING.name())) {
                System.out.println("CHECKOUT BIKE:" + ticketKey);
                //Begin checkout
                String[] checkOutImages = {checkOutBikeImage, checkOutFaceImage};
                ticket.setStatus(TicketStatus.FINISH.name());
                ticket.setOwnerCheckOutID(ownerCheckOutID);
                ticket.setCheckoutTime(checkOutTime);
                ticket.setCheckoutImages(checkOutImages);
                ticket.setPaymentType(paymentType);
                //Store check out ticket
                String newTicketState = genson.serialize(ticket);
                stub.putStringState(ticketKey, newTicketState);
                //Return result
                result = newTicketState;
                System.out.println("FINISH CHECKOUT");
            }
        }
        return result;
    }

    @Transaction()
    public String reportTicket(final Context ctx, final String checkInTime, final String id) {
        String result = null;
        //Create chaincode stub
        ChaincodeStub stub = ctx.getStub();
        String key = "TICKET" + "_" + checkInTime + "_" + id;
        String state = stub.getStringState(key);
        if (state.isEmpty()) {
            String errorMessage = String.format("Ticket %s not found", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ParkingContractError.TICKET_NOT_FOUND.toString());
        }
        Ticket ticket = genson.deserialize(state, Ticket.class);
        ticket.setStatus(TicketStatus.CLAIMING.name());
        state = genson.serialize(ticket);
        //Store new state
        stub.putStringState(key, state);
        result = state;
        return result;
    }

    @Transaction()
    public String getCheckoutTicket(final Context ctx, final String bikeID, final String nfcSerial) {
        String result = null;
        //Create stub
        ChaincodeStub stub = ctx.getStub();
        QueryResultsIterator<KeyValue> queryResultsIterator;
        JSONObject queryString = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        queryString.put("selector", jsonObject.put("status", "KEEPING"));

        if (nfcSerial != null && !nfcSerial.isEmpty()) {//CHECK NFC FIELD NOT EMPTY
            queryString.put("selector", jsonObject.put("bikeID", bikeID));
        } else {
            queryString.put("selector", jsonObject.put("nfcNumber", nfcSerial));
        }
        queryResultsIterator = stub.getQueryResult(queryString.toString());
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
                String errorMessage = String.format("Not found any checkout ticket %s", ticketKey);
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, ParkingContractError.TICKET_NOT_FOUND.toString());
            }
            result = ticketState;
        }
        return result;
    }

    @Transaction()
    public String queryTicketByCustomer(final Context ctx, final String customerID, final String year,
                                        final String month) {
        String result = null;
        //Create chaincode stub
        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = stub.createCompositeKey(IndexName.CUSTOMER_TICKET.name(), customerID, year, month);
        QueryResultsIterator<KeyValue> resultsIterator = stub.getStateByPartialCompositeKey(key);
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
    public String queryTicketByDate(final Context ctx, final String year, final String month, final String pageSize, final String bookmark) {
        String result = null;
        //Create chaincode stub
        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = stub.createCompositeKey(IndexName.YMD_TICKET.name(), year, month);
        QueryResultsIteratorWithMetadata<KeyValue> resultsIterator = stub.getStateByPartialCompositeKeyWithPagination(key, Integer.valueOf(pageSize), bookmark);
        if (resultsIterator != null) {
            TicketQueryResult tmpResult = new TicketQueryResult();
            String newBookmark = resultsIterator.getMetadata().getBookmark();
            List<Ticket> ticketList = new ArrayList<>();
            for (KeyValue keyValue : resultsIterator) {
                //Split composite key to get ticket key
                //Step 1: Get composite key
                CompositeKey compositeKey = stub.splitCompositeKey(keyValue.getKey());
                //Step 2: Get ticket key
                String ticketKey = compositeKey.getAttributes().get(2);
                //Query ticket and add to list
                String ticketState = stub.getStringState(ticketKey);
                Ticket ticket = genson.deserialize(ticketState, Ticket.class);
                ticketList.add(ticket);
            }
            tmpResult.setBookmark(newBookmark);
            tmpResult.setData(genson.serialize(ticketList));
            result = genson.serialize(tmpResult);
        }
        return result;
    }


    @Transaction()
    public String queryAllTicketWithPagination(final Context ctx, final String query, final String pageSize, final String bookmark) {
        String result = "";
        ChaincodeStub stub = ctx.getStub();
        QueryResultsIteratorWithMetadata<KeyValue> resultsIterator = stub.getQueryResultWithPagination(query, Integer.valueOf(pageSize), bookmark);
        if (resultsIterator != null) {
            TicketQueryResult tmpResult = new TicketQueryResult();
            String newBookmark = resultsIterator.getMetadata().getBookmark();
            List<Ticket> ticketList = new ArrayList<>();
            for (KeyValue keyValue : resultsIterator) {
                String ticketState = keyValue.getStringValue();
                Ticket ticket = genson.deserialize(ticketState, Ticket.class);
                ticketList.add(ticket);
            }
            tmpResult.setBookmark(newBookmark);
            tmpResult.setData(genson.serialize(ticketList));
            result = genson.serialize(tmpResult);
        }
        return result;
    }

    @Transaction()
    public String queryKeyHistory(final Context ctx, final String checkInTime, final String id) {
        String result = null;
        //Create chaincode stub
        ChaincodeStub stub = ctx.getStub();
        String key = "TICKET" + "_" + checkInTime + "_" + id;
        QueryResultsIterator<KeyModification> queryResultsIterator = stub.getHistoryForKey(key);
        if (queryResultsIterator != null) {
            List<Ticket> ticketHistory = new ArrayList<>();
            for (KeyModification keyModification : queryResultsIterator) {
                String state = keyModification.getStringValue();
                ticketHistory.add(genson.deserialize(state, Ticket.class));
            }
            result = genson.serialize(ticketHistory);
        }
        return result;
    }

    /*#################################TRANSACTION#################################*/
    private void createDateTimeKey(final ChaincodeStub stub, final String dateTime, final String ticketKey) {
        String date = dateTime.split("-")[0];
        String[] data = date.split("/");
        String year = data[2];
        String month = data[1];
        String YMDIndexKey = stub.createCompositeKey(IndexName.YMD_TICKET.name(), year, month, ticketKey).toString();
        stub.putStringState(YMDIndexKey, DEFAULT_VALUE);
    }

    private boolean checkArgs(List<String> args) {
        for (String arg : args) {
            if (arg != null) {
                if (arg.isEmpty()) {
                    System.out.println("Agr is empty ?" + args.isEmpty());
                    return false;
                }
            }
        }
        return true;
    }
}
