package com.Leave.Management.Repository;

import com.Leave.Management.Entity.Leave;
import com.Leave.Management.Entity.Status;
import com.Leave.Management.Entity.UserInfo;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave,Long> {

    List<Leave> findByUserId(Long userId);
    List<Leave> findByStatus(Status status);
    List<Leave> findByFromDateBetween(LocalDate fromDate, LocalDate toDate);
    List<Leave> findByLeaveType(String leaveType);
    List<Leave> findByStatusAndLeaveType(Status status, String leaveType);

}
