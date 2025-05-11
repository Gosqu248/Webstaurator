package pl.urban.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.urban.backend.service.IpService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ip")
public class IpController {
    private final IpService ipService;

    @GetMapping("/get")
    public String getClientIp(HttpServletRequest request) {
        return ipService.getClientIp(request);
    }
}
