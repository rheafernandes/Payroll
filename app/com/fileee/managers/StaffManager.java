package com.fileee.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fileee.comparators.NameComparator;
import com.fileee.db.util.StaffStore;
import com.fileee.enums.ApiIds;
import com.fileee.enums.PayType;
import com.fileee.enums.ResponseCode;
import com.fileee.exceptions.ClientException;
import com.fileee.exceptions.ResourceNotFoundException;
import com.fileee.exceptions.ServerException;
import com.fileee.models.Request;
import com.fileee.models.Response;
import com.fileee.models.mongo.Staff;
import com.fileee.utils.SortUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaffManager {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String type = "staffMember";
    private StaffStore store;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public StaffManager() {
        this.store = new StaffStore();
    }

    public StaffManager(StaffStore store) {
        this.store = store;
    }

    public Response handleRequest(Request request) throws Exception {
        switch (request.getOperation()) {
            case "createStaffMember": {
                return createStaffMember(request);
            }
            case "listStaffMembers": {
                return listStaffMembers(request);
            }
            case "updateStaffMember": {
                return updateStaffMember(request);
            }
            case "deleteStaffMember": {
                return deleteStaffMember(request);
            }
            default:
                return new Response("error", "1.0", null, ResponseCode.SERVER_ERROR, new HashMap<String, Object>() {{
                    put("message", "No operation found for: " + request.getOperation() + "It was unsuccessful");
                }});
        }
    }

    private Response createStaffMember(Request request) throws Exception {
        Map<String, Object> input = (Map<String, Object>) request.getRequest().get(type);
        validateRequest(input);
        try {
            log.info("StaffManager::createStaffMember:: Trying to create a new Staff Member");
            int id = store.createStaff(input);
            return new Response(ApiIds.createStaffMember, "1.0", null, ResponseCode.OK, new HashMap<String, Object>() {{
                put("message", "Staff Member was created successfully");
                put("id", id);

            }});
        } catch (Exception e) {
            log.error("StaffManager::createStaffMember:: Unable to create a staff member, please try again later" + e.getMessage());
            throw e;
        }
    }

    private Response listStaffMembers(Request request) throws Exception {
        try {
            log.info("StaffManager::listStaffMembers:: Trying to fetch the list of Staff Members");
            List<Map> staffMembers = store.fetchStaff();
            if ((Boolean) request.getRequest().get("sort") && !staffMembers.isEmpty()) {
                List<Staff> staffList = staffMembers.stream()
                        .map(element -> mapper.convertValue(element, Staff.class))
                        .collect(Collectors.toList());
                List<Staff> sortedList = SortUtil.sort(staffList, new NameComparator());
                staffMembers = sortedList.stream()
                        .map(element -> mapper.convertValue(element, Map.class))
                        .collect(Collectors.toList());
            }
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Staff list was successfully fetched");
            responseMap.put("noOfStaff", staffMembers.size());
            responseMap.put("staff", staffMembers);
            Response response = new Response(ApiIds.listStaffMembers, "1.0", null, ResponseCode.OK, responseMap);
            return response;
        } catch (Exception e){
            log.error("StaffManager::listStaffMembers:: Unable to list staff members, please try again later" + e.getMessage());
            throw e;
        }
    }

    private Response updateStaffMember(Request request) throws Exception{
        log.info("StaffManager::updateStaffMember:: Trying to update the list of Staff Members");
        Map<String, Object> input = (Map<String, Object>) request.getRequest().get(type);
        try {
            validateRequest(input);
            input.entrySet().forEach(entry -> System.out.println("Entry" + entry.getKey() + "::" + entry.getValue()));
            int id = (int) request.getRequest().getOrDefault("id", 0);
            Map<String, Object> existing = store.fetchStaffById(id);
            if(null == existing)
                throw new ResourceNotFoundException("ERR_STAFF_UPDATE", "There is no staff member with id: " + id);
            existing.putAll(input);
            store.updateStaff(id, existing);
            return new Response(ApiIds.updateStaffMember, "1.0", null, ResponseCode.OK, new HashMap<String, Object>() {{
                put("message", "Staff Member was updated successfully");
                put("id", id);
            }});
        }catch (Exception e) {
            log.error("StaffManager::updateStaffMember:: Unable to delete staff member, please try again later" + e.getMessage());
            throw e;
        }
    }

    private Response deleteStaffMember(Request request) {
        log.info("StaffManager::deleteStaffMember:: Trying to delete the list of Staff Members");
        int id = (int) request.getRequest().get("id");
        store.deleteStaff(id);
        return new Response(ApiIds.deleteStaffMember, "1.0", null, ResponseCode.OK, new HashMap<String, Object>() {{
            put("message", "It was successful");
            put("id", id);
        }});
    }


    private void validateRequest(Map<String, Object> input) throws Exception {
        List<String> paytypes = Stream.of(PayType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        try {
            log.info("StaffManager::validateRequest:: Validating request");
            Staff staff = mapper.convertValue(input, Staff.class);
            if (StringUtils.isNotBlank(staff.getPayType()) && !paytypes.contains(staff.getPayType())) {
                throw new ClientException("VALIDATION_ERRORS", "Paytype should be one of " + paytypes);
            }
        } catch (IllegalArgumentException e) {
            log.error("StaffManager::validateRequest:: Unable to validate request, please try again later" + e.getMessage());
            throw new ClientException("VALIDATION_ERRORS", e.getMessage());
        }
    }

}


