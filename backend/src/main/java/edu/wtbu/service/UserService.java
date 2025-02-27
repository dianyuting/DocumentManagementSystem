package edu.wtbu.service;

import edu.wtbu.entity.Result;
import edu.wtbu.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
* @author admin
* @description 针对表【user】的数据库操作Service
* @createDate 2024-03-31 22:37:05
*/
public interface UserService extends IService<User> {
    public Result login(String username, String password);
    public Result findUserByUsernameOrEmail( Integer uid,String str);
    public Result getUserById(Integer id);
    public Result register(User u);
    public Result getRandomUsername();

    public Result confirmPassword(String username, String password);
    public Result updatePassword(String username, String password);

    public Result addEncryption(int id,String password, String question, String answer);
    public Result getForgetInfo(String username);
    public Result verifyForgetInfo(String email,Integer code,String username,String answer);
    public Result updateUser(Map map);

    public Result uploadProfile(MultipartFile headImg, String path);
}
