package com.panchayat.panchayat_desk.repository;

import com.panchayat.panchayat_desk.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPaidByIdOrderByPaidAtDesc(Long userId);
    Optional<Payment> findByBillId(Long billId);
}
