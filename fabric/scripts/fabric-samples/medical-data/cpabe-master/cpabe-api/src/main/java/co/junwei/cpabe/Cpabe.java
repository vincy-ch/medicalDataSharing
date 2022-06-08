package co.junwei.cpabe;
import co.junwei.cpabe.policy.LangPolicy;
import it.unisa.dia.gas.jpbc.Element;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import co.junwei.bswabe.Bswabe;
import co.junwei.bswabe.BswabeCph;
import co.junwei.bswabe.BswabeCphKey;
import co.junwei.bswabe.BswabeElementBoolean;
import co.junwei.bswabe.BswabeMsk;
import co.junwei.bswabe.BswabePrv;
import co.junwei.bswabe.BswabePub;
import co.junwei.bswabe.SerializeUtils;

public class Cpabe {

	/**
	 * @param
	 * @author Junwei Wang(wakemecn@gmail.com)
	 */

	public void setup(String pubfile, String mskfile) throws IOException {
		byte[] pub_byte, msk_byte;
		BswabePub pub = new BswabePub();
		BswabeMsk msk = new BswabeMsk();
		Bswabe.setup(pub, msk);

		/* store BswabePub into mskfile */
		pub_byte = SerializeUtils.serializeBswabePub(pub);
		Common.spitFile(pubfile, pub_byte);

		/* store BswabeMsk into mskfile */
		msk_byte = SerializeUtils.serializeBswabeMsk(msk);
		Common.spitFile(mskfile, msk_byte);
	}

	public String keygen(String pubfile, String prvfile, String mskfile,
			String attr_str) throws NoSuchAlgorithmException, IOException {
		BswabePub pub;
		BswabeMsk msk;
		byte[] pub_byte, msk_byte, prv_byte;

		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile(pubfile);
		pub = SerializeUtils.unserializeBswabePub(pub_byte);

		/* get BswabeMsk from mskfile */
		msk_byte = Common.suckFile(mskfile);
		msk = SerializeUtils.unserializeBswabeMsk(pub, msk_byte);

		String[] attr_arr = LangPolicy.parseAttribute(attr_str);
		BswabePrv prv = Bswabe.keygen(pub, msk, attr_arr);

		/* store BswabePrv into prvfile */
		prv_byte = SerializeUtils.serializeBswabePrv(prv);
		return Base64.getEncoder().encodeToString(prv_byte);
//		Common.spitFile(prvfile, prv_byte);
	}

	public void enc(String pubfile, String policy, String inputfile,
			String encfile) throws Exception {
		BswabePub pub;
		BswabeCph cph;
		BswabeCphKey keyCph;
		byte[] plt;
		byte[] cphBuf;
		byte[] aesBuf;
		byte[] pub_byte;
		Element m;

		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile(pubfile);
		pub = SerializeUtils.unserializeBswabePub(pub_byte);

		keyCph = Bswabe.enc2(pub, policy);
		cph = keyCph.cph;
		m = keyCph.rootKey;
		System.err.println("m = " + m.toString());

		if (cph == null) {
			System.out.println("Error happed in enc");
			System.exit(0);
		}

		cphBuf = SerializeUtils.bswabeCphSerialize(cph);

		/* read file to encrypted */
		plt = Common.suckFile(inputfile);
		aesBuf = AESCoder.encrypt(m.toBytes(), plt);
		// PrintArr("element: ", m.toBytes());
		Common.writeCpabeFile(encfile, cphBuf, aesBuf);
	}

	public void dec(String pubfile, String prvfile, String encfile,
			String decfile) throws Exception {
		byte[] aesBuf, cphBuf;
		byte[] plt;
		byte[] prv_byte;
		byte[] pub_byte;
		byte[][] tmp;
		BswabeCph cph;
		BswabePrv prv;
		BswabePub pub;

		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile(pubfile);
		pub = SerializeUtils.unserializeBswabePub(pub_byte);

		/* read ciphertext */
		tmp = Common.readCpabeFile(encfile);
		aesBuf = tmp[0];
		cphBuf = tmp[1];
		cph = SerializeUtils.bswabeCphUnserialize(pub, cphBuf);

		/* get BswabePrv form prvfile */
		prv_byte = Common.suckFile(prvfile);
		prv = SerializeUtils.unserializeBswabePrv(pub, prv_byte);

		BswabeElementBoolean beb = Bswabe.dec(pub, prv, cph);
		System.err.println("e = " + beb.eRoot.toString());
		if (beb.bRoot) {
			plt = AESCoder.decrypt(beb.eRoot.toBytes(), aesBuf);
			Common.spitFile(decfile, plt);
		} else {
			System.exit(0);
		}
	}

