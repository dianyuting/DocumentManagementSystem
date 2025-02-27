<template>
    <el-menu router :default-active="activeIndex" class="el-menu-vertical-demo" @open="handleOpen" @close="handleClose"
        @select="selectMenu">
        <el-menu-item index="/mydocument">
            <el-icon>
                <Folder />
            </el-icon>
            <template #title>我的文件</template>
        </el-menu-item>
        <el-menu-item index="userManageAside-2" @click="showDrawer">
            <el-icon>
                <User />
            </el-icon>
            <template #title>好友列表</template>
        </el-menu-item>
        <el-menu-item index="labelManagement">
            <el-icon>
                <CollectionTag />
            </el-icon>
            <template #title>标签管理</template>
        </el-menu-item>
    </el-menu>
    <el-drawer v-model="drawer" title="好友列表" :before-close="closeDrawer">
        <div style="margin-bottom: 10px; display: flex; justify-content: flex-end;">
            <el-input clearable v-model="friendSearch" placeholder="用户名/邮箱/姓名" @change="friendSearchStart" />
            <el-dropdown>
                <span class="el-dropdown-link">
                    <el-icon size="22" style="cursor: pointer;">
                        <Setting />
                    </el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="addGroupClick">添加分组</el-dropdown-item>
                        <el-dropdown-item @Click="ShowFriendConfig">添加好友</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
        <div v-if="friendSearchDate != null">
            <el-popover :width="400" placement="left" trigger="hover" v-for="item in friendSearchDate" :key="item.id">
                <template #reference>
                    <div style="display: flex;align-items: center;height: 40px;">
                        <el-image v-if="item.photo == null || item.photo == ''" :fit="contain"
                            :src="'api/static/default.jpg'" class="avatar" />
                        <el-image v-else :fit="contain" :src="'api/' + item.photo" class="avatar" />
                        {{ item.username }} <el-text type="info">{{ "\u3000" }}{{ item.gName }}</el-text>
                    </div>
                </template>
                <el-descriptions title="好友信息" direction="vertical" :column="4" border>
                    <el-descriptions-item label="用户名">{{ item.username }}</el-descriptions-item>
                    <el-descriptions-item label="邮箱">{{ item.email }}</el-descriptions-item>
                    <el-descriptions-item v-if="item.sex == 1" label="性别" :span="2">男</el-descriptions-item>
                    <el-descriptions-item v-else-if="item.sex == 2" label="性别" :span="2">女</el-descriptions-item>
                    <el-descriptions-item v-else label="性别" :span="2">未知</el-descriptions-item>
                    <el-descriptions-item v-if="item.name != null && item.name != ''" label="姓名">{{
                        item.name }}</el-descriptions-item>
                    <el-descriptions-item v-else label="姓名">未知</el-descriptions-item>
                    <el-descriptions-item v-if="item.phone != null && item.phone != ''" label="手机号">{{
                        item.phone }}</el-descriptions-item>
                    <el-descriptions-item v-else label="手机号">未知</el-descriptions-item>
                    <el-descriptions-item v-if="item.birthday != null && item.birthday != ''" label="生日" :span="2">{{
                        time_zh(item.birthday) }}</el-descriptions-item>
                    <el-descriptions-item v-else label="生日" :span="2">未知</el-descriptions-item>
                </el-descriptions>
            </el-popover>
        </div>
        <el-tree v-if="friendSearchDate == null" accordion :data="treeContent" :expand-on-click-node="false"
            :highligh-current="true" @node-click="handleNodeClick" @check-change="checkChange">
            <!--作用域插槽，设置列表图标显示和文本-->
            <template #default="{ data }">
                <div style="width: 100%; display: flex; justify-content: space-between; align-items: center;">
                    <div v-if="data.level == 1">
                        {{ data.name }}
                    </div>
                    <el-popover :width="400" placement="left" trigger="hover" v-if="data.level == 2">
                        <template #reference>
                            <div style="display: flex;align-items: center;height: 40px;">
                                <el-image v-if="data.info.photo == null || data.info.photo == ''" :fit="contain"
                                    :src="'api/static/default.jpg'" class="avatar" />
                                <el-image v-else :fit="contain" :src="'api/' + data.info.photo" class="avatar" />
                                {{ data.info.username }}
                            </div>
                        </template>
                        <el-descriptions title="好友信息" direction="vertical" :column="4" border>
                            <el-descriptions-item label="用户名">{{ data.info.username }}</el-descriptions-item>
                            <el-descriptions-item label="邮箱">{{ data.info.email }}</el-descriptions-item>
                            <el-descriptions-item v-if="data.info.sex == 1" label="性别" :span="2">男</el-descriptions-item>
                            <el-descriptions-item v-else-if="data.info.sex == 2" label="性别"
                                :span="2">女</el-descriptions-item>
                            <el-descriptions-item v-else label="性别" :span="2">未知</el-descriptions-item>
                            <el-descriptions-item v-if="data.info.name != null && data.info.name != ''" label="姓名">{{
                                data.info.name }}</el-descriptions-item>
                            <el-descriptions-item v-else label="姓名">未知</el-descriptions-item>
                            <el-descriptions-item v-if="data.info.phone != null && data.info.phone != ''" label="手机号">{{
                                data.info.phone }}</el-descriptions-item>
                            <el-descriptions-item v-else label="手机号">未知</el-descriptions-item>
                            <el-descriptions-item v-if="data.info.birthday != null && data.info.birthday != ''" label="生日"
                                :span="2">{{ time_zh(data.info.birthday) }}</el-descriptions-item>
                            <el-descriptions-item v-else label="生日" :span="2">未知</el-descriptions-item>
                        </el-descriptions>
                    </el-popover>
                    <div>
                        <el-icon @click="editGroupOrFriend($event, data)" size="18"
                            style="cursor: pointer;margin-right: 5px;">
                            <EditPen />
                        </el-icon>
                        <el-icon @click="removeGroupOrFriend($event, data)" size="18" style="cursor: pointer;">
                            <Remove />
                        </el-icon>
                    </div>
                </div>
            </template>
        </el-tree>
    </el-drawer>

    <!--添加好友弹窗-->
    <el-dialog :append-to-body="true" :title="config.title" v-model="config.visible" @close="matFriendClose()">
        <el-input clearable v-model="config.addSearchStr" placeholder="用户名/邮箱" @change="addSearchStart">
            <template #prefix>
                <el-icon class="el-input__icon">
                    <Search />
                </el-icon>
            </template>
        </el-input>
        <el-popover :width="300" placement="bottom" trigger="click" v-for="item in config.addSearchDate" :key="item.id">
            <template #reference>
                <div style="display: flex;align-items: center;height: 40px;">
                    <el-image v-if="item.photo == null || item.photo == ''" :fit="contain" :src="'api/static/default.jpg'"
                        class="avatar" />
                    <el-image v-else :fit="contain" :src="'api/' + item.photo" class="avatar" />
                    {{ item.username }}
                    <el-text v-if="item.status == 'rejected'" type="info">{{ "\u3000已拒绝" }}</el-text>
                    <el-text v-if="item.status == 'success'" type="info">{{ "\u3000已添加" }}</el-text>
                    <el-text v-if="item.status == 'wait'" type="info">{{ "\u3000等待验证" }}</el-text>
                    <el-text v-if="item.status == 'self'" type="info">{{ "\u3000本人" }}</el-text>
                    {{ "\u3000\u3000" }}
                    <el-button v-if="item.status == 'rejected' || item.status == 'noLink'" round
                        @click="sendRequest($event, item.id)">添加好友</el-button>
                </div>
            </template>
            <el-descriptions title="好友信息" direction="vertical" :column="3" border>
                <el-descriptions-item label="用户名">{{ item.username }}</el-descriptions-item>
                <el-descriptions-item v-if="item.sex == 1" label="性别">男</el-descriptions-item>
                <el-descriptions-item v-else-if="item.sex == 2" label="性别">女</el-descriptions-item>
                <el-descriptions-item v-else label="性别">未知</el-descriptions-item>
                <el-descriptions-item v-if="item.birthday != null && item.birthday != ''" label="生日">{{
                    time_zh(item.birthday) }}</el-descriptions-item>
                <el-descriptions-item v-else label="生日">未知</el-descriptions-item>
            </el-descriptions>
        </el-popover>
        <el-collapse style="margin-top: 10px;" v-model="activeNames" @change="handleChange">
            <el-collapse-item style="--el-collapse-border-color:transparent" title="好友申请列表" name="1">
                <div v-for="item in config.friendRequestDate" :key="item.id">
                    <el-row>
                        <el-col :span="16">
                            <el-popover :width="300" placement="bottom" trigger="click"
                                v-for="item in config.friendRequestDate" :key="item.id">
                                <template #reference>
                                    <div style="display: flex;align-items: center;height: 40px;">
                                        <el-image v-if="item.photo == null || item.photo == ''" :fit="contain"
                                            :src="'api/static/default.jpg'" class="avatar" />
                                        <el-image v-else :fit="contain" :src="'api/' + item.photo" class="avatar" />
                                        {{ item.username }}
                                    </div>
                                </template>
                                <el-descriptions title="好友信息" direction="vertical" :column="3" border>
                                    <el-descriptions-item label="用户名">{{ item.username }}</el-descriptions-item>
                                    <el-descriptions-item v-if="item.sex == 1" label="性别">男</el-descriptions-item>
                                    <el-descriptions-item v-else-if="item.sex == 2" label="性别">女</el-descriptions-item>
                                    <el-descriptions-item v-else label="性别">未知</el-descriptions-item>
                                    <el-descriptions-item v-if="item.birthday != null && item.birthday != ''" label="生日">{{
                                        time_zh(item.birthday) }}</el-descriptions-item>
                                    <el-descriptions-item v-else label="生日">未知</el-descriptions-item>
                                </el-descriptions>
                            </el-popover>
                        </el-col>
                        <el-col :span="8">
                            <el-button type="primary" @click="consentFriend(item.mid, item.id)">同意</el-button>
                            <el-button @click="rejectFriendBtn(item.mid)">拒绝</el-button>
                        </el-col>
                    </el-row>
                </div>
            </el-collapse-item>
            <el-collapse-item style="--el-collapse-border-color:transparent" title="已发送的好友请求" name="2">
                <div v-for="item in config.sentFriendDate" :key="item.id">
                    <el-row>
                        <el-col :span="16">{{ item.username }}</el-col>
                        <el-col :span="8" style="display: flex;align-items: center;">
                            <el-text v-if="item.status == 'rejected'" type="info">{{ "\u3000已拒绝" }}</el-text>
                            <el-icon @click="deleteFriend(item.mid)" size="16px" v-if="item.status == 'rejected'"
                                style="margin-left: 10px; cursor: pointer;">
                                <CloseBold />
                            </el-icon>
                            <el-text v-if="item.status == 'wait'" type="info">{{ "\u3000等待验证" }}</el-text>
                        </el-col>
                    </el-row>
                </div>
            </el-collapse-item>
        </el-collapse>
    </el-dialog>
    <el-dialog :append-to-body="true" :title="groupConfig.title" v-model="groupConfig.visible" @close="groupConfigClose()">
        <el-select style="margin-bottom: 10px;" v-model="groupConfig.selectValue" placeholder="请选择分组">
            <el-option v-for="item in treeContent" :key="item.id" :value="item.name" />
        </el-select>
        <el-button  type="primary" @click="groupConfigSub">确定</el-button>
        <el-button @click="groupConfigClose">取消</el-button>
    </el-dialog>
