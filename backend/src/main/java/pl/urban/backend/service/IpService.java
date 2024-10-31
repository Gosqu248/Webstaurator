package pl.urban.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;


@Service
public class IpService {

    public String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }
}