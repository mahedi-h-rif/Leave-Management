package com.Leave.Management.Controller;

import com.Leave.Management.Entity.Leave;
import com.Leave.Management.Entity.Status;
import com.Leave.Management.Service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    private final LeaveService leaveService;

    @Autowired
    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/createLeaveApplication")
    public ResponseEntity<Leave> createLeaveApplication(@RequestBody Leave leave) {
        Leave createdLeave = leaveService.createLeaveApplication(leave);
        return ResponseEntity.ok(createdLeave);
    }

    @PutMapping("/approveLeaveApplication/{leaveId}")
    public ResponseEntity<Leave> approveLeaveApplication(@PathVariable Long leaveId) {
        Leave approvedLeave = leaveService.approveLeaveApplication(leaveId);
        return ResponseEntity.ok(approvedLeave);
    }

    @PutMapping("/rejectLeaveApplication/{leaveId}")
    public ResponseEntity<Leave> rejectLeaveApplication(@PathVariable Long leaveId) {
        Leave rejectedLeave = leaveService.rejectLeaveApplication(leaveId);
        return ResponseEntity.ok(rejectedLeave);
    }

    @GetMapping("/getAllLeavesByUserId/{userId}")
    public ResponseEntity<List<Leave>> getAllLeavesByUserId(@PathVariable Long userId) {
        List<Leave> userLeaves = leaveService.getAllLeavesByUserId(userId);
        return ResponseEntity.ok(userLeaves);
    }

    @GetMapping("/getAllLeavesByStatus/{status}")
    public ResponseEntity<List<Leave>> getAllLeavesByStatus(@PathVariable Status status) {
        List<Leave> leavesByStatus = leaveService.getAllLeavesByStatus(status);
        return ResponseEntity.ok(leavesByStatus);
    }

    @GetMapping("/getAllLeavesByDateRange")
    public ResponseEntity<List<Leave>> getAllLeavesByDateRange(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate
    ) {
        List<Leave> leavesByDateRange = leaveService.getAllLeavesByDateRange(fromDate, toDate);
        return ResponseEntity.ok(leavesByDateRange);
    }

    @GetMapping("/getAllLeavesByLeaveType/{leaveType}")
    public ResponseEntity<List<Leave>> getAllLeavesByLeaveType(@PathVariable String leaveType) {
        List<Leave> leavesByLeaveType = leaveService.getAllLeavesByLeaveType(leaveType);
        return ResponseEntity.ok(leavesByLeaveType);
    }

    @GetMapping("/getAllLeavesByStatusAndLeaveType")
    public ResponseEntity<List<Leave>> getAllLeavesByStatusAndLeaveType(
            @RequestParam Status status,
            @RequestParam String leaveType
    ) {
        List<Leave> leavesByStatusAndLeaveType = leaveService.getAllLeavesByStatusAndLeaveType(status, leaveType);
        return ResponseEntity.ok(leavesByStatusAndLeaveType);
    }
}
