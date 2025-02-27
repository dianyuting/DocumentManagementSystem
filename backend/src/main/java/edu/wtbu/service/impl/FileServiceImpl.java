package edu.wtbu.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wtbu.entity.File;
import edu.wtbu.entity.Result;
import edu.wtbu.mapper.FileMapper;
import edu.wtbu.service.FileService;
import edu.wtbu.service.utils.FileServiceUtil;
import edu.wtbu.service.utils.FileShareUtil;
import edu.wtbu.service.utils.TaggedFilesUtil;
import edu.wtbu.utils.FTPUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author admin
 * @description 针对表【file】的数据库操作Service实现
 * @createDate 2024-03-30 15:34:15
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
        implements FileService {
    @Resource
    FTPUtil ftpUtil;
    @Resource
    FileMapper fileMapper;
    @Resource
    TaggedFilesUtil taggedFilesUtil;
    @Resource
    FileServiceUtil fileServiceUtil;
    @Resource
    FileShareUtil fileShareUtil;

    public boolean saveDatabase(String path, String name, Integer uid, Integer type) {
        File newFile = new File();
        newFile.setParentDirectory(path);
        newFile.setName(name);
        newFile.setIsFile(type);
        newFile.setUid(uid);
        newFile.setCreateTime(new Date());
        if (fileMapper.insert(newFile) <= 0) return false;
        return true;
    }

    @Override
    public Result getAll(Integer uid) {
        File f = fileMapper.selectList(Wrappers.<File>lambdaQuery().eq(File::getUid, uid).eq(File::getParentDirectory, '/')).get(0);
        ArrayList array = fileServiceUtil.getFiles(f);
        return new Result("success", array);
    }

    @Override
    public Result findFileByNameAndTagAndType(Integer uid, String name, String tagName, String type) {
        boolean tagStatus = tagName == null || tagName.equals("");
        boolean typeStatus = type == null || type.equals("");
        List<File> list = null;
        if (tagStatus && typeStatus) {
            list = fileMapper.findFileByName(uid, name);
        } else if (tagStatus) {
            list = fileMapper.findFileByNameAndType(uid, name, type);
        } else {
            list = fileMapper.findFileByNameAndTagAndType(uid, name, tagName, type);
        }
        return new Result("success",fileServiceUtil.findFilesResult(list));
    }


    //创建目录
    @Override
    public Result createFolder(String path, String name, Integer uid) {
        Result result = new Result("fail");
        try {
            if (ftpUtil.existFile(path + '/' + name)) return new Result("fail", "已有该目录");
            if (!ftpUtil.createFolder(path + '/' + name)) {
                result.setData("创建目录失败");
                return result;
            }
            if (!saveDatabase(path, name, uid, 0)) return new Result("fail", "添加目录信息失败");
            result.setFlag("success");
        } catch (IOException e) {
            result.setData("创建目录失败");
        }
        return result;
    }

    //上传单个文件
    @Override
    public Result uploadFile(String path, Integer uid, MultipartFile file) {
        Result result = new Result("fail");
        try {
            if (!ftpUtil.existFile(path)) {
                if (!ftpUtil.createFolder(path))
                    return new Result("fail", "创建目录失败");
            }
            if (ftpUtil.existFile(path, file.getOriginalFilename())) {
                result.setData("已有同名文件");
                return result;
            }
            if (ftpUtil.uploadToFtp(path, file.getOriginalFilename(), file.getInputStream())) {
                if (!saveDatabase(path, file.getOriginalFilename(), uid, 1))
                    return new Result("fail", "添加文件信息失败");
                return new Result("success", "上传成功");
            } else {
                result.setData("上传失败");
            }
        } catch (Exception e) {
            result.setData("文件转换错误");
            return result;
        }
        return result;
    }

    //上传多个文件
    @Override
    public Result uploadFiles(String path, Integer uid, MultipartFile[] files) {
        Result result = new Result("fail");
        try {
            if (!ftpUtil.existFile(path)) ftpUtil.createFolder(path);
            if (ftpUtil.uploadFiles(path, files)) {
                for (MultipartFile file :
                        files) {
                    if (!saveDatabase(path, file.getOriginalFilename(), uid, 1))
                        return new Result("fail", "添加文件信息失败");
                }
                return new Result("success", "上传成功");
            } else {
                result.setData("上传失败");
            }
        } catch (Exception e) {
            result.setData("文件转换错误");
            return result;
        }
        return result;
    }

    //下载
    @Override
    public java.io.File downloadFile(String fileName, String path) throws IOException {
        if (!ftpUtil.existFile(path, fileName)) return null;
        return ftpUtil.downloadFileToFtp(fileName, path);
    }

    //删除
    @Override
    public Result deleteFile(String path, Integer uid, String fileName) {
        boolean status = false;
        try {
            status = delete(path, fileName, uid);
        } catch (Exception e) {
            return new Result("fail", "服务器端错误");
        }
        return status ? new Result("success", "删除成功") : new Result("fail", "删除失败");
    }

    public Boolean delete(String path, String name, Integer uid) throws IOException {
        File file = fileMapper.selectOne(Wrappers.<File>lambdaQuery().eq(File::getUid, uid).eq(File::getParentDirectory, path).eq(File::getName, name));
        if (file.getIsFile() == 1) {
            if (!ftpUtil.existFile(path, name)) return false;
            taggedFilesUtil.deleteTaggedFilesByFileId(file.getId());
            fileShareUtil.deleteByFileId(file.getId());
            fileMapper.delete(Wrappers.<File>lambdaQuery().eq(File::getId, file.getId()));
            return ftpUtil.deleteFileToFtp(path, name);
        } else if (fileMapper.selectList(Wrappers.<File>lambdaQuery().eq(File::getUid, uid).eq(File::getParentDirectory, path + "/" + name)).size() == 0) {
            if (!ftpUtil.existFile(path + "/" + name)) return false;
            taggedFilesUtil.deleteTaggedFilesByFileId(file.getId());
            fileShareUtil.deleteByFileId(file.getId());
            fileMapper.delete(Wrappers.<File>lambdaQuery().eq(File::getId, file.getId()));
            return ftpUtil.deleteFileToFtp(path, name);
        } else {
            List<File> files = fileMapper.selectList(Wrappers.<File>lambdaQuery().eq(File::getUid, uid).eq(File::getParentDirectory, path + "/" + name));
            for (File f : files) {
                while (!delete(f.getParentDirectory(), f.getName(), f.getUid())) ;
            }
            delete(path, name, uid);
            return true;
        }
    }
}




