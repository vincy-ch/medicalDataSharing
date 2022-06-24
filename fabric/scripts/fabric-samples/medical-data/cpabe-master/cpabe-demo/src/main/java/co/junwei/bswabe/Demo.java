package co.junwei.bswabe;

public class Demo {
	final static boolean DEBUG = true;

	final static String dir = "medical-data/cpabe-master/demo/bswabe";

	final static String inputfile = dir + "/input.txt";
	final static String encfile = dir + "input.txt.cpabe";
	final static String decfile = dir + "input.txt.new";

	/* come test data, choose attr and policy */
	/* TODO attr is alphabetic order */
	static String[] attr = { "baf", "fim1", "fim", "foo" };
	static String[] attr_delegate_ok = {"fim", "foo", "baf"};
	static String[] attr_delegate_ko = {"fim"};
	static String policy = "foo bar fim 2of3 baf 2of2";


	public static void main(String[] args) throws Exception {
		BswabePub pub = new BswabePub();
		BswabeMsk msk = new BswabeMsk();
		BswabePrv prv, prv_delegate_ok, prv_delegate_ko;
		BswabeCph cph;
		BswabeElementBoolean result;

		println("// demo for co.junwei.bswabe: start to setup");
		Bswabe.setup(pub, msk);
		println("// demo for co.junwei.bswabe: end to setup");

		println("\n// demo for co.junwei.bswabe: start to keygen");
		prv = Bswabe.keygen(pub, msk, attr);
		println("// demo for co.junwei.bswabe: end to keygen");

		println("\n// demo for co.junwei.bswabe: start to delegate_ok");
		prv_delegate_ok = Bswabe.delegate(pub, prv, attr_delegate_ok);
		println("// demo for co.junwei.bswabe: end to delegate_ok");

		println("\n// demo for co.junwei.bswabe: start to delegate_ko");
		prv_delegate_ko = Bswabe.delegate(pub, prv, attr_delegate_ko);
		println("// demo for co.junwei.bswabe: end to delegate_ko");

		println("\n// demo for co.junwei.bswabe: start to enc");
		BswabeCphKey encrypted = Bswabe.enc(pub, policy);
		cph = encrypted.cph;
		println("// demo for co.junwei.bswabe: end to enc");

		println("\n// demo for co.junwei.bswabe: start to dec");
		result = Bswabe.dec(pub, prv, cph);
		println("// demo for co.junwei.bswabe: end to dec");
		if ((result.bRoot) && (result.eRoot.equals(encrypted.rootKey)))
			System.out.println("succeed in root decrypt");
		else
			System.err.println("failed to decrypting root");

		println("\n// demo for co.junwei.bswabe: start to dec with ok delegated key");
		result = Bswabe.dec(pub, prv_delegate_ok, cph);
		println("// demo for co.junwei.bswabe: end to dec with ok delegated key");
		if ((result.bRoot) && (result.eRoot.equals(encrypted.rootKey))) {
			System.out.println(result.eRoot);
		    System.out.println("succeed in root decrypt with ok delegated key");}
		else
		    System.err.println("failed to decrypting root with ok delegated key");

		println("\n//demo for co.junwei.bswabe: start to dec with ko delegated key");
		result = Bswabe.dec(pub, prv_delegate_ko, cph);
		println("//demo for co.junwei.bswabe: end to dec with ko delegated key");
		if ((result.bRoot) && (result.eRoot.equals(encrypted.rootKey))) {
			System.out.println(result.eRoot);
			System.err.println("succeed in root decrypt with ko delegated key");}
		else
			System.out.println("failed to decrypting root with ko delegated key");
	}

	private static void println(Object o) {
		if (DEBUG)
			System.out.println(o);
	}
}
