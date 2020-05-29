package com.guoguo.chat.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guoguo.chat.utils.JSONUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by alex on 2020/04/18.
 */
@Component
public class RestServiceImpl implements RestService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    @Override
    public <Req, Resp> Resp doGet(String url, Req request, Class<Resp> responseType) throws Exception {
        URIBuilder builder = new URIBuilder(url);
        Map<String, Object> params = this.getParameters(request);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.addParameter(entry.getKey(), Objects.toString(entry.getValue(), ""));
        }
        return restTemplate.getForObject(builder.build(), responseType);
    }

    @Override
    public <Req, Resp> Resp doGet(String url, Req request, ParameterizedTypeReference<Resp> responseType) throws Exception {
        URIBuilder builder = new URIBuilder(url);
        Map<String, Object> params = this.getParameters(request);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.addParameter(entry.getKey(), Objects.toString(entry.getValue(), ""));
        }
        return restTemplate.exchange(builder.build(), HttpMethod.GET, null, responseType).getBody();
    }

    public <Req, Resp> Resp doPost(String url, Req request, Class<Resp> responseType) throws Exception {
        if (request == null) {
            return restTemplate.postForObject(url, null, responseType);
        }
        // headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
        // body
        String jsonBody = new ObjectMapper().writeValueAsString(request);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, httpHeaders);
        return restTemplate.postForObject(url, httpEntity, responseType);
    }

    public <Req, Resp> Resp doPost(String url, Req request,ParameterizedTypeReference<Resp> respType) throws Exception {
        if (request == null) {
            return restTemplate.exchange(url, HttpMethod.POST, null, respType).getBody();
        }
        // headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
        // body
        String jsonBody = new ObjectMapper().writeValueAsString(request);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, respType).getBody();
    }

    public <Req, Resp> Resp doPostAsync(String url, Req request,ParameterizedTypeReference<Resp> respType) throws Exception {
        if (request == null) {
            return asyncRestTemplate.exchange(url, HttpMethod.POST, null, respType).get().getBody();
        }
        // headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
        // body
        String jsonBody = new ObjectMapper().writeValueAsString(request);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, httpHeaders);
        return asyncRestTemplate.exchange(url, HttpMethod.POST, httpEntity, respType).get().getBody();
    }

    @Override
    public <Req, Resp> Resp doDelete(String url, Req request, Class<Resp> responseType) throws Exception {
        HttpHeaders headers =new HttpHeaders();
        Map<String, Object> params = this.getParameters(request);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            headers.add(entry.getKey(), Objects.toString(entry.getValue(), ""));
        }
        ResponseEntity<Resp> exchange = restTemplate.exchange(url, HttpMethod.DELETE,
                new HttpEntity<>(headers), responseType);
        return exchange.getBody();
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private <IN> Map<String, Object> getParameters(IN requestQo){
        Map<String, Object> params;
        if (requestQo instanceof Map) {
            params = new HashMap<>((Map) requestQo);
        } else {
            params = JSONUtils.toMap(requestQo);
        }
        return params;
    }
}
