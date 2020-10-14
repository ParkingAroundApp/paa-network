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
    private final String INDEXNAME_VALUE = "100111010010000010";

    private enum IndexName {
        CUSTOMER_BIKE,
        PLATENUMBER_BIKE,
        BIKE_TICKET,
        OWNER_TICKET,
        TICKET_TRANSACTION;
    }

    private enum BikeContractErrors {
        KEY_NOT_FOUND,
        EMPTY_ARGUMENT,
        BIKE_NOT_FOUND,
        BIKE_ALREADY_EXISTS;
    }

    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String[] bikeData = {
                "{ \"customerID\": \"A\", \"plateNumber\": \"59P2-81240\", \"color\": \"black\", \"modelID\": \"MODEL22\", \"chassisNumber\": \"123456789\", \"status\": \"ENABLED\"}",
                "{ \"customerID\": \"B\", \"plateNumber\": \"59P2-81241\", \"color\": \"red\", \"modelID\": \"MODEL20\", \"chassisNumber\": \"123456789\", \"status\": \"ENABLED\"}",
                "{ \"customerID\": \"C\", \"plateNumber\": \"59P2-81242\", \"color\": \"green\", \"modelID\": \"MODEL24\", \"chassisNumber\": \"123456789\", \"status\": \"ENABLED\"}",
                "{ \"customerID\": \"D\", \"plateNumber\": \"59P2-81243\", \"color\": \"blue\", \"modelID\": \"MODEL25\", \"chassisNumber\": \"123456789\", \"status\": \"ENABLED\"}",
                "{ \"customerID\": \"E\", \"plateNumber\": \"59P2-81239\", \"color\": \"brown\", \"modelID\": \"MODEL26\", \"chassisNumber\": \"123456789\", \"status\": \"ENABLED\"}",
                "{ \"customerID\": \"B\", \"plateNumber\": \"59P2-81245\", \"color\": \"white\", \"modelID\": \"MODEL27\", \"chassisNumber\": \"123456789\", \"status\": \"ENABLED\"}",
                "{ \"customerID\": \"B\", \"plateNumber\": \"59P2-81246\", \"color\": \"gray\", \"modelID\": \"MODEL28\", \"chassisNumber\": \"123456789\", \"status\": \"ENABLED\"}",
        };

        for (int i = 0; i < bikeData.length; i++) {
            String bikeKey = String.format("BIKE%d", i);
            Bike bike = genson.deserialize(bikeData[i], Bike.class);
            createBike(ctx,bikeKey, bike.getCustomerID(), bike.getPlateNumber(), bike.getColor(), bike.getModelID(), bike.getChassisNumber(), bike.getStatus());
        }
    }

    @Transaction()
    public String queryByKey(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String state = stub.getStringState(key);
        if (state.isEmpty()) {
            String errorMessage = String.format("Asset %s not found", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, BikeContractErrors.KEY_NOT_FOUND.toString());
        }
        return state;
    }

    /*#################################BIKE#################################*/
    @Transaction()
    public Bike createBike(final Context ctx, final String bikeKey,
                           final String customerID, final String plateNumber, final String color,
                           final String modelID, final String chassisNumber, final String status) {
        ArrayList<String> args = new ArrayList<>();
        args.add(bikeKey);
        args.add(customerID);
        args.add(plateNumber);
        args.add(color);
        args.add(modelID);
        args.add(chassisNumber);
        args.add(status);
        //If args not empty
        if (checkArgs(args)) {
            //Create stub
            ChaincodeStub stub = ctx.getStub();
            String bikeState = stub.getStringState(bikeKey); //key = BIKE+plateNumber
            //Check exist bike return null
            if (!bikeState.isEmpty()) {
                String errorMessage = String.format("Bike %s already exists", plateNumber);
//              System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, BikeContractErrors.BIKE_ALREADY_EXISTS.toString());
            }
            //Put a new bike to blockchain
            Bike bike = new Bike(customerID, plateNumber, color, modelID, chassisNumber, status);
            bikeState = genson.serialize(bike);
            stub.putStringState(bikeKey, bikeState);
            //Create a new composite
            //Query by customerID
            String customerBikeIndexKey = stub.createCompositeKey(IndexName.CUSTOMER_BIKE.name(), customerID, bikeKey).toString();
            System.out.println("customerBikeIndexKey - " + customerBikeIndexKey);
            stub.putStringState(customerBikeIndexKey, INDEXNAME_VALUE);
            //Query by platenumber
            String platenumberBikeIndexKey = stub.createCompositeKey(IndexName.PLATENUMBER_BIKE.name(), plateNumber, bikeKey).toString();
            System.out.println("platenumberBikeIndexKey - " + platenumberBikeIndexKey);
            stub.putStringState(platenumberBikeIndexKey, INDEXNAME_VALUE);
        }
        return null;
    }

    //Compositekey
