<template>
    <div class="loginBg">
        <div class="loginBox">
            <div class="boxLeft">
                <!--rules设置输入规则-->
                <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                    <el-form-item label="用户名" prop="username">
                        <el-input v-model="form.username" clearable style="width: 210px"/>
                    </el-form-item>
                    <el-form-item label="密码" prop="password">
                        <el-input v-model="form.password" show-password style="width: 210px"/>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="onSubmit">登录</el-button>
                        <el-button @click="onCancel">取消</el-button>
                    </el-form-item>
                    <el-row justify="end">
                        <el-col :span="5" :offset="13"><el-link @click="forgetClick">忘记密码</el-link></el-col>
                        <el-col :span="5" :offset="1"><el-link href="/register" target="_blank">注册账号</el-link></el-col>
                    </el-row>
                </el-form>
            </div>

            <div class="boxRight">
                <p>欢迎来到</p>
            </div>
        </div>
    </div>
    <!--添加忘记密码弹窗-->
    <el-dialog :append-to-body="true" :title="config.title" v-model="config.visible" @close="matClose()">
        <el-form ref="formRef2" :model="config.form" :rules="rules2" label-width="80px">
            <div v-if="config.form.visible">
                <el-form-item label="问题" prop="question">
                    <el-input disabled v-model="config.form.question" clearable />
                </el-form-item>
                <el-form-item label="答案" prop="answer">
                    <el-input v-model="config.form.answer" clearable />
                </el-form-item>
            </div>
            <div v-else>
                <el-form-item label="邮箱">
                    <el-row :gutter="10" style="width: 100%;">
                        <el-col :span="18">
                            <el-input disabled v-model="config.form.email" clearable />
                        </el-col>
                        <el-col :span="6">
                            <el-button :disabled="sendc" @click="sendCode">发送验证码{{ time >= 60 ? '' : time }}</el-button>
                        </el-col>
                    </el-row>
                </el-form-item>
                <el-form-item label="验证码" prop="code">
                    <el-input onkeyup="value=value.replace(/[^\d\-\d]/g,'')" v-model="config.form.code" clearable />
                </el-form-item>
            </div>
            <el-form-item>
                <el-button type="primary" @click="forgetSubmit">确定</el-button>
                <el-button @click="matClose">取消</el-button>
                <el-button v-if="!config.form.visible" @click="vaild">使用密保问题验证</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>
<script>
import { login, forget, sendCode, verifyForget } from '@/request/api';
export default {
    data() {
        return {
            timerId: null,
            timeoutId: null,
            time: 60,
            sendc: false,
            config: {
                title: "忘记密码",
                visible: false,
                form: {
                    visible: false,
                    email: '',
                    question: "",
                    answer: "",
                    code: null,
                }
            },
            rules2: {
                //属性名和表单中的属性名保持一致，不然会验证错误
                code: [{ required: true, trigger: 'blur', message: "请输入验证码" }],
                answer: [{ required: true, trigger: 'blur', message: '请输入答案' }],
            },
            rules: {
                //属性名和表单中的属性名保持一致，不然会验证错误
                username: [{ required: true, trigger: 'blur', message: "请输入用户名" }],
                password: [{ required: true, trigger: 'blur', message: '请输入密码' }],
            },
            form: {
                username: '',
                password: '',
            },
        }
    },
    methods: {
        async onSubmit() {
            var vm = this;
            if (!vm.$refs['formRef']) {
                return
            }
            await vm.$refs['formRef'].validate((valid) => {
                if (valid) {
                    try {
                        login(this.form).then(res => {
                            if (res.flag == 'success') {
                                vm.$store.commit('setToken', res.data.token);
                                vm.$store.commit('setUser', res.data.user);
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
        matClose() {
            this.config.visible = false;
            this.config.form.visible = false;
            this.config.form.email = '';
            this.config.form.answer = '';
            this.config.form.code = '';
        },
        onCancel() {
            this.$router.push('/');
        },
        sendCode() {
            sendCode(this.config.form.email).then(res => {
                alert(res.data);
                this.sendc = true;
                this.timerId = setInterval(() => {
                    this.time--;
                }, 1000)
                this.timeoutId = setTimeout(() => {
                    clearInterval(this.timerId);
                    this.sendc = false;
                    this.time = 60;
                }, 60 * 1000);
            })
        },
        forgetClick() {
            if (this.form.username == '') {
                alert("请输入用户名");
                return;
            }
            forget(this.form.username).then(res => {
                if (res.flag == "success") {
                    this.config.form.email = res.data.email;
                    this.config.form.question = res.data.question;
                    this.config.visible = true;
                } else {
                    alert(res.data);
                }
            })
        },
        forgetSubmit() {
            verifyForget({
                email: this.config.form.email,
                code: this.config.form.code == '' ? null : this.config.form.code,
                username: this.form.username,
                answer: this.config.form.answer == '' ? null : this.config.form.answer
            }).then(res => {
                if (res.flag == "success") {
                    alert(res.data);
                    this.matClose();
                } else {
                    alert(res.data);
                }
            })
        },
        vaild() {
            if (this.config.form.question == null || this.config.form.question == '') {
                alert("您没有设置验证问题");
                return;
            } else {
                this.config.form.code = '';
                this.config.form.visible = true;
            }
        }
    },
    mounted() {

    }
}
</script>
<style>
.loginBg {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-image: url(../assets/img/zz.png);
    background-repeat: no-repeat;
    background-size: 900px 900px;
    background-position: -180px -20px;
}

.boxRight {
    width: 400px;
    height: 500px;
    border-radius: 10px;
    position: absolute;
    right: 30px;
    top: -50px;
    box-shadow: 2px 2px 5px 2px;
    background-color: #A2C4A9;
    background-image: url(../assets/img/retouch_2024042516532877.png);
    background-repeat: no-repeat;
    background-size: contain;
    background-position: right bottom;
}
.boxRight p{
    font-size: 60px;
    font-family: "STHupo";
    color: white;
}

.loginBox {
    position: relative;
    background-color: rgba(255, 255, 255, 0.65);
    backdrop-filter: blur(10px);
    width: 800px;
    height: 400px;
    border-radius: 30px;
}

.boxLeft {
    width: 300px;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>