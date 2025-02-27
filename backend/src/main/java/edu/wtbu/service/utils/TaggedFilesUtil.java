package edu.wtbu.service.utils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import edu.wtbu.entity.Tag;
import edu.wtbu.entity.TaggedFiles;
import edu.wtbu.mapper.TaggedFilesMapper;
import edu.wtbu.service.utils.TagUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Data
public class TaggedFilesUtil {
    @Resource
    TaggedFilesMapper taggedFilesMapper;
    @Resource
    TagUtil tagUtil;
    public Integer getTaggedNum(Integer tagId) {
        return taggedFilesMapper.selectCount(Wrappers.<TaggedFiles>lambdaQuery().eq(TaggedFiles::getTagId, tagId)).intValue();
    }
    public List<HashMap<String,Object>> getTagByFileId(Integer fid) {
        List<TaggedFiles> taggeds = taggedFilesMapper.selectList(Wrappers.<TaggedFiles>lambdaQuery().eq(TaggedFiles::getFileId, fid));
        List<HashMap<String,Object>> tags = new ArrayList<>();
        for (TaggedFiles tagged :
                taggeds) {
            Tag t = tagUtil.getTagById(tagged.getTagId());
            tags.add(new HashMap<>(){{
                put("id",tagged.getId());
                put("name",t.getName());
                put("type",t.getType());
            }});
        }
        return tags;
    }
    public Boolean deleteTaggedFilesByFileId(Integer fid) {
        if (taggedFilesMapper.delete(Wrappers.<TaggedFiles>lambdaQuery().eq(TaggedFiles::getFileId, fid)) > 0)
            return true;
        return false;
    }
    public Boolean deleteTaggedFilesByTagId(Integer tid) {
        if(taggedFilesMapper.delete(Wrappers.<TaggedFiles>lambdaQuery().eq(TaggedFiles::getTagId,tid))>0)
            return true;
        return false;
    }

}
