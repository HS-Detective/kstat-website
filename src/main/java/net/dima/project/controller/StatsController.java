package net.dima.project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

  @Value("${fastapi.base-url}")
  private String baseUrl;

  private final RestTemplate rt = new RestTemplate();

  private String mapMetric(String metric) {
    if ("export".equalsIgnoreCase(metric)) return "EXP_AMT";
    if ("import".equalsIgnoreCase(metric)) return "IMP_AMT";
    return metric; // 기본적으로 그대로 전달 (이미 EXP_AMT인 경우 등)
  }

  private <T> T getFromFastApi(String path, Map<String, Object> params, Class<T> type) {
    StringBuilder urlBuilder = new StringBuilder(baseUrl).append(path).append("?");
    params.forEach((k, v) -> {
      if (v != null) {
        urlBuilder.append(k).append("=").append(v).append("&");
      }
    });
    
    ResponseEntity<T> res = rt.getForEntity(urlBuilder.toString(), type);
    return res.getBody();
  }

  // ---- DTOs ----
  public record PointDTO(String label, long value) {}
  public record KVDTO(String label, long value) {}
  public record ItemShareDTO(String label, String code, long value) {}
  public record CodeLabelDTO(String code, String label) {}

  // 1) 월별 추이
  @GetMapping("/monthly-trend")
  public List<PointDTO> monthlyTrend(
      @RequestParam("metric") String metric,
      @RequestParam(value="country", defaultValue="ALL") String country,
      @RequestParam(value="mti4",    defaultValue="ALL") String mti4, // 프론트엔드 파라미터명에 맞춤
      @RequestParam(value="startYm", required=false) String startYm,
      @RequestParam(value="endYm",   required=false) String endYm
  ){
    Map<String, Object> p = new HashMap<>();
    p.put("metric", mapMetric(metric));
    p.put("country", country);
    p.put("mti_cd", mti4); // 파이썬 엔드포인트는 mti_cd를 기대함
    p.put("startYm", (startYm == null || startYm.isBlank()) ? "202301" : startYm.replace("-", ""));
    p.put("endYm", (endYm == null || endYm.isBlank()) ? "202412" : endYm.replace("-", ""));

    PointDTO[] arr = getFromFastApi("/stats/monthly-trend", p, PointDTO[].class);
    return arr == null ? List.of() : Arrays.asList(arr);
  }

  // 2) 연도 TopN 국가
  @GetMapping("/top-countries")
  public List<KVDTO> topCountries(
      @RequestParam("metric") String metric,
      @RequestParam("year")   int year,
      @RequestParam(value="mti4", defaultValue="ALL") String mti4,
      @RequestParam(value="topN", defaultValue="5")   int topN
  ){
    Map<String, Object> p = new HashMap<>();
    p.put("metric", mapMetric(metric));
    p.put("year", year);
    p.put("mti_cd", mti4);
    p.put("topN", topN);

    KVDTO[] arr = getFromFastApi("/stats/top-countries", p, KVDTO[].class);
    return arr == null ? List.of() : Arrays.asList(arr);
  }

  // 3) 특정 국가 품목 Top10
  @GetMapping("/item-share")
  public List<ItemShareDTO> itemShare(
      @RequestParam("metric")  String metric,
      @RequestParam("year")    int year,
      @RequestParam("country") String country,
      @RequestParam(value="topN", defaultValue="10") int topN
  ){
    Map<String, Object> p = new HashMap<>();
    p.put("metric", mapMetric(metric));
    p.put("year", year);
    p.put("country", country);
    p.put("topN", topN);

    ItemShareDTO[] arr = getFromFastApi("/stats/item-share", p, ItemShareDTO[].class);
    return arr == null ? List.of() : Arrays.asList(arr);
  }

  // 4) 국가 목록 (드롭다운용)
  @GetMapping("/options/countries")
  public List<String> optionCountries(@RequestParam(value="limit", defaultValue="20") int limit) {
    Map<String, Object> p = Map.of("limit", limit);
    String[] arr = getFromFastApi("/stats/options/countries", p, String[].class);
    return arr == null ? List.of() : Arrays.asList(arr);
  }

  // 5) MTI 목록 (드롭다운용)
  @GetMapping("/options/mti4")
  public List<CodeLabelDTO> optionMti4(@RequestParam(value="limit", defaultValue="20") int limit) {
    Map<String, Object> p = Map.of("limit", limit);
    CodeLabelDTO[] arr = getFromFastApi("/stats/options/mti", p, CodeLabelDTO[].class);
    return arr == null ? List.of() : Arrays.asList(arr);
  }
}