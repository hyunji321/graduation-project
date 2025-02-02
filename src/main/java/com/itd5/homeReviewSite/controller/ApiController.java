package com.itd5.homeReviewSite.controller;

import com.itd5.homeReviewSite.Service.S3UploadService;
import net.minidev.json.JSONObject;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/CONT")
public class ApiController {
    @Autowired
    S3UploadService s3UploadService;
    @Value("${uploadFilePath}")
    public String SAVE_PATH;

    @Value("${pythonURL}")
    public String pythonURL;

    @Value("${debugMode}")
    public boolean debugMode;

    @PostMapping(value = "/upload")
    @ResponseBody
    public Map<String, String> imgUpload(@RequestParam("dataFile") MultipartFile file) throws IOException {
        //Log.TraceLog("imgUpload IN");

        //코드 실행 전에 시간 받아오기
        long beforeTime = System.currentTimeMillis();

        Map<String, String> resultMap = new HashMap<String, String>();

        if (file.isEmpty()) {
            System.out.println("파일 없음");
            //  Log.TraceLog("파일 없음");
        }
        String originName = file.getOriginalFilename();
        String saveFilename = originName;

        //aws 서비스 등록
        s3UploadService.saveFile(file, saveFilename);
        //aws 이미지 경로 get
        String url = s3UploadService.getImgUrl(saveFilename);


        JSONObject dbSrvJson = new JSONObject();
        dbSrvJson.put("fileName", originName);
        //dbSrvJson.put("filePath", path + "/" + originName);
        dbSrvJson.put("filePath", url);
        dbSrvJson.put("debug", debugMode);


        JSONObject result = byPass(pythonURL, dbSrvJson, "POST");
        //Log.TraceLog("RESPONSE DATA : " + result);
        System.out.println("response data "+ result.get("data"));
        System.out.println("response data "+ result.get("result"));
        System.out.println("response data type "+ result.getClass());
        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기

        long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
        //Log.TraceLog("실행 시간 : " + secDiffTime);
        System.out.println("실행 시간 "+ secDiffTime);

        String time = Long.toString(secDiffTime);

        resultMap.put("result", result.get("data").toString());
        resultMap.put("time", time);
        //Log.TraceLog("imgUpload OUT");

        //aws 이미지 삭제
        s3UploadService.deleteImage(saveFilename);

        return resultMap;
    }

    /**
     * by pass 통신
     *
     * @param url, data, option(GET/POST)
     * @return ResponseEntity<String> 성공여부
     */
    public JSONObject byPass(String url, JSONObject jsonData, String option) {
        System.out.println("in byPass Function");
        //Log.TraceLog("***************** BY PASS START*****************");
        JSONObject responseJson = new JSONObject();
        try {
            // 연결할 url 생성
            URL start_object = new URL(url);
            //Log.TraceLog("CONNECT URL :" + url);
            System.out.println("connect url");

            // http 객체 생성
            HttpURLConnection start_con = (HttpURLConnection) start_object.openConnection();
            start_con.setDoOutput(true);
            start_con.setDoInput(true);

            // 설정 정보
            start_con.setRequestProperty("Content-Type", "application/json");
            start_con.setRequestProperty("Accept", "application/json");
            start_con.setRequestMethod(option);

            // data 전달
            //Log.TraceLog("REQUEST DATA : " + jsonData);
            System.out.println("data 전달" + jsonData);

            // 출력 부분
            OutputStreamWriter wr = new OutputStreamWriter(start_con.getOutputStream());
            wr.write(jsonData.toString());
            wr.flush();

            // 응답 받는 부분
            StringBuilder start_sb = new StringBuilder();
            int start_HttpResult = start_con.getResponseCode();

            // 결과 성공일 경우 = HttpResult 200일 경우
            if (start_HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(start_con.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    start_sb.append(line);
                }
                responseJson.put("data", start_sb);
                responseJson.put("result", "SUCCESS");
                br.close();
                System.out.println("connect success");
                System.out.println("connect success" + start_sb);

                //Log.TraceLog("***************** BY PASS SUCCESS *****************");
                return responseJson;
            } else {
                // 그 외의 경우(실패)
                //Log.TraceLog("***************** BY PASS FAIL *****************");
                responseJson.put("result", "FAIL");
                System.out.println("connect fail");
                return responseJson;
            }
        } catch (Exception e) {
            //Log.TraceLog("***************** BY PASS FAIL Exception *****************");
            //Log.TraceLog(e.toString());
            responseJson.put("result", "EXCEPTION");
            return responseJson;
        }
    }
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}

