package edu.wtbu.service;

import edu.wtbu.entity.FileShare;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.wtbu.entity.Result;

/**
* @author admin
* @description 针对表【file_share】的数据库操作Service
* @createDate 2024-04-20 16:40:26
*/
public interface FileShareService extends IService<FileShare> {
    public Result addShare(FileShare fileShare);
    public Result getAllShare(Integer mid);
    public Result getAllShareByFile(Integer fileId);
    public Result deleteShare(Integer id);
    public Result updateShare(FileShare fileShare);

}
