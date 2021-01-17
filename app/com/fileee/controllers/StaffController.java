package com.fileee.controllers;

import com.fileee.enums.ApiIds;
import com.fileee.enums.StaffOperations;
import com.fileee.managers.StaffManager;
import com.fileee.models.Request;
import com.fileee.models.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.F;
import play.mvc.Result;


public class StaffController extends BaseController {
    private static final String version = "1.0";
    private StaffManager manager = new StaffManager();
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public F.Promise<Result> createStaffMember() {
        Request request = getRequest();
        try {
            log.info("StaffController :: createStaffMember");
            request.setOperation(StaffOperations.createStaffMember.name());
            Response response = manager.handleRequest(request);
            return getResponseEntity(response, ApiIds.createStaffMember, version);
        } catch (Exception e) {
            log.error("Exception Occurred while creating staff member : "+ e.getMessage(), e);
            return getExceptionResponseEntity(e, ApiIds.createStaffMember, version);
        }
    }

    public F.Promise<Result> updateStaffMember(String id) {
        Request request = getRequest();
        try {
            request.getRequest().put("id", Integer.parseInt(id));
            request.setOperation(StaffOperations.updateStaffMember.name());
            Response response = manager.handleRequest(request);
            return getResponseEntity(response, ApiIds.updateStaffMember, version);//
        } catch (Exception e) {
            log.error("Exception Occurred while updating staff member : "+ e.getMessage(), e);
            return getExceptionResponseEntity(e, ApiIds.updateStaffMember, null);
        }
    }

    public F.Promise<Result> listStaffMember(String sort) {
        Request request = new Request();
        try {
            request.getRequest().put("sort", Boolean.parseBoolean(sort));
            request.setOperation(StaffOperations.listStaffMembers.name());
            Response response = manager.handleRequest(request);
            return getResponseEntity(response, ApiIds.listStaffMembers, version);//
        } catch (Exception e) {
            log.error("Exception Occurred while list staff members : "+ e.getMessage(), e);
            return getExceptionResponseEntity(e, ApiIds.listStaffMembers, version);
        }
    }

    public F.Promise<Result> deleteStaffMember(String id) {
        Request request = new Request();
        try {
            request.getRequest().put("id", Integer.parseInt(id));
            request.setOperation(StaffOperations.deleteStaffMember.name());
            Response response = manager.handleRequest(request);
            return getResponseEntity(response, ApiIds.deleteStaffMember, version);//
        } catch (Exception e) {
            log.error("Exception Occurred while deleting staff member : "+ e.getMessage(), e);
            return getExceptionResponseEntity(e, ApiIds.deleteStaffMember, version);
//        }
        }
    }
}
