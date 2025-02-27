package edu.wtbu.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wtbu.entity.*;
import edu.wtbu.service.FileShareService;
import edu.wtbu.mapper.FileShareMapper;
import edu.wtbu.service.utils.FileServiceUtil;
import edu.wtbu.service.utils.FriendGroupUtil;
import edu.wtbu.service.utils.MemberUtil;
import edu.wtbu.service.utils.UserUtil;
import edu.wtbu.utils.DateUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
* @author admin
* @description 针对表【file_share】的数据库操作Service实现
* @createDate 2024-04-20 16:40:26
*/
@Service
public class FileShareServiceImpl extends ServiceImpl<FileShareMapper, FileShare>
    implements FileShareService{
    @Resource
    FileShareMapper fileShareMapper;
    @Resource
    FriendGroupUtil friendGroupUtil;
    @Resource
    MemberUtil memberUtil;
    @Resource
    FileServiceUtil fileServiceUtil;
    @Resource
    UserUtil userUtil;

    @Override
    public Result addShare(FileShare fileShare) {
        if(fileShareMapper.selectList(Wrappers.<FileShare>lambdaQuery().eq(FileShare::getFileId,fileShare.getFileId()).and(wrapper1->wrapper1.eq(FileShare::getShareMid,fileShare.getShareMid()))).size() > 0){
            return new Result("fail","该用户已经有此文件的分享，如失效，请修改分享日期");
        }
        return fileShareMapper.insert(fileShare) > 0 ? new Result("success","分享成功"):new Result("fail","分享失败");
    }

    /**
     * 获取所有被分享
     * @param mid
     * @return
     */
    @Override
    public Result getAllShare(Integer mid) {
        //mid 接收分享的成员表id，要获取分享者的mid
        Member m = memberUtil.getMemberById(mid);
        //接收分享的用户id
        Integer uid = friendGroupUtil.getGroupById(m.getGid()).getUid();
        //分享者的用户id
        Integer fid = m.getUid();
        //分享者的mid
        Integer friendMid = memberUtil.getMemberId(fid,uid);
        List<FileShare> fileShares = fileShareMapper.selectList(Wrappers.<FileShare>lambdaQuery().eq(FileShare::getShareMid,friendMid));
        List<File> list = new ArrayList<>();
        for (FileShare s:fileShares) {
            Date d = new Date();
            if(DateUtils.isSame(d,s.getEndTiem()) || DateUtils.isSame(d,s.getStartTime())){
                list.add(fileServiceUtil.getFileById(s.getFileId()));
            }else if(DateUtils.compareDates(d,s.getStartTime()) > 0 && DateUtils.compareDates(d,s.getEndTiem()) < 0){
                list.add(fileServiceUtil.getFileById(s.getFileId()));
            }
        }
        return new Result("success",fileServiceUtil.findFilesResult(list));
    }

    /**
     * 获取所有已分享
     * @param fileId
     * @return
     */
    @Override
    public Result getAllShareByFile(Integer fileId) {
        List<FileShare> fileShares = fileShareMapper.selectList(Wrappers.<FileShare>lambdaQuery().eq(FileShare::getFileId,fileId));
        List<HashMap<String,Object>> list = new ArrayList<>();
        for (FileShare f :fileShares) {
            HashMap<String,Object> map = new HashMap<>();
            map.put("shareId",f.getId());
            map.put("startDate",f.getStartTime());
            map.put("endDate",f.getEndTiem());
            Date d = new Date();
            if(DateUtils.isSame(d,f.getEndTiem()) || DateUtils.isSame(d,f.getStartTime())){
                map.put("status","可用");
            }else if(DateUtils.compareDates(d,f.getStartTime()) > 0 && DateUtils.compareDates(d,f.getEndTiem()) < 0){
                map.put("status","可用");
            }else if(DateUtils.compareDates(d,f.getStartTime()) < 0){
                map.put("status","未生效");
            }else if (DateUtils.compareDates(d,f.getEndTiem()) > 0){
                map.put("status","已失效");
            }
//            if(d.after(f.getStartTime()) && d.before(f.getEndTiem())){
//                map.put("status","可用");
//            }else if(d.before(f.getStartTime())){
//                map.put("status","未生效");
//            }else if (d.after(f.getEndTiem())){
//                map.put("status","已失效");
//            }else{
//                map.put("status","可用");
//            }
            Member member = memberUtil.getMemberById(f.getShareMid());
            map.put("userInfo",userUtil.getUserById(member.getUid()));
            list.add(map);
        }
        return new Result("success",list);
    }

    @Override
    public Result deleteShare(Integer id) {
        return fileShareMapper.deleteById(id) > 0 ? new Result("success","取消分享成功"):new Result("fail","取消分享失败");
    }

    @Override
    public Result updateShare(FileShare fileShare) {
        Integer i =fileShareMapper.update(Wrappers.<FileShare>lambdaUpdate().eq(FileShare::getId,fileShare.getId()).set(FileShare::getStartTime,fileShare.getStartTime()).set(FileShare::getEndTiem,fileShare.getEndTiem()));
        return i > 0 ? new Result("success","修改分享成功"):new Result("fail","修改分享失败");
    }
}




