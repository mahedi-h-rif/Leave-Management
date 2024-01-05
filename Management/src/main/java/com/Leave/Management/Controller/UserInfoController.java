package com.Leave.Management.Controller;

import com.Leave.Management.Entity.AuthRequest;
import com.Leave.Management.Entity.UserInfo;
import com.Leave.Management.Service.JwtService;
import com.Leave.Management.Service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserInfoController {

    private UserInfoService userInfoService;

    @Autowired
    UserInfoController(UserInfoService userInfoService){
        this.userInfoService=userInfoService;
    }

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/testing")
    public String testing(){
        return "this is for testingggggg";
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<List<UserInfo>> getAllUsers() {
        List<UserInfo> users = userInfoService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/userProfileBYId/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<UserInfo> getUserBYId(@PathVariable Long userId) {
        Optional<UserInfo> user = userInfoService.getUserById(userId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/userProfile")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','USER')")
    public ResponseEntity<UserInfo> getUser() {
        Optional<UserInfo> user = userInfoService.getUserByEmail(userEmail());
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserInfo> createUser(@RequestBody UserInfo userInfo) {
        UserInfo createdUser = userInfoService.createUser(userInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserInfo> updateUser(@RequestBody UserInfo updatedUserInfo) {
        UserInfo updatedUser = userInfoService.updateUser(userEmail(), updatedUserInfo);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userInfoService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/updateLeaveBalance")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateLeaveBalanceForAllUsers(@RequestParam String leaveType, @RequestParam Long balance) {
        try {
            userInfoService.updateLeaveBalanceForAllUsers(leaveType, balance);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PreAuthorize("hasAnyAuthority()('USER','ADMIN','MANAGER')")
    public String userEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication principal is an instance of UserDetails
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        return "Welcome to User Profile";
    }

    @PutMapping("/ChangePassword")
    @PreAuthorize("hasAnyAuthority()('USER','ADMIN','MANAGER')")
    public String changePassword(@RequestParam String oldPassword,@RequestParam String newPassword){
        boolean flag = userInfoService.changePassword(userEmail(),oldPassword,newPassword);
        if(flag)
            return "Password changed successfully.";
        else
            return "Password changed failed.";
    }

    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

}