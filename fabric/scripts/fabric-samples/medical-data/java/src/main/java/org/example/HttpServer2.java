package org.example;

import org.hyperledger.fabric.gateway.ContractException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpServer2 {
    static ClientApp clientApp = null;
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(250, 250, 200, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(700));
    public static void main(String[] args) throws Exception {
        try {
            ServerSocket ss=new ServerSocket(8887);

            while(true){
                Socket socket=ss.accept();
                BufferedReader bd=new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                /**
                 * 接受HTTP请求
                 */
                String requestHeader;
                int contentLength=0;
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
                        contentLength=Integer.parseInt(postParamterLength);
                        System.out.println("POST参数长度是："+Integer.parseInt(postParamterLength));
                    }
                }

                String str = "";
                if((str = bd.readLine()) != null) {
                    str = str.trim();
                    System.out.println("收到客户端消息：" + str);
                    HandlePost handlePost = new HandlePost(clientApp, str, socket);
                    executor.execute(handlePost);
                }
                String[] paramList = str.split("&");
                if (paramList[0].substring(paramList[0].indexOf("=") + 1).trim().equals("registerUser")) {
                   Thread.sleep(2000); 
                   clientApp = new ClientApp("user1");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   static class HandlePost implements Runnable {
       ClientApp clientApp;
       String params;
       Socket socket;

       public HandlePost(ClientApp clientApp, String params, Socket socket) {
           this.clientApp = clientApp;
           this.params = params;
           this.socket = socket;
       }

        @Override
       public void run(){
            String[] paramList = params.split("&");
            String method = paramList[0].substring(paramList[0].indexOf("=") + 1).trim();
            System.out.printf("method: %s\n", method);

            String result = null;
            switch (method) {
                case "enrollAdmin": {
                    try {
                        EnrollAdmin.main(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result = "success";
                    break;
                }
                case "registerUser": {
                    try {
                        RegisterUser.main(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result = "success";
                    break;
                }
                case "queryAllMedicalData": {
                    try {
                        result = clientApp.queryAllMedicalData();
                    } catch (ContractException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "createUser": {
                    String createUserName = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();
                    String userType = paramList[2].substring(paramList[2].indexOf("=") + 1).trim();
                    try {
                        result = clientApp.createUser(createUserName, userType);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
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
                        result = "上传成功，数据ID为：" + clientApp.createMedicalData(encKey, highEncFile, mediumEncFile, platformId,
                                individualId, describe, policy, attNum, num);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "priKeyGen": {
                    String att = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();
                    try {
                        result = ClientApp.priKeyGen(att);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "queryMedicalDataById": {
                    String dataId = paramList[1].substring(paramList[1].indexOf("=") + 1).trim();                   
                    try {
                       result = clientApp.queryMedicalDataById("MEDICAL_DATA"+dataId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    break;
                }
                case "requestMedicalDataById": {
                    String dataId = paramList[2].substring(paramList[2].indexOf("=") + 1).trim();
                    String userId = paramList[3].substring(paramList[3].indexOf("=") + 1).trim();
                    try {
                        result = clientApp.requestMedicalData(dataId, userId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "querySharingRecordByUserId": {
                    String userId = paramList[2].substring(paramList[2].indexOf("=") + 1).trim();
                    String isRequester = paramList[3].substring(paramList[3].indexOf("=") + 1).trim();
                    try {
                        result = clientApp.querySharingRecordByUserId(userId, isRequester);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:{
                }
            }

            //发送回执
            PrintWriter pw= null;
            try {
                pw = new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            pw.println("HTTP/1.1 200 OK");
            pw.println("Content-type:text/html");
            pw.println();
            if (result != null) {
                pw.println(result);
            }
            pw.flush();
            System.out.println("返回客户端");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
   }
}
