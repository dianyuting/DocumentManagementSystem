package edu.wtbu.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wtbu.entity.Result;
import edu.wtbu.entity.Tag;
import edu.wtbu.service.TagService;
import edu.wtbu.mapper.TagMapper;
import edu.wtbu.service.utils.TaggedFilesUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author admin
 * @description 针对表【tag】的数据库操作Service实现
 * @createDate 2024-04-15 14:45:35
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
        implements TagService {
    @Resource
    TagMapper tagMapper;
    @Resource
    TaggedFilesUtil taggedFilesUtil;

    public List<HashMap<String, Object>> tagsMap(List<Tag> tags) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (Tag tag : tags) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", tag.getId());
            map.put("name", tag.getName());
            map.put("type", tag.getType());
            map.put("uid", tag.getUid());
            map.put("num", taggedFilesUtil.getTaggedNum(tag.getId()));
            list.add(map);
        }
        return list;
    }

    @Override
    public Result getTagByUserId(Integer uid) {
        List<Tag> tags = tagMapper.selectList(Wrappers.<Tag>lambdaQuery().eq(Tag::getUid, uid));
        return new Result("success", tagsMap(tags));
    }

    @Override
    public Result getTagByName(Integer uid, String name) {
        List<Tag> tags = tagMapper.selectList(Wrappers.<Tag>lambdaQuery().eq(Tag::getUid, uid).like(Tag::getName, "%" + name + "%"));
        return new Result("success", tagsMap(tags));
    }

    @Override
    public Result getTagByType(Integer uid, String type) {
        List<Tag> tags = tagMapper.selectList(Wrappers.<Tag>lambdaQuery().eq(Tag::getUid, uid).like(Tag::getType, "%" + type + "%"));
        return new Result("success", tagsMap(tags));
    }

    @Override
    public Result addTag(Tag tag) {
        Tag tag1 = tagMapper.selectOne(Wrappers.<Tag>lambdaQuery().eq(Tag::getUid, tag.getUid()).and(w -> w.eq(Tag::getName, tag.getName())));
        if (tag1 != null) return new Result("fail", "该标签已存在");
        if (tagMapper.insert(tag) > 0) return new Result("success");
        return new Result("fail", "添加失败");
    }

    @Override
    public Result updateTag(Tag tag) {
        Tag tag1 = tagMapper.selectOne(Wrappers.<Tag>lambdaQuery().eq(Tag::getUid, tag.getUid()).and(w -> w.eq(Tag::getName, tag.getName())));
        if(tag1 != null && tag1.getId() != tag.getId()) return new Result("fail", "该标签已存在");
        if (tagMapper.updateById(tag) > 0) return new Result("success");
        return new Result("fail", "修改失败");
    }

    @Override
    public Result deleteTag(Integer id) {
        if(!taggedFilesUtil.deleteTaggedFilesByTagId(id)) return new Result("fail","删除失败");
        if (tagMapper.deleteById(id) > 0) return new Result("success");
        return new Result("fail", "删除失败");
    }
}




