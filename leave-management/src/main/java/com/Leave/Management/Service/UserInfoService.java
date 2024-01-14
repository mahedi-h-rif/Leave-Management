package com.Leave.Management.Service;

import com.Leave.Management.Dto.UserCreationDTO;
import com.Leave.Management.Dto.UserInfoWithoutPasswordDTO;
import com.Leave.Management.Entity.UserInfo;
import com.Leave.Management.Entity.leaveAmount;
import com.Leave.Management.Repository.UserInfoRepository;
import com.Leave.Management.Repository.leaveAmountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {


    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private leaveAmountRepository leaveAmountRepository;

    @Autowired
    private PasswordEncoder encoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserInfo> userDetail = userInfoRepository.findByEmail(username);

        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    public Page<UserInfo> getAllUsers(int offset,int pageSize,String field){
        Page<UserInfo> userInfos = userInfoRepository.findAll(PageRequest.of(offset,pageSize).withSort(Sort.by(field)));
        return userInfos;
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
        leaveAmount leaveAmount = leaveAmountRepository.findById(1L).get();
        if (leaveType.equalsIgnoreCase("sick")) {
            leaveAmount.setSickLeave(balance);
            leaveAmountRepository.save(leaveAmount);
        }
        else if (leaveType.equalsIgnoreCase("casual")) {
            leaveAmount.setCasualLeave(balance);
            leaveAmountRepository.save(leaveAmount);
        }
        else {
            throw new IllegalArgumentException("Invalid leave type");
        }
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


    public UserInfoWithoutPasswordDTO createUser(UserCreationDTO userInfoDTO) {
        UserInfo userInfo = new UserInfo();
        userInfo.setName(userInfoDTO.getName());
        userInfo.setEmail(userInfoDTO.getEmail());
        userInfo.setPassword(encoder.encode(userInfoDTO.getPassword()));
        userInfo.setRole(userInfoDTO.getRole());

        UserInfo savedUser = userInfoRepository.save(userInfo);
        UserInfoWithoutPasswordDTO userWithoutPasswordDTO = convertToDTOs(savedUser);

        return userWithoutPasswordDTO;
    }

    private UserCreationDTO convertToDTO(UserInfo userInfo) {
        UserCreationDTO userInfoDTO = new UserCreationDTO();
        userInfoDTO.setName(userInfo.getName());
        userInfoDTO.setEmail(userInfo.getEmail());
        userInfoDTO.setRole(userInfo.getRole());
        return userInfoDTO;
    }


    public UserInfoWithoutPasswordDTO getUserWithoutPassword(Long userId) {
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findById(userId);


            UserInfo userInfo = optionalUserInfo.get();
            return convertToDTOs(userInfo);

            // Handle user not found
            //throw new ChangeSetPersister.NotFoundException("User not found with ID: " + userId);

    }

    public UserInfoWithoutPasswordDTO convertToDTOs(UserInfo userInfo) {
        UserInfoWithoutPasswordDTO userWithoutPasswordDTO = new UserInfoWithoutPasswordDTO();
        userWithoutPasswordDTO.setName(userInfo.getName());
        userWithoutPasswordDTO.setEmail(userInfo.getEmail());
        userWithoutPasswordDTO.setRole(userInfo.getRole());
        userWithoutPasswordDTO.setSickLeave(userInfo.getSickLeave());
        userWithoutPasswordDTO.setCasualLeave(userInfo.getCasualLeave());
        userWithoutPasswordDTO.setCustomLeave(userInfo.getCustomLeave());
        userWithoutPasswordDTO.setMaximumCustomLeave(userInfo.getMaximumCustomLeave());

        return userWithoutPasswordDTO;
    }

}
