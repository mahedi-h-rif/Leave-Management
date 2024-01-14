package com.Leave.Management.Repository;

import com.Leave.Management.Entity.Leave;
import com.Leave.Management.Entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave,Long> {

   /* @Query("SELECT l FROM Leave l WHERE l.userId = :userId")
    Page<Leave> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT l FROM Leave l WHERE l.status = :status")
    Page<Leave> findAllByStatus(Status status, Pageable pageable);

    @Query("SELECT l FROM Leave l WHERE l.fromDate >= :fromDate AND l.toDate <= :toDate")
    Page<Leave> findAllByDateRange(LocalDate fromDate, LocalDate toDate, Pageable pageable);*/


    List<Leave> findByUserInfoId(Long userId);
    Page<Leave> findByStatus(PageRequest pageRequest, Status status);
    Page<Leave> findByFromDateBetween(PageRequest pageRequest, LocalDate fromDate, LocalDate toDate);
    Page<Leave> findByLeaveType(PageRequest pageRequest,String leaveType);
    Page<Leave> findByStatusAndLeaveType(PageRequest pageRequest,Status status, String leaveType);

}
