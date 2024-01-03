package com.Leave.Management.Controller;

import com.Leave.Management.Entity.UserInfo;
import com.Leave.Management.Service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<UserInfo>> getAllUsers() {
        List<UserInfo> users = userInfoService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserInfo> getUserByEmail(@PathVariable String email) {
        Optional<UserInfo> user = userInfoService.getUserByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserInfo> createUser(@RequestBody UserInfo userInfo) {
        UserInfo createdUser = userInfoService.createUser(userInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{email}")
    public ResponseEntity<UserInfo> updateUser(@PathVariable String email, @RequestBody UserInfo updatedUserInfo) {
        UserInfo updatedUser = userInfoService.updateUser(email, updatedUserInfo);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        userInfoService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateLeaveBalance")
    public ResponseEntity<Void> updateLeaveBalanceForAllUsers(@RequestParam String leaveType, @RequestParam Long balance) {
        try {
            userInfoService.updateLeaveBalanceForAllUsers(leaveType, balance);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}