	public String enc2(String pubfile, String policy, String highInputfile, String mediumInputfile,
					String highEncfile, String mediumEncfile) throws Exception {
		BswabePub pub;
		BswabeCph cph;
		BswabeCphKey keyCph;
		byte[] plt;
		byte[] cphBuf;
		byte[] aesBuf;
		byte[] pub_byte;
		Element rootKey, childKey;

		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile(pubfile);
		pub = SerializeUtils.unserializeBswabePub(pub_byte);

		keyCph = Bswabe.enc2(pub, policy);
		cph = keyCph.cph;
		rootKey = keyCph.rootKey;
		childKey = keyCph.childKey;

		if (cph == null) {
			System.out.println("Error happed in enc");
			System.exit(0);
		}

		cphBuf = SerializeUtils.bswabeCphSerialize(cph);

		if (policy.endsWith("2of2")) {
			// high文件是用root加密 medium文件是用child加密
			/* read file to encrypted */
			plt = Common.suckFile(highInputfile);
			aesBuf = AESCoder.encrypt(rootKey.toBytes(), plt);
			Common.writeCpabeFile(highEncfile, cphBuf, aesBuf);

			plt = Common.suckFile(mediumInputfile);
			aesBuf = AESCoder.encrypt(childKey.toBytes(), plt);
			Common.writeCpabeFile(mediumEncfile, cphBuf, aesBuf);
		} else {
			// high文件是用child加密 medium文件是用root加密
			/* read file to encrypted */
			plt = Common.suckFile(highInputfile);
			aesBuf = AESCoder.encrypt(childKey.toBytes(), plt);
			Common.writeCpabeFile(highEncfile, cphBuf, aesBuf);

			plt = Common.suckFile(mediumInputfile);
			aesBuf = AESCoder.encrypt(rootKey.toBytes(), plt);
			Common.writeCpabeFile(mediumEncfile, cphBuf, aesBuf);
		}

		//Base64 Encoded
		String keyEnc = Base64.getEncoder().encodeToString(cphBuf);

		return keyEnc;
	}

	public int dec2(String pubfile, String priKey, String highEncfile, String mediumEncfile,
					String highDecfile, String mediumDecfile, String keyEnc) throws Exception {
		byte[] aesBuf, cphBuf;
		byte[] plt;
		byte[] prv_byte;
		byte[] pub_byte;
		byte[][] tmp;
		BswabeCph cph;
		BswabePrv prv;
		BswabePub pub;
		boolean[] res = new boolean[2];

		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile(pubfile);
		pub = SerializeUtils.unserializeBswabePub(pub_byte);

		/* read cph buf */
		//Base64 Decoded
		cphBuf = Base64.getDecoder().decode(keyEnc);
		cph = SerializeUtils.bswabeCphUnserialize(pub, cphBuf);

		/* get BswabePrv form prvfile */
//		prv_byte = Common.suckFile(prvfile);
		prv_byte = Base64.getDecoder().decode(priKey);
		prv = SerializeUtils.unserializeBswabePrv(pub, prv_byte);

		BswabeElementBoolean beb = Bswabe.dec2(pub, prv, cph);

		if (cph.p.k == 2) {
			// root是high文件的加密key child是medium文件的加密key
			if (beb.bChild) {
				/* read ciphertext */
				tmp = Common.readCpabeFile(mediumEncfile);
				aesBuf = tmp[0];
				plt = AESCoder.decrypt(beb.eChild.toBytes(), aesBuf);
				Common.spitFile(mediumDecfile, plt);
				res[0] = true;
			} else {
				System.out.println("can't resolve medium file");
			}

			if (beb.bRoot) {
				/* read ciphertext */
				tmp = Common.readCpabeFile(highEncfile);
				aesBuf = tmp[0];
				plt = AESCoder.decrypt(beb.eRoot.toBytes(), aesBuf);
				Common.spitFile(highDecfile, plt);
				res[1] = true;
			} else {
				System.out.println("can't resolve high file");
			}
		} else {
			// root是medium文件的加密key child是high文件的加密key
			if (beb.bChild) {
				/* read ciphertext */
				tmp = Common.readCpabeFile(highEncfile);
				aesBuf = tmp[0];
				plt = AESCoder.decrypt(beb.eChild.toBytes(), aesBuf);
				Common.spitFile(highDecfile, plt);
				res[1] = true;
			} else {
				System.out.println("can't resolve medium file");
			}

			if (beb.bRoot) {
				/* read ciphertext */
				tmp = Common.readCpabeFile(mediumEncfile);
				aesBuf = tmp[0];
				plt = AESCoder.decrypt(beb.eRoot.toBytes(), aesBuf);
				Common.spitFile(mediumDecfile, plt);
				res[0] = true;
			} else {
				System.out.println("can't resolve high file");
			}
		}

		int result = 0;
		if(res[0]) result += 1;
		if(res[1]) result += 10;
		return result;
	}

}