//    @Transaction()
//    public Bike createBike(final Context ctx,
//                           final String customerID, final String plateNumber, final String color,
//                           final String modelID, final String chassisNumber, final String status) {
//        ArrayList<String> args = new ArrayList<>();
//        args.add(customerID);
//        args.add(plateNumber);
//        args.add(color);
//        args.add(modelID);
//        args.add(chassisNumber);
//        args.add(status);
//        //If args not empty
//        if (checkArgs(args)) {
//            //Create stub
//            ChaincodeStub stub = ctx.getStub();
//            CompositeKey bikeKey = stub.createCompositeKey("BIKE",status,plateNumber);
//            String bikeState = stub.getStringState(bikeKey.toString()); //key = BIKE+plateNumber
//            //Check exist bike return null
//            if (!bikeState.isEmpty()) {
//                String errorMessage = String.format("Bike %s already exists", plateNumber);
////              System.out.println(errorMessage);
//                throw new ChaincodeException(errorMessage, BikeContractErrors.BIKE_ALREADY_EXISTS.toString());
//            }
//            //Put a new bike to blockchain
//            Bike bike = new Bike(customerID, plateNumber, color, modelID, chassisNumber, status);
//            bikeState = genson.serialize(bike);
//            stub.putStringState(bikeKey.toString(), bikeState);
//            //Create a new composite
//            //Query by customerID
//            String customerBikeIndexKey = stub.createCompositeKey(IndexName.CUSTOMER_BIKE.name(), customerID, bikeKey.toString()).toString();
//            System.out.println("customerBikeIndexKey - " + customerBikeIndexKey);
//            stub.putStringState(customerBikeIndexKey, INDEXNAME_VALUE);
//            //Query by platenumber
//            String platenumberBikeIndexKey = stub.createCompositeKey(IndexName.PLATENUMBER_BIKE.name(), plateNumber, bikeKey.toString()).toString();
//            System.out.println("platenumberBikeIndexKey - " + platenumberBikeIndexKey);
//            stub.putStringState(platenumberBikeIndexKey, INDEXNAME_VALUE);
//        }
//        return null;
//    }

    //Query bike by key
    @Transaction()
    public Bike queryBike(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String bikeState = stub.getStringState(key);

        if (bikeState.isEmpty()) {
            String errorMessage = String.format("Bike %s not found", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, BikeContractErrors.BIKE_NOT_FOUND.toString());
        }
        Bike bike = genson.deserialize(bikeState, Bike.class);
        return bike;
    }

