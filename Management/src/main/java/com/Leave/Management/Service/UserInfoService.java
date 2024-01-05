package com.Leave.Management.Service;

import com.Leave.Management.Entity.UserInfo;
import com.Leave.Management.Repository.UserInfoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {


    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserInfo> userDetail = userInfoRepository.findByEmail(username);

        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    public String addUser(UserInfo userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        userInfoRepository.save(userInfo);
        return "User Added Successfully";
    }

    public List<UserInfo> getAllUsers(){
        return userInfoRepository.findAll();
    }

    public Optional<UserInfo> getUserById(Long id){return userInfoRepository.findById(id);}

    public Optional<UserInfo> getUserByEmail(String email){
        return userInfoRepository.findByEmail(email);
    }

    public UserInfo createUser(UserInfo userInfo){
        return userInfoRepository.save(userInfo);
    }

    public UserInfo updateUser(String email, UserInfo updatedUserInfo) {
        Optional<UserInfo> existingUser = userInfoRepository.findByEmail(email);
           if (existingUser.isPresent()) {

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
            if (leaveType.equalsIgnoreCase("sick")) {
                user.setSickLeave(balance);
            } else if (leaveType.equalsIgnoreCase("casual")) {
                user.setCasualLeave(balance);
            } else if (leaveType.equalsIgnoreCase("custom")) {
                user.setCustomLeave(balance);
            } else {
                throw new IllegalArgumentException("Invalid leave type");
            }
        }
        userInfoRepository.saveAll(users);
    }

    public void deleteUserById(Long id) {
        userInfoRepository.deleteById(id);
    }


    @Transactional
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        Optional<UserInfo> user = userInfoRepository.findByEmail(email);
        if (user.get().getPassword().equals(oldPassword)) {
            user.get().setPassword(oldPassword);
            return true;
        }
        return false;
    }


}
