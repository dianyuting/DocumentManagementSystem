package edu.wtbu.service.utils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import edu.wtbu.entity.File;
import edu.wtbu.mapper.FileMapper;
import edu.wtbu.service.utils.TaggedFilesUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Component
@Data
public class FileServiceUtil {
    @Resource
    FileMapper fileMapper;
    @Resource
    TaggedFilesUtil taggedFilesUtil;
    public ArrayList getFiles(File f) {
        List<File> files;
        if (f.getParentDirectory().equals("/"))
            files = fileMapper.selectList(Wrappers.<File>lambdaQuery().eq(File::getParentDirectory, f.getParentDirectory() + f.getName()).orderByAsc(File::getIsFile).orderByAsc(File::getName));
        else
            files = fileMapper.selectList(Wrappers.<File>lambdaQuery().eq(File::getParentDirectory, f.getParentDirectory() + "/" + f.getName()).orderByAsc(File::getIsFile).orderByAsc(File::getName));
        ArrayList a = new ArrayList();
        for (File file : files) {
            a.add(new HashMap<>() {{
                put("id", file.getId());
                put("parentDirectory", file.getParentDirectory());
                put("name", file.getName());
                put("isFile", file.getIsFile());
                List<HashMap<String, Object>> l = taggedFilesUtil.getTagByFileId(file.getId());
                if (l != null && l.size() > 0) put("tags", l);
                if (fileMapper.selectList(Wrappers.<File>lambdaQuery().eq(File::getParentDirectory, file.getParentDirectory() + "/" + file.getName())).size() > 0) {
                    put("children", getFiles(file));
                }
            }});
        }
        return a;
    }

    public ArrayList findFilesResult(List<File> list){
        ArrayList arrayList = new ArrayList();
        for (File f : list) {
            arrayList.add(new HashMap<>(){{
                put("id", f.getId());
                put("parentDirectory", f.getParentDirectory());
                put("name", f.getName());
                put("isFile", f.getIsFile());
                List<HashMap<String, Object>> l = taggedFilesUtil.getTagByFileId(f.getId());
                if (l != null && l.size() > 0) put("tags", l);
                if (f.getIsFile() == 0){
                    put("children", getFiles(f));
                }
            }});
        }
        return arrayList;
    }

    public File getFileById(Integer id){
        return fileMapper.selectOne(Wrappers.<File>lambdaQuery().eq(File::getId,id));
    }
}