//    //Composite key
//    @Transaction()
//    public Bike queryBike(final Context ctx, final String plateNumber,final String status) {
//        ChaincodeStub stub = ctx.getStub();
//        CompositeKey bikeKey = stub.createCompositeKey("BIKE",status,plateNumber);
//        String bikeState = stub.getStringState(bikeKey.toString());
//
//        if (bikeState.isEmpty()) {
//            String errorMessage = String.format("Bike %s not found", bikeKey.toString());
//            System.out.println(errorMessage);
//            throw new ChaincodeException(errorMessage, BikeContractErrors.BIKE_NOT_FOUND.toString());
//        }
//        Bike bike = genson.deserialize(bikeState, Bike.class);
//        return bike;
//    }

    //Querying a bike by plate number
    @Transaction()
    public Bike queryBikeByPlateNumber(final Context ctx, final String plateNumber) {
        Bike result = null;
        //Checking non-empty argument
        if (plateNumber.isEmpty()) {
            return null;
        }
        //System.out.println("Platenumber:  " + plateNumber);
        //Create chaincode stub
        ChaincodeStub stub = ctx.getStub();
        //Get composite keys by partial part
        // plate number
        QueryResultsIterator<KeyValue> resultsIterator
                = stub.getStateByPartialCompositeKey(stub.createCompositeKey(IndexName.PLATENUMBER_BIKE.name(), plateNumber).toString());
        if (resultsIterator != null) {
            Iterator<KeyValue> iterator = resultsIterator.iterator();
            if (iterator.hasNext()) {
                KeyValue keyValue = iterator.next();
                //Split a composite key to get bikeID
                CompositeKey compositeKey = stub.splitCompositeKey(keyValue.getKey());
                //System.out.println("Compositekey:" + compositeKey.getObjectType() + "-" + compositeKey.getAttributes().toString());
                String bikeID = compositeKey.getAttributes().get(1);

                String bikeState = stub.getStringState(bikeID);
                //System.out.println("Bike by platenumber:" + bikeState);
                //Check if bike exist
                if (bikeState.isEmpty()) {
                    String errorMessage = String.format("Bike %s not found", bikeID);
                    System.out.println(errorMessage);
                    throw new ChaincodeException(errorMessage, BikeContractErrors.BIKE_NOT_FOUND.toString());
                }
                result = genson.deserialize(bikeState, Bike.class);
            }
        }
        return result;
    }

    //Query a list of bike by customerID
    @Transaction()
    public String queryBikeByOwnerID(final Context ctx, final String customerID) throws Exception {
        String reuslt = BikeContractErrors.BIKE_NOT_FOUND.name();
        //Checking non-empty argument
        if (customerID.isEmpty()) {
            return BikeContractErrors.EMPTY_ARGUMENT.name();
        }
        System.out.println("customerID:  " + customerID);
        //Create chaincode stub
        ChaincodeStub stub = ctx.getStub();
        //Get composite keys by partial part
        //customerID
        QueryResultsIterator<KeyValue> resultsIterator = stub.getStateByPartialCompositeKey(IndexName.CUSTOMER_BIKE.name(), customerID);
        if (resultsIterator != null) {
            List<Bike> bikeList = new ArrayList<>();
            for (KeyValue keyValue : resultsIterator) {
                //Split a composite key to get bikeID
                CompositeKey compositeKey = stub.splitCompositeKey(keyValue.getKey());
                String bikeID = compositeKey.getAttributes().get(1);

                String bikeState = stub.getStringState(bikeID);
                //Verify if bike exist
                if (bikeState.isEmpty()) {
                    String errorMessage = String.format("Bike %s not exist", bikeID);
                    System.out.println(errorMessage);
                    throw new ChaincodeException(errorMessage, BikeContractErrors.BIKE_NOT_FOUND.toString());
                }
                Bike bike = genson.deserialize(bikeState, Bike.class);
                bikeList.add(bike);
            }
            reuslt = genson.serialize(bikeList);
        }
        return reuslt;
    }

    @Transaction()
    public String queryAllBike(final Context ctx) {
        String result = "";
        ChaincodeStub stub = ctx.getStub();
        return result;
    }

    /*#################################TICKET#################################*/
    @Transaction()
    public Ticket createCheckInTicket(final Context ctx, final String bikeID, final String createTime, final String checkinBikeImage, final String checkInFaceImage, final String status) {
        ArrayList<String> args = new ArrayList<>();
        args.add(bikeID);
        args.add(createTime);
        args.add(checkinBikeImage);
        args.add(checkInFaceImage);
        args.add(status);
        //If args not empty
        if (checkArgs(args)) {
            //Create chaincode stub
            ChaincodeStub stub = ctx.getStub();
            //Verify exist

        }
        return null;
    }

    @Transaction
    public String updateTicketStatus(final Context ctx) {
        return null;
    }

    @Transaction
    public String updateCheckoutImage(final Context ctx) {
        return null;
    }

    @Transaction
    public String queryTicketByBikeID(final Context ctx, final String bikeID) {
        return null;
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
