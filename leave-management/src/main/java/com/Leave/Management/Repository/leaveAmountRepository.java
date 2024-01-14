package com.Leave.Management.Repository;

import com.Leave.Management.Entity.leaveAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface leaveAmountRepository extends JpaRepository<leaveAmount,Long> {
    Optional<leaveAmount> findById(Long id);
    void save(Optional<leaveAmount> leaveAmount);
}
