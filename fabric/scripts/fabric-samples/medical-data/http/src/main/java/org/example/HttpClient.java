package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Http请求工具类
 * @author snowfigure
 * @since 2014-8-24 13:30:56
 * @version v1.0.1
 */
public class HttpClient {
    static boolean proxySet = false;
    static String proxyHost = "127.0.0.1";
    static int proxyPort = 8888;

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param isproxy
     *               是否使用代理模式
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param, boolean isproxy) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = null;
            if(isproxy){//使用代理模式
                @SuppressWarnings("static-access")
                Proxy proxy = new Proxy(Proxy.Type.DIRECT.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                conn = (HttpURLConnection) realUrl.openConnection(proxy);
            }else{
                conn = (HttpURLConnection) realUrl.openConnection();
            }

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");    // POST方法


            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            conn.connect();

            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.write("\r\n");
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String dir = "encFile";
        String highTextFile = dir + "/high.xlsx";
        String mediumTextFile = dir + "/medium.png";
        String highEncFile = dir + "/high_enc.xlsx";
        String mediumEncFile = dir + "/medium_enc.png";
        String highDecFile = dir + "/high_dec.xlsx";
        String mediumDecFile = dir + "/medium_dec.png";
        String encKey = "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";
        String priKey = "AAAAgGde8iH8i2RNqPj+RNWPkuFVpNfMGMPo0IQeSAl/G2FDPQX5pl7bTzPTvR3QYgYaKWM/PlqE8CtA+PPOoBhwaFWFfdT3wGpoi64n96mR/7+rHMY/xC4Ha9leb3FeEHEQxCn+RdBjYZSJsangKhpwzoguhPNZA8hRgkSQ6xZoVp3oAAAAAgAAAApIb3NwaXRhbDoxAAAAgGrWCD5mb69xrI53kNC7FoaaFgqhSwURtsgGR/6Ww8zB7ZZw0J9+bN5CxmEUqicYDPMwEYXahBxM4D+6Cypz/3N3cg6Tb7hly93++syTBi3PMD+yMRL3uPew5jcR1/rdwOk6paLCiMUAELSueLZSiwnKGK3YCEh484ZcGRQ2CpYeAAAAgITVRdMnng2Byld8LVNzvTyHkFRF/HbdMCCSmYecMmnNT9Ph7SmWnWNcfNolFoUKSqdOEoSV+RCQFcZo8U1n2vwm/zT30hUoDmjWMR/aEJ+urp7qe6raDYUKzeGXdh3btl519IryziP4bOASv0inWVywXGkZG/BSiGNJeNv/l5Y/AAAAA0E6MQAAAIB/y00h3TvAUSVyb7AS+ezXRmHJ7Yejz83fOcIgnTEunLRfSIqq6fDpyUkuNceX2jLDgpE1ljBA3Zdc5vx9LhBLDlC8AvM29C//fLi2nk+nmrZunlZpKCRxjt82lJ5DleN4u0dM3330amretX9qBaxNvzng7kze43smi+ptrpLemAAAAICAe+TkGZli6UJjVrZ3EAyhMqm/sVYf+1WKV99os++ucGrB19WOygxfcIXFk5bglbOXLUqzfas04K4V02TL7LrRojj517ytAsUFJAmLAz2JeONFjt7duE4vWv/pbdRQOTNafPiIJpfN+fqsgNTyByub1RSrSCbm9PwyQtnc+iDQig==";

        String res = "";
        String url = "http://39.96.201.238:8888";

        /*---------------区块链登录管理员-------------*/
        String enrollAdminParams = "method=enrollAdmin";
        res = HttpClient.sendPost(url,enrollAdminParams,false);
        System.out.println(res);

        /*----------------注册区块链用户--------------*/
        String registerUserParams = "method=registerUser";
        res = HttpClient.sendPost(url,registerUserParams,false);
        System.out.println(res);

        /*--------------注册医疗数据共享用户------------*/
        String createUserParams =
                "method=createUser" +
                        "&createUserName=北京大学第三医院" +
                        "&userType=PLATFORM";
        res = HttpClient.sendPost(url,createUserParams,false);
        System.out.println(res);

        /*----------------分级加密医疗数据--------------*/
        String encMedicalDataParams =
                "method=encMedicalData" +
                        "&highTextFile=" + highTextFile +
                        "&mediumTextFile=" + mediumTextFile +
                        "&highEncFile=" +highEncFile +
                        "&mediumEncFile=" + mediumEncFile +
                        "&policy=Research-Institute:1 Insurance-Company:1 Hospital:1 1of3 A:1 2of2";
        encKey = HttpClient.sendPost(url,encMedicalDataParams,false);
        System.out.println(encKey);

        /*-----------------注册医疗数据---------------*/
        StringBuilder individualIds = new StringBuilder("0");
        for(int i = 1; i < 30; i++) {
            individualIds.append(" ");
            individualIds.append(i);
        }
        String createMedicalDataParams =
                "method=createMedicalData" +
                        "&encKey=" + encKey +
                        "&highEncFile=" +highEncFile +
                        "&mediumEncFile=" + mediumEncFile +
                        "&platformId=100" +
                        "&individualId=" + individualIds +
                        "&describe=在我院呼吸科就诊的COPD患者（年龄>=18），人数30人，数据包括症状描述，用药情况，住院费用等" +
                        "&policy=Research-Institute Insurance-Company Hospital 1of3 A 2of2" +
                        "&attNum=5"+
                        "&num=30";
        res = HttpClient.sendPost(url,createMedicalDataParams,false);
        System.out.println(res);

        /*----------------查询所有医疗数据--------------*/
        String queryAllMedicalDataParams = "method=queryAllMedicalData";
        res = HttpClient.sendPost(url,queryAllMedicalDataParams,false);
        System.out.println(res);

        /*----------------按ID查询医疗数据--------------*/
        String queryMedicalDataByIdParams =
                "method=queryMedicalDataById" +
                        "&dataId=001";
        res = HttpClient.sendPost(url,queryMedicalDataByIdParams,false);
        System.out.println(res);

        /*----------------请求获取医疗数据--------------*/
        String requestMedicalDataByIdParams =
                "method=requestMedicalDataById" +
                        "&dataId=000" +
                        "&userId=001";
        res = HttpClient.sendPost(url,requestMedicalDataByIdParams,false);
        System.out.println(res);

        /*------------------用户申请密钥----------------*/
        String priKeyGenParams =
                "method=priKeyGen" +
                        "&attr=Hospital:1 A:1";
        priKey = HttpClient.sendPost(url,priKeyGenParams,false);
        System.out.println(priKey);

        /*----------------用户解密医疗数据--------------*/
        String decMedicalDataParams =
                "method=decMedicalData" +
                        "&highEncFile=" + highEncFile +
                        "&mediumEncFile=" + mediumEncFile +
                        "&highDecFile=" + highDecFile +
                        "&mediumDecFile=" + mediumDecFile +
                        "&priKey=" + priKey +
                        "&encKey=" + encKey;
        res = HttpClient.sendPost(url,decMedicalDataParams,false);
        System.out.println(res);

        /*--------------用户查询医疗数据共享情况------------*/
        String querySharingRecordByUserIdParams =
                "method=querySharingRecordByUserId" +
                        "&userId=001" +
                        "&isRequester=false";
        System.out.println(querySharingRecordByUserIdParams);
        res = HttpClient.sendPost(url,querySharingRecordByUserIdParams,false);
        System.out.println(res);

        /*--------------查询用户剩余贡献点------------*/
        String queryPointByIdParams =
                "method=queryPointById" +
                        "&userId=001";
        System.out.println(queryPointByIdParams);
        res = HttpClient.sendPost(url,queryPointByIdParams,false);
        System.out.println(res);
    }
}
