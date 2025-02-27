<template>
    <div class="updateBg">
        <div class="updateBox">
            <el-upload class="avatar-uploader" ref="upload" action="#" :http-request="uploadImage" :auto-upload="true"
                :on-change="fileChange" :show-file-list="false" :before-upload="beforeAvatarUpload"
                :on-success="handleAvatarSuccess">
                <el-image :fit="contain" :src="imageUrl" class="avatar" />
            </el-upload>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="用户名" prop="userName">
                    <el-input v-model="form.userName" disabled clearable />
                </el-form-item>
                <el-form-item label="邮箱" prop="email" :required="true">
                    <el-input v-model="form.email" />
                </el-form-item>
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="form.name" />
                </el-form-item>
                <el-form-item label="性别" prop="sex">
                    <el-radio-group v-model="form.sex" class="ml-4">
                        <el-radio :value="1" size="large">男</el-radio>
                        <el-radio :value="2" size="large">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="form.phone" />
                </el-form-item>
                <el-form-item label="生日" prop="birthday">
                    <el-date-picker v-model="form.birthday" type="date" placeholder="Pick a day" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit">修改</el-button>
                    <el-button @click="onCancel">取消</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>
<script>
import { getUserInfo } from '@/request/api';
import { uploadProfile, updateUserInfo } from '@/request/api'
export default {
    data() {
        return {
            rules: {
                //属性名和表单中的属性名保持一致，不然会验证错误
                userName: [{ required: true, trigger: 'blur', message: "请输入用户名" }],
                email: [{ required: true, trigger: 'blur', message: "请输入邮箱" },
                { type: 'email', trigger: 'blur', message: '请输入正确的邮箱格式' }],
                phone: [{ pattern: /^1[123456789]\d{9}$/, trigger: 'blur', message: "请输入正确的手机号" }],
            },
            imageUrl: '/api/static/default.jpg',
            form: {
                userName: null,
                email: null,
                name: null,
                sex: null,
                phone: null,
                birthday: null,
                photo: null,
            },
        }
    },
    methods: {
        async onSubmit() {
            var vm = this;
            if (!vm.$refs['formRef']) return;
            await vm.$refs['formRef'].validate((valid) => {
                if (valid) {
                    try {
                        console.log(vm.form)
                        var user = null;
                        if (typeof (this.$store.getters.getUser) != 'object')
                            user = JSON.parse(this.$store.getters.getUser);
                        else user = this.$store.getters.getUser;

                        if (this.form.photo != user.profilePhoto) {
                            const formData = new FormData();
                            formData.append("path", user.profilePhoto);
                            uploadProfile(formData).then((res) => {
                                if (res.flag == "success") {
                                    console.log("原头像删除成功")
                                } else if (res.flag == "fail") {
                                    alert(res.data);
                                }
                            })
                        }
                        updateUserInfo({
                            id: user.uid,
                            email: vm.form.email,
                            name: vm.form.name,
                            sex: vm.form.sex,
                            phone: vm.form.phone,
                            birthday: vm.form.birthday,
                            photo: vm.form.photo
                        }).then(res => {
                            if (res.flag == 'success') {
                                user.name = vm.form.name;
                                user.profilePhoto = vm.form.photo;
                                this.$store.commit('setUser', user);
                                vm.$router.push('/main');
                            } else {
                                alert(res.data);
                            }
                        })
                    } catch (error) {
                        console.log(error);
                    }
                } else {
                    alert("请填写所有带*的信息")
                }
            })
        },
        onCancel() {
            this.$router.push('/main');
        },
        uploadImage(param) {
            var vm = this;
            const formData = new FormData();
            formData.append('headImg', param.file);
            if (vm.form.photo != null && vm.form.photo != '') {
                formData.append("path", vm.form.photo);
            }
            console.log(formData)
            uploadProfile(formData).then((res) => {
                if (res.flag == "success") {
                    param.onSuccess();
                    vm.form.photo = res.data;
                    vm.imageUrl = "/api/" + res.data;
                }
            })
        },
        handleAvatarSuccess() {
            this.$refs.upload.clearFiles();
        },
        // fileChange(uploadFile) {
        //     //this.$refs.upload.clearFiles(); //清除文件对象
        //     //alert(uploadFile.raw)
        //     //this.fileList = [{name: uploadFile.name, url: file.url}]
        //     //this.imageUrl = uploadFile.url;
        // },
        beforeAvatarUpload(rawFile) {
            if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/jpg' && rawFile.type !== 'image/png' && rawFile.type !== 'image/bmp' && rawFile.type !== 'image/gif') {
                alert('上传文件格式错误')
                return false
            } else if (rawFile.size / 1024 / 1024 > 2) {
                alert('上传文件大小不能超过2MB')
                return false
            }
            return true
        }
    },
    mounted() {
        var user = null;
        if (typeof (this.$store.getters.getUser) != 'object')
            user = JSON.parse(this.$store.getters.getUser);
        else user = this.$store.getters.getUser;
        getUserInfo(user.uid).then(res => {
            if (res.flag == "success") {
                console.log("用户信息", res.data);
                this.form.userName = res.data.username;
                this.form.email = res.data.email;
                this.form.name = res.data.name;
                this.form.sex = res.data.sex;
                this.form.phone = res.data.phone;
                this.form.birthday = res.data.birthday;
                this.form.photo = res.data.photo;
                if (res.data.photo != null) {
                    this.imageUrl = "/api/" + res.data.photo;
                }
            }
        });
        window.addEventListener("beforeunload", async(event) => {
            event.returnValue = "确定离开吗"
            var user = null;
            if (typeof (this.$store.getters.getUser) != 'object')
                user = JSON.parse(this.$store.getters.getUser);
            else user = this.$store.getters.getUser;
            if (user.profilePhoto == this.form.photo) {
                return
            } else {
                if (this.imageUrl != '/api/static/default.jpg') {
                    const formData = new FormData();
                    formData.append("path", this.form.photo);
                    await uploadProfile(formData).then((res) => {
                        if (res.flag == "success") {
                            console.log("未保存头像删除成功")
                        } else if (res.flag == "fail") {
                            alert(res.data);
                        }
                    })
                }
            }
        })
    },
    beforeRouteLeave(to, form, next) {
        var user = null;
        if (typeof (this.$store.getters.getUser) != 'object')
            user = JSON.parse(this.$store.getters.getUser);
        else user = this.$store.getters.getUser;
        if (this.form.photo != null && this.form.photo != '') {
            if (this.form.photo == '/api/static/default.jpg') {
                next();
                return
            }
            if (user.profilePhoto == this.form.photo) {
                next();
            } else {
                const formData = new FormData();
                formData.append("path", this.form.photo);
                uploadProfile(formData).then((res) => {
                    if (res.flag == "success") {
                        console.log("未保存头像删除成功")
                        next();
                    } else if (res.flag == "fail") {
                        alert(res.data);
                    }
                })
            }
        } else {
            next();
        }
    },
}
</script>
<style>
.avatar-uploader .el-upload .avatar {
    width: 150px;
    height: 150px;
    margin-bottom: 10px;
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
}

.updateBox {
    width: 50%;
    padding: 10px;
    border-radius: 20px;
    background-color: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(10px);
}

.updateBg {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-image: url(../assets/img/retouch_2024042523485170.png);
    background-repeat: no-repeat;
    background-size: 1026px 436px;
    background-position: right bottom;
}
</style>