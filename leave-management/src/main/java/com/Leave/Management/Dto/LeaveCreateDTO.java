package com.Leave.Management.Dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveCreateDTO {
    @Temporal(TemporalType.DATE)
    private LocalDate fromDate;
    @Temporal(TemporalType.DATE)
    private LocalDate toDate;
    private String leaveType;
    private String remark;
}
