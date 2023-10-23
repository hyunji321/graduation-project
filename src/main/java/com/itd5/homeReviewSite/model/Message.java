package com.itd5.homeReviewSite.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Message {
    private String title;         // 사용자에게 전달할 메시지의 title
    private String message;           // 사용자에게 전달할 메시지
    private String redirectUri;     // 리다이렉트 uri
    private String cancelUri;
    private RequestMethod method;   // http 요청 메서드
    private Map<String, Object> data;   // 화면(View)으로 전달할 데이터(파라미터)

}