</template>
<script>
import { deleteGroup, updateGroup, updateFriendGroup, verifiedFriendRequest, deleteFriend, rejectFriendRequest, getFriendRequest, getSentFriendRequest, getGroup, addGroup, findFriend, findUserByUsernameOrEmail, sendFriendRequest } from '@/request/api'
import { ElMessage,ElMessageBox  } from "element-plus";
import { CloseBold, Folder, User, CollectionTag, Setting, Remove, EditPen, Search } from '@element-plus/icons-vue'
export default {
    data() {
        return {
            uid: 0,
            friendSearch: "",
            friendSearchDate: null,
            drawer: false,
            activeIndex: '/main',
            treeContent: [],
            config: {
                title: "添加好友",
                visible: false,
                addSearchStr: "",
                addSearchDate: null,
                sentFriendDate: null,
                friendRequestDate: null,
            },
            groupConfig: {
                title: "设置分组",
                visible: false,
                selectValue: "",
                uid: 0,
                mid: 0,
                type: '',
            }
        }
    },
    components: {
        Folder,
        User,
        CollectionTag,
        Setting,
        Remove,
        EditPen,
        Search,
        CloseBold
    },
    methods: {
        matFriendClose() {
            this.config.addSearchStr = '';
            this.config.addSearchDate = null;
            this.config.visible = false;
        },
        groupConfigClose() {
            this.groupConfig.type = "";
            this.groupConfig.selectValue = "";
            this.groupConfig.uid = 0;
            this.groupConfig.mid = 0;
            this.groupConfig.visible = false;
        },
        ShowFriendConfig() {
            this.config.visible = true;
            this.getFriendRequestDate();
            this.getSentFriendDate();
        },
        async getSentFriendDate() {
            await getSentFriendRequest(this.uid).then(res => {
                if (res.flag == "success") {
                    this.config.sentFriendDate = res.data;
                    if (res.data == undefined || res.data == null || Object.keys(res.data).length == 0) {
                        this.config.sentFriendDate = null;
                    }
                }
            });
        },
        async getFriendRequestDate() {
            await getFriendRequest(this.uid).then(res => {
                if (res.flag == "success") {
                    console.log("fq", res.data);
                    this.config.friendRequestDate = res.data;
                    if (res.data == undefined || res.data == null || Object.keys(res.data).length == 0) {
                        this.config.friendRequestDate = null;
                    }
                }
            });
        },
        async deleteFriend(mid) {
            await deleteFriend({
                mid: mid
            }).then(res => {
                if (res.flag == "success") {
                    this.getSentFriendDate();
                    this.getFriendRequestDate();
                    ElMessage({
                        type: 'success',
                        message: "删除成功",
                    })
                }
            })
        },
        sendRequest(event, id) {
            event.stopPropagation();
            this.groupConfig.uid = id;
            this.groupConfig.type = "send";
            this.groupConfig.visible = true;
        },
        consentFriend(mid, uid) {
            this.groupConfig.mid = mid;
            this.groupConfig.uid = uid;
            this.groupConfig.type = "consent"
            this.groupConfig.visible = true;
        },
        async editGroupOrFriend(event, data) {
            event.stopPropagation();
            if (data.level == 1) {
                this.$prompt("请输入组名", "操作验证", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    inputPattern: /\S/,
                    inputErrorMessage: "组名不能为空",
                }).then(({ value }) => {
                    updateGroup({
                        id: data.id,
                        name: value,
                        uid: data.uid
                    }).then(res => {
                        if (res.flag == "success") {
                            this.showAllGroup();
                            ElMessage({
                                type: 'success',
                                message: res.data,
                            })
                            this.groupConfigClose();
                        } else {
                            ElMessage.error(res.data);
                        }
                    })
                }).catch(() => {
                    ElMessage({
                        type: 'info',
                        message: "取消输入",
                    })
                })
            } else if (data.level == 2) {
                this.groupConfig.mid = data.mid;
                this.groupConfig.type = "update"
                this.groupConfig.visible = true;
                console.log(data);
            }
        },
        async removeGroupOrFriend(event, data) {
            event.stopPropagation();
            if (data.level == 1) {
                ElMessageBox.confirm("此操作将删除该组下所有好友，确定删除吗？", "提示", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning"
                }).then(() => {
                    deleteGroup({
                        id: data.id,
                        name: data.name,
                        uid: data.uid
                    }).then(res => {
                        if (res.flag == "success") {
                            this.showAllGroup();
                            ElMessage({
                                type: 'success',
                                message: res.data,
                            })
                            this.groupConfigClose();
                        } else {
                            ElMessage.error(res.data);
                        }
                    })
                }).catch(() => {
                    ElMessage({
                        type: 'info',
                        message: "取消输入",
                    })
                })
            }else if (data.level == 2){
                await this.deleteFriend(data.mid);
            }
            console.log(data);
        },
        async groupConfigSub() {
            if (this.groupConfig.selectValue == null || this.groupConfig.selectValue == '') {
                alert("请选择分组");
                return;
            }
            if (this.groupConfig.type == "send") {
                await sendFriendRequest({
                    uid: this.groupConfig.uid,
                    gid: this.treeContent.find(item => item.name == this.groupConfig.selectValue).id
                }).then(res => {
                    if (res.flag == "success") {
                        this.getSentFriendDate();
                        this.getFriendRequestDate();
                        this.addSearchStart();
                        this.showAllGroup();
                        ElMessage({
                            type: 'success',
                            message: res.data,
                        })
                        this.groupConfigClose();
                    } else {
                        ElMessage.error(res.data);
                    }
                })
            } else if (this.groupConfig.type == "consent") {
                await verifiedFriendRequest({
                    mid: this.groupConfig.mid,
                    uid: this.groupConfig.uid,
                    gid: this.treeContent.find(item => item.name == this.groupConfig.selectValue).id
                }).then(res => {
                    if (res.flag == "success") {
                        this.getSentFriendDate();
                        this.getFriendRequestDate();
                        this.addSearchStart();
                        this.showAllGroup();
                        ElMessage({
                            type: 'success',
                            message: res.data,
                        })
                        this.groupConfigClose();
                    } else {
                        ElMessage.error(res.data);
                    }
                })
            } else if (this.groupConfig.type == "update") {
                await updateFriendGroup({
                    mid: this.groupConfig.mid,
                    gid: this.treeContent.find(item => item.name == this.groupConfig.selectValue).id
                }).then(res => {
                    if (res.flag == "success") {
                        this.showAllGroup();
                        ElMessage({
                            type: 'success',
                            message: "修改成功",
                        })
                        this.groupConfigClose();
                    } else {
                        ElMessage.error(res.data);
                    }
                })
            }
        },
        async rejectFriendBtn(mid) {
            await rejectFriendRequest({
                mid: mid
            }).then(res => {
                if (res.flag == "success") {
                    this.getFriendRequestDate();
                    ElMessage({
                        type: 'success',
                        message: "拒绝成功",
                    })
                } else {
                    ElMessage.error(res.data);
                }
            })
        },
        time_zh(time) {
            if (time == null || time == "") {
                return "";
            }
            let a = new Date(time).getTime();
            const date = new Date(a);
            const Y = date.getFullYear() + '-';
            const M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
            const D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate());
            const dateString = Y + M + D;
            return dateString;
        },
        async addSearchStart() {
            if (this.config.addSearchStr == null || this.config.addSearchStr == "") {
                this.config.addSearchDate = null;
                return;
            }
            await findUserByUsernameOrEmail({
                uid: this.uid,
                str: this.config.addSearchStr
            }).then(res => {
                if (res.flag == "success") {
                    this.config.addSearchDate = res.data;
                    console.log(res.data);
                    if (res.data == undefined || res.data == null || Object.keys(res.data).length == 0) {
                        ElMessage({
                            type: 'warning',
                            message: "请输入完整、正确的用户信息进行查找",
                        })
                    }
                }
            })
        },
        async friendSearchStart() {
            if (this.friendSearch == null || this.friendSearch == "") {
                this.friendSearchDate = null;
                this.showAllGroup();
                return;
            }
            await findFriend({
                uid: this.uid,
                str: this.friendSearch
            }).then(res => {
                if (res.flag == "success") {
                    this.friendSearchDate = res.data;
                }
            })
        },
        handleOpen(key, keyPath) {
            console.log(key, keyPath)
        },
        handleClose(key, keyPath) {
            console.log(key, keyPath)
        },
        //菜单选择事件
        selectMenu(index, path) {
            console.log(path)
            if (index === 'userManageAside-2') return
            this.activeIndex = index;
            //this.$router.push(index);
        },
        //好友列表节点点击事件
        handleNodeClick(data) {
            if (data.level === 2) {
                this.drawer = false;
                this.activeIndex = "userManageAside-2";
                this.$store.commit('setFriendMid',data.mid);
                this.$router.push('/friendFile');
            }
        },
        //好友列表抽屉显示
        showDrawer() {
            this.drawer = true;
        },
        closeDrawer() {
            this.drawer = false;
        },
        addGroupClick() {
            this.$prompt("请输入组名", "操作验证", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                inputPattern: /\S/,
                inputErrorMessage: "组名不能为空",
            }).then(({ value }) => {
                addGroup({
                    name: value,
                    uid: this.uid,
                }).then(res => {
                    if (res.flag == "success") {
                        this.showAllGroup();
                        ElMessage({
                            type: 'success',
                            message: "添加成功",
                        })
                    }
                })
            }).catch(() => {
                ElMessage({
                    type: 'info',
                    message: "取消输入",
                })
            })
        },
        async showAllGroup() {
            var user = null;
            if (typeof (this.$store.getters.getUser) != 'object')
                user = JSON.parse(this.$store.getters.getUser);
            else
                user = this.$store.getters.getUser;
            await getGroup(user.uid).then(res => {
                if (res.flag == "success") {
                    this.treeContent = res.data;
                }
            })
        }
    },
    created() {
    },
    mounted() {
        var user = null;
        if (typeof (this.$store.getters.getUser) != 'object')
            user = JSON.parse(this.$store.getters.getUser);
        else
            user = this.$store.getters.getUser;
        this.uid = user.uid;
        this.showAllGroup();
    },
    watch: {
        '$route': {
            handler(newVal) {
                if (newVal.href === '/friendFile') {
                    this.activeIndex = "userManageAside-2";
                } else {
                    this.activeIndex = newVal.href;
                }
            },
            immediate: true,
        }
    },
    // beforeUpdate() {
    //     let index = localStorage.getItem('index');
    //     if (index) {
    //         this.activeIndex = index;
    //     } else {
    //         this.activeIndex = this.$route.path
    //     }
    // },
}

</script>
<style scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 400px;
}
.el-menu-vertical-demo{
    height: 100%;
    padding-top: 20px;
    background-color: rgba(255,255,255,0.65);
    background-image: url("../assets/img/retouch_2024042519304465.png") ;
    background-repeat: no-repeat;
    background-position: right bottom;
    background-size: contain;
}
.el-menu-vertical-demo .el-menu-item{
    font-size: 17px;
}

.avatar {
    width: 35px;
    height: 35px;
    border-radius: 50%;
}

.el-tree {
    --el-tree-node-content-height: 40px;
    font-size: 15px;
}
/* style="background-image: url(../assets/img/retouch_2024042518290713.png);" */
.el-drawer,.el-drawer__body,.el-drawer__body{
    background-image: url(../assets/img/retouch_2024042518290713.png) !important;
    background-repeat: no-repeat;
    background-position: right bottom;
    background-size: contain;
}
.el-drawer__wrapper{
    z-index: -3000 !important;
}
/* .el-menu-item.is-active{
    background-color:  #a0cfff !important;
} */
</style>