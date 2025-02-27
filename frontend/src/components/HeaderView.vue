<template>
    <div class="title">
        <div class="title-text">
            胖达文档<br />
            Panda Documents
        </div>
        <el-dropdown>
            <div class="el-dropdown-link">
                {{ name == null || name == '' ? username : name }}
                <el-image :fit="contain" :src="imageUrl" class="avatar" />
            </div>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item @click="updateInfo">个人信息</el-dropdown-item>
                    <el-dropdown-item @click="account">账号安全</el-dropdown-item>
                    <el-dropdown-item @click="updatePassword">修改密码</el-dropdown-item>
                    <el-dropdown-item @click="quite">退出登录</el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
    </div>
    <!--添加账号安全弹窗-->
    <el-dialog :append-to-body="true" :title="config.title" v-model="config.visible" @close="matClose()">
        <el-form ref="formRef" :model="config.form" :rules="rules" label-width="80px">
            <el-form-item label="邮箱">
                <el-input disabled v-model="config.form.email" clearable />
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input show-password v-model="config.form.password" clearable />
            </el-form-item>
            <el-form-item label="问题" prop="question">
                <el-input v-model="config.form.question" clearable />
            </el-form-item>
            <el-form-item label="答案" prop="answer">
                <el-input v-model="config.form.answer" clearable />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSubmit">确定</el-button>
                <el-button @click="onCancel">取消</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>
<script>
import { forget, addEncryption, confirmPassword, updatePassword } from "@/request/api"
import { ElMessage } from "element-plus";

export default {
    data() {
        return {
            username: '',
            name: '',
            imageUrl: '/api/static/default.jpg',
            rules: {
                //属性名和表单中的属性名保持一致，不然会验证错误
                question: [{ required: true, trigger: 'blur', message: "请输入验证问题" }],
                password: [{ required: true, trigger: 'blur', message: '请输入密码' }],
                answer: [{ required: true, trigger: 'blur', message: '请输入验证答案' }],
            },
            config: {
                title: '密保信息',
                visible: false,
                form: {
                    email: '',
                    question: '',
                    answer: '',
                    password: ''
                }
            }
        }
    },
    methods: {
        matClose() {
            this.config.visible = false;
        },
        async account() {
            this.config.visible = true;
            await forget(this.username).then(res => {
                if (res.flag == "success") {
                    this.config.form.email = res.data.email;
                    this.config.form.question = res.data.question;
                } else {
                    alert(res.data);
                }
            })
        },
        updatePassword() {
            this.$prompt("请输入原密码", "操作验证", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                inputPattern: /\S/,
                inputErrorMessage: "密码不能为空",
                inputType: 'password'
            }).then(({ value }) => {
                confirmPassword({
                    username: this.username,
                    password: value
                }).then(res => {
                    if (res.flag == "success") {
                        this.$prompt("请输入新密码", "操作验证", {
                            confirmButtonText: "确定",
                            cancelButtonText: "取消",
                            inputPattern: /\S/,
                            inputErrorMessage: "密码不能为空",
                            inputType: 'password'
                        }).then(({ value }) => {
                            updatePassword({
                                username: this.username,
                                password: value
                            }).then(res => {
                                if (res.flag == "success") {
                                    ElMessage({
                                        type: 'success',
                                        message: res.data,
                                    })
                                } else {
                                    alert(res.data)
                                }
                            })
                        }).catch(() => {
                            ElMessage({
                                type: 'info',
                                message: "取消输入",
                            })
                        })
                    } else {
                        alert(res.data)
                    }
                })
            }).catch(() => {
                ElMessage({
                    type: 'info',
                    message: "取消输入",
                })
            })
        },
        async onSubmit() {
            if (!this.$refs['formRef']) {
                return
            }
            var user = null;
            if (typeof (this.$store.getters.getUser) != 'object')
                user = JSON.parse(this.$store.getters.getUser);
            else user = this.$store.getters.getUser;
            await this.$refs['formRef'].validate((valid) => {
                if (valid) {
                    try {
                        addEncryption({
                            id: user.uid,
                            password: this.config.form.password,
                            question: this.config.form.question,
                            answer: this.config.form.answer
                        }).then(res => {
                            if (res.flag == "success") {
                                alert("设置成功");
                                this.config.visible = false;
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
            this.config.visible = false;
        },
        quite() {
            this.$store.commit('logout')
            this.$router.push({ path: '/', replace: true })
        },
        updateInfo() {
            this.$router.push('/updateUserInfo');
        },
    },
    mounted() {
        var user = null;
        if (typeof (this.$store.getters.getUser) != 'object')
            user = JSON.parse(this.$store.getters.getUser);
        else user = this.$store.getters.getUser;
        this.username = user.username;
        this.name = user.name;
        if (user.profilePhoto != null && user.profilePhoto != '')
            this.imageUrl = "/api/" + user.profilePhoto;
    }
}
</script>
<style scoped>
.title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0px 40px;
    margin: 0px;
}

.title-text {
    color: #606b62;
    font-weight: bold;
    font-size: 24px;
}

.el-dropdown-link {
    cursor: pointer;
    color: #303133;
    display: flex;
    align-items: center;
    font-size: 20px;
}

.el-dropdown-link:focus-visible {
    outline: unset;
}

.avatar {
    margin-left: 20px;
    width: 80px;
    height: 80px;
    border-radius: 50%;
}
</style>