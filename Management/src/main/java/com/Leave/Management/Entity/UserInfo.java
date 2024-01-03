package com.Leave.Management.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.hibernate.engine.internal.Cascade;
import org.hibernate.mapping.List;

import java.util.ArrayList;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private Long SickLeave;
    private Long casualLeave;
    private Long CustomLeave;

}
