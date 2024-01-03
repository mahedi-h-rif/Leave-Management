package com.Leave.Management.Service;

import com.Leave.Management.Entity.UserInfo;
import com.Leave.Management.Repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService {


    @Autowired
    private UserInfoRepository userInfoRepository;

    public List<UserInfo> getAllUsers(){
        return userInfoRepository.findAll();
    }

    public Optional<UserInfo> getUserByEmail(String email){
        return userInfoRepository.findByEmail(email);
    }

    public UserInfo createUser(UserInfo userInfo){
        return userInfoRepository.save(userInfo);
    }

    public UserInfo updateUser(String email, UserInfo updatedUserInfo) {
            // Check if the user exists
        Optional<UserInfo> existingUser = userInfoRepository.findByEmail(email);
           if (existingUser.isPresent()) {

            // Update the existing user with the new information
            updatedUserInfo.setId(existingUser.get().getId());
            updatedUserInfo.setCasualLeave(existingUser.get().getCasualLeave());
            updatedUserInfo.setCustomLeave(existingUser.get().getCustomLeave());
            updatedUserInfo.setSickLeave(existingUser.get().getSickLeave());
            if(updatedUserInfo.getName()==null)
                updatedUserInfo.setName(existingUser.get().getName());
            if(updatedUserInfo.getEmail()==null)
                updatedUserInfo.setEmail(existingUser.get().getEmail());
            if (updatedUserInfo.getRole()==null)
                updatedUserInfo.setRole(existingUser.get().getRole());
            if(updatedUserInfo.getPassword()==null)
                updatedUserInfo.setPassword(existingUser.get().getPassword());

            return userInfoRepository.save(updatedUserInfo);
        } else {
            return null;
        }
    }

    public void deleteUser(String email){
        userInfoRepository.deleteByEmail(email);
    }

    public void updateLeaveBalanceForAllUsers(String leaveType, Long balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Balance is required");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        List<UserInfo> users = userInfoRepository.findAll();
        for (UserInfo user : users) {
            if (leaveType.equalsIgnoreCase("sick leave")) {
                user.setSickLeave(balance);
            } else if (leaveType.equalsIgnoreCase("casual leave")) {
                user.setCasualLeave(balance);
            } else if (leaveType.equalsIgnoreCase("custom leave")) {
                user.setCustomLeave(balance);
            } else {
                throw new IllegalArgumentException("Invalid leave type");
            }
        }
        userInfoRepository.saveAll(users);
    }

}
