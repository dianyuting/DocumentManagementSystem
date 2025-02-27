package edu.wtbu.service;

import edu.wtbu.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.wtbu.entity.Result;

/**
* @author admin
* @description 针对表【member】的数据库操作Service
* @createDate 2024-04-19 15:46:25
*/
public interface MemberService extends IService<Member> {
    public Result getAllFriendRequest(Integer uid);
    public Result getAllSentRequest(Integer uid);
    public Result findFriendByStr(Integer uid,String str);
    public Result sendFriendRequest(Integer friendId,Integer gid);
    public Result verifiedFriend(Integer mid,Integer uid,Integer gid);
    public Result rejectRequest(Integer mid);
    public Result deleteFriend(Integer mid);
    public Result updateGroup(Integer mid,Integer gid);
}
