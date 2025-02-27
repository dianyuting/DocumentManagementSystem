package edu.wtbu.controller;

import edu.wtbu.entity.Result;
import edu.wtbu.entity.TaggedFiles;
import edu.wtbu.service.TaggedFilesService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("tagged")
@RestController
public class TaggedFilesController {
    @Resource
    TaggedFilesService taggedFilesService;
    @GetMapping("{fid}")
    public Result getTagByUserIdAndByFileId(@PathVariable Integer fid){
        return taggedFilesService.getTagByFileId(fid);
    }
    @PostMapping("")
    public Result addTaggedFiles(@RequestBody TaggedFiles taggedFiles){
        return taggedFilesService.addTaggedFiles(taggedFiles);
    }
    @DeleteMapping("")
    public Result deleteTaggedFiles(@RequestBody Map m){
        return taggedFilesService.deleteTaggedFiles(Integer.parseInt(m.get("id").toString()));
    }
}
