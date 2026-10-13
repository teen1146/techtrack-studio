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
public class ComputerDevice extends AbstractDevice {

    public ComputerDevice(String deviceName, String issue) {
        super(deviceName, issue); 
    }

    // เขียนทับเมธอด (Polymorphism) เพื่อกำหนดราคาขั้นต่ำของคอมพิวเตอร์
    @Override
    public double getBaseRepairCost() {
        return 500.0; // ค่าเปิดเครื่องคอมพิวเตอร์
    }
}