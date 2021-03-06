/*
*SPDX-License-Identifier: Apache-2.0
*/

package org.hyperledger.fabric.samples.medicalData;

import com.alibaba.fastjson.JSON;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Java implementation of the Fabric Car Contract described in the Writing Your
 * First Application tutorial
 */
@Contract(
        name = "FabMedicalData",
        info = @Info(
                title = "FabMedicalData contract",
                description = "The medical data contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "f.carr@example.com",
                        name = "F Carr",
                        url = "https://hyperledger.example.com")))
@Default
public final class FabMedicalData implements ContractInterface {

    private enum FabMedicalDataErrors {
        DATA_NOT_FOUND,
        USER_NOT_ENOUGH_POINT,
    }

    public static String getMaxMedicalDataId() {
        maxMedicalDataId++;
        return maxMedicalDataId.toString();
    }

    public static String getMaxSharingRecordId() {
        maxSharingRecordId++;
        return maxSharingRecordId.toString();
    }

    public static String getMaxUserId() {
        maxUserId++;
        return maxUserId.toString();
    }

    private static Integer maxMedicalDataId = -1;
    private static Integer maxSharingRecordId = -1;
    private static Integer maxUserId = 99;

    @Transaction()
    public String queryMedicalDataById(final Context ctx, final String id) {
        ChaincodeStub stub = ctx.getStub();
        String medicalDataState = stub.getStringState(id);

        if (medicalDataState.isEmpty()) {
            String errorMessage = String.format("Medical data %s does not exist", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabMedicalDataErrors.DATA_NOT_FOUND.toString());
        }
        MedicalData medicalData = JSON.parseObject(medicalDataState, MedicalData.class);
        medicalData.setUrl(null);
        medicalData.setKeys(null);
        System.out.println(medicalData);

        return JSON.toJSONString(medicalData);
    }

    @Transaction()
    public String[] queryMedicalDataByPlatform(final Context ctx, final String platform) {
        ChaincodeStub stub = ctx.getStub();

        // ??????platform
        String platformState = stub.getStringState(String.format("%s%03d", StateType.USER, Integer.valueOf(platform)));

        // ??????medical data
        String queryString = String.format("{\"selector\":{\"type\":\"%s\",\"platform\":\"%s\"}}",
                StateType.MEDICAL_DATA, JSON.toJSONString(platformState));
        QueryResultsIterator<KeyValue> resultPointString = stub.getQueryResult(queryString);
        Iterator<KeyValue> iterator = resultPointString.iterator();

        if (!iterator.hasNext()) {
            String errorMessage = String.format("Medical data about %s does not exist", platform);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabMedicalDataErrors.DATA_NOT_FOUND.toString());
        }

        List<MedicalData> medicalDatas = new ArrayList<MedicalData>();
        while (iterator.hasNext()) {
            MedicalData medicalData = JSON.parseObject(iterator.next().getValue(), MedicalData.class);
            medicalDatas.add(medicalData);
        }

