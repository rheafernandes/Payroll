package com.fileee.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fileee.enums.ResponseCode;
import com.fileee.exceptions.ClientException;
import com.fileee.exceptions.MiddlewareException;
import com.fileee.exceptions.ResourceNotFoundException;
import com.fileee.models.Request;
import com.fileee.models.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.naming.ServiceUnavailableException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class BaseController extends Controller {
    private static ObjectMapper mapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    protected Request getRequest() {
        JsonNode requestData = request().body().asJson();
        Request req = mapper.convertValue(requestData, Request.class);
        return req;
    }


    protected Promise<Result> getResponseEntity(Response response, String apiId, String version) {
        int statusCode = response.getResponseCode().code();
        response.setId(apiId);
        response.setVer(version);
        response.setTs(getResponseTimestamp());
        return Promise.<Result>pure(Results.status(statusCode , Json.toJson(response)).as("application/json"));
    }

    protected Promise<Result> getResponseFileEntity(Response response, String apiId, String version) {
        int statusCode = response.getResponseCode().code();
        response.setId(apiId);
        response.setVer(version);
        response.setTs(getResponseTimestamp());
        return Promise.<Result>pure(Results.status(statusCode, (File) response.getResult().get("salary")).as("application/pdf"));
    }

    protected Promise<Result> getExceptionResponseEntity(Exception e, String apiId, String version) {
        log.error("BaseController::getExceptionResponseEntity:: Exception Occurred while creating staff member : "+ e.getMessage(), e);
        int statusCode = getStatus(e);
        Response response = getErrorResponse(e);
        response.setId(apiId);
        response.setVer(version);
        response.setTs(getResponseTimestamp());
        return Promise.<Result>pure(Results.status(statusCode ,Json.toJson(response)).as("application/json"));
    }

    private Response getErrorResponse(Exception e) {
        Response response = new Response();
        String message = e.getMessage();
        if (e instanceof MiddlewareException) {
            response.setResponseCode(((MiddlewareException) e).getResponseCode());
        } else {
            response.setResponseCode(ResponseCode.SERVER_ERROR);
        }
        response.setResult(new HashMap<String, Object>() {{
            put("message", message);
        }});
        return response;
    }

    private int getStatus(Exception e) {
        if (e instanceof ClientException) {
            return Results.badRequest().status();
        } else if (e instanceof ResourceNotFoundException) {
            return Results.notFound().status();
        }else if (e instanceof ServiceUnavailableException){
            return Results.status(ResponseCode.SERVICE_UNAVAILABLE.code()).status();
        }
        return Results.internalServerError().status();
    }


    private String getResponseTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'XXX");
        return sdf.format(new Date());
    }


}
