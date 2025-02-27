<template>
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
            <el-icon @click="downFile($event, data)" v-if="data.isFile === 1" style="margin-left: 20px; cursor: pointer;"
                size="22px">
                <Download />
            </el-icon>
        </template>
    </el-tree>
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
import { getBeSharedFile, download } from '@/request/api';
import {Folder,FolderOpened,Document,Download} from '@element-plus/icons-vue'
export default {
    data() {
        return {
            mid: null,
            treeContent: [],
            //双击事件
            nodeCount: 0,
            curNode: null,
            nodeTimer: null,
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
    components:{
        Download,Document,FolderOpened,Folder
    },
    methods: {
        async showFiles() {
            if (this.mid == null || this.mid == undefined) {
                alert("请重新通过好友列表选择好友点击进入");
                this.$router.push('/mydocument');
                return
            }
            await getBeSharedFile(this.mid).then(res => {
                if (res.flag == "success") {
                    console.log(res.data);
                    this.treeContent = res.data;
                } else {
                    alert(res.data);
                }
            })
        },
        matClose() {
            this.docFile = false;
            this.videoUrl = null;
            this.imgUrl = null;
            this.textUrl = null;
            this.matPrintDialog.visible = false;
        },
        //文件列表的节点点击事件
        async handleNodeClick(data, node) {
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
    },
    mounted() {
        this.mid = this.$store.state.friendMid;
        this.showFiles();
    }
}
</script>
<style></style>