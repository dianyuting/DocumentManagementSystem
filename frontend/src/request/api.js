//@指src目录
import request from "@/request/index.js"
/**
 * @description 用户登录
 */
export function login(data) {
    return request({
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=utf-8"
        },
        method: "get",
        url: "/api/user/login/" + data.username + '/' + data.password,
    });
}
/**
 * @description 上传用户头像
 */
export function uploadProfile(data) {
    return request({
        headers: {
            "Content-Type": "multipart/form-data;charset=utf-8"
        },
        method: "post",
        url: "/api/user/uploadProfile",
        data: data,
    });
}

/**
 * @description 用户注册
 */
export function register(data) {
    return request({
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
        method: "post",
        url: "/api/user/register",
        data: data,
    });
}

/**
 * @description 获取随机用户名
 */
export function getRandomUsername() {
    return request({
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
        },
        method: "get",
        url: "/api/user/getRandomUsername",
    });
}

/**
 * @description 获取用户信息
 */
export function getUserInfo(data) {
    return request({
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
        },
        method: "get",
        url: "api/user/getUserById/" + data,
    });
}

/**
 * @description 用户注册
 */
export function updateUserInfo(data) {
    return request({
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
        method: "put",
        url: "/api/user/updateUser",
        data: data,
    });
}
/**
 * @description 获取用户密保信息
 */
export function forget(data) {
    return request({
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
        method: "get",
        url: "api/user/forget/" + data,
    });
}
/**
 * @description 设置用户密保信息
 */
export function addEncryption(data) {
    return request({
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
        method: "post",
        url: "api/user/addEncryption",
        data: data,
    });
}
/**
 * @description 发送验证码
 */
export function sendCode(data) {
    return request({
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
        method: "get",
        url: "api/email/" + data,
    });
}
/**
 * @description 验证忘记密码密保信息
 */
export function verifyForget(data) {
    return request({
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
        method: "post",
        url: "api/user/verify",
        data: data,
    });
}
/**
 * @description 确认密码
 */
export function confirmPassword(data) {
    return request({
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
        method: "get",
        url: "api/user/confirmPassword/" + data.username + "/" + data.password,
    });
}
/**
 * @description 修改密码
 */
export function updatePassword(data) {
    return request({
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
        method: "put",
        url: "api/user/updatePassword",
        data: data,
    });
}


/**
 * @description 上传文件
 */
export function upload(data) {
    return request({
        headers: {
            "Content-Type": "multipart/form-data;charset=utf-8"
        },
        method: "post",
        url: "/api/file/upload",
        data: data,
    });
}

/**
 * @description 创建文件夹
 */
export function createFiler(data) {
    return request({
        headers: {
            "Content-Type": "multipart/form-data;charset=utf-8"
        },
        method: "post",
        url: "/api/file/createFolder",
        data: data,
    });
}
/**
 * @description 获取所有文件信息
 */
export function showAll(data) {
    return request({
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=utf-8"
        },
        method: "get",
        url: "/api/file/getAll/" + data,
    })
}
/**
 * @description 根据标签或文件名查找文件信息
 */
export function findFile(data) {
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "post",
        url: "/api/file/findFile",
        data: data,
    })
}
/**
 * @description 下载文件
 */
export function download(data) {
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "post",
        url: "/api/file/download",
        data: data,
        responseType: 'arraybuffer'
    });
}
/**
 * @description 下载文件
 */
export function deletFile(data) {
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "delete",
        url: "/api/file",
        data: data,
    });
}




/**
 * @description 获取所有标签
 */
export function getTagByUid(data) {
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "get",
        url: "/api/tag/all/" + data,
    });
}
/**
 * @description 标签名查找
 */
export function getTagByName(data) {
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "get",
        url: "/api/tag/name/" + data.uid + '/' + data.name,
    });
}
/**
 * @description 标签类型查找
 */
export function getTagByType(data) {
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "get",
        url: "/api/tag/type/" + data.uid + "/" + data.type,
    });
}

/**
 * @description 添加标签
 */
export function addTag(data) {
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "post",
        url: "/api/tag",
        data: data,
    });
}

/**
 * @description 修改标签
 */
export function updateTag(data) {
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "put",
        url: "/api/tag",
        data: data,
    });
}

/**
 * @description 删除标签
 */
export function deleteTag(data) {
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "delete",
        url: "/api/tag",
        data: data,
    });
}




/**
 * @description 文件添加标签
 */
export function fileAddTag(data) {
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "post",
        url: "/api/tagged",
        data: data,
    });
}

/**
 * @description 文件删除标签
 */
export function fileDeleteTag(data) {
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "delete",
        url: "/api/tagged",
        data: data,
    });
}




/**
 * @description 删除分组
 */
export function deleteGroup(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "delete",
        url: "/api/group",
        data: data,
    });
}
/**
 * @description 获取分组
 */
export function getGroup(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "get",
        url: "/api/group/"+data,
    });
}
/**
 * @description 添加分组
 */
export function addGroup(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "post",
        url: "/api/group",
        data: data,
    });
}
/**
 * @description 修改分组
 */
export function updateGroup(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "put",
        url: "/api/group",
        data: data,
    });
}



/**
 * @description 根据用户名或邮箱查找用户
 */
export function findUserByUsernameOrEmail(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method:"get",
        url:"/api/user/findUserByUsernameOrEmail/"+data.uid+"/"+data.str,
    });
}



/**
 * @description 获取好友请求
 */
export function getFriendRequest(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "get",
        url: "/api/friend/request/"+data
    });
}
/**
 * @description 获取发送的好友请求
 */
export function getSentFriendRequest(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "get",
        url: "/api/friend/sent/" + data,
    });
}
/**
 * @description 查找好友
 */
export function findFriend(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "get",
        url: "/api/friend/find/"+data.uid+"/"+data.str,
    });
}
/**
 * @description 发送好友请求
 */
export function sendFriendRequest(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "post",
        url: "/api/friend/send",
        data: data,
    });
}
/**
 * @description 接受好友请求
 */
export function verifiedFriendRequest(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "post",
        url: "/api/friend/verified",
        data: data,
    });
}
/**
 * @description 拒绝好友请求
 */
export function rejectFriendRequest(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "put",
        url: "/api/friend/reject",
        data: data,
    });
}
/**
 * @description 删除好友
 */
export function deleteFriend(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "delete",
        url: "/api/friend",
        data: data,
    });
}
/**
 * @description 修改好友分组
 */
export function updateFriendGroup(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "put",
        url: "/api/friend/updateGroup",
        data: data,
    });
}




/**
 * @description 分享文件
 */
export function shareFile(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "post",
        url: "/api/share",
        data: data,
    });
}
/**
 * @description 查看被分享的文件
 */
export function getBeSharedFile(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "get",
        url: "/api/share/beShared/"+data,
    });
}
/**
 * @description 查看分享的文件
 */
export function getShareFile(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "get",
        url: "/api/share/share/"+data,
    });
}

/**
 * @description 删除文件分享
 */
export function deleteShareFile(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "delete",
        url: "/api/share",
        data: data,
    });
}
/**
 * @description 修改文件分享（日期）
 */
export function updateShareFile(data){
    return request({
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        method: "put",
        url: "/api/share",
        data: data,
    });
}