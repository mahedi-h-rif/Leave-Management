package com.Leave.Management.Dto;

import lombok.Data;

@Data
public class UserInfoWithoutPasswordDTO {
    private String name;
    private String email;
    private String role;
    private Long sickLeave;
    private Long casualLeave;
    private Long customLeave;
    private Long maximumCustomLeave;
}
