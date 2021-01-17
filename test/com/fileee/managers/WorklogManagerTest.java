package com.fileee.managers;

import com.fileee.db.connector.MongoConnector;
import com.fileee.db.util.MongoUtil;
import com.fileee.db.util.StaffStore;
import com.fileee.enums.PayType;
import com.fileee.enums.ResponseCode;
import com.fileee.enums.WorklogOperations;
import com.fileee.exceptions.ClientException;
import com.fileee.exceptions.ResourceNotFoundException;
import com.fileee.models.Request;
import com.fileee.models.Response;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MongoUtil.class, MongoConnector.class})
@PowerMockIgnore({"javax.management.*", "sun.security.ssl.*", "javax.net.ssl.*", "javax.crypto.*"})
public class WorklogManagerTest {
    private static StaffStore store = null;
    private static WorklogManager manager;

    @BeforeClass
    public static void setUpClass() {
        store = Mockito.mock(StaffStore.class);
        manager = new WorklogManager(store);
    }

    @AfterClass
    public static void tearDownClass() {
        store = null;
        manager = null;
    }

    @Test
    public void testUpdateWorkLog() throws Exception {
        Request request = getUpdateWorkLogMonthly();
        Document existingDocument = new Document("name", "Amy");
        existingDocument.append("payType", PayType.Hourly.name());
        when(store.fetchStaffById(Mockito.anyInt()))
                .thenReturn(existingDocument);
        Document updatedDocument = new Document("name", "Amy");
        updatedDocument.append("payType", PayType.Hourly.name());
        updatedDocument.append("workLog", new HashMap<String, Object>()  {{
            put("workLog", new HashMap<String, Float> (){{
                put("1610734625", 2F);
            }});
            put("rate", 23.5);
        }});
        when(store.addWorkLog(Mockito.anyInt(), Mockito.anyMap()))
                .thenReturn(updatedDocument);
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
    }

    @Test
    public void testUpdateWorkLogWithExisting() throws Exception {
        Request request = getUpdateWorkLogMonthly();
        Document existingDocument = new Document("name", "Amy");
        existingDocument.append("payType", PayType.Hourly.name());
        existingDocument.append("workLog", new HashMap<String, Object>()  {{
            put("workLog", new HashMap<String, Float> (){{
                put("1610734625", 2F);
            }});
            put("rate", 23.5);
        }});
        when(store.fetchStaffById(Mockito.anyInt()))
                .thenReturn(existingDocument);
        Document updatedDocument = new Document("name", "Amy");
        updatedDocument.append("payType", PayType.Hourly.name());
        updatedDocument.append("workLog", new HashMap<String, Object>()  {{
           put("workLog", new HashMap<String, Float> (){{
               put("1610734625", 2F);
               put("1610821025", 2F);
           }});
           put("rate", 23.5);
        }});
        when(store.addWorkLog(Mockito.anyInt(), Mockito.anyMap()))
                .thenReturn(updatedDocument);
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
    }

    @Test(expected = ClientException.class)
    public void testUpdateWorkLogClientError() throws Exception {
        Request request = getUpdateWorkLogMonthly();
        Document existingDocument = new Document("name", "Amy");
        existingDocument.append("payType", PayType.Monthly.name());
        when(store.fetchStaffById(Mockito.anyInt()))
                .thenReturn(existingDocument);
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateWorkLogWithInvalidID() throws Exception {
        Request request = getUpdateWorkLogMonthly();
        when(store.fetchStaffById(Mockito.anyInt()))
                .thenReturn(null);
        manager.handleRequest(request);
    }

    @Test
    public void testFetchSalaryHourly() throws Exception {
        Request request = getFetchSalaryRequest();
        Document existingDocument = new Document("name", "Amy");
        existingDocument.append("payType", PayType.Hourly.name());
        existingDocument.append("workLog", new HashMap<String, Object>()  {{
            put("workLog", new HashMap<String, Float> (){{
                put("1610734625", 6F);
                put("1610821025", 2F);
                put("1610389025", 4F);
                put("1610475425", 6F);
                put("1610561825", 8F);
                put("1610648225", 4F);

            }});
            put("rate", 12.5);
        }});
        when(store.fetchStaffById(Mockito.anyInt()))
                .thenReturn(existingDocument);
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
        Assert.assertEquals(275.0, response.getResult().get("salary"));
    }

    @Test
    public void testFetchSalaryMonthly() throws Exception {
        Request request = getFetchSalaryMonthlyRequest();
        Document existingDocument = new Document("name", "Amy");
        existingDocument.append("payType", PayType.Monthly.name());
        existingDocument.append("workLog", new HashMap<String, Object>()  {{
            put("rate", 1000);
        }});
        when(store.fetchStaffById(Mockito.anyInt()))
                .thenReturn(existingDocument);
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
        Assert.assertEquals(12000.0, response.getResult().get("salary"));
    }

    @Test
    public void testFetchSalaryHourlyPdf() throws Exception {
        Request request = getFetchSalaryRequestPdf();
        Document existingDocument = new Document("name", "Amy");
        existingDocument.append("payType", PayType.Hourly.name());
        existingDocument.append("workLog", new HashMap<String, Object>()  {{
            put("workLog", new HashMap<String, Float> (){{
                put("1610734625", 6F);
                put("1610821025", 2F);
                put("1610389025", 4F);
                put("1610475425", 6F);
                put("1610561825", 8F);
                put("1610648225", 4F);

            }});
            put("rate", 12.5);
        }});
        when(store.fetchStaffById(Mockito.anyInt()))
                .thenReturn(existingDocument);
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
        Assert.assertTrue( response.getResult().get("salary") instanceof File);
    }


    private static Request getUpdateWorkLogMonthly() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("workLog", new HashMap<String, Object>() {{
            put("rate", "23.5");
            put("date", "08-01-2021");
            put("hours", "3");
        }});
        requestMap.put("id", 2);
        Request request = new Request();
        request.setOperation(WorklogOperations.updateWorkLog.name());
        request.setRequest(requestMap);
        return request;
    }

    private static Request getFetchSalaryRequest() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("pdf", false);
        requestMap.put("from", "11-01-2021");
        requestMap.put("to", "15-01-2021");
        requestMap.put("id", 2);
        Request request = new Request();
        request.setOperation(WorklogOperations.fetchSalary.name());
        request.setRequest(requestMap);
        return request;
    }

    private static Request getFetchSalaryMonthlyRequest() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("pdf", false);
        requestMap.put("from", "11-01-2020");
        requestMap.put("to", "15-01-2021");
        requestMap.put("id", 2);
        Request request = new Request();
        request.setOperation(WorklogOperations.fetchSalary.name());
        request.setRequest(requestMap);
        return request;
    }

    private static Request getFetchSalaryRequestPdf() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("pdf", true);
        requestMap.put("from", "11-01-2020");
        requestMap.put("to", "15-01-2021");
        requestMap.put("id", 2);
        Request request = new Request();
        request.setOperation(WorklogOperations.fetchSalary.name());
        request.setRequest(requestMap);
        return request;
    }

}
