
package com.fileee.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.fileee.models.mongo.WorkLog;
import com.fileee.utils.HtmlTemplateUtil;
import com.fileee.utils.PdfGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class WorklogManager {

    private StaffStore store;
    private ObjectMapper mapper = new ObjectMapper();
    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    ZoneId zoneId = ZoneId.systemDefault();
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String type = "workLog";

    public WorklogManager() {
        store = new StaffStore();
    }

    public WorklogManager(StaffStore store) {
        this.store = store;
    }


    public Response handleRequest(Request request) throws Exception {
        switch (request.getOperation()) {
            case "updateWorkLog": {
                return updateWorkLog(request);
            }
            case "fetchSalary": {
                return fetchSalary(request);
            }
            default:
                return new Response("error", "1.0", null, ResponseCode.SERVER_ERROR, new HashMap<String, Object>() {{
                    put("message", "Invalid operation, unsuccessful");
                }});
        }
    }

    private Response updateWorkLog(Request request) throws Exception {
        try {
            log.info("WorklogManager::addWorkLog:: Trying to add worklog");
            Map<String, Object> input = (Map<String, Object>) request.getRequest().get(type);
            int staffId = (int) request.getRequest().getOrDefault("id", 0);
            Map<String, Object> existing = store.fetchStaffById(staffId);
            if (null == existing)
                throw new ResourceNotFoundException("ERR_ADD_WORKLOG", "There is no staff member with id: " + staffId);
            Staff existingStaff = mapper.convertValue(existing, Staff.class);
            validateRequest(input, existingStaff);
            WorkLog existingWorkLog = existingStaff.getWorkLog();
            WorkLog workLog = getWorkLog(input, existingWorkLog);
            Map<String, Object> workLogMap = mapper.convertValue(workLog, Map.class);
            store.addWorkLog(staffId, workLogMap);
            return new Response(ApiIds.updateWorklog, "1.0", null, ResponseCode.OK, new HashMap<String, Object>() {{
                put("message", "Successfully added worklog");
                put("id", staffId);
            }});
        } catch (Exception e) {
            log.error("WorklogManager::addWorkLog:: Unable to add worklog, please try again later" + e.getMessage());
            throw e;
        }
    }


    private Response fetchSalary(Request request) throws Exception {
        try {
            log.info("WorklogManager::fetchSalary:: Trying to fetch salary");
            Map<String, Object> input = (Map<String, Object>) request.getRequest();
            int staffId = (int) input.getOrDefault("id", 0);
            Boolean pdf = (Boolean) input.getOrDefault("pdf", false);
            Map<String, Object> existing = store.fetchStaffById(staffId);
            if (null == existing)
                throw new ResourceNotFoundException("ERR_ADD_WORKLOG", "There is no staff member with id: " + staffId);
            Staff existingStaff = mapper.convertValue(existing, Staff.class);
            Double salary = fetchSalaryDetails(input, existingStaff);
            if (pdf) {
                File file = generatePdf(existingStaff, salary, input);
                return new Response(ApiIds.fetchSalary, "1.0", null, ResponseCode.OK, new HashMap<String, Object>() {{
                    put("message", "Successfully Fetched Salary");
                    put("id", staffId);
                    put("salary", file);
                }});
            } else
                return new Response(ApiIds.fetchSalary, "1.0", null, ResponseCode.OK, new HashMap<String, Object>() {{
                    put("message", "Successfully Fetched Salary");
                    put("id", staffId);
                    put("salary", salary);
                }});
        } catch (Exception e) {
            log.error("WorklogManager::fetchSalary:: Unable to fetch salary , please try again later" + e.getMessage());
            throw e;
        }
    }


    private WorkLog getWorkLog(Map<String, Object> requestMap, WorkLog workLog) {
        try {
            log.info("WorklogManager::getWorkLog:: Trying to fetch worklog");
            WorkLog newWorkLog = (null != workLog) ? workLog : new WorkLog();
            String dateString = (String) requestMap.getOrDefault("date", "");
            Float hours = Float.parseFloat((String) requestMap.getOrDefault("hours", "0.0"));
            Double rate = Double.parseDouble((String) requestMap.get("rate"));
            if (StringUtils.isNotBlank(dateString) || 0.0 != hours) {
                LocalDate date = LocalDate.parse(dateString, df);
                String epochKey = date.atStartOfDay(zoneId).toEpochSecond() + "";
                Map<String, Float> log = (null != newWorkLog.getWorkLog()) ? newWorkLog.getWorkLog() : new HashMap<>();
                log.put(epochKey, hours);
                newWorkLog.setWorkLog(log);
            }
            newWorkLog.setRate(rate);
            return newWorkLog;
        } catch (Exception e) {
            log.error("WorklogManager::getWorkLog:: Unable to fetch worklog , please try again later" + e.getMessage());
            throw new ServerException("ERR_GET_WORKLOG", "There was an issue while fetching worklog, please try again.");
        }
    }

    private void validateRequest(Map<String, Object> input, Staff existing) {
        log.info("WorklogManager::validateRequest:: Validating the input request");
        String payType = existing.getPayType();
        if (StringUtils.equalsIgnoreCase(payType, PayType.Hourly.name())) {
            if (StringUtils.isBlank((String) input.getOrDefault("date", "")))
                throw new ClientException("ERR_CREATE_WORKLOG", "For employee of type "
                        + payType + " date is mandatory parameter");
            if (StringUtils.isBlank((String) input.getOrDefault("hours", "")))
                throw new ClientException("ERR_CREATE_WORKLOG", "For employee of type "
                        + payType + " hours  is mandatory parameter");
            if (existing.getWorkLog() != null && existing.getWorkLog().getRate() == 0.0
                    && StringUtils.isBlank((String) input.getOrDefault("rate", "")))
                throw new ClientException("ERR_CREATE_WORKLOG", "For employee of type "
                        + payType + " rate  is mandatory parameter");
        } else {
            if (StringUtils.isNotBlank((String) input.getOrDefault("date", "")))
                throw new ClientException("ERR_CREATE_WORKLOG", "For employee of type "
                        + payType + " can't pass the date ");
            if (StringUtils.isNotBlank((String) input.getOrDefault("hours", "")))
                throw new ClientException("ERR_CREATE_WORKLOG", "For employee of type "
                        + payType + " can't pass the hour");
            if (StringUtils.isBlank((String) input.getOrDefault("rate", ""))
                    && existing.getWorkLog().getRate() == 0.0)
                throw new ClientException("ERR_CREATE_WORKLOG", "For employee of type "
                        + payType + " rate  is mandatory parameter");
        }
    }

    //Handle Empty worklog for monthly with just rate
    private Double fetchSalaryDetails(Map<String, Object> request, Staff staff) {
        try {
            log.info("WorklogManager::fetchSalaryDetails:: Trying to fetch salary details");
            String fromString = (String) request.getOrDefault("from", "");
            String toString = (String) request.getOrDefault("to", "");
            LocalDate fromDate = LocalDate.parse(fromString, df);
            LocalDate toDate = LocalDate.parse(toString, df);
            if (null == staff.getWorkLog() || 0.0 == staff.getWorkLog().getRate())
                return 0.0;
            if (StringUtils.equalsIgnoreCase(staff.getPayType(), PayType.Monthly.name())) {
                return Math.abs((toDate.getYear() - fromDate.getYear()) * 12 + fromDate.getMonthValue() - toDate.getMonthValue()) * staff.getWorkLog().getRate();
            } else {
                if (null == staff.getWorkLog().getWorkLog())
                    return 0.0;
                Float total = staff.getWorkLog().getWorkLog().entrySet()
                        .stream().filter(entry -> Long.parseLong(entry.getKey()) >= fromDate.atStartOfDay(zoneId).toEpochSecond()
                                && Long.parseLong(entry.getKey()) <= toDate.atStartOfDay(zoneId).toEpochSecond())
                        .map(entry -> entry.getValue())
                        .reduce(0.0F, (totalHours, value) -> totalHours + value);
                return total * staff.getWorkLog().getRate();
            }
        } catch (Exception e) {
            log.error("WorklogManager::fetchSalaryDetails:: There was an issue while fetching salary details, please try again later" + e.getMessage());
            throw new ServerException("ERR_FETCH_SALARY_DETAILS", "There was an issue while fetching salary details, please try again.");
        }
    }

    private File generatePdf(Staff staff, Double salary, Map<String, Object> input) {
        String htmlString = HtmlTemplateUtil.template
                .replace("${name}", staff.getName())
                .replace("${payType}", staff.getPayType())
                .replace("${from}", (String) input.get("from"))
                .replace("${to}", (String) input.get("to"))
                .replace("${salary}", salary + "");
        try {
            log.info("WorklogManager::generatePdf:: Trying to generate pdf");
            return PdfGenerator.convertHtmlStringToPdfFile(htmlString, staff.getName() + "_" + staff.getId());
        } catch (Exception e) {
            log.error("WorklogManager::generatePdf:: Unable to generate pdf, please try again later" + e.getMessage());
            throw new ServerException("ERR_GENERATE_PDF", "There was an issue with pdf generation, please try again.");
        }
    }


}

