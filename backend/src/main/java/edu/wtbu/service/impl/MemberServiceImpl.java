package edu.wtbu.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wtbu.entity.FriendGroup;
import edu.wtbu.entity.Member;
import edu.wtbu.entity.Result;
import edu.wtbu.entity.User;
import edu.wtbu.service.MemberService;
import edu.wtbu.mapper.MemberMapper;
import edu.wtbu.service.utils.FileShareUtil;
import edu.wtbu.service.utils.FriendGroupUtil;
import edu.wtbu.service.utils.MemberUtil;
import edu.wtbu.service.utils.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author admin
 * @description 针对表【member】的数据库操作Service实现
 * @createDate 2024-04-19 15:46:25
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member>
        implements MemberService {
    @Resource
    MemberMapper memberMapper;
    @Resource
    FriendGroupUtil friendGroupUtil;
    @Resource
    UserUtil userUtil;
    @Resource
    FileShareUtil fileShareUtil;
    @Resource
    MemberUtil memberUtil;

    /**
     * 获取所有好友申请
     *
     * @param uid
     * @return
     */
    @Override
    public Result getAllFriendRequest(Integer uid) {
        List<Member> members = memberMapper.selectList(Wrappers.<Member>lambdaQuery().eq(Member::getUid, uid).and(wrappers -> wrappers.eq(Member::getStatus, "wait")));
        List<HashMap<String, Object>> l = new ArrayList<>();
        for (Member m : members) {
            FriendGroup g = friendGroupUtil.getGroupById(m.getGid());
            HashMap<String, Object> map = userUtil.getUserById(g.getUid());
            map.put("mid", m.getId());
            l.add(map);
        }
        return new Result("success", l);
    }

    /**
     * 获取所有已经发送的申请(未通过，拒绝)
     *
     * @param uid
     * @return
     */
    @Override
    public Result getAllSentRequest(Integer uid) {
        List<FriendGroup> groups = friendGroupUtil.getGroupByUid(uid);
        List<Member> members = new ArrayList<>();
        for (FriendGroup g : groups) {
            members.addAll(memberMapper.selectList(Wrappers.<Member>lambdaQuery().eq(Member::getGid, g.getId())
                    .and(wrapper1 -> wrapper1.eq(Member::getStatus, "wait")
                            .or(wrapper2 -> wrapper2.eq(Member::getStatus, "rejected")))));
        }
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (Member m : members) {
            HashMap<String, Object> map = userUtil.getUserById(m.getUid());
            map.put("status", m.getStatus());
            map.put("mid", m.getId());
            list.add(map);
        }
        return new Result("success", list);
    }

    /**
     * 发送好友请求
     *
     * @param friendId
     * @param gid
     * @return
     */
    @Override
    public Result sendFriendRequest(Integer friendId, Integer gid) {
        Integer userId = friendGroupUtil.getGroupById(gid).getUid();
        Integer mid = memberUtil.getMemberId(friendId, userId);
        if (mid != null) {
            Member m = memberMapper.selectOne(Wrappers.<Member>lambdaQuery().eq(Member::getId, mid));
            m.setStatus("success");
            memberMapper.updateById(m);
            return verifiedFriend(m.getId(), friendId, gid);
        }
        Member member = new Member();
        member.setGid(gid);
        member.setUid(friendId);
        member.setStatus("wait");
        return memberMapper.insert(member) > 0 ? new Result("success", "已发送好友验证申请") : new Result("fail", "好友验证申请发送失败");
    }

    /**
     * 通过好友请求
     *
     * @param mid
     * @param uid
     * @param gid
     * @return
     */
    @Override
    public Result verifiedFriend(Integer mid, Integer uid, Integer gid) {
        if (memberMapper.update(Wrappers.<Member>lambdaUpdate().eq(Member::getId, mid).set(Member::getStatus, "success")) <= 0)
            return new Result("fail", "添加失败");
        Member member = new Member();
        member.setGid(gid);
        member.setUid(uid);
        member.setStatus("success");
        return memberMapper.insert(member) > 0 ? new Result("success", "添加成功") : new Result("fail", "添加失败");
    }

    /**
     * 拒绝好友请求
     *
     * @param mid
     * @return
     */
    @Override
    public Result rejectRequest(Integer mid) {
        if (memberMapper.update(Wrappers.<Member>lambdaUpdate().eq(Member::getId, mid).set(Member::getStatus, "rejected")) <= 0)
            return new Result("fail", "拒绝失败");
        return new Result("success");
    }

    /**
     * 根据好友用户名、姓名、邮箱查找好友
     *
     * @param uid
     * @param str
     * @return
     */
    @Override
    public Result findFriendByStr(Integer uid, String str) {
        List<FriendGroup> friendGroups = friendGroupUtil.getGroupByUid(uid);
        List<Member> members = new ArrayList<>();
        for (FriendGroup g : friendGroups) {
            members.addAll(memberMapper.selectList(Wrappers.<Member>lambdaQuery().eq(Member::getGid, g.getId())));
        }
        List<HashMap<String, Object>> list1 = new ArrayList<>();
        for (Member m : members) {
            HashMap<String, Object> map = userUtil.getUserById(m.getUid());
            map.put("mid", m.getId());
            FriendGroup g = friendGroupUtil.getGroupById(m.getGid());
            map.put("gid", g.getId());
            map.put("gName", g.getName());
            list1.add(map);
        }
        List<HashMap<String, Object>> list2 = userUtil.findUserByStr(str);
        List<HashMap<String, Object>> list3 = new ArrayList<>();
        for (HashMap<String, Object> m : list1) {
            if (list2.stream().filter(item -> Integer.parseInt(item.get("id").toString()) == Integer.parseInt(m.get("id").toString())).findAny().isPresent()) {
                list3.add(m);
            }
        }
        return new Result("success", list3);
    }

    /**
     * 删除好友
     *
     * @param mid
     * @return
     */
    @Override
    public Result deleteFriend(Integer mid) {
        return memberUtil.deleteMemberById(mid)? new Result("success") :new Result("fail");
    }

    /**
     * 修改好友分组
     *
     * @param mid
     * @return
     */
    @Override
    public Result updateGroup(Integer mid, Integer gid) {
        return memberMapper.update(Wrappers.<Member>lambdaUpdate().eq(Member::getId, mid).set(Member::getGid, gid)) > 0 ?
                new Result("success") : new Result("fail");
    }
}




