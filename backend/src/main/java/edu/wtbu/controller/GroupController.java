package edu.wtbu.controller;

import edu.wtbu.entity.FriendGroup;
import edu.wtbu.entity.Result;
import edu.wtbu.service.FriendGroupService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RequestMapping("group")
@RestController
public class GroupController {
    @Resource
    FriendGroupService groupService;
    @PostMapping()
    public Result addGroup(@RequestBody FriendGroup group){
        return groupService.addGroup(group);
    }
    @PutMapping()
    public Result updateGroup(@RequestBody FriendGroup group){
        return groupService.updateGroup(group);
    }
    @DeleteMapping()
    public Result deleteGroup(@RequestBody FriendGroup group){
        return groupService.deleteGroup(group);
    }
    @GetMapping("{uid}")
    public Result getGroup(@PathVariable Integer uid){
        return groupService.getGroup(uid);
    }
}
