package pl.urban.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;


@Service
public class IpService {
    public String getClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
