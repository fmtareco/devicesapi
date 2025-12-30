package com.example.devicesapi.controllers;

import com.example.devicesapi.config.RedisConfig;
import com.example.devicesapi.config.SecurityConfig;
import com.example.devicesapi.dtos.DeviceResponse;
import com.example.devicesapi.entities.Device;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import java.util.Arrays;

@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CacheControllerTest {

    @Container
    @ServiceConnection
    static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:7.4.2"))
            .withExposedPorts(6379);

    /**
     * RestClient interface to perform API http calls
      */
    static public RestClient restClient;

    /**
     * API endpoint elements
     */
    static final String host = "localhost";
    //static final String port = "8282";
    static String protocol = "http";
    static String endPoint = "/api/devices";
    static String baseUrl ;



    /**
     * test battery setup:
     * - create API endpoint
     * - create RestClient http handler
     * - inits list of created devices
     */
    @BeforeAll
    public static void setUp() {
        String port = "8282";
        baseUrl = protocol+ "://" + host + ":" + port + endPoint;
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(
                        httpHeaders -> {
                            httpHeaders.setBasicAuth("user", "xpto123");
                            //httpHeaders.set("Content-Type", "application/json");
                            httpHeaders.set("API-Key", "devices-api-key");
                            httpHeaders.set("API-Secret", "devices-api-secret");
                        })
                .build();

    }

    //---------------------------------------------------------------------------------------//
    //                                   tests methods                                       //
    //---------------------------------------------------------------------------------------//

    /**
     * test 1
     * - lists all the existent devices before the tests
     */
    @Test
    @Order(1)
    void getAllDevices() {
        String msg = "Total devices";
//        if (!ObjectUtils.isEmpty(param)) {
//            msg += " of " +  param + "="+value;
//        }
        int numDevices=0;
        Device[] payload = fetchExistingDevices(null, null);
        if (payload != null) {
            numDevices = payload.length;
        }
        msg += " : " + numDevices;
        traceTestSummary("GET ALL", true, msg);
        if (payload != null) {
            Arrays.stream(payload).forEach(System.out::println);
        }
    }

    @Order(2)
    @Test
    void findDevice() {
        String id = "d14e55ec-5d2b-4f83-bc04-b7baca0641a5";
        try {
            DeviceResponse retDr =  restClient.get()
                    .uri(baseUrl + "/{id}", id )
                    .retrieve()
                    .body(DeviceResponse.class);
            traceTestSummary("GET {id}", true, "["+id+"]");
            System.out.println("["+retDr.id()+"]");
            System.out.println("["+retDr.name()+","+retDr.brand()+","+retDr.state()+"]");
        } catch (Exception e) {
            traceTestSummary("GET {id}", false, "["+id+"]");
            System.out.println(e.getMessage());
        }
        // Step 3: Verify Cache is Updated
//        Cache cache = cacheManager.getCache(ProductService.PRODUCT_CACHE);
//        assertNotNull(cache);
//        ProductDto cachedProduct = cache.get(product.getId(), ProductDto.class);
//        assertNotNull(cachedProduct);

    }

    /**
     * generic method to fetch the existing devices
     *
     * @param param - optional parameter name to filter the returned set of devices
     * @param value - optional parameter value to filter the returned set of devices
     * @return list of devices
     */
    Device[] fetchExistingDevices(String param, String value) {
        String uri = baseUrl ;
        if (!ObjectUtils.isEmpty(param)) {
            uri+= "?"+param+"="+value;
        }
        Device[] devicesLst = restClient.get()
                .uri(uri)
                .retrieve()
                .body(Device[].class);
        return devicesLst;
    }
    void traceTestSummary(String method, boolean success, String message) {
        System.out.println();
        System.out.println();
        System.out.println("-".repeat(80));
        System.out.println();
        System.out.println(method + " : " + (success?"OK":"FAILED"));
        if (!ObjectUtils.isEmpty(message)) {
            System.out.println(message);
        }
        System.out.println();
    }


}