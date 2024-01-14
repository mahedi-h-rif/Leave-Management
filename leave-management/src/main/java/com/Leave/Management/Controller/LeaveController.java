package com.Leave.Management.Controller;

import com.Leave.Management.Dto.LeaveCreateDTO;
import com.Leave.Management.Dto.LeaveDTO;
import com.Leave.Management.Entity.Leave;
import com.Leave.Management.Entity.Status;
import com.Leave.Management.Entity.UserInfo;
import com.Leave.Management.Service.LeaveService;
import com.Leave.Management.Service.UserInfoService;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    private final LeaveService leaveService;
    private final UserInfoService userInfoService;

    @GetMapping("/testing")
    public String testing(){
        return "this is for testingggggg";
    }

    @Autowired
    public LeaveController(LeaveService leaveService, UserInfoService userInfoService) {
        this.leaveService = leaveService;
        this.userInfoService = userInfoService;
    }

    @PostMapping("/createLeaveApplication")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','USER')")
    public ResponseEntity<LeaveDTO> createLeaveApplication(@RequestBody LeaveCreateDTO leave) {
        LeaveDTO createdLeave = leaveService.createLeaveApplication(leave,userEmail());
        return ResponseEntity.ok(createdLeave);
    }


    @PutMapping("/approveLeaveApplication/{leaveId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<LeaveDTO> approveLeaveApplication(@PathVariable Long leaveId) {
        LeaveDTO approvedLeave = leaveService.approveLeaveApplication(leaveId);
        return ResponseEntity.ok(approvedLeave);
    }

    @PutMapping("/rejectLeaveApplication/{leaveId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<LeaveDTO> rejectLeaveApplication(@PathVariable Long leaveId) {
        LeaveDTO rejectedLeave = leaveService.rejectLeaveApplication(leaveId);
        return ResponseEntity.ok(rejectedLeave);
    }

    @GetMapping("/getAllLeavesByUserId/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<LeaveDTO>> getAllLeavesByUserId(@PathVariable Long userId) {
        List<LeaveDTO> userLeaves = leaveService.getAllLeavesByUserId(userId);
        return ResponseEntity.ok(userLeaves);
    }

    @GetMapping("/userLeaveProfile")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','USER')")
    public ResponseEntity<List<LeaveDTO>> userLeaveProfile() {
        Optional<UserInfo> userInfo = userInfoService.getUserByEmail(userEmail());
        List<LeaveDTO> userLeaves = leaveService.getAllLeavesByUserId(userInfo.get().getId());
        return ResponseEntity.ok(userLeaves);
    }

    @GetMapping("/getAllLeavesByStatus/{status}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<LeaveDTO>> getAllLeavesByStatus(@RequestParam int offset, @RequestParam int pageSize,@RequestParam String field, @PathVariable Status status) {
        List<LeaveDTO> leavesByStatus = leaveService.getAllLeavesByStatus(offset,pageSize,field,status);
        return ResponseEntity.ok(leavesByStatus);
    }

    @GetMapping("/getAllLeavesByDateRange")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<LeaveDTO>> getAllLeavesByDateRange(
            @RequestParam int offset, @RequestParam int pageSize,@RequestParam String field,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate)
    {
        List<LeaveDTO> leavesByDateRange = leaveService.getAllLeavesByDateRange(offset,pageSize,field,fromDate, toDate);
        return ResponseEntity.ok(leavesByDateRange);
    }

    @GetMapping("/getAllLeavesByYear")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<LeaveDTO>> getAllLeavesByYear(
            @RequestParam int offset, @RequestParam int pageSize,@RequestParam String field,
            @RequestParam int year)
    {
        LocalDate fromDate= LocalDate.of(year,1,1);
        LocalDate toDate= LocalDate.of(year,12,31);
        //toDate.plusYears(year).plusMonths(12).plusDays(31);
        System.out.println(fromDate);
        System.out.println(toDate);
        List<LeaveDTO> leavesByDateRange = leaveService.getAllLeavesByDateRange(offset,pageSize,field,fromDate, toDate);
        return ResponseEntity.ok(leavesByDateRange);
    }

    @GetMapping("/getAllLeavesByLeaveType/{leaveType}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<LeaveDTO>> getAllLeavesByLeaveType(
            @RequestParam int offset, @RequestParam int pageSize,@RequestParam String field,
            @PathVariable String leaveType) {
        List<LeaveDTO> leavesByLeaveType = leaveService.getAllLeavesByLeaveType(offset,pageSize,field,leaveType);
        return ResponseEntity.ok(leavesByLeaveType);
    }

    @GetMapping("/getAllLeavesByStatusAndLeaveType")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<LeaveDTO>> getAllLeavesByStatusAndLeaveType(
            @RequestParam int offset, @RequestParam int pageSize,@RequestParam String field,
            @RequestParam Status status,
            @RequestParam String leaveType
    ) {
        List<LeaveDTO> leavesByStatusAndLeaveType = leaveService.getAllLeavesByStatusAndLeaveType(offset,pageSize,field,status, leaveType);
        return ResponseEntity.ok(leavesByStatusAndLeaveType);
    }

    @PreAuthorize("hasAnyAuthority()('USER','ADMIN','MANAGER')")
    public String userEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        return "Not found.";
    }

}
