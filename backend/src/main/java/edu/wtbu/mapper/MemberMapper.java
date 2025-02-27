package edu.wtbu.mapper;

import edu.wtbu.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.HashMap;

/**
* @author admin
* @description 针对表【member】的数据库操作Mapper
* @createDate 2024-04-19 15:46:25
* @Entity edu.wtbu.entity.Member
*/
public interface MemberMapper extends BaseMapper<Member> {
    public HashMap<String ,Object> getMemberByUserIdAndFriendId(Integer userId,Integer friendId);
}




