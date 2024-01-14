package com.Leave.Management.Dto;

import com.Leave.Management.Entity.Status;
import com.Leave.Management.Entity.UserInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveDTO {
    @Temporal(TemporalType.DATE)
    private LocalDate fromDate;
    @Temporal(TemporalType.DATE)
    private LocalDate toDate;
    private String leaveType;
    private String remark;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Long userId;
}
