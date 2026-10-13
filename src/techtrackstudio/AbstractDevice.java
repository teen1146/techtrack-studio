/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package techtrackstudio;

/**
 *
 * @author midnight
 */
public abstract class AbstractDevice {
    // ซ่อนตัวแปร (Encapsulation)
    private String deviceName;
    private String issue;

    public AbstractDevice(String deviceName, String issue) {
        this.deviceName = deviceName;
        this.issue = issue;
    }

    // Getter & Setter (Encapsulation)
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    // เมธอดนามธรรม (Polymorphism) ให้คลาสลูกไปปรับใช้
    public abstract double getBaseRepairCost(); 
}