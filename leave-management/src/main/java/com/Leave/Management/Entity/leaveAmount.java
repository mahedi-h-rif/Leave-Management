package com.Leave.Management.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class leaveAmount {
    @Id
    @NotNull
    private Long id;
    private Long sickLeave=10L;
    private Long casualLeave=10L;
}

