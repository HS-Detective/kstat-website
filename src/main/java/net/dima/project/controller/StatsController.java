package net.dima.project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

  @Value("${supabase.url}")       private String baseUrl;
  @Value("${supabase.serviceKey}") private String serviceKey;

  private final RestTemplate rt = new RestTemplate();

  private HttpHeaders headers() {
    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_JSON);
    h.setAccept(List.of(MediaType.APPLICATION_JSON));
    h.set("apikey", serviceKey);
    h.setBearerAuth(serviceKey);  // service_role key
    return h;
  }
  private <T> T postRpc(String fn, Map<String,Object> body, Class<T> type){
    ResponseEntity<T> res = rt.exchange(
      baseUrl + "/rest/v1/rpc/" + fn,
      HttpMethod.POST,
      new HttpEntity<>(body, headers()),
      type
    );
    return res.getBody();
  }

  // ---- DTOs ----
  public record PointDTO(String label, long value) {}
  public record KVDTO(String label, long value) {}
  public record ItemShareDTO(String label, String code, long value) {}
  public record LabelDTO(String label) {}
  public record CodeLabelDTO(String code, String label) {}

  // 1) 월별 추이 (최근 24개월 범위 고정)
  @GetMapping("/monthly-trend")
  public List<PointDTO> monthlyTrend(
      @RequestParam("metric") String metric,
      @RequestParam(value="country", defaultValue="ALL") String country,
      @RequestParam(value="mti4",    defaultValue="ALL") String mti4,
      @RequestParam(value="startYm", required=false) String startYm,
      @RequestParam(value="endYm",   required=false) String endYm
  ){
	  String s = (startYm == null || startYm.isBlank()) ? "2023-01" : startYm;
	  String e = (endYm   == null || endYm.isBlank())   ? "2024-12" : endYm;
	  
    Map<String,Object> p = Map.of(
      "metric", metric,
      "start_ym", s,     // ← RPC 파라미터 키와 맞춤
      "end_ym",   e,
      "country_in", country,
      "mti4_in",    (mti4 == null || mti4.isBlank() ? "ALL" : mti4.toUpperCase())
    );
    PointDTO[] arr = postRpc("api_monthly_trend_v1", p, PointDTO[].class);
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
    Map<String,Object> p = Map.of(
      "metric",  metric,
      "year_in", year,
      "mti4_in", (mti4 == null || mti4.isBlank() ? "ALL" : mti4.toUpperCase()),
      "topn",    Math.min(Math.max(topN,1),10)
    );
    KVDTO[] arr = postRpc("api_top_countries_v1", p, KVDTO[].class);
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
    Map<String,Object> p = Map.of(
      "metric",     metric,
      "year_in",    year,
      "country_in", country,
      "topn",       Math.min(Math.max(topN,1),10)
    );
    ItemShareDTO[] arr = postRpc("api_item_share_v1", p, ItemShareDTO[].class);
    return arr == null ? List.of() : Arrays.asList(arr);
  }
  
	//▼ 상위 국가 20
	@GetMapping("/options/countries")
	public List<String> optionCountries(@RequestParam(value="limit", defaultValue="20") int limit){
	 LabelDTO[] arr = postRpc("opt_countries_v1", Map.of("limit_in", limit), LabelDTO[].class);
	 return arr == null ? List.of() : Arrays.stream(arr).map(LabelDTO::label).toList();
	}
	
	//▼ 상위 MTI 20
	@GetMapping("/options/mti4")
	public List<CodeLabelDTO> optionMti4(@RequestParam(value="limit", defaultValue="20") int limit){
	 CodeLabelDTO[] arr = postRpc("opt_mti4_v1", Map.of("limit_in", limit), CodeLabelDTO[].class);
	 return arr == null ? List.of() : Arrays.asList(arr);
}
}