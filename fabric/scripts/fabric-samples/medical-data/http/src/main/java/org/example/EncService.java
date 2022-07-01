package org.example;

import co.junwei.cpabe.Cpabe;

public class EncService {

    final static Cpabe test = new Cpabe();

    static String dir = "enc";

    static String pubfile = dir + "/pub_key";
    static String mskfile = dir + "/master_key";

    public EncService() throws Exception {
        setup();
    }

    public void setup() throws Exception{
        test.setup(pubfile, mskfile);
    }

    public String keygen(String attr_str) throws Exception{
        return test.keygen(pubfile, mskfile, attr_str);
    }

    public String encMedicalData(String highTextFile, String mediumTextFile, String highEncFile,
                                 String mediumEncFile, String policy) throws Exception {
        return test.enc(pubfile, policy, highTextFile, mediumTextFile, highEncFile, mediumEncFile);
    }

    public String decMedicalData(String priKey, String highEncFile, String mediumEncFile, String highDecFile,
                                      String mediumDecFile, String keyEnc) throws Exception {
        return test.dec(pubfile, priKey, highEncFile, mediumEncFile, highDecFile, mediumDecFile, keyEnc);
    }
}
