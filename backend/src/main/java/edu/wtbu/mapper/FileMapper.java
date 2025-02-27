package edu.wtbu.mapper;

import edu.wtbu.entity.File;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author admin
* @description 针对表【file】的数据库操作Mapper
* @createDate 2024-04-10 12:51:55
* @Entity edu.wtbu.entity.File
*/
public interface FileMapper extends BaseMapper<File> {

    public List<File> findFileByName(Integer uid,String name);
    public List<File> findFileByNameAndType(Integer uid,String name, String type);
    public List<File> findFileByNameAndTagAndType(Integer uid,String name, String tag ,String type);
}




