package edu.wtbu.service.utils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import edu.wtbu.entity.Member;
import edu.wtbu.mapper.MemberMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@Data
public class MemberUtil {
    @Resource
    MemberMapper memberMapper;
    @Resource
    FriendGroupUtil friendGroupUtil;
    @Resource
    FileShareUtil fileShareUtil;

    public boolean deleteMemberByGroup(Integer gid) {
        List<Member> members = memberMapper.selectList(Wrappers.<Member>lambdaQuery().eq(Member::getGid, gid));
        for (Member m:members) {
            deleteMemberById(m.getId());
        }
        return true ;
    }
    public boolean deleteMemberById(Integer mid){
        Member member = memberMapper.selectOne(Wrappers.<Member>lambdaQuery().eq(Member::getId, mid));
        Integer friendId = member.getUid();
        Integer userId = friendGroupUtil.getGroupById(member.getGid()).getUid();
        Integer fMemberId = getMemberId(friendId, userId);
        if (fMemberId != null) {
            memberMapper.deleteById(fMemberId);
            fileShareUtil.deleteByMid(fMemberId);
        }
        memberMapper.deleteById(mid);
        fileShareUtil.deleteByMid(mid);
        return true;
    }
    public List<Member> getMemberByGroup(Integer gid){
        return memberMapper.selectList(Wrappers.<Member>lambdaQuery().eq(Member::getGid,gid).eq(Member::getStatus,"success"));
    }
    public Member getMemberById(Integer id){
        return memberMapper.selectOne(Wrappers.<Member>lambdaQuery().eq(Member::getId,id));
    }

    public String getStatus(Integer uid,Integer fid){
        Integer mid = getMemberId(uid,fid);
        if (mid == null) return "noLink";
        Member member = memberMapper.selectOne(Wrappers.<Member>lambdaQuery().eq(Member::getId,mid));
        return member.getStatus();
    }

    /**
     * 根据用户id和好友id获取成员列表的id
     * @param friendId
     * @param userId
     * @return
     */
    public Integer getMemberId(Integer userId,Integer friendId){
        HashMap<String ,Object> m = memberMapper.getMemberByUserIdAndFriendId(userId, friendId);
        return m != null ? Integer.parseInt(m.get("memberId").toString()) : null;
    }
}
