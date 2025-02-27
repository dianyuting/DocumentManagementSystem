package edu.wtbu.controller;

import edu.wtbu.entity.Result;
import edu.wtbu.entity.User;
import edu.wtbu.interceptor.PassToken;
import edu.wtbu.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequestMapping("user")
@RestController
public class UserController {
    @Resource
    UserService userService;

    @GetMapping("login/{username}/{password}")
    @PassToken
    public Result login(@PathVariable String username, @PathVariable String password) {
        return userService.login(username, password);
    }

    @GetMapping("forget/{username}")
    @PassToken
    public Result forget(@PathVariable String username) {
        return userService.getForgetInfo(username);
    }
    @GetMapping("findUserByUsernameOrEmail/{uid}/{str}")
    public Result findUserByUsernameOrEmail(@PathVariable Integer uid,@PathVariable String str){
        return userService.findUserByUsernameOrEmail(uid,str);
    }

    @PostMapping("verify")
    @PassToken
    public Result verify(@RequestBody Map m) {
        String email = m.get("email") == null || m.get("email").toString().equals("") ? null : m.get("email").toString();
        Integer code = m.get("code") == null || m.get("code").toString().equals("") ? null : Integer.parseInt(m.get("code").toString());
        String username = m.get("username") == null || m.get("username").toString().equals("") ? null : m.get("username").toString();
        String answer = m.get("answer") == null || m.get("answer").toString().equals("") ? null : m.get("answer").toString();
        return userService.verifyForgetInfo(email, code, username, answer);
    }

    @GetMapping("getRandomUsername")
    @PassToken
    public Result getRandomUsername() {
        return userService.getRandomUsername();
    }

    @GetMapping("getUserById/{id}")
    public Result getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping("updateUser")
    public Result updateUser(@RequestBody Map map) {
        return userService.updateUser(map);
    }

    @PostMapping("addEncryption")
    public Result addEncryption(@RequestBody Map map) {
        return userService.addEncryption(Integer.parseInt(map.get("id").toString()), map.get("password").toString(), map.get("question").toString(), map.get("answer").toString());
    }

    @GetMapping("confirmPassword/{username}/{password}")
    public Result confirmPassword(@PathVariable String username, @PathVariable String password) {
        return userService.confirmPassword(username, password);
    }

    @PutMapping("updatePassword")
    public Result updatePassword(@RequestBody Map map) {
        return userService.updatePassword(map.get("username").toString(), map.get("password").toString());
    }

    @PostMapping("uploadProfile")
    @PassToken
    public Result uploadProfile(@RequestPart(value = "headImg", required = false) MultipartFile headImg,
                                @RequestPart(value = "path", required = false) String path) {
        return userService.uploadProfile(headImg, path);
    }

    @PostMapping("register")
    @PassToken
    public Result register(@RequestBody User user) {
        return userService.register(user);
    }
}
