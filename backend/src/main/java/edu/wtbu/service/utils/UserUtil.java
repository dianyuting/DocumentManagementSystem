package edu.wtbu.service.utils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import edu.wtbu.entity.Result;
import edu.wtbu.entity.User;
import edu.wtbu.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Data
public class UserUtil {
    @Resource
    UserMapper userMapper;

    public HashMap<String, Object> getUserById(Integer id) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, id));
        return hashByUser(user);
    }
    public HashMap<String,Object> hashByUser(User user){
        if (user != null) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", user.getId());
            map.put("email", user.getEmail());
            map.put("username", user.getUsername());
            map.put("name", user.getName());
            map.put("sex", user.getSex());
            map.put("phone", user.getPhone());
            map.put("photo", user.getPhoto());
            map.put("birthday", user.getBirthday());
            return map;
        }
        return null;
    }

    public List<HashMap<String,Object>> findUserByStr(String str) {
        List<User> users = userMapper.selectList(Wrappers.<User>lambdaQuery().like(User::getEmail, str)
                .or(wrapper1 -> wrapper1.like(User::getName, str)
                .or(wrapper2 -> wrapper2.like(User::getUsername, str))));
        List<HashMap<String,Object>> list = new ArrayList<>();
        for (User user:users) {
            list.add(hashByUser(user));
        }
        return list;
    }
}
