package com.fileee.controllers;

import com.fileee.enums.ApiIds;
import com.fileee.enums.WorklogOperations;
import com.fileee.managers.WorklogManager;
import com.fileee.models.Request;
import com.fileee.models.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.F;
import play.mvc.Result;

public class WorklogController extends BaseController {

    private static final String version = "1.0";
    private WorklogManager manager = new WorklogManager();
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public F.Promise<Result> updateWorkLog(String id) {
        Request request = getRequest();
        try {
            request.getRequest().put("id", Integer.parseInt(id));
            request.setOperation(WorklogOperations.updateWorkLog.name());
            Response response = manager.handleRequest(request);
            return getResponseEntity(response, ApiIds.updateWorklog, version);//
        } catch (Exception e) {
            log.error("Exception Occurred while updating staff member work log : "+ e.getMessage(), e);
            return getExceptionResponseEntity(e, ApiIds.updateWorklog, null);
        }
    }

    public F.Promise<Result> getSalary(String id, String pdf, String from, String to) {
        Request request = new Request();
        try {
            Boolean shouldPdf = Boolean.parseBoolean(pdf);
            request.getRequest().put("id", Integer.parseInt(id));
            request.getRequest().put("pdf", shouldPdf);
            request.getRequest().put("from", from);
            request.getRequest().put("to", to);
            request.setOperation(WorklogOperations.fetchSalary.name());
            Response response = manager.handleRequest(request);
            if (shouldPdf)
                return getResponseFileEntity(response, ApiIds.fetchSalary, version);
            else
                return getResponseEntity(response, ApiIds.fetchSalary, version);
        } catch (Exception e) {
            log.error("Exception Occurred while generating salary : "+ e.getMessage(), e);
            return getExceptionResponseEntity(e, ApiIds.fetchSalary, version);
        }
    }

}
