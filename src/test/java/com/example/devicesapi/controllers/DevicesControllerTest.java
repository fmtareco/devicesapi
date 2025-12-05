package com.example.devicesapi.controllers;

import com.example.devicesapi.dtos.DeviceCreateRequest;
import com.example.devicesapi.dtos.DeviceResponse;
import com.example.devicesapi.dtos.DeviceUpdateRequest;
import com.example.devicesapi.entities.Device;
import com.example.devicesapi.entities.Device.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DeviceControllerTest {

    /**
     * RestClient interface to perform API http calls
      */
    static public RestClient restClient;

    /**
     * API endpoint elements
     */
    static final String host = "localhost";
    static final String port = "8080";
    static String protocol = "http";
    static String endPoint = "/api/devices";
    static String baseUrl ;

    /**
     * Sample test data
     */
    enum Brands {
        Apple,
        Samsung,
    }
    enum Models {
        Iphone,
        Galaxy,
    }


    /**
     * list of created devices during the tests
     * (eliminated at end)
     */
    static List<DeviceResponse> createdDevices ;


    /**
     * test battery setup:
     * - create API endpoint
     * - create RestClient http handler
     * - inits list of created devices
     */
    @BeforeAll
    public static void setUp() {
        restClient = RestClient.create();
        baseUrl = protocol+ "://" + host + ":" + port + endPoint;
        createdDevices = new ArrayList<>();
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
    void getAllBefore() {
        testFetchDevices("getAllBefore", null, null);
    }

    /**
     * test 2
     * - creates (and also fails) a set of devices determined by the values
     * returned by the method source
     */
    @Order(2)
    @ParameterizedTest
    @MethodSource("provideValuesForCreation")
    void createNewDevice( String name, String brand, String state) {
        testCreateDevice("createNewDevice", name, brand, state);
    }

    /**
     * test 3
     * - full updates on the new devices, determined by the values
     * returned by the method source
     */
    @Order(3)
    @ParameterizedTest
    @MethodSource("provideValuesForFullUpdate")
    void testFullUpdates(UUID id, String name, String brand, String state) {
        testFullUpdateDevice("testFullUpdates", id, name, brand, state);
    }

    /**
     * test 4
     * - partial updates on the new devices, determined by the values
     * returned by the method source
     */
    @Order(4)
    @ParameterizedTest
    @MethodSource("provideValuesForPartialUpdate")
    void testPartialUpdates(UUID id, String name, String brand, String state) {
        testPartialUpdateDevice("testPartialUpdates", id, name, brand, state);
    }

    /**
     * test 5
     * - updates the state (to IN_USE) of the new devices, according the values
     * returned by the method source
     *
     */
    @Order(5)
    @ParameterizedTest
    @MethodSource("provideValuesForUpdateLock")
    void testUpdateLocks(UUID id, String name, String brand, String state) {
        testPartialUpdateDevice("testUpdateLocks", id, name, brand, state);
    }

    /**
     * test 6
     * - expected failed deletes, due to the devices locked state
     */
    @Order(6)
    @ParameterizedTest
    @MethodSource("provideCreatedDevices")
    void failedDeletes(DeviceResponse dr) {
        testDeleteDevice("failedDeletes", dr);
    }

    /**
     * test 7
     * - single selection (GET) to the devices
     */
    @Order(7)
    @ParameterizedTest
    @MethodSource("provideCreatedDevices")
    void findCreatedDevice(DeviceResponse dr) {
        testFindDevice("findCreatedDevice", dr);
    }

    /**
     * test 8
     * - multiple selection (GET?brand=) to all the devices of a particular brand
     * scans all brand of the enum
     */
    @Order(8)
    @ParameterizedTest
    @EnumSource(Brands.class)
    void getAllOfBrand(Brands brand) {
        testFetchDevices("getAllOfBrand", "brand", brand.toString());
    }

    /**
     * test 9
     * - multiple selection (GET?state=) to all the devices on a particular state
     * scans all states of the enum
     */
    @Order(9)
    @ParameterizedTest
    @EnumSource(Device.State.class)
    void getAllOfState(State state) {
        testFetchDevices("getAllOfState", "state", state.toString());
    }

    /**
     * test 10
     * - updates the state (to AVAILABLE) of the new devices, according the values
     * returned by the method source
     *
     */
    @Order(10)
    @ParameterizedTest
    @MethodSource("provideValuesForUpdateUnlock")
    void testUpdateUnlocks(UUID id, String name, String brand, String state) {
        testPartialUpdateDevice("testUpdateUnlocks", id, name, brand, state);
    }

    /**
     * test 11
     * - delete of all the created devices during the tests
     */
    @Order(11)
    @ParameterizedTest
    @MethodSource("provideCreatedDevices")
    void finalDelete(DeviceResponse dr) {
        testDeleteDevice("finalDelete", dr);
    }

    /**
     * test 12
     * - lists all the existent devices after all the tests
     */
    @Test
    @Order(12)
    void getAllAfter() {
        testFetchDevices("getAllAfter", null, null);
    }



    //---------------------------------------------------------------------------------------//
    //                                   utility methods                                     //
    //---------------------------------------------------------------------------------------//

    /**
     * traces the outcome of a particular test method
     *
     * @param method - called method
     * @param success - whether the test succeeded or failed
     * @param message - context message to display
     */
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

    /**
     * generic method to execute a multiple selection
     *
     * @param method - executing test method
     * @param param - optional parameter name to filter the returned set of devices
     * @param value - optional parameter value to filter the returned set of devices
     */
    void testFetchDevices(String method, String param, String value) {
        String uri = baseUrl ;
        String msg = "Total devices";
        if (!ObjectUtils.isEmpty(param)) {
            uri+= "?"+param+"="+value;
            msg += " of " +  param + "="+value;
        }
        Device[] payload = restClient.get()
                .uri(uri)
                .retrieve()
                .body(Device[].class);
        int numDevices=0;
        if (payload != null) {
            numDevices = payload.length;
        }
        msg += " : " + numDevices;
        traceTestSummary(method, true, msg);
        if (payload != null) {
            Arrays.stream(payload).forEach(System.out::println);
        }
    }

    /**
     * generic method to execute a device creation
     *
     * @param method - executing test method
     * @param name - name of the device to be created
     * @param brand - brand of the device to be created
     * @param state - state of the device to be created
     */
    void testCreateDevice(String method, String name, String brand, String state) {
        try {
            DeviceCreateRequest dcr = DeviceCreateRequest.builder()
                    .name(name)
                    .brand(brand)
                    .state(state)
                    .build();
            DeviceResponse dr = restClient.post()
                    .uri(baseUrl)
                    .contentType(APPLICATION_JSON)
                    .body(dcr)
                    .retrieve()
                    .body(DeviceResponse.class);
            traceTestSummary(method, true, "["+name+","+brand+","+state+"]");
            System.out.println(dr.getId());
            createdDevices.add(dr);
        } catch (Exception e) {
            traceTestSummary(method, false, "["+name+","+brand+","+state+"]");
            System.out.println(e.getMessage());
        }
    }

    /**
     * generic method to execute a single device selection
     *
     * @param method - executing test method
     * @param dr - response with the id of the device to select
     */
    void testFindDevice(String method, DeviceResponse dr) {
        try {
            DeviceResponse retDr =  restClient.get()
                    .uri(baseUrl + "/{id}", dr.getId() )
                    .retrieve()
                    .body(DeviceResponse.class);
            traceTestSummary(method, true, "["+dr.getId()+"]");
            System.out.println("["+retDr.getId()+"]");
            System.out.println("["+retDr.getName()+","+retDr.getBrand()+","+retDr.getState()+"]");
        } catch (Exception e) {
            traceTestSummary(method, false, "["+dr.getId()+"]");
            System.out.println("["+dr.getName()+","+dr.getBrand()+","+dr.getState()+"]");
            System.out.println(e.getMessage());
        }
    }

    /**
     * generic method to execute a single device elimination
     *
     * @param method - executing test method
     * @param dr - response with the id of the device to delete
     */
    void testDeleteDevice(String method, DeviceResponse dr) {
        try {
            restClient.delete()
                    .uri(baseUrl + "/{id}", dr.getId() )
                    .retrieve()
                    .toBodilessEntity();
            traceTestSummary(method, true, "["+dr.getId()+"]");
            System.out.println("["+dr.getName()+","+dr.getBrand()+","+dr.getState()+"]");
        } catch (Exception e) {
            traceTestSummary(method, false, "["+dr.getId()+"]");
            System.out.println("["+dr.getName()+","+dr.getBrand()+","+dr.getState()+"]");
            System.out.println(e.getMessage());
        }
    }

    /**
     * generic method to execute a device partial update
     *
     * @param method - executing test method
     * @param id -  id of the device to update
     * @param name - (optional) new name of the device
     * @param brand - (optional) new brand of the device
     * @param state - (optional) new state of the device
     */
    void testPartialUpdateDevice(String method, UUID id, String name, String brand, String state) {
        try {
            DeviceUpdateRequest dur = DeviceUpdateRequest.builder()
                    .name(name)
                    .brand(brand)
                    .state(state)
                    .build();
            restClient.patch()
                    .uri(baseUrl + "/{id}", id )
                    .contentType(APPLICATION_JSON)
                    .body(dur)
                    .retrieve()
                    .body(DeviceResponse.class);
            traceTestSummary(method, true, "["+id+"]");
            System.out.println("["+name+","+brand+","+state+"]");
        } catch (Exception e) {
            traceTestSummary(method, false, "["+id+"]");
            System.out.println("["+name+","+brand+","+state+"]");
            System.out.println(e.getMessage());
        }
    }

    /**
     * generic method to execute a device full update
     *
     * @param method - executing test method
     * @param id -  id of the device to update
     * @param name - new name of the device
     * @param brand - new brand of the device
     * @param state - new state of the device
     */
    void testFullUpdateDevice(String method, UUID id, String name, String brand, String state) {
        try {
            DeviceUpdateRequest dur = DeviceUpdateRequest.builder()
                    .name(name)
                    .brand(brand)
                    .state(state)
                    .build();
            restClient.put()
                    .uri(baseUrl + "/{id}", id )
                    .contentType(APPLICATION_JSON)
                    .body(dur)
                    .retrieve()
                    .body(DeviceResponse.class);
            traceTestSummary(method, true, "["+id+"]");
            System.out.println("["+name+","+brand+","+state+"]");
        } catch (Exception e) {
            traceTestSummary(method, false, "["+id+"]");
            System.out.println("["+name+","+brand+","+state+"]");
            System.out.println(e.getMessage());
        }
    }



    //---------------------------------------------------------------------------------------//
    //                                  values provider methods                              //
    //---------------------------------------------------------------------------------------//

    /**
     * @return a set of values corresponding to the new devices
     *
     */
    private static Stream<Arguments> provideCreatedDevices() {
        return createdDevices.stream()
                .map(Arguments::of);
    }

    /**
     * @return a set of values to test the devices creation
     *
     */
    private static Stream<Arguments> provideValuesForCreation() {
        return Stream.of(
                Arguments.of(Models.Iphone.toString(), Brands.Apple.toString(), Device.State.AVAILABLE.toString()),
                Arguments.of(Models.Iphone.toString(), Brands.Apple.toString(), Device.State.AVAILABLE.toString()),
                Arguments.of(null, Brands.Samsung.toString(), Device.State.AVAILABLE.toString()),
                Arguments.of(Models.Galaxy.toString(), null, Device.State.AVAILABLE.toString()),
                Arguments.of(Models.Galaxy.toString(), Brands.Samsung.toString(), null),
                Arguments.of(Models.Galaxy.toString(), Brands.Samsung.toString(), "InvalidState"),
                Arguments.of(Models.Galaxy.toString(), Brands.Samsung.toString(), Device.State.INACTIVE.toString())
        );
    }

    /**
     * @return a set of values to test the devices partial update
     *
     */
    private static Stream<Arguments> provideValuesForPartialUpdate() {
        List<Arguments> args = new ArrayList<>();
        if (createdDevices.isEmpty())
            return args.stream();
        UUID id = createdDevices.getFirst().getId();
        args.add(Arguments.of(id, null, null, null));
        List<String> models = new ArrayList<>();
        models.add("");
        for(Models mdl : Models.values()) {
            models.add(mdl.toString());
        }
        List<String> brands = new ArrayList<>();
        brands.add("");
        for(Brands brd : Brands.values())  {
            brands.add(brd.toString());
        }
        for(String model : models) {
            for(String brand : brands) {
                args.add(Arguments.of(id, model, brand, null));
            }
        }
        args.add(Arguments.of(id, null, null, Device.State.INACTIVE.toString()));
        args.add(Arguments.of(id, null, null, Device.State.AVAILABLE.toString()));
        args.add(Arguments.of(id, null, null, "UnknowState"));
        args.add(Arguments.of(id, null, null, Device.State.IN_USE.toString()));
        args.add(Arguments.of(id, "SommeModel", null, null));
        args.add(Arguments.of(id, null, "SomeBrand", null));
        args.add(Arguments.of(id, null, null, Device.State.AVAILABLE.toString()));
        return args.stream();
    }

    /**
     * @return a set of values to test the devices full update
     *
     */
    private static Stream<Arguments> provideValuesForFullUpdate() {
        List<Arguments> args = new ArrayList<>();
        if (createdDevices.isEmpty())
            return args.stream();
        DeviceResponse dr=createdDevices.getFirst();
        UUID id = dr.getId();
        List<String> models = new ArrayList<>();
        for(Models mdl : Models.values()) {
            models.add(mdl.toString());
        }
        List<String> brands = new ArrayList<>();
        for(Brands brd : Brands.values())  {
            brands.add(brd.toString());
        }
        for(String model : models) {
            for(String brand : brands) {
                args.add(Arguments.of(id, model, brand, Device.State.AVAILABLE.toString()));
            }
        }
        String model =dr.getName();
        String brand = dr.getBrand();
        args.add(Arguments.of(id, model, brand, Device.State.AVAILABLE.toString()));
        args.add(Arguments.of(id, model, brand, "UnknowState"));
        args.add(Arguments.of(id, model, brand, Device.State.IN_USE.toString()));
        args.add(Arguments.of(id, "SommeModel", brand, Device.State.AVAILABLE.toString()));
        args.add(Arguments.of(id, model, "SomeBrand", Device.State.AVAILABLE.toString()));
        args.add(Arguments.of(id, model, brand, Device.State.IN_USE.toString()));
        args.add(Arguments.of(id, "SommeModel", "SomeBrand", Device.State.AVAILABLE.toString()));
        return args.stream();
    }

    /**
     * @return a set of values to test the devices state update (to IN_USE)
     *
     */
    private static Stream<Arguments> provideValuesForUpdateLock() {
        List<Arguments> args = new ArrayList<>();
        for (DeviceResponse dr : createdDevices) {
            args.add(Arguments.of(dr.getId(), null, null, Device.State.IN_USE.toString()));
        }
        return args.stream();
    }

    /**
     * @return a set of values to test the devices state update (to AVAILABLE)
     *
     */
    private static Stream<Arguments> provideValuesForUpdateUnlock() {
        List<Arguments> args = new ArrayList<>();
        for (DeviceResponse dr : createdDevices) {
            args.add(Arguments.of(dr.getId(), null, null, Device.State.AVAILABLE.toString()));
        }
        return args.stream();
    }


}