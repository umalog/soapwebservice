package com.umalog;

import com.umalog.generateddata.FindNumberRequest;
import com.umalog.generateddata.FindNumberResponse;
import com.umalog.services.NumberSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class FindNumberEndPoint {
    private static final String URI = "http://generateddata.umalog.com";

    private NumberSearchService numberSearchService;

    @Autowired
    public FindNumberEndPoint(NumberSearchService numberSearchService){
        this.numberSearchService = numberSearchService;
    }

    @PayloadRoot(namespace = URI, localPart = "findNumberRequest")
    @ResponsePayload
    public FindNumberResponse findNumber(@RequestPayload FindNumberRequest rq){
        FindNumberResponse rs = new FindNumberResponse();
        rs.setResult(numberSearchService.findResult(rq.getNumber()));
        return rs;
    }
}
