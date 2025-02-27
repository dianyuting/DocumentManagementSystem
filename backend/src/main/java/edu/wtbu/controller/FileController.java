package edu.wtbu.controller;

import edu.wtbu.entity.Result;
import edu.wtbu.service.FileService;
import edu.wtbu.utils.JsonUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;

@RequestMapping("file")
@RestController
public class FileController {
    @Resource
    FileService fileService;

    @GetMapping("getAll/{uid}")
    public Result getAll(@PathVariable Integer uid) {
        return fileService.getAll(uid);
    }

    @PostMapping("findFile")
    public Result findFile(@RequestBody Map map) {
        Integer uid = Integer.parseInt(map.get("uid").toString());
        String name = map.get("name") == null || map.get("name").toString().equals("") ? "%%" : "%" + map.get("name").toString() + "%";
        String tagName = map.get("tagName") == null || map.get("tagName").toString().equals("") ? null : "%" + map.get("tagName").toString() + "%";
        String type = map.get("type") == null || map.get("type").toString().equals("") ? null : "%" + map.get("type").toString() + "%";
        return fileService.findFileByNameAndTagAndType(uid, name, tagName, type);
    }

    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //用requestPart会报错，前后端数据不匹配
    public Result upload(@RequestParam("path") String path,
                         @RequestParam("uid") Integer uid,
                         @RequestParam(value = "uploadfile1", required = false) MultipartFile file,
                         @RequestParam(value = "uploadfile2", required = false) MultipartFile[] files) {
        if (file != null && !file.isEmpty()) {
            return fileService.uploadFile(path, uid, file);
        }
        if (files != null && files.length > 0) {
            return fileService.uploadFiles(path, uid, files);
        }
        return new Result("false", "上传文件为空");
    }

    @PostMapping("createFolder")
    public Result createFolder(@RequestParam("path") String path,
                               @RequestParam("name") String name,
                               @RequestParam("uid") Integer uid) {
        return fileService.createFolder(path, name, uid);
    }

    //前端get请求没有请求体
    @PostMapping("download")
//    public ResponseEntity<InputStreamResource> download(@RequestParam("path") String path,@RequestParam("name") String name) throws FileNotFoundException {
//        java.io.File file = fileService.downloadFile(name, path);
//        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
//        ResponseEntity<InputStreamResource> responseEntity = ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=" + name)
//                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,HttpHeaders.CONTENT_DISPOSITION)
//                //以流的形式返回文件数据
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .contentLength(file.length())
//                .body(resource);
//        file.delete();
//        return responseEntity;
//    }
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder b = new StringBuilder();
        BufferedReader r = request.getReader();
        String l;
        while ((l = r.readLine()) != null) {
            b.append(l);
        }
        Map m = JsonUtil.jsonToMap(b.toString());
        java.io.File file = fileService.downloadFile(m.get("name").toString(), m.get("path").toString());
        if (file == null) {
            response.setStatus(404);
            response.getWriter().append(new String("文件不存在".getBytes("UTF-8"), "ISO-8859-1"));
            return;
        }
        InputStream is = new FileInputStream(file);
        ServletOutputStream os = response.getOutputStream();
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition",
                "attachment;fileName=" + new String(m.get("name").toString().getBytes("UTF-8"), "ISO-8859-1"));
        response.setContentType("application/octet-stream;charset=UTF-8");
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        is.close();
        file.delete();
    }

    @DeleteMapping()
    public Result deleteFile(@RequestBody Map m) {
        return fileService.deleteFile(m.get("path").toString(), Integer.parseInt(m.get("uid").toString()), m.get("fileName").toString());
    }
}
