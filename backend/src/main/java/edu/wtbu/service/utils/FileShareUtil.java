package edu.wtbu.service.utils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import edu.wtbu.entity.FileShare;
import edu.wtbu.mapper.FileShareMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class FileShareUtil {
    @Resource
    FileShareMapper fileShareMapper;
    public Integer deleteByMid(Integer mid){
        return fileShareMapper.delete(Wrappers.<FileShare>lambdaQuery().eq(FileShare::getShareMid,mid));
    }
    public Integer deleteByFileId(Integer fid){
        return fileShareMapper.delete(Wrappers.<FileShare>lambdaQuery().eq(FileShare::getFileId,fid));
    }
}
