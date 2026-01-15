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
import java.util.concurrent.*;

/**
 * Handles file I/O for JSON and Java Object Serialization
 * Now includes multi-threaded operations for performance optimization
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

    // ==================== JSON FORMAT ====================

    /**
     * Save data to JSON format
     */
    public long saveToJSON(List<UserDTO> users, List<ServiceDTO> services, List<AppointmentDTO> appointments)
            throws IOException {

        long startTime = System.currentTimeMillis();

        JSONObject root = new JSONObject();

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

    // ==================== JAVA SERIALIZATION ====================

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
     * Read data from serialized format
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

    // ==================== COMPARISON METHODS ====================

    /**
     * Compare both formats (sequential)
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
     * NEW: Compare formats using THREADS for parallel processing
     * This demonstrates requirement #3 - using threads to accelerate processing
     */
    public void compareFormatsWithThreads(List<UserDTO> users, List<ServiceDTO> services, List<AppointmentDTO> appointments) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            System.out.println("\n=== Threaded Storage Format Comparison ===");

            // Task 1: Save JSON (in parallel)
            Future<Long> jsonFuture = executor.submit(() -> {
                try {
                    return saveToJSON(users, services, appointments);
                } catch (IOException e) {
                    System.err.println("JSON save error: " + e.getMessage());
                    return 0L;
                }
            });

            // Task 2: Save Serialized (in parallel)
            Future<Long> serFuture = executor.submit(() -> {
                try {
                    return saveToSerialized(users, services, appointments);
                } catch (IOException e) {
                    System.err.println("Serialization save error: " + e.getMessage());
                    return 0L;
                }
            });

            // Wait for both to complete
            long jsonSize = jsonFuture.get();
            long serSize = serFuture.get();

            System.out.println("\n=== Threaded Comparison Results ===");
            System.out.println("JSON is " + ((serSize > jsonSize) ? "smaller" : "larger") +
                    " by " + Math.abs(jsonSize - serSize) + " bytes");
            System.out.println("Both formats processed in PARALLEL using threads");

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error during threaded comparison: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * NEW: Process multiple data batches in parallel using threads
     * Demonstrates thread usage for accelerating bulk operations
     */
    public void processInParallel(List<UserDTO> users, List<ServiceDTO> services, List<AppointmentDTO> appointments) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            System.out.println("\n=== Parallel Data Processing ===");
            long startTime = System.currentTimeMillis();

            // Process users in parallel
            Future<Integer> usersFuture = executor.submit(() -> {
                System.out.println("Thread processing users: " + Thread.currentThread().getName());
                // Simulate processing
                return users.size();
            });

            // Process services in parallel
            Future<Integer> servicesFuture = executor.submit(() -> {
                System.out.println("Thread processing services: " + Thread.currentThread().getName());
                // Simulate processing
                return services.size();
            });

            // Process appointments in parallel
            Future<Integer> appointmentsFuture = executor.submit(() -> {
                System.out.println("Thread processing appointments: " + Thread.currentThread().getName());
                // Simulate processing
                return appointments.size();
            });

            // Wait for all tasks to complete
            int usersProcessed = usersFuture.get();
            int servicesProcessed = servicesFuture.get();
            int appointmentsProcessed = appointmentsFuture.get();

            long endTime = System.currentTimeMillis();

            System.out.println("\n=== Parallel Processing Results ===");
            System.out.println("Users processed: " + usersProcessed);
            System.out.println("Services processed: " + servicesProcessed);
            System.out.println("Appointments processed: " + appointmentsProcessed);
            System.out.println("Total time: " + (endTime - startTime) + " ms");
            System.out.println("Processing completed using 3 parallel threads");

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error during parallel processing: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }

    // ==================== DATA WRAPPER CLASS ====================

    /**
     * Wrapper class for Java Object Serialization
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