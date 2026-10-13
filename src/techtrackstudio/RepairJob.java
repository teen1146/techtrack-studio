package techtrackstudio;

public class RepairJob {
    private String ticketID;
    private String customerName;
    private String phone;
    private AbstractDevice device;
    private double estimatedPrice;
    private String status; 
    
    // ข้อมูลตอนรับเครื่อง
    private String receiveDateTime; 
    
    // ข้อมูลตอนส่งมอบเครื่อง
    private double finalPrice;
    private String returnDateTime; // เปลี่ยนจาก returnDate เป็นเก็บทั้งวันและเวลา
    private String receiverName;
    private String pickupProof;

    public RepairJob(String ticketID, String customerName, String phone, AbstractDevice device, String status) {
        this.ticketID = ticketID;
        this.customerName = customerName;
        this.phone = phone;
        this.device = device;
        this.status = status;
        this.estimatedPrice = device.getBaseRepairCost(); 
    }

    public String getTicketID() { return ticketID; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public AbstractDevice getDevice() { return device; }
    
    public double getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(double estimatedPrice) { this.estimatedPrice = estimatedPrice; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReceiveDateTime() { return receiveDateTime; }
    public void setReceiveDateTime(String receiveDateTime) { this.receiveDateTime = receiveDateTime; }

    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }

    public String getReturnDateTime() { return returnDateTime; }
    public void setReturnDateTime(String returnDateTime) { this.returnDateTime = returnDateTime; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getPickupProof() { return pickupProof; }
    public void setPickupProof(String pickupProof) { this.pickupProof = pickupProof; }
}