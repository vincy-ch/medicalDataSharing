package co.junwei.cpabe;

public class Demo {
	final static boolean DEBUG = true;

	static String dir = "fabric/scripts/fabric-samples/medical-data/cpabe-master/demo/cpabe";

    static String pubfile = dir + "/pub_key";
	static String mskfile = dir + "/master_key";

	static String highInputfile = dir + "/high_input.txt";
	static String mediumInputfile = dir + "/medium_input.txt";
	static String highEncfile = dir + "/high_enc";
	static String mediumEncfile = dir + "/medium_enc";
	static String highDecfile_user1 = dir + "/high_dec_user1.txt";
	static String highDecfile_user2 = dir + "/high_dec_user2.txt";
	static String mediumDecfile_user1 = dir + "/medium_dec_user1.txt";
	static String mediumDecfile_user2 = dir + "/medium_dec_user2.txt";

	public static void main(String[] args) throws Exception {

		// user1
		String satisfy_both_attr = "cn:student2 uid:student2 title:student cn:student3";
		// user2
		String satisfy_medium_attr = "cn:student2 uid:student2 cn:student3";

		String policy = "uid:student2 cn:student2 cn:student3 3of3 title:student 2of2";

		Cpabe test = new Cpabe();
		test.setup(pubfile, mskfile);

		System.out.println("----------user1-----------");
		String satisfy_both_priKey = test.keygen(pubfile, mskfile, satisfy_both_attr);
		String satisfy_both_encKey = test.enc(pubfile, policy, highInputfile, mediumInputfile, highEncfile, mediumEncfile);
		test.dec(pubfile, satisfy_both_priKey, highEncfile, mediumEncfile, highDecfile_user1, mediumDecfile_user1, satisfy_both_encKey);

		System.out.println("----------user2-----------");
		String satisfy_medium_priKey = test.keygen(pubfile, mskfile, satisfy_medium_attr);
		String satisfy_medium_encKey = test.enc(pubfile, policy, highInputfile, mediumInputfile, highEncfile, mediumEncfile);
		test.dec(pubfile, satisfy_medium_priKey, highEncfile, mediumEncfile, highDecfile_user2, mediumDecfile_user2, satisfy_medium_encKey);
	}

}
