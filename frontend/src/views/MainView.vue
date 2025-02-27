<template>
    <el-container class="firstContainer">
        <el-header height="100px" style="padding: 0px;">
            <HeaderView />
        </el-header>
        <el-container height="100%">
            <el-aside width="200px" height="100%"
                style="margin: 3px 10px;box-shadow: 2px 2px 5px 2px #79917e;border-radius: 10px;">
                <LeftView />
            </el-aside>
            <el-main height="100%" style="
                    margin: 3px 30px;
                    box-shadow: 2px 2px 5px 2px #79917e;border-radius: 10px;
                    background-color: rgba(255,255,255,0.65);">
                <router-view />
            </el-main>
        </el-container>
    </el-container>
</template>
<script>
import { download } from '@/request/api';
import HeaderView from '@/components/HeaderView.vue';
import LeftView from '@/components/LeftView.vue';
export default {
    data() {
        return {
            d: '',
        }
    },
    components: {
        HeaderView,
        LeftView,
    },
    methods: {
        //下载
        async onSubmit() {
            //var vm = this;
            await download(this.d).then(res => {
                if (res.flag == 'success') {
                    const content = res.data;
                    const blob = new Blob([content], {
                        type: 'application/octet-strem'
                    });
                    const name = res.fileName;
                    if ('download' in document.createElement('a')) {
                        const link = document.createElement('a');
                        link.download = name;
                        link.style.display = 'none';
                        link.href = URL.createObjectURL(blob);
                        console.log('ee', link.href);
                        document.body.appendChild(link);
                        link.click();
                        URL.revokeObjectURL(link.href); //释放URL对象
                        document.body.removeChild(link);
                    }
                }
            })
        },
    }
}
</script>
<style>
.firstContainer {
    position: absolute;
    top: 0px;
    left: 0px;
    width: 100%;
    height: 100%;
    background-image: url("../assets/img/retouch_2024042521555293.png") ;
    background-repeat: no-repeat;
    background-position: right bottom;
    background-size: contain;
}
</style>