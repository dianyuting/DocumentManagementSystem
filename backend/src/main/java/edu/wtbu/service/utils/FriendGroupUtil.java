package edu.wtbu.service.utils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import edu.wtbu.entity.FriendGroup;
import edu.wtbu.mapper.FriendGroupMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class FriendGroupUtil {
    @Resource
    FriendGroupMapper friendGroupMapper;

    /**
     * 根据用户查找用户的分组
     * @param uid
     * @return
     */
    //
    public List<FriendGroup> getGroupByUid(Integer uid) {
        return friendGroupMapper.selectList(Wrappers.<FriendGroup>lambdaQuery().eq(FriendGroup::getUid, uid).orderByAsc(FriendGroup::getName));
    }
    /**
     * 根据组id查找组
     */
    public FriendGroup getGroupById(Integer id){
        return friendGroupMapper.selectOne(Wrappers.<FriendGroup>lambdaQuery().eq(FriendGroup::getId,id));
    }
}
