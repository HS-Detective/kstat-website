package net.dima.project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // 필요 시 프론트 도메인으로 제한
public class ChatApiController {

    private final RestTemplate rest = new RestTemplate();

    // application.properties 또는 환경변수로
    @Value("${fastapi.base-url}")
    private String fastApiBase;

    @Value("${n8n.base-url}")
    private String n8nBase;

    // n8n Header Auth (Name=authorization, Value=kitadima2)
    @Value("${n8n.api-key}")
    private String n8nApiKey;

    /** 세션 생성 */
    @PostMapping("/{mode}/new-session")
    public Map<String, String> newSession(@PathVariable("mode") String mode) {
        return Map.of("sessionId", UUID.randomUUID().toString());
    }

    /** 챗봇 요청 */
    @PostMapping("/{mode}")
    public Map<String, Object> chat(@PathVariable("mode") String mode,
            @RequestBody Map<String, Object> body) {

        String targetUrl;
        boolean callN8n = false;

        switch (mode) {
            case "hs":       targetUrl = fastApiBase + "/hs"; break;
            case "nav":      targetUrl = fastApiBase + "/nav"; break;
            case "glossary": targetUrl = fastApiBase + "/glossary"; break;
            case "faq":      targetUrl = fastApiBase + "/faq"; break;
            case "stats":    targetUrl = fastApiBase + "/chat/stats"; break; 
            default:         return Map.of("reply", "알 수 없는 모드입니다.");
        }

        try {
            // 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (callN8n && n8nApiKey != null && !n8nApiKey.isEmpty()) {
                headers.set("authorization", n8nApiKey); // n8n Header Auth
            }

            // 요청
            HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
            ResponseEntity<Map> resp = rest.postForEntity(targetUrl, req, Map.class);

            @SuppressWarnings("unchecked")
            Map<String, Object> respBody = (Map<String, Object>) resp.getBody();

            Object reply = (respBody != null && respBody.get("reply") != null)
                    ? respBody.get("reply")
                    : "(응답 없음)";

            return Map.of(
                    "reply", reply,
                    "meta", Map.of("mode", mode, "error", false)
            );

        } catch (Exception e) {
            return Map.of(
                    "reply", "연동 오류: " + e.getMessage(),
                    "meta", Map.of("mode", mode, "error", true)
            );
        }
    }
}
