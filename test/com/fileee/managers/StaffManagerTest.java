package com.fileee.managers;

import com.fileee.db.connector.MongoConnector;
import com.fileee.db.util.MongoUtil;
import com.fileee.db.util.SequenceGenerator;
import com.fileee.db.util.StaffStore;
import com.fileee.enums.PayType;
import com.fileee.enums.ResponseCode;
import com.fileee.enums.StaffOperations;
import com.fileee.exceptions.ClientException;
import com.fileee.models.Request;
import com.fileee.models.Response;
import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({MongoUtil.class, MongoConnector.class})
@PowerMockIgnore({"javax.management.*", "sun.security.ssl.*", "javax.net.ssl.*", "javax.crypto.*"})
public class StaffManagerTest {

    private static SequenceGenerator sequenceGenerator = null;
    private static StaffStore store = null;
    private static StaffManager manager;

    @BeforeClass
    public static void setUpClass() {
        sequenceGenerator = Mockito.mock(SequenceGenerator.class);
        store = Mockito.mock(StaffStore.class);
        manager = new StaffManager(store);
    }

    @AfterClass
    public static void tearDownClass() {
        store = null;
        manager = null;
    }


    @Test
    public void testCreateStaffWithValidData() throws Exception {
        Request request = getCreateRequest();
//        when(sequenceGenerator.generateSequence(Mockito.anyString())).thenReturn(1);
        when(store.createDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject())).thenReturn(new Document(new HashMap<String, Object>() {{
            put("name", "Amy");
            put("payType", "Monthly");
        }}));
        when(sequenceGenerator.generateSequence(Mockito.anyString())).thenReturn(1);
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
    }

    @Test
    public void testCreateStaffWithAdditionalData() throws Exception {
        Request request = getCreateRequestWithExtraProps();
        when(store.createDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject())).thenReturn(new Document(new HashMap<String, Object>() {{
            put("name", "Amy");
            put("payType", "Monthly");
        }}));
        when(sequenceGenerator.generateSequence(Mockito.anyString())).thenReturn(2);
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
    }

    @Test(expected = ClientException.class)
    public void testCreateStaffWithInvalidPaytype() throws Exception {
        Request request = getCreateRequestWithInvalidPayType();
        when(sequenceGenerator.generateSequence(Mockito.anyString())).thenReturn(2);
        manager.handleRequest(request);
    }


    @Test
    public void testUpdateStaffWithValidData() throws Exception {
        Request request = getUpdateRequest();
        when(store.fetchDocumentById(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject()))
                .thenReturn(new Document(new HashMap<String, Object>() {{
                    put("name", "Amy");
                    put("payType", PayType.Monthly.name());
                }}));
        when(store.updateDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject(), Mockito.anyObject()))
                .thenReturn(new Document(new HashMap<String, Object>() {{
                    put("name", "Amy Ferns");
                    put("payType", PayType.Hourly.name());
                }}));
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
    }

    @Test
    public void testListStaff() throws Exception {
        Request request = getListRequest();
        when(store.fetchDocuments(Mockito.anyString(), Mockito.anyString())).thenReturn(getStaffList());
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
    }

    @Test
    public void testListStaffWithNoDataInDB() throws Exception {
        Request request = getListRequestSort();
        when(store.fetchDocuments(Mockito.anyString(), Mockito.anyString())).thenReturn(new ArrayList<>());
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
    }

    @Test
    public void testListStaffWithSort() throws Exception {
        Request request = getListRequestSort();
        when(store.fetchDocuments(Mockito.anyString(), Mockito.anyString())).thenReturn(getStaffList());
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
    }

    @Test
    public void testDeleteStaffWithValidId() throws Exception {
        Request request = getDeleteRequestValid();
        when(store.deleteDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject()))
                .thenReturn(new Document(new HashMap<String, Object>() {{
                    put("name", "Amy");
                    put("_id", "1");
                }}));
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
    }

    @Test
    public void testDeleteStaffWithInvalidId() throws Exception {
        Request request = getDeleteRequestInValid();
        when(store.deleteDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject()))
                .thenReturn(new Document(new HashMap<String, Object>() {{
                    put("name", "Amy");
                    put("_id", "1");
                }}));
        Response response = manager.handleRequest(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseCode.OK, response.getResponseCode());
    }


    private static Request getCreateRequest() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("staffMember", new HashMap<String, Object>() {{
            put("name", "Amy");
            put("payType", PayType.Monthly.name());
        }});
        Request request = new Request();
        request.setOperation(StaffOperations.createStaffMember.name());
        request.setRequest(requestMap);
        return request;
    }

    private static Request getCreateRequestWithExtraProps() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("staffMember", new HashMap<String, Object>() {{
            put("name", "Amy");
            put("payType", "Monthly");
            put("phone", "1234");
        }});
        Request request = new Request();
        request.setOperation(StaffOperations.createStaffMember.name());
        request.setRequest(requestMap);
        return request;
    }

    private static Request getCreateRequestWithInvalidPayType() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("staffMember", new HashMap<String, Object>() {{
            put("name", "Amy");
            put("payType", "Yearly");
        }});
        Request request = new Request();
        request.setOperation(StaffOperations.createStaffMember.name());
        request.setRequest(requestMap);
        return request;
    }

    private static Request getListRequest() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("sort", false);
        Request request = new Request();
        request.setOperation(StaffOperations.listStaffMembers.name());
        request.setRequest(requestMap);
        return request;
    }

    private static Request getListRequestSort() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("sort", true);
        Request request = new Request();
        request.setOperation(StaffOperations.listStaffMembers.name());
        request.setRequest(requestMap);
        return request;
    }

    private static Request getUpdateRequest() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("id", 1);
        requestMap.put("staffMember", new HashMap<String, Object>() {{
            put("name", "Amy Ferns");
            put("payType", "Hourly");
        }});
        Request request = new Request();
        request.setOperation(StaffOperations.updateStaffMember.name());
        request.setRequest(requestMap);
        return request;
    }

    private static Request getDeleteRequestValid() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("id", 1);
        Request request = new Request();
        request.setOperation(StaffOperations.deleteStaffMember.name());
        request.setRequest(requestMap);
        return request;
    }

    private static Request getDeleteRequestInValid() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("id", 5);
        Request request = new Request();
        request.setOperation(StaffOperations.deleteStaffMember.name());
        request.setRequest(requestMap);
        return request;
    }

    private static List<Map> getStaffList() {
        List<Map> staffList = new ArrayList<>();
        staffList.add(new HashMap() {{
            put("name", "Naomi");
        }});
        staffList.add(new HashMap() {{
            put("name", "Amy");
        }});
        staffList.add(new HashMap() {{
            put("name", "Sara");
        }});
        staffList.add(new HashMap() {{
            put("name", "Hanson");
        }});
        staffList.add(new HashMap() {{
            put("name", "James");
        }});
        return staffList;
    }


}
