package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    static FabricService fabricService = null;
    static EncService encService;

    static {
        try {
            encService = new EncService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            ServerSocket ss=new ServerSocket(8888);
            while(true){
                Socket socket=ss.accept();
                BufferedReader bd=new BufferedReader(new InputStreamReader(socket.getInputStream()));

                /**
                 * 接受HTTP请求
                 */
                String requestHeader;
                while((requestHeader=bd.readLine())!=null&&!requestHeader.isEmpty()){
                    System.out.println(requestHeader);
                    /**
                     * 获得GET参数
                     */
                    if(requestHeader.startsWith("GET")){
                        int begin = requestHeader.indexOf("GET")+3;
                        int end = requestHeader.indexOf("HTTP/");
                        String condition=requestHeader.substring(begin, end);
                        System.out.println("GET参数是："+condition);
                    }
                    /**
                     * 获得POST参数
                     * 1.获取请求内容长度
                     */
                    if(requestHeader.startsWith("Content-Length")){
                        System.out.println("这是post");
                        int begin=requestHeader.indexOf("Content-Length:")+"Content-Length:".length();
                        String postParamterLength=requestHeader.substring(begin).trim();
                        System.out.println("POST参数长度是："+Integer.parseInt(postParamterLength));
                    }
                }
                String result = null;
                String str = "";
                if((str = bd.readLine()) != null) {
                    str = str.trim();
                    System.out.println("收到客户端消息：" + str);
                    result = handlePost(str);
                }

                //发送回执
                PrintWriter pw=new PrintWriter(socket.getOutputStream());

                pw.println("HTTP/1.1 200 OK");
                pw.println("Content-type:text/html");
                pw.println();
                if (result != null) {
                    pw.println(result);
                }

                pw.flush();
                System.out.println("返回客户端");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String handlePost(String params) throws Exception {
        String[] paramList = params.split("&");
        String method = paramList[0].substring(paramList[0].indexOf("=") + 1).trim();
        System.out.printf("method: %s\n", method);

        switch (method) {
            case "enrollAdmin": {
                FabricService.enrollAdmin();
                return "success";
            }
            case "registerUser": {
                FabricService.registerUser();
                fabricService = new FabricService("user1");
                return "success";
            }
            case "queryAllMedicalData": {
                return fabricService.queryAllMedicalData();
            }
            case "createUser": {
                String createUserName = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();
                String userType = paramList[2].substring(paramList[2].indexOf("=") + 1).trim();
                return fabricService.createUser(createUserName, userType);
            }
            case "encMedicalData": {
                String highTextFile = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();
                String mediumTextFile = paramList[2].substring(paramList[2].indexOf("=") + 1).trim();
                String highEncFile = paramList[3].substring(paramList[3].indexOf("=") + 1).trim();
                String mediumEncFile = paramList[4].substring(paramList[4].indexOf("=") + 1).trim();
                String policy = paramList[5].substring(paramList[5].indexOf("=") + 1).trim();
                return "加密后的对称密钥为："+ encService.encMedicalData(highTextFile, mediumTextFile, highEncFile,
                                mediumEncFile, policy);
                }
            case "createMedicalData": {
                    String encKey = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();
                    String highEncFile = paramList[2].substring(paramList[2].indexOf("=") + 1).trim();
                    String mediumEncFile = paramList[3].substring(paramList[3].indexOf("=") + 1).trim();
                    String platformId = paramList[4].substring(paramList[4].indexOf("=") + 1).trim();
                    String individualId = paramList[5].substring(paramList[5].indexOf("=") + 1).trim();
                    String describe = paramList[6].substring(paramList[6].indexOf("=") + 1).trim();
                    String policy = paramList[7].substring(paramList[7].indexOf("=") + 1).trim();
                    String attNum = paramList[8].substring(paramList[8].indexOf("=") + 1).trim();
                    String num = paramList[9].substring(paramList[9].indexOf("=") + 1).trim();
                    try {
                        return "上传成功，数据ID为：" + fabricService.createMedicalData(encKey, highEncFile, mediumEncFile, platformId,
                                individualId, describe, policy, attNum, num);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
            case "priKeyGen": {
                String att = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();
                return "属性关联私钥为：" + encService.keygen(att);
            }
            case "queryMedicalDataById": {
                String dataId = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();
                try {
                    return fabricService.queryMedicalDataById("MEDICAL_DATA"+dataId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "requestMedicalDataById": {
                String dataId = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();
                String userId = paramList[2].substring(paramList[2].indexOf("=") + 1).trim();
                try {
                    return fabricService.requestMedicalData(dataId, userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "decMedicalData": {
                String highEncFile = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();
                String mediumEncFile = paramList[2].substring(paramList[2].indexOf("=") + 1).trim();
                String highDecFile = paramList[3].substring(paramList[3].indexOf("=") + 1).trim();
                String mediumDecFile = paramList[4].substring(paramList[4].indexOf("=") + 1).trim();
                String priKey = paramList[5].substring(paramList[5].indexOf("=") + 1).trim();
                String encKey = paramList[6].substring(paramList[6].indexOf("=") + 1).trim();
                try {
                    return encService.decMedicalData(highEncFile, mediumEncFile, highDecFile, mediumDecFile,
                            priKey, encKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "querySharingRecordByUserId": {
                String userId = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();
                String isRequester = paramList[2].substring(paramList[2].indexOf("=") + 1).trim();
                return fabricService.querySharingRecordByUserId(userId, isRequester);
            }
            case "queryPointById": {
                String userId = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();
                try {
                    return "用户剩余贡献点为" + fabricService.queryPointById(userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return null;
    }
}
