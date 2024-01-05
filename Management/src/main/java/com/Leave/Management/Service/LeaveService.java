package com.Leave.Management.Service;

import com.Leave.Management.Entity.Leave;
import com.Leave.Management.Entity.Status;
import com.Leave.Management.Entity.UserInfo;
import com.Leave.Management.Repository.LeaveRepository;
import com.Leave.Management.Repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveService {

    private final LeaveRepository leaveRepository;

    private final UserInfoRepository userInfoRepository;

    @Autowired
    public LeaveService(LeaveRepository leaveRepository, UserInfoRepository userInfoRepository) {
        this.leaveRepository = leaveRepository;
        this.userInfoRepository = userInfoRepository;
    }


    public Leave createLeaveApplication(Leave leave) {
        UserInfo userInfo = userInfoRepository.findById(leave.getUserId()).orElse(null);
        if (userInfo == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (leave.getFromDate().isAfter(leave.getToDate())) {
            throw new IllegalArgumentException("Invalid date range");
        }
        if (leave.getLeaveType() == null || leave.getLeaveType().isEmpty()) {
            throw new IllegalArgumentException("Leave type is required");
        }
        /*
        if (leave.getRemark() == null || leave.getRemark().isEmpty()) {
            throw new IllegalArgumentException("Remark is required");
        }*/
        if (leave.getFromDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot apply for past dates");
        }
        if (leave.getLeaveType().equalsIgnoreCase("sick") && userInfo.getSickLeave() < 1) {
            throw new IllegalArgumentException("No sick leave balance available");
        }
        if (leave.getLeaveType().equalsIgnoreCase("casual") && userInfo.getCasualLeave() < 1) {
            throw new IllegalArgumentException("No casual leave balance available");
        }
        if (leave.getLeaveType().equalsIgnoreCase("custom") && userInfo.getCustomLeave() < 1) {
            throw new IllegalArgumentException("No custom leave balance available");
        }
        Long balance = ChronoUnit.DAYS.between(leave.getFromDate(),leave.getToDate());
        updateLeaveBalance(userInfo.getId(),leave.getLeaveType(),balance*(-1L));
        leave.setStatus(Status.PENDING);
        return leaveRepository.save(leave);
    }



    public Leave approveLeaveApplication(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId).orElse(null);
        if (leave == null) {
            throw new IllegalArgumentException("Leave not found");
        }
        if (leave.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Leave is already " + leave.getStatus());
        }
        leave.setStatus(Status.ACCEPTED);
        return leaveRepository.save(leave);
    }



    public Leave rejectLeaveApplication(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId).orElse(null);
        if (leave == null) {
            throw new IllegalArgumentException("Leave not found");
        }
        if (leave.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Leave is already " + leave.getStatus());
        }
        Long balance = ChronoUnit.DAYS.between(leave.getFromDate(),leave.getToDate());
        updateLeaveBalance(leave.getUserId(),leave.getLeaveType(),balance);
        leave.setStatus(Status.REJECTED);
        //leave.setRemark(remark);
        return leaveRepository.save(leave);
    }



    public List<Leave> getAllLeavesByUserId(Long userId) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElse(null);
        if (userInfo == null) {
            throw new IllegalArgumentException("User not found");
        }
        return leaveRepository.findByUserId(userId);
    }

    public List<Leave> getAllLeavesByStatus(Status status) {
        return leaveRepository.findByStatus(status);
    }

    public List<Leave> getAllLeavesByDateRange(LocalDate fromDate, LocalDate toDate) {
        return leaveRepository.findByFromDateBetween(fromDate, toDate);
    }

    public List<Leave> getAllLeavesByLeaveType(String leaveType) {
        return leaveRepository.findByLeaveType(leaveType);
    }

    public List<Leave> getAllLeavesByStatusAndLeaveType(Status status, String leaveType) {
        return leaveRepository.findByStatusAndLeaveType(status, leaveType);
    }




    public UserInfo updateLeaveBalance(Long userId, String leaveType, Long balance) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElse(null);
        if (leaveType.equalsIgnoreCase("sick leave")) {
            userInfo.setSickLeave(balance+userInfo.getSickLeave());
        } else if (leaveType.equalsIgnoreCase("casual leave")) {
            userInfo.setCasualLeave(balance+userInfo.getCasualLeave());
        } else if (leaveType.equalsIgnoreCase("custom leave")) {
            userInfo.setCustomLeave(balance+userInfo.getCustomLeave());
        } else {
            throw new IllegalArgumentException("Invalid leave type");
        }
        return userInfoRepository.save(userInfo);
    }




}
