package edu.wtbu.service;

import edu.wtbu.entity.File;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.wtbu.entity.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author admin
 * @description 针对表【file】的数据库操作Service
 * @createDate 2024-03-30 15:34:15
 */
public interface FileService extends IService<File> {
    public Result getAll(Integer uid);

    public Result findFileByNameAndTagAndType(Integer uid, String name, String tagName,String type);

    public Result createFolder(String path, String name, Integer uid);

    public Result uploadFile(String path, Integer uid, MultipartFile file);

    public Result uploadFiles(String path, Integer uid, MultipartFile[] files);

    public java.io.File downloadFile(String fileName, String path) throws IOException;

    public Result deleteFile(String path, Integer uid, String fileName);

}
