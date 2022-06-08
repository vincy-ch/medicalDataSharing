/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSON;
import org.hyperledger.fabric.gateway.*;

public class ClientApp {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");

	}

	static String student_attr = "objectClass:inetOrgPerson objectClass:organizationalPerson "
			+ "sn:student2 cn:student2 uid:student2 userPassword:student2 "
			+ "ou:idp o:computer mail:student2@sdu.edu.cn title:student2";
	static String policy = "sn:student2 cn:student2 uid:student2 3of3 an:student2 1of2";

        static String dir = "enc";
	static String priFile = dir + "/priFile";
	static String highTextFile = dir + "/high.xlsx";
	static String mediumTextFile = dir + "/medium.png";
	static String highEncFile = dir + "/high_enc.xlsx";
	static String mediumEncFile = dir + "/medium_enc.png";
	static String highDecFile = dir + "/high_dec.xlsx";
	static String mediumDecFile = dir + "/medium_dec.png";

        static String textFile = dir + "/表1_糖尿病_example.xlsx";
        static String encFile = dir + "/表2_糖尿病_example.xlsx";
        static String decFile = dir + "/表3_糖尿病_example.xlsx";

	Contract contract;

	public static void main(String[] args) throws Exception {
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallet.createFileSystemWallet(walletPath);

		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "first-network", "connection-org1.yaml");

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "user1").networkConfig(networkConfigPath).discovery(true);

		// create a gateway connection
		try (Gateway gateway = builder.connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("medical-data");

			byte[] result;

			result = contract.evaluateTransaction("queryAllMedicalData");
			System.out.println(new String(result));


		//	Enc.setup();
		//	Enc.keygen(student_attr, priFile);
		//	String encKey = Enc.enc(textFile, encFile, policy);


		//	result = contract.submitTransaction("createMedicalData", "00", "00", "在我院呼吸科就诊的COPD患者（年龄大于18），人数216人，数据包括症状描述，用药情况，住院费用等",
		//			"中敏感：医院或保险公司；低敏感：所有人", "39.16.57.1:/data/001", encKey);
		//	System.out.println(new String(result));

//			result = contract.evaluateTransaction("queryAllMedicalData");
//			System.out.println(new String(result));

		//	result = contract.evaluateTransaction("queryMedicalDataById", "MEDICAL_DATA001");
		//	System.out.println(new String(result));
		//	MedicalData medicalData = JSON.parseObject(result, MedicalData.class);
		//	System.out.println(medicalData.getKeys());
		//	Enc.dec(priFile, encFile, decFile, medicalData.getKeys());
		}
	}

	public ClientApp(String user) throws IOException {
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallet.createFileSystemWallet(walletPath);

		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "first-network", "connection-org1.yaml");

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "user1").networkConfig(networkConfigPath).discovery(true);

		// create a gateway connection
		Gateway gateway = builder.connect();

		// get the network and contract
		Network network = gateway.getNetwork("mychannel");
		this.contract = network.getContract("medical-data");
	}

	public String queryAllMedicalData() throws ContractException {
		byte[] result;

		result = contract.evaluateTransaction("queryAllMedicalData");
		System.out.println(new String(result));
		List<MedicalData> medicalData = JSONArray.parseArray(new String(result), MedicalData.class);
		return new String(medicalData.toString());	
	}

	public static String priKeyGen(String attr) throws Exception {
		return Enc.keygen(attr, priFile);
	}

	public String requestMedicalData(String dataId, String userId) throws Exception {
		byte[] result;
		result = contract.submitTransaction("requestMedicalData", dataId, userId);
		System.out.println(new String(result));
		return (new String(result));
	}

	public String encMedicalData(String highTextFile, String mediumTextFile, String highEncFile,
								 String mediumEncFile, String policy) throws Exception {
		String encKey = Enc.enc(highTextFile, mediumTextFile, highEncFile, mediumEncFile, policy);
		return encKey;
	}

	public String createMedicalData(String encKey, String highEncFile, String mediumEncFile, String platformId,
									String individualId, String describe, String policy, String attNum, String num)
			throws Exception {

		byte[] result;
		result = contract.submitTransaction("createMedicalData", platformId, individualId, describe,
				policy, highEncFile + "+" + mediumEncFile, encKey, attNum, num);
		System.out.println(new String(result));

		return (new String(result));
	}

	public String createUser(String userName, String userType) throws Exception {
		byte[] result;
		result = contract.submitTransaction("createUser", userName, userType);
		System.out.println(new String(result));

		return (new String(result));
	}

	public String queryMedicalDataById(String id) throws Exception {
		byte[] result;

		result = contract.evaluateTransaction("queryMedicalDataById", id);
		System.out.println(new String(result));
		MedicalData medicalData = JSON.parseObject(result, MedicalData.class);

		return new String(result);
	}

	public String decMedicalData(String highEncFile, String mediumEncFile, String highDecFile, String mediumDecFile,
								 String priKey, String encKey) throws Exception {
		int decSuccess = Enc.dec(priKey, highEncFile, mediumEncFile, highDecFile, mediumDecFile, encKey);
//		InputStream mediumData = new FileInputStream(mediumDecFile);
//		InputStream highData = new FileInputStream(highEncFile);
//		byte[] content = new byte[mediumData.available() + highData.available()];
		String result = "";
		result += "低敏感数据解密：";
		if (decSuccess % 10 == 1) {
//			mediumData.read(content);
//			mediumData.close();
			result += "成功";
		} else {
			result += "失败";
		}
		result += "\n中敏感数据解密：";
		if (decSuccess / 10 == 1) {
//			highData.read(content);
//			highData.close();
			result += "成功";
		} else {
			result += "失败";
		}
		return result;
	}

	public String querySharingRecordByUserId(String userId, String isRequester) throws Exception {
		byte[] result;

		result = contract.evaluateTransaction("querySharingRecordByUserId", userId, isRequester);
		System.out.println(new String(result));

		return new String(result);
	}

	public String queryPointById(String userId) throws Exception {
		byte[] result;

		result = contract.evaluateTransaction("queryPointById", userId);
		System.out.println(new String(result));

		return new String(result);
	}
}
