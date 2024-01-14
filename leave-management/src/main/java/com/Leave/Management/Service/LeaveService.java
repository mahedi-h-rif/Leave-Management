package com.Leave.Management.Service;

import com.Leave.Management.Dto.LeaveCreateDTO;
import com.Leave.Management.Dto.LeaveDTO;
import com.Leave.Management.Entity.Leave;
import com.Leave.Management.Entity.Status;
import com.Leave.Management.Entity.UserInfo;
import com.Leave.Management.Entity.leaveAmount;
import com.Leave.Management.Repository.LeaveRepository;
import com.Leave.Management.Repository.UserInfoRepository;
import com.Leave.Management.Repository.leaveAmountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final UserInfoRepository userInfoRepository;
    private final leaveAmountRepository leaveAmountRepository;

    @Autowired
    public LeaveService(LeaveRepository leaveRepository, UserInfoRepository userInfoRepository, com.Leave.Management.Repository.leaveAmountRepository leaveAmountRepository) {
        this.leaveRepository = leaveRepository;
        this.userInfoRepository = userInfoRepository;
        this.leaveAmountRepository = leaveAmountRepository;
    }


    public LeaveDTO createLeaveApplication(LeaveCreateDTO leave, String userName){
        Optional<UserInfo> userInfo = userInfoRepository.findByEmail(userName);
        leaveAmount leaveAmount = leaveAmountRepository.findById(1L).get();
        Long balance = ChronoUnit.DAYS.between(leave.getFromDate(),leave.getToDate());
        if (userInfo.isEmpty()) {
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
        if (leave.getLeaveType().equalsIgnoreCase("sick") && userInfo.get().getSickLeave()+balance>leaveAmount.getSickLeave()){
            throw new IllegalArgumentException("Not enough sick leave balance available");
        }
        if (leave.getLeaveType().equalsIgnoreCase("casual") && userInfo.get().getCasualLeave()+balance>leaveAmount.getCasualLeave()){
            throw new IllegalArgumentException("Not enough casual leave balance available");
        }
        if (leave.getLeaveType().equalsIgnoreCase("custom") && userInfo.get().getCustomLeave()+balance>userInfo.get().getMaximumCustomLeave()) {
            throw new IllegalArgumentException("Not enough custom leave balance available");
        }
        updateLeaveBalance(userInfo.get().getId(),leave.getLeaveType(),balance);
        Leave leave1 = LeaveCreateDTOToLeave(leave);
        leave1.setStatus(Status.PENDING);
        leave1.setUserInfo(userInfo.get());
        leaveRepository.save(leave1);
        return leaveToLeaveDTO(leave1);
    }

    private LeaveDTO leaveToLeaveDTO(Leave leave1) {
        LeaveDTO leaveDTO= new LeaveDTO();
        leaveDTO.setLeaveType(leave1.getLeaveType());
        leaveDTO.setFromDate(leave1.getFromDate());
        leaveDTO.setToDate(leave1.getToDate());
        leaveDTO.setRemark(leave1.getRemark());
        leaveDTO.setStatus(leave1.getStatus());
        leaveDTO.setUserId(leave1.getUserInfo().getId());
        return leaveDTO;
    }

    private Leave LeaveCreateDTOToLeave(LeaveCreateDTO leave) {
        Leave leave1 = new Leave();
        leave1.setLeaveType(leave.getLeaveType());
        leave1.setFromDate(leave.getFromDate());
        leave1.setToDate(leave.getToDate());
        leave1.setRemark(leave.getRemark());
        return leave1;
    }


    public LeaveDTO approveLeaveApplication(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId).orElse(null);
        if (leave == null) {
            throw new IllegalArgumentException("Leave not found");
        }
        if (leave.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Leave is already " + leave.getStatus());
        }
        leave.setStatus(Status.ACCEPTED);
        return leaveToLeaveDTO(leaveRepository.save(leave));
    }



    public LeaveDTO rejectLeaveApplication(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId).orElse(null);
        if (leave == null) {
            throw new IllegalArgumentException("Leave not found");
        }
        if (leave.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Leave is already " + leave.getStatus());
        }
        Long balance = ChronoUnit.DAYS.between(leave.getFromDate(),leave.getToDate());
        updateLeaveBalance(leave.getUserInfo().getId(),leave.getLeaveType(),balance*(-1L));
        leave.setStatus(Status.REJECTED);
        //leave.setRemark(remark);
        return leaveToLeaveDTO(leaveRepository.save(leave));
    }



    public List<LeaveDTO> getAllLeavesByUserId(Long userId) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElse(null);
        if (userInfo == null) {
            throw new IllegalArgumentException("User not found");
        }
        List<Leave> leaves= leaveRepository.findByUserInfoId(userId);

        List<LeaveDTO> leaveDTOs = new ArrayList<>();
        for (Leave leave : leaves) {
            leaveDTOs.add(leaveToLeaveDTO(leave));
        }
        return leaveDTOs;
    }

    public List<LeaveDTO> getAllLeavesByStatus(int offset,int pageSize,String field,Status status) {
        Page<Leave> leaves = leaveRepository.findByStatus(PageRequest.of(offset,pageSize).withSort(Sort.by(field)),status);
        List<LeaveDTO> leaveDTOs = new ArrayList<>();
        for (Leave leave : leaves) {
            leaveDTOs.add(leaveToLeaveDTO(leave));
        }
        return leaveDTOs;
    }

    public List<LeaveDTO> getAllLeavesByDateRange(int offset,int pageSize,String field,LocalDate fromDate, LocalDate toDate) {
        Page<Leave> leaves = leaveRepository.findByFromDateBetween(PageRequest.of(offset,pageSize).withSort(Sort.by(field)),fromDate, toDate);
        List<LeaveDTO> leaveDTOs = new ArrayList<>();
        for (Leave leave : leaves) {
            leaveDTOs.add(leaveToLeaveDTO(leave));
        }
        return leaveDTOs;
    }

    public List<LeaveDTO> getAllLeavesByLeaveType(int offset,int pageSize,String field,String leaveType) {
        Page<Leave> leaves = leaveRepository.findByLeaveType(PageRequest.of(offset,pageSize).withSort(Sort.by(field)),leaveType);
        List<LeaveDTO> leaveDTOs = new ArrayList<>();
        for (Leave leave : leaves) {
            leaveDTOs.add(leaveToLeaveDTO(leave));
        }
        return leaveDTOs;
    }

    public List<LeaveDTO> getAllLeavesByStatusAndLeaveType(int offset,int pageSize,String field,Status status, String leaveType) {
        Page<Leave> leaves = leaveRepository.findByStatusAndLeaveType(PageRequest.of(offset,pageSize).withSort(Sort.by(field)),status, leaveType);
        List<LeaveDTO> leaveDTOs = new ArrayList<>();
        for (Leave leave : leaves) {
            leaveDTOs.add(leaveToLeaveDTO(leave));
        }
        return leaveDTOs;
    }

    public UserInfo updateLeaveBalance(Long userId, String leaveType, Long balance) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElse(null);
        if (leaveType.equalsIgnoreCase("sick")) {
            userInfo.setSickLeave(balance+userInfo.getSickLeave());
        } else if (leaveType.equalsIgnoreCase("casual")) {
            userInfo.setCasualLeave(balance+userInfo.getCasualLeave());
        } else if (leaveType.equalsIgnoreCase("custom")) {
            userInfo.setCustomLeave(balance+userInfo.getCustomLeave());
        } else {
            throw new IllegalArgumentException("Invalid leave type");
        }
        return userInfoRepository.save(userInfo);
    }

}
