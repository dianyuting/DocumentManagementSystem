package edu.wtbu.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wtbu.entity.File;
import edu.wtbu.entity.Result;
import edu.wtbu.entity.User;
import edu.wtbu.mapper.FileMapper;
import edu.wtbu.service.UserService;
import edu.wtbu.mapper.UserMapper;
import edu.wtbu.service.utils.MemberUtil;
import edu.wtbu.service.utils.UserUtil;
import edu.wtbu.utils.FTPUtil;
import edu.wtbu.utils.FileUtil;
import edu.wtbu.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author admin
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-03-31 22:37:05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    UserMapper userMapper;
    @Resource
    UserUtil userUtil;

    @Resource
    FileMapper fileMapper;

    @Resource
    FileUtil fileUtil;
    @Resource
    FTPUtil ftpUtil;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MemberUtil memberUtil;

    @Override
    public Result login(String username, String password) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username).eq(User::getPassword, password));
        if (user != null) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("user", new HashMap<String, Object>() {
                {
                    put("uid", user.getId());
                    put("username", user.getUsername());
                    put("name", user.getName());
                    put("profilePhoto", user.getPhoto());
                }
            });
            map.put("token", JwtUtils.getJwtToken(user.getId(), user.getUsername()));
            return new Result("success", map);
        }
        return new Result("fail", "用户名或密码错误");
    }

    /**
     * 根据用户名、邮箱查找用户
     */
    @Override
    public Result findUserByUsernameOrEmail(Integer uid, String str) {
        List<User> users = userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getUsername, str).or(wrapper1 -> wrapper1.eq(User::getEmail, str)));
        List<HashMap<String, Object>> l = new ArrayList<>();
        for (User u : users) {
            HashMap<String, Object> m = userUtil.hashByUser(u);
            if (uid == u.getId()) {
                m.put("status", "self");
            } else {
                m.put("status", memberUtil.getStatus(uid, u.getId()));
            }
            l.add(m);
        }
        return new Result("success", l);
    }

    @Override
    public Result getUserById(Integer id) {
        HashMap<String, Object> map = userUtil.getUserById(id);
        if (map != null) {
            return new Result("success", map);
        }
        return new Result("fail", "用户不存在");
    }


    /**
     * 新增用户
     *
     * @return
     */
    @Override
    public Result register(User u) {
        //判断username是否存在
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, u.getUsername()));
        if (user != null) return new Result("fail", "用户名已存在");
        //判断手机号是否重复
        if (userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, u.getPhone())) != null)
            return new Result("fail", "手机号已存在");
        //判断邮箱是否重复
        if (userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, u.getEmail())) != null)
            return new Result("fail", "邮箱重复");
        //添加用户创建日期
        u.setCreateTime(new Date());
        //根据username创建改用户的跟目录
        try {
            if (!ftpUtil.createFolder("/" + u.getUsername())) {
                return new Result("fail", "创建目录失败");
            }
        } catch (IOException e) {
            return new Result("fail", "创建目录失败");
        }
        if (userMapper.insert(u) <= 0) return new Result("fail", "创建用户失败");
        //记录目录信息至数据库
        File newFile = new File();
        newFile.setParentDirectory("/");
        newFile.setName(u.getUsername());
        newFile.setIsFile(0);
        newFile.setUid(u.getId());
        newFile.setCreateTime(new Date());
        if (fileMapper.insert(newFile) <= 0) return new Result("fail", "添加目录信息失败");
        return new Result("success");
    }

    /**
     * 获取随机用户名
     *
     * @return
     */
    @Override
    public Result getRandomUsername() {
        Result result = new Result();
        String name = randomUsername();
        while (true) {
            if (userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, name)) == null) break;
            name = randomUsername();
        }
        result.setFlag("success");
        result.setData(name);
        return result;
    }

    @Override
    public Result confirmPassword(String username, String password) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username).and(w -> w.eq(User::getPassword, password)));
        return user == null ? new Result("fail", "密码错误") : new Result("success");
    }

    @Override
    public Result updatePassword(String username, String password) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        user.setPassword(password);
        return userMapper.updateById(user) > 0 ? new Result("success", "修改成功") : new Result("fail", "修改失败");
    }

    @Override
    public Result addEncryption(int id, String password, String question, String answer) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, id));
        if (!user.getPassword().equals(password)) return new Result("fail", "密码错误");
        user.setVerifyQuestion(question);
        user.setVerifyAnswer(answer);
        userMapper.updateById(user);
        return new Result("success");
    }

    @Override
    public Result getForgetInfo(String username) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (user == null) return new Result("fail", "用户信息不存在");
        HashMap<String, Object> map = new HashMap<>() {{
            put("id", user.getId());
            put("email", user.getEmail());
            put("question", user.getVerifyQuestion());
        }};
        return new Result("success", map);
    }

    @Override
    public Result verifyForgetInfo(String email, Integer code, String username, String answer) {
        if (email != null && code != null) {
            if (!redisTemplate.hasKey(email)) {
                return new Result("fail", "未发送验证码，或验证码失效");
            }
            Integer c = Integer.parseInt(redisTemplate.opsForValue().get(email).toString());
            if (c.equals(code)) {
                User u = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, email));
                u.setPassword("88888888");
                userMapper.updateById(u);
                return new Result("success", "已重置密码为88888888");
            } else {
                return new Result("fail", "验证码错误");
            }
        } else if (username != null && answer != null) {
            User u = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
            if (u.getVerifyAnswer().equals(answer)) {
                u.setPassword("88888888");
                userMapper.updateById(u);
                return new Result("success", "已重置密码为88888888");
            } else return new Result("fail", "验证答案错误");
        }
        return new Result("fail", "信息不全");
    }

    @Override
    public Result updateUser(Map map) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, Integer.parseInt(map.get("id").toString())));
        //判断手机号是否重复
        if (map.get("phone") != null) {
            User u = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, map.get("phone").toString()));
            if (u != null && u.getId() != user.getId())
                return new Result("fail", "手机号已存在");
        }
        //判断邮箱是否重复
        User eu = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, map.get("email").toString()));
        if (eu != null && eu.getId() != user.getId())
            return new Result("fail", "邮箱重复");
        if (user == null) return new Result("fail", "用户不存在");
        user.setEmail(map.get("email").toString());
        user.setName(map.get("name") == null ? null : map.get("name").toString());
        user.setSex(map.get("sex") == null ? null : Integer.parseInt(map.get("sex").toString()));
        user.setPhone(map.get("phone") == null ? null : map.get("phone").toString());
        user.setPhoto(map.get("photo") == null ? null : map.get("photo").toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            user.setBirthday(map.get("birthday") == null ? null : df.parse(map.get("birthday").toString()));
        } catch (Exception e) {
            user.setBirthday(null);
        }
        return userMapper.updateById(user) > 0 ? new Result("success") : new Result("fail", "修改失败");
    }

    /**
     * 上传头像文件
     *
     * @return
     */
    @Override
    public Result uploadProfile(MultipartFile headImg, String path) {
        if (path != null && !path.equals("")) {
            if (!fileUtil.del(System.getProperty("user.dir") + "/upload", path.substring(path.indexOf('/') + 1))) {
                return new Result("fail", "原头像删除失败");
            }
        }
        if (headImg == null) return new Result("success", "删除图片成功");
        if (!imageTypeRight(headImg)) return new Result("fail", "图片格式不正确");
        String fileName = headImg.getOriginalFilename();
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        // 只有当满足图片格式时才进来，重新赋图片名，防止出现名称重复的情况
        String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileSuffix;
        // 该方法返回的为当前项目的工作目录，即在哪个地方启动的java线程
        java.io.File fileTransfer = new java.io.File(fileUtil.getPath(System.getProperty("user.dir") + "/upload"), newFileName);

        try {
            //文件上传
            headImg.transferTo(fileTransfer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将图片相对路径返回给前端
        return new Result("success", "headimage/" + newFileName);
    }

    /**
     * 验证图片的格式
     *
     * @return
     */
    private boolean imageTypeRight(MultipartFile file) {
        //校验图片格式
        List<String> imageType = new ArrayList<>() {{
            add("jpg");
            add("jpeg");
            add("png");
            add("bmp");
            add("gif");
        }};
        String fileName = file.getOriginalFilename();
        //获取文件名后缀
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!imageType.contains(fileSuffix)) return false;
        return true;
    }


    /**
     * 生成随机用户名
     *
     * @return
     */
    public String randomUsername() {
        String str = UUID.randomUUID().toString().replace("-", "");
        Random random = new Random();
        int n = random.nextInt(str.length());
        int m = random.nextInt(str.length());
        String name = str.substring(n > m ? m : n, n > m ? n : m);
        return name;
    }
}




