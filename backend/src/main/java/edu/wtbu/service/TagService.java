package edu.wtbu.service;

import edu.wtbu.entity.Result;
import edu.wtbu.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author admin
* @description 针对表【tag】的数据库操作Service
* @createDate 2024-04-15 14:45:35
*/
public interface TagService extends IService<Tag> {
    public Result getTagByUserId(Integer uid);
    public Result getTagByName(Integer uid,String name);
    public Result getTagByType(Integer uid,String type);
    public Result addTag(Tag tag);
    public Result updateTag(Tag tag);
    public Result deleteTag(Integer id);
}
