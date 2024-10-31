package pl.urban.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.urban.backend.service.IpService;


@RestController
@RequestMapping("/api/ip")
public class IpController {

    private final IpService ipService;

    public IpController(IpService ipService) {
        this.ipService = ipService;
    }

    @GetMapping("/get")
    public String getClientIp(HttpServletRequest request) {
        return ipService.getClientIp(request);
    }
}