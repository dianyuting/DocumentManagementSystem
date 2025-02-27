package edu.wtbu.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wtbu.entity.FriendGroup;
import edu.wtbu.entity.Member;
import edu.wtbu.entity.Result;
import edu.wtbu.mapper.FriendGroupMapper;
import edu.wtbu.service.FriendGroupService;
import edu.wtbu.service.utils.FriendGroupUtil;
import edu.wtbu.service.utils.MemberUtil;
import edu.wtbu.service.utils.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author admin
 * @description 针对表【friend_group】的数据库操作Service实现
 * @createDate 2024-04-19 18:35:30
 */
@Service
public class FriendGroupServiceImpl extends ServiceImpl<FriendGroupMapper, FriendGroup>
        implements FriendGroupService {
    @Resource
    FriendGroupMapper groupMapper;
    @Resource
    MemberUtil memberUtil;
    @Resource
    UserUtil userUtil;
    @Resource
    FriendGroupUtil friendGroupUtil;

    @Override
    public Result addGroup(FriendGroup group) {
        if (groupMapper.selectList(Wrappers.<FriendGroup>lambdaQuery().eq(FriendGroup::getUid, group.getUid()).eq(FriendGroup::getName, group.getName())).size() > 0)
            return new Result("fail", "该分组已存在");
        return groupMapper.insert(group) > 0 ? new Result("success", "添加成功") : new Result("fail", "添加失败");
    }

    @Override
    public Result updateGroup(FriendGroup group) {
        if (groupMapper.selectList(Wrappers.<FriendGroup>lambdaQuery().eq(FriendGroup::getUid, group.getUid()).eq(FriendGroup::getName, group.getName())).size() > 0)
            return new Result("fail", "该分组已存在");
        return groupMapper.updateById(group) > 0 ? new Result("success", "修改成功") : new Result("fail", "修改失败");
    }

    @Override
    public Result deleteGroup(FriendGroup group) {
        memberUtil.deleteMemberByGroup(group.getId());
        return groupMapper.deleteById(group) > 0 ? new Result("success", "删除成功") : new Result("fail", "删除失败");
    }

    @Override
    public Result getGroup(Integer uid) {
        List<FriendGroup> l = friendGroupUtil.getGroupByUid(uid);
        ArrayList arrayList = new ArrayList();
        for (FriendGroup g : l) {
            arrayList.add(new HashMap<>() {{
                put("level", 1);
                put("id", g.getId());
                put("uid", g.getUid());
                put("name", g.getName());
                List<Member> members = memberUtil.getMemberByGroup(g.getId());
                if (members.size() > 0) {
                    ArrayList memberList = new ArrayList();
                    for (Member m : members) {
                        HashMap<String, Object> user = userUtil.getUserById(m.getUid());
                        memberList.add(new HashMap<>() {{
                            put("level", 2);
                            put("mid",m.getId());
                            put("friendId",m.getUid());
                            put("info", user);
                        }});
                    }
                    //排序
                    Collections.sort(memberList, new Comparator<HashMap>() {
                        @Override
                        public int compare(HashMap h1, HashMap h2) {
                            HashMap map1 = (HashMap) h1.get("info");
                            HashMap map2 = (HashMap) h2.get("info");
                            return map1.get("username").toString().compareTo(map2.get("username").toString());
                        }
                    });
                    put("children", memberList);
                }
            }});
        }
        return new Result("success", arrayList);
    }

}




