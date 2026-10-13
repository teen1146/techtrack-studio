/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package techtrackstudio;

/**
 *
 * @author midnight
 */
// สืบทอดคุณสมบัติ (Inheritance) จากคลาสแม่
public class MobileDevice extends AbstractDevice {

    public MobileDevice(String deviceName, String issue) {
        super(deviceName, issue); 
    }

    // เขียนทับเมธอด (Polymorphism) เพื่อกำหนดราคาขั้นต่ำของมือถือ
    @Override
    public double getBaseRepairCost() {
        return 300.0; // ค่าเปิดเครื่องมือถือ
    }
}