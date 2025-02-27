package edu.wtbu.controller;

import edu.wtbu.entity.FileShare;
import edu.wtbu.entity.Result;
import edu.wtbu.service.FileShareService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("share")
@RestController
public class FileShareController {
    @Resource
    FileShareService fileShareService;

    @PostMapping()
    public Result addShare(@RequestBody FileShare fileShare){
        return fileShareService.addShare(fileShare);
    }

    @GetMapping("beShared/{mid}")
    public Result getAllShare(@PathVariable Integer mid){
        return fileShareService.getAllShare(mid);
    }
    @GetMapping("share/{fileId}")
    public Result getAllShareByFile(@PathVariable Integer fileId){
        return fileShareService.getAllShareByFile(fileId);
    }
    @DeleteMapping()
    public Result deleteShare(@RequestBody Map map){
        return fileShareService.deleteShare(Integer.parseInt(map.get("id").toString()));
    }
    @PutMapping()
    public Result updateShare(@RequestBody FileShare fileShare){
        return fileShareService.updateShare(fileShare);
    }
}
