package com.Leave.Management.Controller;

import com.Leave.Management.Entity.Leave;
import com.Leave.Management.Entity.Status;
import com.Leave.Management.Entity.UserInfo;
import com.Leave.Management.Service.LeaveService;
import com.Leave.Management.Service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    private final LeaveService leaveService;
    private final UserInfoService userInfoService;

    @Autowired
    public LeaveController(LeaveService leaveService, UserInfoService userInfoService) {
        this.leaveService = leaveService;
        this.userInfoService = userInfoService;
    }

    @PostMapping("/createLeaveApplication")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','USER')")
    public ResponseEntity<Leave> createLeaveApplication(@RequestBody Leave leave) {
        Leave createdLeave = leaveService.createLeaveApplication(leave);
        return ResponseEntity.ok(createdLeave);
    }


    @PutMapping("/approveLeaveApplication/{leaveId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<Leave> approveLeaveApplication(@PathVariable Long leaveId) {
        Leave approvedLeave = leaveService.approveLeaveApplication(leaveId);
        return ResponseEntity.ok(approvedLeave);
    }

    @PutMapping("/rejectLeaveApplication/{leaveId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<Leave> rejectLeaveApplication(@PathVariable Long leaveId) {
        Leave rejectedLeave = leaveService.rejectLeaveApplication(leaveId);
        return ResponseEntity.ok(rejectedLeave);
    }

    @GetMapping("/getAllLeavesByUserId/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<Leave>> getAllLeavesByUserId(@PathVariable Long userId) {
        List<Leave> userLeaves = leaveService.getAllLeavesByUserId(userId);
        return ResponseEntity.ok(userLeaves);
    }

    @GetMapping("/userLeaveProfile")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','USER')")
    public ResponseEntity<List<Leave>> userLeaveProfile() {
        Optional<UserInfo> userInfo = userInfoService.getUserByEmail(userEmail());
        List<Leave> userLeaves = leaveService.getAllLeavesByUserId(userInfo.get().getId());
        return ResponseEntity.ok(userLeaves);
    }

    @GetMapping("/getAllLeavesByStatus/{status}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<Leave>> getAllLeavesByStatus(@PathVariable Status status) {
        List<Leave> leavesByStatus = leaveService.getAllLeavesByStatus(status);
        return ResponseEntity.ok(leavesByStatus);
    }

    @GetMapping("/getAllLeavesByDateRange")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<Leave>> getAllLeavesByDateRange(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate)
    {
        List<Leave> leavesByDateRange = leaveService.getAllLeavesByDateRange(fromDate, toDate);
        return ResponseEntity.ok(leavesByDateRange);
    }

    @GetMapping("/getAllLeavesByYear")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<Leave>> getAllLeavesByYear(
            @RequestParam int year)
    {
        LocalDate fromDate=null;
        fromDate.plusYears(year).plusDays(1).plusMonths(1);
        LocalDate toDate=null;
        toDate.plusYears(year).plusDays(31).plusMonths(12);
        List<Leave> leavesByDateRange = leaveService.getAllLeavesByDateRange(fromDate, toDate);
        return ResponseEntity.ok(leavesByDateRange);
    }

    @GetMapping("/getAllLeavesByLeaveType/{leaveType}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<Leave>> getAllLeavesByLeaveType(@PathVariable String leaveType) {
        List<Leave> leavesByLeaveType = leaveService.getAllLeavesByLeaveType(leaveType);
        return ResponseEntity.ok(leavesByLeaveType);
    }

    @GetMapping("/getAllLeavesByStatusAndLeaveType")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<Leave>> getAllLeavesByStatusAndLeaveType(
            @RequestParam Status status,
            @RequestParam String leaveType
    ) {
        List<Leave> leavesByStatusAndLeaveType = leaveService.getAllLeavesByStatusAndLeaveType(status, leaveType);
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
