package com.bookify.service;

import com.bookify.dto.AppointmentDTO;
import com.bookify.dto.ServiceDTO;
import com.bookify.dto.UserDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * StorageService - Handles file I/O for JSON and Java Object Serialization
 * Requirement: Store and read application data using JSON format
 * Requirement: Store and read application data using Java Object Stream format
 * Requirement: Compare file sizes and time to load for the two formats
 */
@Service
public class StorageService {

    private static final String DATA_DIR = "data/";
    private static final String JSON_FILE = DATA_DIR + "bookify_data.json";
    private static final String SERIALIZED_FILE = DATA_DIR + "bookify_data.ser";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public StorageService() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    /**
     * Save data to JSON format
     */
    public long saveToJSON(List<UserDTO> users, List<ServiceDTO> services, List<AppointmentDTO> appointments)
            throws IOException {

        long startTime = System.currentTimeMillis();

        JSONObject root = new JSONObject();

        // Convert Users to JSON
        JSONArray usersArray = new JSONArray();
        for (UserDTO user : users) {
            JSONObject userObj = new JSONObject();
            userObj.put("id", user.getId());
            userObj.put("username", user.getUsername());
            userObj.put("email", user.getEmail());
            userObj.put("fullName", user.getFullName());
            userObj.put("phone", user.getPhone());
            userObj.put("role", user.getRole() != null ? user.getRole().name() : null);
            userObj.put("status", user.getStatus() != null ? user.getStatus().name() : null);
            userObj.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : null);
            usersArray.put(userObj);
        }
        root.put("users", usersArray);

        // Convert Services to JSON
        JSONArray servicesArray = new JSONArray();
        for (ServiceDTO service : services) {
            JSONObject serviceObj = new JSONObject();
            serviceObj.put("id", service.getId());
            serviceObj.put("name", service.getName());
            serviceObj.put("description", service.getDescription());
            serviceObj.put("durationMinutes", service.getDurationMinutes());
            serviceObj.put("price", service.getPrice());
            serviceObj.put("active", service.getActive());
            servicesArray.put(serviceObj);
        }
        root.put("services", servicesArray);

        // Convert Appointments to JSON
        JSONArray appointmentsArray = new JSONArray();
        for (AppointmentDTO appointment : appointments) {
            JSONObject apptObj = new JSONObject();
            apptObj.put("id", appointment.getId());
            apptObj.put("clientId", appointment.getClientId());
            apptObj.put("serviceId", appointment.getServiceId());
            apptObj.put("appointmentDateTime", appointment.getAppointmentDateTime().format(formatter));
            apptObj.put("status", appointment.getStatus().name());
            apptObj.put("notes", appointment.getNotes());
            appointmentsArray.put(apptObj);
        }
        root.put("appointments", appointmentsArray);

        // Write to file
        try (FileWriter file = new FileWriter(JSON_FILE)) {
            file.write(root.toString(2)); // Pretty print with indent
        }

        long endTime = System.currentTimeMillis();
        long fileSize = new File(JSON_FILE).length();

        System.out.println("JSON Storage:");
        System.out.println("  - Time to save: " + (endTime - startTime) + " ms");
        System.out.println("  - File size: " + fileSize + " bytes");

