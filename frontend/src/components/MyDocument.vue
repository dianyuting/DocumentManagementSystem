<template >
    <!-- 鼠标右键文件列表的显示框 -->
    <div v-show="menuShow" :style="rightMenu">
        <el-menu :default-active="activeIndex" class="el-menu-vertical-demo" @select="selectMenuNode">
            <el-menu-item id="addDir" index="addDir">
                <template #title>新增文件夹</template>
            </el-menu-item>
            <el-upload class="avatar-uploader" ref="uploadF" action="#1" :http-request="uploadFile" :auto-upload="true"
                :on-change="fileChange" :show-file-list="false" :on-success="handleAvatarSuccess">
                <el-menu-item id="addFile" index="addFile">
                    <template #title>上传文件</template>
                </el-menu-item>
            </el-upload>
            <el-menu-item id="addTag" index="addTag">
                <template #title>标签管理</template>
            </el-menu-item>
            <el-menu-item id="addShare" index="addShare">
                <template #title>分享管理</template>
            </el-menu-item>
            <el-menu-item id="dele" index="dele">
                <template #title>删除</template>
            </el-menu-item>
        </el-menu>
    </div>
    <!--添加标签弹窗-->
    <el-dialog :append-to-body="true" :title="config.title" v-model="config.visible" @close="matTagClose()">
        <el-row>
            <el-col :span="12">
                <el-select v-model="config.selectTag" filterable placeholder="Select" style="width: 240px">
                    <el-option v-for="item in config.options" :key="item.id" :label="item.name" :value="item.name" />
                </el-select>
            </el-col>
            <el-col :span="12">
                <el-button plain @click="addTaggedFile">添加</el-button>
            </el-col>
        </el-row>
        <el-row style="margin-top: 20px;">
            <el-tag style="margin-right: 10px;" v-for="tag in optionData.tags" :key="tag.id" closable :type="tag.type"
                @close="deleteTag(tag)">
                {{ tag.name }}
            </el-tag>
        </el-row>
    </el-dialog>

    <!--分享文件弹窗-->
    <el-dialog :append-to-body="true" :title="shareConfig.title" v-model="shareConfig.visible" @close="shareConfigClose()">
        <el-select v-model="shareConfig.valueStrictly" value-key="id" placeholder="请选择要分享的好友" multiple
            @change="shareTreeNodeClick">
            <el-tree v-model="shareConfig.valueStrictly" node-key="id" :data="shareConfig.friends">
                <template #default="{ data }">
                    <el-option disabled :key="data.id * 10 + data.level" :label="data.name"
                        :value="data.level + ':' + data.id" v-if="data.level == 1">{{
                            data.name }}</el-option>
                    <el-option :key="data.info.id * 10 + data.level" :label="data.info.username"
                        :value="data.level + ':' + data.mid" v-else-if="data.level == 2">{{ data.info.username
                        }}</el-option>
                </template>
            </el-tree>
        </el-select>
        <el-date-picker style="margin-top: 10px;" v-model="shareConfig.shareDate" type="daterange" range-separator="至"
            start-placeholder="结束时间" end-placeholder="开始时间" />
        <el-collapse style="margin-top: 20px;">
            <el-collapse-item title="已分享的好友" name="1">
                <el-table :data="shareConfig.sharedFeiends" style="width: 100%">
                    <el-table-column prop="userInfo.username" label="用户名" />
                    <el-table-column prop="startDate" :formatter="time_zh" label="开始时间" />
                    <el-table-column prop="endDate" :formatter="time_zh" label="结束时间" />
                    <el-table-column prop="status" label="状态" />
                    <el-table-column :align="right" width="180px">
                        <template #header>
                            操作
                        </template>
                        <template #default="scope">
                            <el-button @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                            <el-button type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-collapse-item>
        </el-collapse>
        <div style="margin-top: 10px;">
            <el-button type="primary" @click="shareSubmit">确定</el-button>
            <el-button @click="shareConfigClose">取消</el-button>
        </div>
    </el-dialog>
    <el-dialog :append-to-body="true" :title="setShareDateConfig.title" v-model="setShareDateConfig.visible"
        @close="setShareDateConfigClose()">
        <el-date-picker style="margin-top: 10px;" v-model="setShareDateConfig.shareDate" type="daterange"
            range-separator="至" start-placeholder="结束时间" end-placeholder="开始时间" @change="setShareDateChange" />
    </el-dialog>
    <!-- 上方查找、刷新区域 -->
    <el-row style="margin-bottom: 10px;">
        <el-col :offset="13" :span="2">
            <el-select class="noRadiusRight" v-model="findTagType" clearable placeholder="标签类型" @change="findTagTypeChange">
                <el-option v-for="item in tagType" :key="item.key" :value="item.value" />
            </el-select>
        </el-col>
        <el-col :span="2">
            <el-select class="noRadiusRight noRadiusLeft" v-model="findTag" clearable filterable placeholder="标签名"
                @change="findTagChange">
                <el-option v-for="item in findTags" :key="item.id" :label="item.name" :value="item.name" />
            </el-select>
        </el-col>
        <el-col :span="5">
            <el-input class="noRadiusLeft" v-model="findFileName" placeholder="文件名查找" clearable />
        </el-col>
        <el-col :span="1" style="display: flex; justify-content: center;align-items: center;">
            <el-icon size="22px" @click="findStart" style="cursor: pointer;">
                <Search />
            </el-icon>
        </el-col>
        <el-col :span="1" style="display: flex; justify-content: center;align-items: center;">
            <el-icon size="22px" @click="show" style="cursor: pointer;">
                <Refresh />
            </el-icon>
        </el-col>
    </el-row>
    <!-- 头部在根目录创建文件夹的按钮 -->
    <div v-if="!isFind">
        <el-button circle size="large" @click="addFolder">
            <el-icon size="22px">
                <FolderAdd />
            </el-icon>
        </el-button>
        <el-button circle size="large">
            <el-upload class="avatar-uploader" ref="uploadF" action="#2" :http-request="uploadFile" :auto-upload="true"
                :on-change="fileChange" :show-file-list="false" :on-success="handleAvatarSuccess">
                <el-icon size="22px">
                    <DocumentAdd />
                </el-icon>
            </el-upload>
        </el-button>
    </div>
    <!-- 文件列表 -->
    <el-tree v-if="treeContent" accordion :data="treeContent" :highligh-current="true" @node-click="handleNodeClick"
        @check-change="checkChange" @node-contextmenu="rightClick"
        style="background-color: transparent;">
        <!--作用域插槽，设置文件列表图标显示和文本-->
        <template #default="{ data, node }">
            <el-icon v-if="data.isFile === 1" size="22px">
                <Document />
            </el-icon>
            <el-icon v-else-if="node.expanded" size="22px">
                <FolderOpened />
            </el-icon>
            <el-icon v-else size="22px">
                <Folder />
            </el-icon>
            <span>{{ data.name }}</span>
            <el-tag v-for="item in data.tags" :key="item.id" style="margin-left: 20px;" :type="item.type">{{
                item.name }}</el-tag>
            <el-icon @click="downFile($event, data)" v-if="data.isFile === 1" style="margin-left: 20px; cursor: pointer;"
                size="22px">
                <Download />
            </el-icon>
        </template>
    </el-tree>
    <!-- 图片预览弹窗 ,弹窗:append-to-body设为true加入到界面中，:visible改为v-model，前面的是以前的用法-->
    <el-dialog :append-to-body="true" :title="matPrintDialog.title" v-model="matPrintDialog.visible" :fullscreen="true"
        @onClose="matClose()" @onConfirm="matConfirm()">
        <div style="padding: 50px;">
            <el-image :src="imgUrl" v-if="imgUrl"
                style="position:absolute;top:0;bottom:0;left:0;right:0;width:100%;height:85%;margin:auto;"
                fit="contain"></el-image>
            <iframe v-if="textUrl" :src="textUrl" frameborder="0"
                style="width: 100%;height: 100%;min-height: 500px;"></iframe>
            <video v-if="videoUrl" :src="videoUrl" controls style="width: 100%;height: 100%;max-height: 800px;"></video>
            <div v-if="docFile">
                <div id="docfileshow" ref="docFileShow"></div>
            </div>
        </div>
    </el-dialog>
