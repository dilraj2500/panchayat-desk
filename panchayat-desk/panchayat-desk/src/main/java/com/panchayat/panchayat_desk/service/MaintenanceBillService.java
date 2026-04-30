package com.panchayat.panchayat_desk.service;

import com.panchayat.panchayat_desk.model.MaintenanceBill;
import com.panchayat.panchayat_desk.model.Payment;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.repository.MaintenanceBillRepository;
import com.panchayat.panchayat_desk.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaintenanceBillService {

    private final MaintenanceBillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    // CREATE SINGLE BILL
    public MaintenanceBill createBill(MaintenanceBill bill) {
        return billRepository.save(bill);
    }

    // GENERATE BILLS FOR SOCIETY
    public void generateBillsForSociety(List<User> residents,
                                        Long societyId,
                                        BigDecimal amount,
                                        String billMonth,
                                        LocalDate dueDate) {

        for (User resident : residents) {

            MaintenanceBill bill = MaintenanceBill.builder()
                    .resident(resident)
                    .society(resident.getSociety())
                    .amount(amount)
                    .billMonth(billMonth)
                    .dueDate(dueDate)
                    .status(MaintenanceBill.BillStatus.UNPAID)
                    .build();

            billRepository.save(bill);

            // OPTIONAL: Send email on bill creation
            emailService.sendEmail(
                    resident.getEmail(),
                    "Maintenance Bill Generated",
                    "Dear " + resident.getName()
                            + ", your maintenance bill for " + billMonth
                            + " is generated. Amount: " + amount
            );
        }
    }

    // GET BILL BY ID
    public MaintenanceBill findById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found!"));
    }

    // RESIDENT BILLS
    public List<MaintenanceBill> findByResident(Long residentId) {
        return billRepository.findByResidentId(residentId);
    }

    // SOCIETY BILLS
    public List<MaintenanceBill> findBySociety(Long societyId) {
        return billRepository.findBySocietyId(societyId);
    }

    // UNPAID BILLS (IMPORTANT FIX 🔥)
    public List<MaintenanceBill> findUnpaidBySociety(Long societyId) {
        return billRepository.findBySocietyIdAndStatus(
                societyId,
                MaintenanceBill.BillStatus.UNPAID
        );
    }

    // COUNT UNPAID
    public long countUnpaidBySociety(Long societyId) {
        return billRepository.countBySocietyIdAndStatus(
                societyId,
                MaintenanceBill.BillStatus.UNPAID
        );
    }

    // PAYMENT
    public Payment payBill(Long billId, User paidBy, Payment.PaymentMethod method) {

        MaintenanceBill bill = findById(billId);

        if (bill.getStatus() == MaintenanceBill.BillStatus.PAID) {
            throw new RuntimeException("Bill already paid!");
        }

        bill.setStatus(MaintenanceBill.BillStatus.PAID);
        billRepository.save(bill);

        Payment payment = Payment.builder()
                .bill(bill)
                .paidBy(paidBy)
                .amountPaid(bill.getAmount())
                .paymentMethod(method)
                .transactionId("TXN-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // OPTIONAL EMAIL AFTER PAYMENT
        emailService.sendEmail(
                paidBy.getEmail(),
                "Payment Successful",
                "Your maintenance payment is successful for " + bill.getBillMonth()
        );

        return savedPayment;
    }

    // PAYMENT HISTORY
    public List<Payment> getPaymentHistory(Long userId) {
        return paymentRepository.findByPaidByIdOrderByPaidAtDesc(userId);
    }
}