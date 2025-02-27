package edu.wtbu.service.utils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import edu.wtbu.entity.Tag;
import edu.wtbu.mapper.TagMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TagUtil {
    @Resource
    TagMapper tagMapper;
    public Tag getTagById(Integer id) {
        return tagMapper.selectOne(Wrappers.<Tag>lambdaQuery().eq(Tag::getId,id));
    }
}
