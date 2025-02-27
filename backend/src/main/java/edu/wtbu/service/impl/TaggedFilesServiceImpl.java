package edu.wtbu.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wtbu.entity.Result;
import edu.wtbu.entity.TaggedFiles;
import edu.wtbu.service.TaggedFilesService;
import edu.wtbu.mapper.TaggedFilesMapper;
import edu.wtbu.service.utils.TaggedFilesUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author admin
 * @description 针对表【tagged_files】的数据库操作Service实现
 * @createDate 2024-04-15 17:24:32
 */
@Service
public class TaggedFilesServiceImpl extends ServiceImpl<TaggedFilesMapper, TaggedFiles>
        implements TaggedFilesService {
    @Resource
    TaggedFilesMapper taggedFilesMapper;
    @Resource
    TaggedFilesUtil taggedFilesUtil;

    @Override
    public Result getTagByFileId(Integer fid) {
        List<HashMap<String,Object>> l = taggedFilesUtil.getTagByFileId(fid);
        if (l != null && l.size() > 0) return new Result("success", l);
        return new Result("fail");
    }

    @Override
    public Result addTaggedFiles(TaggedFiles taggedFiles) {
        if (taggedFilesMapper.selectList(Wrappers.<TaggedFiles>lambdaQuery()
                .eq(TaggedFiles::getFileId, taggedFiles.getFileId())
                .and(w -> w.eq(TaggedFiles::getTagId, taggedFiles.getTagId()))).size() > 0)
            return new Result("fail","该标签已存在");
            if (taggedFilesMapper.insert(taggedFiles) > 0) return new Result("success");
        return new Result("fail", "添加失败");
    }

    @Override
    public Result deleteTaggedFiles(Integer taggedId) {
        if (taggedFilesMapper.deleteById(taggedId) > 0)
            return new Result("success");
        return new Result("fail", "删除失败");
    }
}




