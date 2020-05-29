package com.guoguo.chat.rest;

import org.springframework.core.ParameterizedTypeReference;

/**
 * Created by alex on 2020/04/18.
 */
public interface RestService {

    /**
     * Call restful service with http get method.
     *
     * @param url
     * @param request
     * @param responseType
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doGet(String url, Req request, Class<Resp> responseType) throws Exception;

    /**
     * Call restful service with http get method.
     *
     * @param url
     * @param request
     * @param parameterizedTypeReference
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doGet(String url, Req request, ParameterizedTypeReference<Resp> parameterizedTypeReference) throws Exception;

    /**
     * Call restful service with http post method.
     *
     * @param url
     * @param request
     * @param responseType
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doPost(String url, Req request, Class<Resp> responseType) throws Exception;


    /**
     * Call restful service with http post method.
     *
     * @param url
     * @param request
     * @param parameterizedTypeReference
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doPost(String url, Req request, ParameterizedTypeReference<Resp> parameterizedTypeReference) throws Exception;

    /**
     * Call restful service with http post method.
     *
     * @param url
     * @param request
     * @param parameterizedTypeReference
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doPostAsync(String url, Req request, ParameterizedTypeReference<Resp> parameterizedTypeReference) throws Exception;

    /**
     * Call restful service with http delete method.
     *
     * @param url
     * @param request
     * @param responseType
     * @return
     * @throws Exception
     */
    <Req, Resp> Resp doDelete(String url, Req request, Class<Resp> responseType) throws Exception;
}
