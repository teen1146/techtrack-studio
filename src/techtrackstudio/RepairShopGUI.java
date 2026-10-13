package techtrackstudio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RepairShopGUI extends JFrame {
    
    private JTable ongoingTable, historyTable;
    private DefaultTableModel ongoingModel, historyModel;
    private JTextField searchField;
    
    private Font mainFont = new Font("SansSerif", Font.PLAIN, 14);
    private Font boldFont = new Font("SansSerif", Font.BOLD, 14);
    
    private DefaultComboBoxModel<String> deviceComboModel = new DefaultComboBoxModel<>(new String[]{
        "Computer/Laptop", "Mobile/Tablet", "Printer", "--- พิมพ์เพิ่มเอง (อื่นๆ) ---"
    });
    
    public RepairShopGUI() {
        setTitle("TechTrack Studio - IT Repair Shop Manager");
        setSize(1050, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        DatabaseHelper.createTable();
        initUI();
        loadDataFromDB();
    }
    
    private void styleModernButton(JButton btn, Color normalColor, Color hoverColor) {
        btn.setBackground(normalColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(boldFont);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverColor); }
            public void mouseExited(MouseEvent e) { btn.setBackground(normalColor); }
        });
    }
    
    private void styleTable(JTable table) {
        table.setFont(mainFont);
        table.setRowHeight(35); 
        table.getTableHeader().setFont(boldFont);
        table.getTableHeader().setBackground(new Color(230, 230, 235));
        table.getTableHeader().setPreferredSize(new Dimension(100, 40));
        table.setSelectionBackground(new Color(173, 216, 230)); 
    }
    
    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(40, 44, 52));
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("TechTrack Studio [SQLite]");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);
        
        JButton addJobBtn = new JButton("➕ รับเครื่องซ่อมใหม่");
        styleModernButton(addJobBtn, new Color(46, 204, 113), new Color(39, 174, 96));
        
        searchField = new JTextField(20);
        searchField.setFont(mainFont);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            new EmptyBorder(8, 10, 8, 10) 
        ));
        
        JButton searchBtn = new JButton("🔍 ค้นหา");
        styleModernButton(searchBtn, new Color(52, 152, 219), new Color(41, 128, 185));
        
        actionPanel.add(searchField);
        actionPanel.add(searchBtn);
        actionPanel.add(addJobBtn);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(boldFont);
        
        // [อัปเดตคอลัมน์] เพิ่มคอลัมน์ "เวลารับเครื่อง" และ "เวลาส่งมอบ"
        String[] columns = {"Ticket ID", "ชื่อลูกค้า", "เบอร์โทร", "อุปกรณ์", "อาการเสีย", "ราคาประเมิน", "ราคาจริง", "เวลารับเครื่อง", "เวลาส่งมอบ", "สถานะ"};
        
        ongoingModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        ongoingTable = new JTable(ongoingModel);
        styleTable(ongoingTable);
        
        JPanel ongoingPanel = new JPanel(new BorderLayout());
        ongoingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        ongoingPanel.add(new JScrollPane(ongoingTable), BorderLayout.CENTER);
        
        JPanel bottomOngoingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomOngoingPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton finishJobBtn = new JButton("✅ ยืนยันส่งมอบเครื่อง");
        styleModernButton(finishJobBtn, new Color(155, 89, 182), new Color(142, 68, 173));
        bottomOngoingPanel.add(finishJobBtn);
        ongoingPanel.add(bottomOngoingPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("🔧 สถานะเครื่องปัจจุบัน", ongoingPanel);
        
        historyModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyModel);
        styleTable(historyTable);
        
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        tabbedPane.addTab("📂 ประวัติการซ่อม", historyPanel);
        
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        addJobBtn.addActionListener(e -> openAddJobDialog());
        finishJobBtn.addActionListener(e -> openDeliveryDialog()); 
        
        searchBtn.addActionListener(e -> searchJobs());
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { searchJobs(); }
        });
    }
    
    private void loadDataFromDB() {
        ongoingModel.setRowCount(0);
        historyModel.setRowCount(0);
        
        ArrayList<RepairJob> jobs = DatabaseHelper.getAllJobs();
        for (RepairJob job : jobs) {
            String finalPriceDisplay = job.getStatus().equals("สำเร็จ") ? String.valueOf(job.getFinalPrice()) : "-";
            String returnTimeDisplay = (job.getReturnDateTime() != null && !job.getReturnDateTime().isEmpty()) ? job.getReturnDateTime() : "-";
            
            Object[] row = {
                job.getTicketID(), job.getCustomerName(), job.getPhone(),
                job.getDevice().getDeviceName(), job.getDevice().getIssue(),
                job.getEstimatedPrice(), finalPriceDisplay, 
                job.getReceiveDateTime(), returnTimeDisplay, job.getStatus()
            };
            if (job.getStatus().equals("สำเร็จ")) {
                historyModel.addRow(row);
            } else {
                ongoingModel.addRow(row);
            }
        }
    }
    
    private void openDeliveryDialog() {
        int selectedRow = ongoingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "กรุณาคลิกเลือกคิวงานในตารางก่อนครับ", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = ongoingTable.convertRowIndexToModel(selectedRow);
        String ticketId = (String) ongoingModel.getValueAt(modelRow, 0);
        
        ArrayList<RepairJob> jobs = DatabaseHelper.getAllJobs();
        RepairJob tempJob = null;
        for (RepairJob job : jobs) {
            if (job.getTicketID().equals(ticketId)) { tempJob = job; break; }
        }
        if (tempJob == null) return;
        final RepairJob selectedJob = tempJob;
        
        JDialog dialog = new JDialog(this, "ส่งมอบเครื่อง: " + ticketId, true);
        dialog.setSize(450, 320);
        dialog.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(5, 2, 15, 15));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JTextField finalPriceField = new JTextField(String.valueOf(selectedJob.getEstimatedPrice()));
        finalPriceField.setFont(mainFont);
        finalPriceField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar()) && e.getKeyChar() != '.') e.consume(); }
        });
        
        // ดึงเวลาปัจจุบันอัตโนมัติ (วัน/เดือน/ปี ชั่วโมง:นาที)
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        JTextField dateTimeField = new JTextField(currentDateTime);
        dateTimeField.setFont(mainFont);
        
        JTextField receiverField = new JTextField(selectedJob.getCustomerName());
        receiverField.setFont(mainFont);
        JTextField proofField = new JTextField("เซ็นรับเรียบร้อย");
        proofField.setFont(mainFont);
        
        JLabel l1 = new JLabel(" ราคาหลังซ่อม (บาท):"); l1.setFont(boldFont);
        JLabel l2 = new JLabel(" เวลาส่งมอบ:"); l2.setFont(boldFont);
        JLabel l3 = new JLabel(" ผู้รับของคืน:"); l3.setFont(boldFont);
        JLabel l4 = new JLabel(" หมายเหตุ/หลักฐาน:"); l4.setFont(boldFont);
        
        p.add(l1); p.add(finalPriceField);
        p.add(l2); p.add(dateTimeField);
        p.add(l3); p.add(receiverField);
        p.add(l4); p.add(proofField);
        
        JButton confirmBtn = new JButton("ยืนยันส่งมอบ");
        styleModernButton(confirmBtn, new Color(46, 204, 113), new Color(39, 174, 96));
        
        confirmBtn.addActionListener(e -> {
            if (finalPriceField.getText().trim().isEmpty() || receiverField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "กรุณากรอกข้อมูลให้ครบ", "ผิดพลาด", JOptionPane.ERROR_MESSAGE); 
                return;
            }
            
            selectedJob.setFinalPrice(Double.parseDouble(finalPriceField.getText()));
            selectedJob.setReturnDateTime(dateTimeField.getText()); // บันทึกเวลาส่งมอบ
            selectedJob.setReceiverName(receiverField.getText());
            selectedJob.setPickupProof(proofField.getText());
            selectedJob.setStatus("สำเร็จ");
            
            DatabaseHelper.updateJobToFinished(selectedJob);
            
            dialog.dispose();
            loadDataFromDB(); 
            
            JOptionPane.showMessageDialog(this, "บันทึกเวลาส่งมอบและย้ายไปหน้าประวัติเรียบร้อย!");
        });
        
        dialog.setLayout(new BorderLayout());
        dialog.add(p, BorderLayout.CENTER);
        
        JPanel bottomP = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomP.setBorder(new EmptyBorder(0, 0, 15, 0));
        bottomP.add(confirmBtn);
        dialog.add(bottomP, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void openAddJobDialog() {
        JDialog dialog = new JDialog(this, "ฟอร์มรับเครื่องซ่อม", true);
        dialog.setSize(480, 420); 
        dialog.setLocationRelativeTo(this);
        
        JPanel p = new JPanel(new GridLayout(7, 2, 15, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField(); nameField.setFont(mainFont);
        JTextField phoneField = new JTextField(); phoneField.setFont(mainFont);
        
        JComboBox<String> deviceCombo = new JComboBox<>(deviceComboModel);
        deviceCombo.setFont(mainFont);
        deviceCombo.setEditable(true); 
        
        JLabel otherLabel = new JLabel(" ระบุชื่ออุปกรณ์:"); otherLabel.setFont(boldFont);
        JTextField otherField = new JTextField(); otherField.setFont(mainFont);
        otherLabel.setVisible(false);
        otherField.setVisible(false);
        
        deviceCombo.addActionListener(e -> {
            Object selected = deviceCombo.getSelectedItem();
            if (selected != null && selected.toString().contains("--- พิมพ์เพิ่มเอง")) {
                otherLabel.setVisible(true);
                otherField.setVisible(true);
            } else {
                otherLabel.setVisible(false);
                otherField.setVisible(false);
                otherField.setText("");
            }
            dialog.revalidate(); 
            dialog.repaint();
        });
        
        JTextField issueField = new JTextField(); issueField.setFont(mainFont);
        JTextField priceField = new JTextField(); priceField.setFont(mainFont);
        
        priceField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }
        });
        
        JLabel l1 = new JLabel(" ชื่อลูกค้า:"); l1.setFont(boldFont);
        JLabel l2 = new JLabel(" เบอร์โทร (10 หลัก):"); l2.setFont(boldFont);
        JLabel l3 = new JLabel(" ประเภทอุปกรณ์:"); l3.setFont(boldFont);
        JLabel l4 = new JLabel(" อาการเสีย:"); l4.setFont(boldFont);
        JLabel l5 = new JLabel(" ราคาประเมิน (บาท):"); l5.setFont(boldFont);
        
        p.add(l1); p.add(nameField);
        p.add(l2); p.add(phoneField);
        p.add(l3); p.add(deviceCombo);
        p.add(otherLabel); p.add(otherField); 
        p.add(l4); p.add(issueField);
        p.add(l5); p.add(priceField);
        
        JButton saveBtn = new JButton("บันทึกข้อมูล");
        styleModernButton(saveBtn, new Color(52, 152, 219), new Color(41, 128, 185));
        
        saveBtn.addActionListener(e -> {
            String phone = phoneField.getText();
            if (phone.length() != 10 || !phone.matches("\\d+")) {
                JOptionPane.showMessageDialog(dialog, "กรุณากรอกเบอร์โทรให้ถูกต้อง", "ผิดพลาด", JOptionPane.ERROR_MESSAGE); 
                return;
            }
            if (nameField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "กรุณากรอกข้อมูลให้ครบ", "ผิดพลาด", JOptionPane.ERROR_MESSAGE); 
                return;
            }
            
            String finalDeviceName = "";
            if (otherField.isVisible()) {
                if (otherField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "กรุณาระบุชื่ออุปกรณ์ในช่องเพิ่มเติม", "ผิดพลาด", JOptionPane.ERROR_MESSAGE); 
                    return;
                }
                finalDeviceName = otherField.getText().trim();
                
                boolean exists = false;
                for (int i = 0; i < deviceComboModel.getSize(); i++) {
                    if (deviceComboModel.getElementAt(i).equalsIgnoreCase(finalDeviceName)) {
                        exists = true; break;
                    }
                }
                if (!exists) {
                    deviceComboModel.insertElementAt(finalDeviceName, deviceComboModel.getSize() - 1);
                }
            } else {
                Object selectedItem = deviceCombo.getSelectedItem();
                if (selectedItem != null) {
                    finalDeviceName = selectedItem.toString().trim();
                }
            }
            
            if (finalDeviceName.isEmpty() || finalDeviceName.contains("---")) {
                JOptionPane.showMessageDialog(dialog, "กรุณาระบุชื่ออุปกรณ์ให้ถูกต้อง", "ผิดพลาด", JOptionPane.ERROR_MESSAGE); 
                return;
            }
            
            AbstractDevice device = (finalDeviceName.toLowerCase().contains("computer") || finalDeviceName.toLowerCase().contains("laptop")) 
                                    ? new ComputerDevice(finalDeviceName, issueField.getText()) 
                                    : new MobileDevice(finalDeviceName, issueField.getText());
            
            int totalRows = DatabaseHelper.getAllJobs().size();
            String ticket = "REP-" + (1001 + totalRows);
            
            RepairJob newJob = new RepairJob(ticket, nameField.getText(), phone, device, "รอคิว");
            newJob.setEstimatedPrice(Double.parseDouble(priceField.getText()));
            
            // [บันทึกเวลารับเครื่องปัจจุบันอัตโนมัติ]
            String currentReceiveTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            newJob.setReceiveDateTime(currentReceiveTime);
            
            DatabaseHelper.insertJob(newJob);
            
            dialog.dispose();
            loadDataFromDB();
            
            JOptionPane.showMessageDialog(this, "บันทึกรับเครื่องสำเร็จ! เวลารับ: " + currentReceiveTime);
        });
        
        dialog.setLayout(new BorderLayout());
        dialog.add(p, BorderLayout.CENTER);
        
        JPanel bottomP = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomP.setBorder(new EmptyBorder(0, 0, 15, 0));
        bottomP.add(saveBtn);
        dialog.add(bottomP, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void searchJobs() {
        String text = searchField.getText();
        TableRowSorter<DefaultTableModel> sorter1 = new TableRowSorter<>((DefaultTableModel) ongoingTable.getModel());
        ongoingTable.setRowSorter(sorter1);
        TableRowSorter<DefaultTableModel> sorter2 = new TableRowSorter<>((DefaultTableModel) historyTable.getModel());
        historyTable.setRowSorter(sorter2);
        
        if (text.trim().isEmpty()) {
            sorter1.setRowFilter(null); 
            sorter2.setRowFilter(null);
        } else {
            sorter1.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            sorter2.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new RepairShopGUI().setVisible(true));
    }
}