        return medicalDatas.stream().map(MedicalData::toString).collect(Collectors.toList()).
                toArray(new String[medicalDatas.size()]);
    }

    @Transaction()
    public String queryPointById(final Context ctx, final String id) {
        ChaincodeStub stub = ctx.getStub();
        String user = stub.getStringState(String.format("%s%03d", StateType.USER, Integer.valueOf(id)));

        if (user.isEmpty()) {
            String errorMessage = String.format("user %s does not exist", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabMedicalDataErrors.DATA_NOT_FOUND.toString());
        }

        User user1 = JSON.parseObject(user, User.class);

        return user1.getPoint().toString();
    }

    @Transaction()
    public void initLedger(final Context ctx) throws IOException, ClassNotFoundException {
        ChaincodeStub stub = ctx.getStub();
        for (int i = 0; i < 100; i++) {
            User user2 = new User(String.valueOf(i), "user", 1000, User.UserType.INDIVIDUAL);
            stub.putStringState(String.format("%s%03d", StateType.USER, Integer.valueOf(user2.getId())),
                    JSON.toJSONString(user2));
        }
    }

    @Transaction()
    public String createUser(final Context ctx, final String name, final String userType) {
        ChaincodeStub stub = ctx.getStub();
        String finalName = name;

        if (userType.equals(User.UserType.INDIVIDUAL.toString())) {
            finalName = "user";
        }
        User user = new User(getMaxUserId(), finalName, 1000, User.string2UserType(userType));

        stub.putStringState(String.format("%s%03d", StateType.USER, Integer.valueOf(user.getId())),
                JSON.toJSONString(user));
        return user.getId();
    }

    @Transaction()
    public String createMedicalData(final Context ctx, final String platform, final String individualIds,
                                    final String describe, final String permission, final String url,
                                    final String keys, final String attNum, final String num) {
        ChaincodeStub stub = ctx.getStub();

//        String medicalDataState = stub.getStringState(key);
//        if (!medicalDataState.isEmpty()) {
//            String errorMessage = String.format("Medical Data %s already exists", key);
//            System.out.println(errorMessage);
//            throw new ChaincodeException(errorMessage, FabMedicalDataErrors.DATA_ALREADY_EXISTS.toString());
//        }

        String[] individualList = individualIds.split(" ");
        for (String individualId: individualList) {
            String indivi = stub.getStringState(String.format("%s%03d", StateType.USER, Integer.valueOf(individualId)));
            if (indivi.isEmpty()) {
                String errorMessage = String.format("individual %s does not exist", individualId);
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, FabMedicalDataErrors.DATA_NOT_FOUND.toString());
            }
        }

        String platf = stub.getStringState(String.format("%s%03d", StateType.USER, Integer.valueOf(platform)));
        if (platf.isEmpty()) {
            String errorMessage = String.format("platform %s does not exist", platform);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabMedicalDataErrors.DATA_NOT_FOUND.toString());
        }
        User userPlatf = JSON.parseObject(platf, User.class);
        Integer point = Integer.parseInt(attNum) * Integer.parseInt(num);

        MedicalData medicalData = new MedicalData(getMaxMedicalDataId(), userPlatf, Arrays.asList(individualList),
                describe, permission, url, keys, point);
        stub.putStringState(String.format("%s%03d", StateType.MEDICAL_DATA, Integer.valueOf(medicalData.getId())),
                JSON.toJSONString(medicalData));

        return medicalData.getId();
    }

    @Transaction()
    public String requestMedicalData(final Context ctx, final String dataId, final String userId) {
        ChaincodeStub stub = ctx.getStub();

        // ??????????????????
        String medicalDataState = stub.getStringState(String.format("%s%03d", StateType.MEDICAL_DATA,
                Integer.valueOf(dataId)));
        if (medicalDataState.isEmpty()) {
            String errorMessage = String.format("Medical data %s does not exist", dataId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabMedicalDataErrors.DATA_NOT_FOUND.toString());
        }
        MedicalData medicalData = JSON.parseObject(medicalDataState, MedicalData.class);
        Integer point = medicalData.getPoint();

        // ???????????????
        String userState = stub.getStringState(String.format("%s%03d", StateType.USER, Integer.valueOf(userId)));
        if (userState.isEmpty()) {
            String errorMessage = String.format("user %s does not exist", userId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabMedicalDataErrors.DATA_NOT_FOUND.toString());
        }
        User user = JSON.parseObject(userState, User.class);

        // ????????????????????????
        if (user.getPoint() < point) {
            String errorMessage = String.format("user %s does not have enough point %d", userId, user.getPoint());
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabMedicalDataErrors.USER_NOT_ENOUGH_POINT.toString());
        }

        // ?????????????????????
        User platform = medicalData.getPlatform();
        List<String> individualIds = medicalData.getIndividualIds();

        // ????????????????????????????????????
        user.decreasePoint(point);
        platform.increasePoint(point);
        stub.delState(String.format("%s%03d", StateType.USER, Integer.valueOf(user.getId())));
        stub.delState(String.format("%s%03d", StateType.USER, Integer.valueOf(platform.getId())));
        stub.putStringState(String.format("%s%03d", StateType.USER, Integer.valueOf(user.getId())),
                JSON.toJSONString(user));
        stub.putStringState(String.format("%s%03d", StateType.USER, Integer.valueOf(platform.getId())),
                JSON.toJSONString(platform));

        // ????????????????????????
        for (String individualId: individualIds) {
            String individualStr = stub.getStringState(String.format("%s%03d", StateType.USER,
                    Integer.valueOf(individualId)));
            User individual = JSON.parseObject(individualStr, User.class);
            individual.increasePoint(point);
            stub.delState(String.format("%s%03d", StateType.USER, Integer.valueOf(individual.getId())));
            stub.putStringState(String.format("%s%03d", StateType.USER, Integer.valueOf(individual.getId())),
                    JSON.toJSONString(individual));
        }

        // ??????????????????
        Date date = new Date(); // this object contains the current date value
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SharingRecord sharingRecord = new SharingRecord(getMaxSharingRecordId(), medicalData.getId(), user,
                platform, individualIds, formatter.format(date), point);
        stub.putStringState(String.format("%s%03d", StateType.SHARING_RECORD, Integer.valueOf(sharingRecord.getId())),
                JSON.toJSONString(sharingRecord));

        return JSON.toJSONString(medicalData);
    }

    /**
     * Retrieves every car between CAR0 and CAR999 from the ledger.
     *
     * @param ctx the transaction context
     * @return array of Cars found on the ledger
     */
    @Transaction()
    public String queryAllMedicalData(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        Logger log = Logger.getLogger("testLog");

        System.out.print("entry queryAllMedicalData function");
        log.info("entry queryAllMedicalData function");
        final String startKey = "MEDICAL_DATA000";
        final String endKey = "MEDICAL_DATA999";
        List<MedicalData> medicalDatas = new ArrayList<MedicalData>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);
        System.out.printf("queryAllMedicalData results: %s", results);
        log.info("queryAllMedicalData results" + results);

        for (KeyValue result: results) {
            System.out.printf("print result: %s", result);
            log.info("print result: " + result);
            MedicalData medicalData = JSON.parseObject(result.getStringValue(), MedicalData.class);
            System.out.printf("print medical data: %s", medicalData);
            log.info("print medical data: " + medicalData);
            medicalDatas.add(medicalData);
        }

        return JSON.toJSONString(medicalDatas);
    }

    @Transaction()
    public List<String> querySharingRecordByUserId(final Context ctx, final String userId, final String isRequester) {
        Logger log = Logger.getLogger("testLog");
        ChaincodeStub stub = ctx.getStub();

        // ??????user
        int userIdInteger = Integer.parseInt(userId);
        String userString = stub.getStringState(String.format("%s%03d", StateType.USER, Integer.valueOf(userId)));
        User user = JSON.parseObject(userString, User.class);
        String queryString = "";
        if (isRequester.equals("true")) {
            queryString = String.format("{\"selector\":{\"type\":\"SHARING_RECORD\",\"requester.id\":\"%d\"}}",
                    userIdInteger);
        } else if (user.getUserType() == User.UserType.INDIVIDUAL) {
            queryString = String.format("{\"selector\":{\"type\":\"SHARING_RECORD\",\"ownIndividual.id\":\"%d\"}}",
                    userIdInteger);
        } else {
            queryString = String.format("{\"selector\":{\"type\":\"SHARING_RECORD\",\"ownPlatform.id\":\"%d\"}}",
                    userIdInteger);
        }
        log.info(queryString);

        QueryResultsIterator<KeyValue> results = stub.getQueryResult(queryString);

        List<String> sharingRecords = new ArrayList<>();
        for (KeyValue result: results) {
            log.info("print querySharingRecordByUserId result: " + result);
            SharingRecord sharingRecord = JSON.parseObject(result.getValue(), SharingRecord.class);
            log.info("print SharingRecord: " + sharingRecord);
            sharingRecords.add(sharingRecord.toString());
        }
        String response = JSON.toJSONString(sharingRecords);
        return sharingRecords;
    }

}

