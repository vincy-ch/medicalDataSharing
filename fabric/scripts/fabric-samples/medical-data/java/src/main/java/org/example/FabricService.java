/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class FabricService {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");

	}

	Contract contract;

	public static void enrollAdmin() throws Exception {
		// Create a CA client for interacting with the CA.
		Properties props = new Properties();
		props.put("pemFile",
				"../../first-network/crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem");
		props.put("allowAllHostNames", "true");
		HFCAClient caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
		CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		caClient.setCryptoSuite(cryptoSuite);

		// Create a wallet for managing identities
		Wallet wallet = Wallet.createFileSystemWallet(Paths.get("wallet"));

		// Check to see if we've already enrolled the admin user.
		boolean adminExists = wallet.exists("admin");
		if (adminExists) {
			System.out.println("An identity for the admin user \"admin\" already exists in the wallet");
			return;
		}

		// Enroll the admin user, and import the new identity into the wallet.
		final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
		enrollmentRequestTLS.addHost("localhost");
		enrollmentRequestTLS.setProfile("tls");
		Enrollment enrollment = caClient.enroll("admin", "adminpw", enrollmentRequestTLS);
		Wallet.Identity user = Wallet.Identity.createIdentity("Org1MSP", enrollment.getCert(), enrollment.getKey());
		wallet.put("admin", user);
		System.out.println("Successfully enrolled user \"admin\" and imported it into the wallet");
	}

	public static void registerUser() throws Exception {

		// Create a CA client for interacting with the CA.
		Properties props = new Properties();
		props.put("pemFile",
				"../../first-network/crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem");
		props.put("allowAllHostNames", "true");
		HFCAClient caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
		CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		caClient.setCryptoSuite(cryptoSuite);

		// Create a wallet for managing identities
		Wallet wallet = Wallet.createFileSystemWallet(Paths.get("wallet"));

		// Check to see if we've already enrolled the user.
		boolean userExists = wallet.exists("user1");
		if (userExists) {
			System.out.println("An identity for the user \"user1\" already exists in the wallet");
			return;
		}

		userExists = wallet.exists("admin");
		if (!userExists) {
			System.out.println("\"admin\" needs to be enrolled and added to the wallet first");
			return;
		}

		Wallet.Identity adminIdentity = wallet.get("admin");
		org.hyperledger.fabric.sdk.User admin = new User() {

			@Override
			public String getName() {
				return "admin";
			}

			@Override
			public Set<String> getRoles() {
				return null;
			}

			@Override
			public String getAccount() {
				return null;
			}

			@Override
			public String getAffiliation() {
				return "org1.department1";
			}

			@Override
			public Enrollment getEnrollment() {
				return new Enrollment() {

					@Override
					public PrivateKey getKey() {
						return adminIdentity.getPrivateKey();
					}

					@Override
					public String getCert() {
						return adminIdentity.getCertificate();
					}
				};
			}

			@Override
			public String getMspId() {
				return "Org1MSP";
			}

		};

		// Register the user, enroll the user, and import the new identity into the wallet.
		RegistrationRequest registrationRequest = new RegistrationRequest("user1");
		registrationRequest.setAffiliation("org1.department1");
		registrationRequest.setEnrollmentID("user1");
		String enrollmentSecret = caClient.register(registrationRequest, admin);
		Enrollment enrollment = caClient.enroll("user1", enrollmentSecret);
		Wallet.Identity user = Wallet.Identity.createIdentity("Org1MSP", enrollment.getCert(), enrollment.getKey());
		wallet.put("user1", user);
		System.out.println("Successfully enrolled user \"user1\" and imported it into the wallet");
	}

	public FabricService(String user) throws IOException {
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
		return medicalData.toString();
	}

	public String requestMedicalData(String dataId, String userId) throws Exception {
		byte[] result;
		result = contract.submitTransaction("requestMedicalData", dataId, userId);
		System.out.println(new String(result));
		return (new String(result));
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