        return fileSize;
    }

    /**
     * Read data from JSON format
     */
    public DataWrapper readFromJSON() throws IOException {
        long startTime = System.currentTimeMillis();

        String content = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
        JSONObject root = new JSONObject(content);

        List<UserDTO> users = new ArrayList<>();
        List<ServiceDTO> services = new ArrayList<>();
        List<AppointmentDTO> appointments = new ArrayList<>();

        // Parse Users
        JSONArray usersArray = root.getJSONArray("users");
        for (int i = 0; i < usersArray.length(); i++) {
            JSONObject userObj = usersArray.getJSONObject(i);
            UserDTO user = UserDTO.builder()
                    .id(userObj.optLong("id"))
                    .username(userObj.optString("username"))
                    .email(userObj.optString("email"))
                    .fullName(userObj.optString("fullName"))
                    .phone(userObj.optString("phone", null))
                    .build();
            users.add(user);
        }

        // Parse Services
        JSONArray servicesArray = root.getJSONArray("services");
        for (int i = 0; i < servicesArray.length(); i++) {
            JSONObject serviceObj = servicesArray.getJSONObject(i);
            ServiceDTO service = ServiceDTO.builder()
                    .id(serviceObj.optLong("id"))
                    .name(serviceObj.optString("name"))
                    .description(serviceObj.optString("description"))
                    .durationMinutes(serviceObj.optInt("durationMinutes"))
                    .active(serviceObj.optBoolean("active"))
                    .build();
            services.add(service);
        }

        // Parse Appointments
        JSONArray appointmentsArray = root.getJSONArray("appointments");
        for (int i = 0; i < appointmentsArray.length(); i++) {
            JSONObject apptObj = appointmentsArray.getJSONObject(i);
            AppointmentDTO appointment = AppointmentDTO.builder()
                    .id(apptObj.optLong("id"))
                    .clientId(apptObj.optLong("clientId"))
                    .serviceId(apptObj.optLong("serviceId"))
                    .appointmentDateTime(LocalDateTime.parse(apptObj.optString("appointmentDateTime"), formatter))
                    .notes(apptObj.optString("notes", null))
                    .build();
            appointments.add(appointment);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("  - Time to load: " + (endTime - startTime) + " ms");

        return new DataWrapper(users, services, appointments);
    }

    /**
     * Save data using Java Object Serialization
     */
    public long saveToSerialized(List<UserDTO> users, List<ServiceDTO> services, List<AppointmentDTO> appointments)
            throws IOException {

        long startTime = System.currentTimeMillis();

        DataWrapper data = new DataWrapper(users, services, appointments);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIALIZED_FILE))) {
            oos.writeObject(data);
        }

        long endTime = System.currentTimeMillis();
        long fileSize = new File(SERIALIZED_FILE).length();

        System.out.println("\nJava Serialization:");
        System.out.println("  - Time to save: " + (endTime - startTime) + " ms");
        System.out.println("  - File size: " + fileSize + " bytes");

        return fileSize;
    }

    /**
     * Read data from Java Object Serialization
     */
    public DataWrapper readFromSerialized() throws IOException, ClassNotFoundException {
        long startTime = System.currentTimeMillis();

        DataWrapper data;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SERIALIZED_FILE))) {
            data = (DataWrapper) ois.readObject();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("  - Time to load: " + (endTime - startTime) + " ms");

        return data;
    }

    /**
     * Compare both formats
     */
    public void compareFormats(List<UserDTO> users, List<ServiceDTO> services, List<AppointmentDTO> appointments) {
        try {
            System.out.println("\n=== Storage Format Comparison ===");

            long jsonSize = saveToJSON(users, services, appointments);
            long serSize = saveToSerialized(users, services, appointments);

            System.out.println("\n=== Comparison Results ===");
            System.out.println("JSON is " + ((serSize > jsonSize) ? "smaller" : "larger") +
                    " by " + Math.abs(jsonSize - serSize) + " bytes");

        } catch (IOException e) {
            System.err.println("Error during comparison: " + e.getMessage());
        }
    }

    /**
     * Data wrapper class for serialization
     */
    public static class DataWrapper implements Serializable {
        private static final long serialVersionUID = 1L;

        private List<UserDTO> users;
        private List<ServiceDTO> services;
        private List<AppointmentDTO> appointments;

        public DataWrapper(List<UserDTO> users, List<ServiceDTO> services, List<AppointmentDTO> appointments) {
            this.users = users;
            this.services = services;
            this.appointments = appointments;
        }

        public List<UserDTO> getUsers() { return users; }
        public List<ServiceDTO> getServices() { return services; }
        public List<AppointmentDTO> getAppointments() { return appointments; }
    }
}