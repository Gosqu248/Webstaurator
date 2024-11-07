package pl.urban.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.urban.backend.service.TpayService;

import java.util.Map;
@RestController
@RequestMapping("/api/Tpay")
public class TpayController {

    private final TpayService tpayService;

    public TpayController(TpayService tpayService) {
        this.tpayService = tpayService;
    }

    @GetMapping("/generateAuthHeader")
    public ResponseEntity<Map<String, Object>> getToken() {
        Map<String, Object> tokenResponse = tpayService.requestToken();
        return ResponseEntity.ok(tokenResponse);
    }
}
