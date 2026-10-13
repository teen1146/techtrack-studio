package techtrackstudio;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:repair_data.db";

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS repairs (" +
                "ticketID TEXT PRIMARY KEY, " +
                "customerName TEXT, " +
                "phone TEXT, " +
                "deviceName TEXT, " +
                "issue TEXT, " +
                "estimatedPrice REAL, " +
                "finalPrice REAL, " +
                "status TEXT, " +
                "receiveDateTime TEXT, " +
                "returnDateTime TEXT, " +
                "receiverName TEXT, " +
                "pickupProof TEXT)";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public static void insertJob(RepairJob job) {
        String sql = "INSERT INTO repairs(ticketID, customerName, phone, deviceName, issue, estimatedPrice, finalPrice, status, receiveDateTime, returnDateTime, receiverName, pickupProof) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, job.getTicketID());
            pstmt.setString(2, job.getCustomerName());
            pstmt.setString(3, job.getPhone());
            pstmt.setString(4, job.getDevice().getDeviceName());
            pstmt.setString(5, job.getDevice().getIssue());
            pstmt.setDouble(6, job.getEstimatedPrice());
            pstmt.setDouble(7, job.getFinalPrice());
            pstmt.setString(8, job.getStatus());
            pstmt.setString(9, job.getReceiveDateTime());
            pstmt.setString(10, job.getReturnDateTime());
            pstmt.setString(11, job.getReceiverName());
            pstmt.setString(12, job.getPickupProof());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }

    public static void updateJobToFinished(RepairJob job) {
        String sql = "UPDATE repairs SET finalPrice = ?, status = ?, returnDateTime = ?, receiverName = ?, pickupProof = ? WHERE ticketID = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, job.getFinalPrice());
            pstmt.setString(2, job.getStatus());
            pstmt.setString(3, job.getReturnDateTime());
            pstmt.setString(4, job.getReceiverName());
            pstmt.setString(5, job.getPickupProof());
            pstmt.setString(6, job.getTicketID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating data: " + e.getMessage());
        }
    }

    public static ArrayList<RepairJob> getAllJobs() {
        ArrayList<RepairJob> list = new ArrayList<>();
        String sql = "SELECT * FROM repairs";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String ticket = rs.getString("ticketID");
                String name = rs.getString("customerName");
                String phone = rs.getString("phone");
                String devName = rs.getString("deviceName");
                String issue = rs.getString("issue");
                double estPrice = rs.getDouble("estimatedPrice");
                double finalPrice = rs.getDouble("finalPrice");
                String status = rs.getString("status");
                
                AbstractDevice device = (devName.toLowerCase().contains("computer") || devName.toLowerCase().contains("laptop")) 
                                        ? new ComputerDevice(devName, issue) 
                                        : new MobileDevice(devName, issue);
                
                RepairJob job = new RepairJob(ticket, name, phone, device, status);
                job.setEstimatedPrice(estPrice);
                job.setFinalPrice(finalPrice);
                job.setReceiveDateTime(rs.getString("receiveDateTime"));
                job.setReturnDateTime(rs.getString("returnDateTime"));
                job.setReceiverName(rs.getString("receiverName"));
                job.setPickupProof(rs.getString("pickupProof"));
                
                list.add(job);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
        return list;
    }
}