</template>
<script>
import { Download, Folder, Document, FolderOpened, FolderAdd, DocumentAdd, Search, Refresh } from '@element-plus/icons-vue'
import { deleteShareFile, updateShareFile, shareFile, getShareFile, getGroup, createFiler, showAll, upload, download, deletFile, getTagByUid, fileAddTag, fileDeleteTag, findFile } from '@/request/api';
//import pdfLib from 'pdfjs-dist/build/pdf'
//vue3不支持vue-pdf
//import pdf from 'vue-pdf';
//import CMapReaderFactory from 'vue-pdf-signature/src/CMapReaderFactory';
export default {
    data() {
        return {
            isFind: false,
            findTagType: '',
            findTag: '',
            findFileName: '',
            findTags: null,
            config: {
                title: "标签管理",
                visible: false,
                selectTag: null,
                options: [],
            },
            shareConfig: {
                title: "文件分享",
                visible: false,
                friends: null,
                valueStrictly: [],
                shareDate: [],
                sharedFeiends: [],
            },
            setShareDateConfig: {
                title: "选择分享时间区间",
                visible: false,
                shareId: 0,
                shareDate: [],
            },
            tagType: [{ value: "primary", key: 1 },
            { value: "success", key: 2 },
            { value: "info", key: 2 },
            { value: "warning", key: 2 },
            { value: "danger", key: 2 },],
            //鼠标右键框
            activeIndex: null,
            menuShow: false,
            rightMenu: null,
            //右键选中的节点
            optionData: null,
            node: null,
            tree: null,
            //双击事件
            nodeCount: 0,
            curNode: null,
            nodeTimer: null,
            //树节点数据
            treeContent: [],
            matPrintDialog: {
                title: "预览",
                visible: false,
            },
            imgUrl: null,
            textUrl: null,
            videoUrl: null,
            docFile: false,
            imgType: ['bmp', 'jpg', 'jpeg', 'png', 'tif', 'gif', 'pcx', 'tga', 'exif', 'fpx', 'svg', 'psd', 'cdr', 'pcd', 'dxf', 'ufo', 'eps', 'ai', 'raw', 'WMF', 'webp', 'avif', 'apng'],
            videoType: ['wmv', 'asf', 'asx', 'rm', 'rmvb', 'mp4', '3gp', 'mov', 'm4v', 'avi', 'dat', 'mkv', 'flv', 'vob'],
            wordType: ['text', 'pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'rar', 'zip', '7z', 'apz', 'ar', 'bz', 'car', 'dar', 'cpgz', 'f', 'ha', 'hbc', 'hbc2', 'hbe', 'hpk', 'hyp'],
            //pdfUrl: '',
            fileTypeMap: {
                "xls": "application/vnd.ms-excel;charset=utf-8",
                "xlsx": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8",
                "doc": "application/msword;charset=utf-8",
                "docx": "application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=utf-8",
                "pdf": "application/pdf;charset=utf-8",
                "ppt": "application/pdf;charset=utf-8",
                "pptx": "application/vnd.openxmlformats-officedocument.presentationml.presentation;charset=utf-8",
                "png": "image/png",
                "gif": "image/gif",
                "jpeg": "image/jpeg",
                "jpg": "image/jpeg",
                "txt": "text/plain;charset=utf-8",
            },
        }
    },
    components: {
        Folder, Document, FolderOpened, FolderAdd, DocumentAdd, Search, Refresh, Download
        //pdf,
    },
    methods: {
        time_zh(row, column, time) {
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
        async findStart() {
            let isName = this.findFileName == null || this.findFileName == '';
            let isTag = this.findTag == null || this.findTag == '';
            let isType = this.findTagType == null || this.findTagType == '';
            if (isName && isTag && isType) {
                this.show();
                return
            }
            var user = null;
            if (typeof (this.$store.getters.getUser) != 'object')
                user = JSON.parse(this.$store.getters.getUser);
            else
                user = this.$store.getters.getUser;
            await findFile({
                uid: user.uid,
                name: this.findFileName,
                tagName: this.findTag,
                type: this.findTagType
            }).then(res => {
                if (res.flag == "success") {
                    this.treeContent = res.data
                    this.isFind = true;
                }
            })
        },
        findTagChange() {
            if (this.findTag == null || this.findTag == '') {
                this.findTagType = '';
                return
            }
            this.findTagType = this.findTags.find(item => item.name == this.findTag).type;
        },
        findTagTypeChange() {
            if (this.findTagType == null || this.findTagType == '') {
                this.showTagsByUid();
                this.findTag = '';
            }
            this.findTags = this.findTags.filter(item => item.type == this.findTagType)
        },
        //文件分享弹窗关闭事件
        shareConfigClose() {
            this.shareConfig.friends = null;
            this.shareConfig.valueStrictly = [];
            this.shareConfig.shareDate = [];
            this.shareConfig.visible = false;
        },
        setShareDateConfigClose() {
            this.setShareDateConfig.shareId = 0;
            this.setShareDateConfig.shareDate = [];
            this.setShareDateConfig.visible = false;
        },
        //分享好友节点点击
        shareTreeNodeClick(data) {
            // console.log("data", data);
            // this.shareConfig.valueStrictly = this.shareConfig.valueStrictly.filter((item,index,arr)=>{
            //     return arr.indexOf(item, 0) === index;
            // })
            console.log("data", data);
        },
        //分享提交
        shareSubmit() {
            console.log(this.shareConfig.valueStrictly);
            console.log(this.shareConfig.shareDate);
            if (this.shareConfig.valueStrictly == undefined || this.shareConfig.valueStrictly == null || Object.keys(this.shareConfig.valueStrictly).length == 0) {
                alert("请选择分享对象");
                return
            }
            if (this.shareConfig.shareDate == undefined || this.shareConfig.shareDate == null || Object.keys(this.shareConfig.shareDate).length == 0) {
                alert("请选择分享日期");
                return
            }
            this.shareConfig.valueStrictly.forEach(element => {
                shareFile({
                    fileId: this.optionData.id,
                    shareMid: element.split(":")[1],
                    startTime: this.shareConfig.shareDate[0],
                    endTiem: this.shareConfig.shareDate[1],
                }).then(res => {
                    if (res.flag == "success") {
                        this.showSharedFeiends();
                    } else {
                        alert(res.data);
                    }
                })
            });
        },
        //显示文件已分享的所有好友
        async showSharedFeiends() {
            await getShareFile(this.optionData.id).then(res => {
                if (res.flag == "success") {
                    this.shareConfig.sharedFeiends = res.data;
                    console.log(res.data);
                }
            })
        },
        //编辑分享的好友设置
        handleEdit(index, row) {
            console.log("index:" + index);
            console.log(row);
            this.setShareDateConfig.shareId = row.shareId;
            this.setShareDateConfig.visible = true;
        },
        setShareDateChange() {
            updateShareFile({
                id: this.setShareDateConfig.shareId,
                startTime: this.setShareDateConfig.shareDate[0],
                endTiem: this.setShareDateConfig.shareDate[1],
            }).then(res => {
                if (res.flag == "success") {
                    this.setShareDateConfigClose();
                    this.showSharedFeiends();
                }
            })
        },
        //删除好友分享
        async handleDelete(index, row) {
            console.log("index:" + index);
            console.log(row);
            deleteShareFile({
                id: row.shareId,
            }).then(res => {
                if (res.flag == "success") {
                    this.showSharedFeiends();
                } else {
                    alert(res.data);
                }
            })
        },
        //显示所有好友（根据组显示）
        async showAllGroup() {
            var user = null;
            if (typeof (this.$store.getters.getUser) != 'object')
                user = JSON.parse(this.$store.getters.getUser);
            else
                user = this.$store.getters.getUser;
            await getGroup(user.uid).then(res => {
                if (res.flag == "success") {
                    this.shareConfig.friends = res.data;
                }
            })
        },
        //标签管理关闭事件
        matTagClose() {
            this.config.visible = false;
        },
        //删除标签事件
        async deleteTag(tag) {
            console.log("删除标签", tag);
            await fileDeleteTag({ id: tag.id }).then(res => {
                if (res.flag == "success") {
                    this.optionData.tags.splice(this.optionData.tags.indexOf(tag), 1);
                } else {
                    alert(res.data);
                }
            });
        },
        //添加标签事件
        addTaggedFile() {
            let s = this.config.options.find(item => item.name == this.config.selectTag);
            if (this.optionData) {
                console.log(this.optionData)
                fileAddTag({
                    fileId: this.optionData.id,
                    tagId: s.id
                }).then(res => {
                    if (res.flag == "success") {
                        this.show();
                        this.config.visible = false;
                    } else {
                        alert(res.data);
                    }
                })
            }
        },
        //预览弹窗关闭事件
        matClose() {
            this.docFile = false;
            this.videoUrl = null;
            this.imgUrl = null;
            this.textUrl = null;
            this.matPrintDialog.visible = false;
        },
        // 鼠标右击框里的点击事件
        async selectMenuNode(index) {
            var user = null;
            if (typeof (this.$store.getters.getUser) != 'object')
                user = JSON.parse(this.$store.getters.getUser);
            else
                user = this.$store.getters.getUser;
            if (index === 'addDir') {
                this.addFolder('select');
            } else if (index === 'addFile') {
                console.log('addFile');
                //删除文件
            } else if (index === 'dele') {
                await deletFile({
                    path: this.optionData.parentDirectory,
                    fileName: this.optionData.name,
                    uid: user.uid
                }).then(res => {
                    if (res.flag == "success") {
                        this.$message.success({
                            showClose: true,
                            message: res.data,
                            type: 'success',
                        })
                        this.show();
                    } else {
                        alert(res.data);
                        return;
                    }
                })

            } else if (index == 'addTag') {
                await getTagByUid(user.uid).then(res => {
                    if (res.flag == "success") {
                        this.config.options = res.data;
                        this.config.visible = true;
                    }
                })
            } else if (index === "addShare") {
                this.showAllGroup();
                this.showSharedFeiends();
                this.shareConfig.visible = true;
            }
        },
        //文件列表的鼠标右击事件，显示右击框
        rightClick(e, data, n, t) {
            this.menuShow = false;
            this.rightMenu = {
                'box-shadow': '0 6px 12px rgba(0,0,0,0.175)',
                'z-index': '1000',
                border: '1px solid rgba(0,0,0,0.15)',
                position: 'fixed',
                top: e.pageY + 'px',
                left: e.pageX + 'px'
            };
            this.optionData = data;
            this.node = n;
            this.tree = t;
            e.preventDefault();
            this.menuShow = true;
            document.addEventListener('click', (ev) => {
                if (ev.target != document.querySelector('.el-menu-item.is-actibe'))
                    this.foo();
            })
        },
        //右击框隐藏
        foo() {
            //取消鼠标监听事件
            this.menuShow = false;
            document.removeEventListener('click', this.foo);
        },
        //文件列表的节点点击事件
        async handleNodeClick(data, node) {
            this.foo();
            if (data.isFile == 0) {
                this.nodeCount = 0;
                this.nodeTimer = null;
                this.curNode = null;
                return;
            }
            this.nodeCount++;
            if (this.curNode && this.nodeCount >= 2) {
                this.nodeCount = 0;
                if (this.curNode == node) { //鼠标双击事件
                    const type = data.name.substring(data.name.lastIndexOf('.') + 1);
                    var res = await download({
                        path: data.parentDirectory,
                        name: data.name
                    });
                    this.matPrintDialog.visible = true;
                    let t = this.fileTypeMap[type];
                    if (this.imgType.includes(type)) {
                        let blob = new Blob([res.data], { t });
                        const imageUrl = URL.createObjectURL(blob);
                        this.imgUrl = imageUrl;
                    } else if (this.videoType.includes(type)) {
                        // 视频类型的
                        const blob = new Blob([res.data])
                        this.videoUrl = window.URL.createObjectURL(blob);
                    } else if (type === 'doc' || type === 'docx') {
                        // word类型的用docx-preview插件
                        this.docFile = true
                        let docx = require("docx-preview")
                        this.$nextTick(() => {
                            docx.renderAsync(res.data, document.getElementById('docfileshow')).then(x => console.log("docx: finished", x))
                        })
                    } else if (type == 'pdf') {
                        this.matPrintDialog.visible = false;
                        this.getObjectUrl(res.data);
                    } else if (type === 'txt') {
                        let blob = new Blob([res.data], { t });
                        alert(URL.createObjectURL(blob));
                        this.textUrl = URL.createObjectURL(blob);
                    } else {
                        this.matPrintDialog.visible = false;
                        this.$message.success({
                            showClose: true,
                            message: "该类型文件不支持预览",
                            type: 'error',
                        })
                    }
                    this.curNode = null;
                    return
                }
            }
            this.curNode = node;
            this.nodeTimer = setTimeout(() => {
                this.curNode = null;
                this.nodeCount = 0;
            }, 300)
        },
        //显示pdf
        async getObjectUrl(data) {
            console.log(data);
            let url = null;
            let file = new Blob([data], { type: "application/pdf" });
            if (window.createObjectURL != undefined) {
                url = window.createObjectURL(file);
            } else if (window.webkitURL != undefined) {
                try {
                    url = window.webkitURL.createObjectURL(file);
                } catch (error) { console.log(error) }
            } else if (window.URL != undefined) {
                try {
                    url = window.URL.createObjectURL(file);
                } catch (error) { console.log(error) }
            }
            window.open(url, '_blank');
            // url = pdf.createLoadingTask({
            //     url:url,
            //     //CMapReaderFactory
            // });
            // this.pdfUrl = url;
        },
        //下载文件
        async downFile(event, data) {
            event.stopPropagation();
            var res = await download({
                path: data.parentDirectory,
                name: data.name
            });
            if (res) {
                const content = res.data
                const blob = new Blob([content], {
                    type: 'application/octet-strem'
                });
                // 如果后端返回文件名
                //const name = res.fileName.split(';')[1].split('=')[1];
                if ('download' in document.createElement('a')) {
                    // 非IE下载
                    const link = document.createElement('a');
                    link.download = data.name;
                    link.style.display = 'none';
                    link.href = URL.createObjectURL(blob);
                    console.log('ee', link.href);
                    document.body.appendChild(link);
                    link.click();
                    URL.revokeObjectURL(link.href); // 释放URL 对象
                    document.body.removeChild(link);
                }
            }
        },
        // 添加文件夹
        async addFolder(n) {
            const path = await this.promptInput('请输入文件夹的名称', '输入错误');
            if (!path) return;
            if (path.indexOf('/') !== -1) {
                alert("文件夹名称不可带有'/'字符");
                return
            }
            if (path.indexOf('\\') !== -1) {
                alert("文件夹名称不可带有'\\'字符");
                return
            }
            if (n !== 'select') {
                this.optionData = null;
                this.node = null;
                this.tree = null;
            }
            var user = null;
            if (typeof (this.$store.getters.getUser) != 'object')
                user = JSON.parse(this.$store.getters.getUser);
            else
                user = this.$store.getters.getUser;
            const formData = new FormData();
            formData.append("name", path);
            formData.append("uid", user.uid);
            if (this.optionData != null)
                formData.append("path", this.optionData.parentDirectory + "/" + this.optionData.name);
            else
                formData.append("path", "/" + user.username);
            createFiler(formData).then((res) => {
                if (res.flag == "success") {
                    alert('添加成功');
                    this.show();
                } else if (res.flag == "fail") {
                    alert(res.data);
                }
            })
        },
        // 输入弹窗
        async promptInput(message, validation) {
            try {
                const { value } = await this.$prompt(message, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputErrorMessage: validation,
                });
                return value.trim();
            } catch {
                this.$message({
                    type: 'info',
                    message: '取消输入',
                });
            }
        },
        //获取显示文件列表
        async show() {
            this.isFind = false;
            var user = null;
            if (typeof (this.$store.getters.getUser) != 'object')
                user = JSON.parse(this.$store.getters.getUser);
            else user = this.$store.getters.getUser;
            showAll(user.uid).then((res) => {
                if (res.flag == "success") {
                    console.log(res.data);
                    this.treeContent = res.data
                } else {
                    alert(res.data);
                }
            })
        },
        //上传文件
        uploadFile(param) {
            const formData = new FormData();
            var user = null;
            if (typeof (this.$store.getters.getUser) != 'object')
                user = JSON.parse(this.$store.getters.getUser);
            else
                user = this.$store.getters.getUser;
            if (param.action == "#2") {
                this.optionData = null;
                this.node = null;
                this.tree = null;
                formData.append("path", "/" + user.username);
            } else {
                if (this.optionData.isFile === 1)
                    formData.append('path', this.optionData.parentDirectory);
                else
                    formData.append('path', this.optionData.parentDirectory + "/" + this.optionData.name);
            }
            formData.append("uid", user.uid);
            formData.append("uploadfile1", param.file);
            console.log(formData);
            upload(formData).then((res) => {
                if (res.flag == "success") {
                    param.onSuccess();
                    alert("上传成功");
                    this.show();
                } else {
                    alert(res.data);
                }
            })
        },
        //上传文件的列表清空
        handleAvatarSuccess() {
            this.$refs.uploadF.clearFiles();
        },
        async showTagsByUid() {
            var user = null;
            if (typeof (this.$store.getters.getUser) != 'object')
                user = JSON.parse(this.$store.getters.getUser);
            else
                user = this.$store.getters.getUser;
            await getTagByUid(user.uid).then(res => {
                if (res.flag == "success") {
                    this.findTags = res.data;
                }
            })
        }
    },
    //已挂载状态
    mounted() {
        this.show();
        this.showTagsByUid();
    },
}
</script>
<style scoped>

.noRadiusRight .el-select__wrapper {
    border-top-right-radius: 0;
    border-bottom-right-radius: 0;
}

.noRadiusLeft .el-select__wrapper,
.noRadiusLeft .el-input__wrapper {
    border-top-left-radius: 0;
    border-bottom-left-radius: 0;
}
</style>