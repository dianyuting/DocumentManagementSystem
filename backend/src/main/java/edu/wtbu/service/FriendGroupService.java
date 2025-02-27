package edu.wtbu.service;

import edu.wtbu.entity.FriendGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.wtbu.entity.Result;

/**
* @author admin
* @description 针对表【friend_group】的数据库操作Service
* @createDate 2024-04-19 18:35:30
*/
public interface FriendGroupService extends IService<FriendGroup> {
    public Result addGroup(FriendGroup group);
    public Result updateGroup(FriendGroup group);
    public Result deleteGroup(FriendGroup group);
    public Result getGroup(Integer uid);
}
