package com.example.devicesapi.repository;

import com.example.devicesapi.entities.Device;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final DevicesRepository repo;

    public DataLoader(DevicesRepository _repo) {
        this.repo = _repo;
    }

    @Override
    public void run(String... args) throws Exception {
        loadDevice("Evolve", "BlackBerry");
        loadDevice("keyone", "BlackBerry");
        loadDevice("Leap", "BlackBerry");
        loadDevice("Passport", "BlackBerry");

        loadDevice("R600", "Ericsson");
        loadDevice("T36", "Ericsson");
        loadDevice("A2618", "Ericsson");
        loadDevice("R380", "Ericsson");

        loadDevice("Moto G57", "Motorola");
        loadDevice("Moto Pad 60 Neo", "Motorola");
        loadDevice("G96", "Motorola");
        loadDevice("Edge 60", "Motorola");

        loadDevice("3210", "Nokia");
        loadDevice("C300", "Nokia");
        loadDevice("XR21", "Nokia");
        loadDevice("G400", "Nokia");

        loadDevice("Poco F8 Ultra", "Xiaomi");
        loadDevice("Poco Pad X1", "Xiaomi");
        loadDevice("Redmi K90", "Xiaomi");
        loadDevice("Pad 8 Pro", "Xiaomi");

        loadDevice("Galaxy M17", "Samsung");
        loadDevice("Galaxy A26", "Samsung");
        loadDevice("Galaxy ZFold7", "Samsung");
        loadDevice("PGalaxy Tab S11", "Samsung");

        loadDevice("IPhone 17", "Apple");
        loadDevice("IPhone Air", "Apple");
        loadDevice("IPhone 16 Pro", "Apple");
        loadDevice("IPad Air 13", "Apple");

        loadDevice("Xperia 1 V", "Sony");
        loadDevice("Xperia 10", "Sony");
        loadDevice("Xperia L2", "Sony");
        loadDevice("Xperia XA1 Ultra", "Sony");

        loadDevice("9600", "Qtek");
        loadDevice("A9100", "Qtek");
        loadDevice("8020", "Qtek");
        loadDevice("S100", "Qtek");

        loadDevice("Pad Pro", "OnePlus");
        loadDevice("Ace 3V", "OnePlus");
        loadDevice("13T", "OnePlus");
        loadDevice("15", "OnePlus");

        loadDevice("Mate X7", "Huawei");
        loadDevice("nova 14 Lite", "Huawei");
        loadDevice("Pura 80 Pro+", "Huawei");
        loadDevice("Nova Y73", "Huawei");

        loadDevice("W31", "LG");
        loadDevice("K42", "LG");
        loadDevice("Q31", "LG");
        loadDevice("Velvet", "LG");

        loadDevice("A62", "Alcatel");
        loadDevice("1B", "Alcatel");
        loadDevice("3T 8", "Alcatel");
        loadDevice("Tetra", "Alcatel");

        loadDevice("GSmart M3447", "Gigabyte");
        loadDevice("GSmart i350", "Gigabyte");
        loadDevice("GSmart Simba SX1", "Gigabyte");
        loadDevice("GSmart Aku A1", "Gigabyte");

        loadDevice("M800", "Mitsubishi");
        loadDevice("Trium Eclipse", "Mitsubishi");
        loadDevice("Trium Mars", "Mitsubishi");
        loadDevice("M520", "Mitsubishi");

        loadDevice("Pixi", "Palm");
        loadDevice("Treo Pro", "Palm");
        loadDevice("Pre 2", "Palm");
        loadDevice("Centro", "Palm");

        loadDevice("A31", "Siemens");
        loadDevice("S75", "Siemens");
        loadDevice("CX75", "Siemens");
        loadDevice("SL65", "Siemens");


    }

    private void loadDevice(String name, String brand) {
        if (repo.findDeviceByNameAndBrand(name,brand).isEmpty())
            repo.save(Device.create(name, brand));
    }
}