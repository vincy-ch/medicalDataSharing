package org.example;

import co.junwei.cpabe.Cpabe;

public class Enc {

    final static boolean DEBUG = true;
    final static Cpabe test = new Cpabe();

    static String dir = "enc";

    static String pubfile = dir + "/pub_key";
    static String mskfile = dir + "/master_key";

    public Enc() {
    }

    public static void setup() throws Exception{
        println("//start to setup");
        test.setup(pubfile, mskfile);
        println("//end to setup");
    }

    public static String keygen(String attr_str, String prvFile) throws Exception{
        println("//start to keygen");
        return test.keygen(pubfile, prvFile, mskfile, attr_str);
//        println("//end to keygen");

    }

    public static String enc(String highTextFile, String mediumTextFile, String highEncFile,
                             String mediumEncFile, String policy) throws Exception{
        println("//start to enc");
        String keyEnc = test.enc2(pubfile, policy, highTextFile, mediumTextFile, highEncFile, mediumEncFile);
        println("//end to enc");
        return keyEnc;
    }

    public static int dec(String priKey, String highEncFile, String mediumEncFile, String highDecFile,
                           String mediumDecFile, String keyEnc) throws Exception {
        println("//start to dec");
        int decSuccess = test.dec2(pubfile, priKey, highEncFile, mediumEncFile, highDecFile, mediumDecFile, keyEnc);
        println("//end to dec");
        return decSuccess;
    }

    /* connect element of array with blank */
    public static String array2Str(String[] arr) {
        int len = arr.length;
        String str = arr[0];

        for (int i = 1; i < len; i++) {
            str += " ";
            str += arr[i];
        }

        return str;
    }

    private static void println(Object o) {
        if (DEBUG)
            System.out.println(o);
    }
}
