package edu.wtbu.controller;

import edu.wtbu.entity.Result;
import edu.wtbu.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("friend")
@RestController
public class MemberController {
    @Resource
    MemberService memberService;
    @GetMapping("request/{uid}")
    public Result getAllFriendRequest(@PathVariable Integer uid){
        return memberService.getAllFriendRequest(uid);
    }
    @GetMapping("sent/{uid}")
    public Result getAllSentRequest(@PathVariable Integer uid){
        return memberService.getAllSentRequest(uid);
    }
    @GetMapping("find/{uid}/{str}")
    public Result findFriendByStr(@PathVariable Integer uid,@PathVariable String str){
        return memberService.findFriendByStr(uid,str);
    }
    @PostMapping("send")
    public Result sendFriendRequest(@RequestBody Map map){
        Integer uid = Integer.parseInt(map.get("uid").toString());
        Integer gid = Integer.parseInt(map.get("gid").toString());
        return memberService.sendFriendRequest(uid,gid);
    }
    @PostMapping("verified")
    public Result verifiedFriend(@RequestBody Map map){
        Integer mid = Integer.parseInt(map.get("mid").toString());
        Integer uid = Integer.parseInt(map.get("uid").toString());
        Integer gid = Integer.parseInt(map.get("gid").toString());
        return memberService.verifiedFriend(mid,uid,gid);
    }
    @PutMapping("reject")
    public Result rejectRequest(@RequestBody Map map){
        Integer mid = Integer.parseInt(map.get("mid").toString());
        return memberService.rejectRequest(mid);
    }
    @DeleteMapping()
    public Result deleteFriend(@RequestBody Map map){
        Integer mid = Integer.parseInt(map.get("mid").toString());
        return memberService.deleteFriend(mid);
    }
    @PutMapping("updateGroup")
    public Result updateGroup(@RequestBody Map map){
        Integer mid = Integer.parseInt(map.get("mid").toString());
        Integer gid = Integer.parseInt(map.get("gid").toString());
        return memberService.updateGroup(mid,gid);
    }
}
