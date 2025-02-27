package edu.wtbu.service;

import edu.wtbu.entity.Result;
import edu.wtbu.entity.TaggedFiles;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author admin
 * @description 针对表【tagged_files】的数据库操作Service
 * @createDate 2024-04-15 17:24:32
 */
public interface TaggedFilesService extends IService<TaggedFiles> {

    public Result getTagByFileId(Integer fid);

    public Result addTaggedFiles(TaggedFiles taggedFiles);

    public Result deleteTaggedFiles(Integer taggedId);
}
