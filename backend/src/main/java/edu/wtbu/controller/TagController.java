package edu.wtbu.controller;

import edu.wtbu.entity.Result;
import edu.wtbu.entity.Tag;
import edu.wtbu.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("tag")
@RestController
public class TagController {
    @Resource
    TagService tagService;
    @GetMapping("/all/{uid}")
    public Result getTagByUserId(@PathVariable Integer uid){
        return tagService.getTagByUserId(uid);
    }
    @GetMapping("/name/{uid}/{name}")
    public Result getTagByName(@PathVariable Integer uid,@PathVariable String name){
        return tagService.getTagByName(uid,name);
    }
    @GetMapping("/type/{uid}/{type}")
    public Result getTagByType(@PathVariable Integer uid,@PathVariable String type){
        return tagService.getTagByType(uid,type);
    }
    @PostMapping("")
    public Result addTag(@RequestBody Tag tag){
        return tagService.addTag(tag);
    }
    @PutMapping("")
    public Result updateTag(@RequestBody Tag tag){
        return tagService.updateTag(tag);
    }
    @DeleteMapping("")
    public Result deleteTag(@RequestBody Map m){
        return tagService.deleteTag(Integer.parseInt(m.get("id").toString()));
    }

}
