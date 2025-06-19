package io.yuyf.meng.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class CloudflareService {

    private static final Logger logger = LoggerFactory.getLogger(CloudflareService.class);
    private static final String URL = "https://api.cloudflare.com/client/v4/zones/36785a9f880aaee81de3c58b44218d71/dns_records/5eb20883e8ecb98573982079b69b292a";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${meng.cloudflare-api-token}")
    private String cloudflareApiToken;

    @Scheduled(cron = "0 0/5 * * * *")
    public void patchLocalAddress() throws Exception {
        try {
            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + cloudflareApiToken);

            // Create entity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);
            JsonObject body = JsonParser.parseString(response.getBody()).getAsJsonObject();
            
            String remoteAdress = this.queryLocalAddress();
            JsonObject result = body.get("result").getAsJsonObject();
            if (result.get("content").getAsString().equals(remoteAdress)) {
                logger.info("Local address is already up to date: {}", remoteAdress);
                return;
            }
            result.addProperty("content", remoteAdress);
            HttpEntity<String> patchEntity = new HttpEntity<>(body.toString(), headers);
            restTemplate.exchange(URL, HttpMethod.PATCH, patchEntity, String.class);
            logger.info("Patched local address to: {}", remoteAdress);
        }
        catch (Exception e) {
            logger.error("Failed to patch local address", e);
        }
    
    }


    public String queryLocalAddress() {
        return restTemplate.getForObject("https://ipv6.icanhazip.com/", String.class).trim();
    }
}
