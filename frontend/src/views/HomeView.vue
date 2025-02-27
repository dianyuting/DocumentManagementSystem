<template>
  <div style="padding-top: 8px;">
    <el-button type="primary" @click="btn1">登录</el-button>
    <el-button type="primary" @click="btn2">注册</el-button>
  </div>
  <div class="home">
    <el-carousel :interval="4000" type="card" height="500px" >
      <el-carousel-item v-for="item in carouselDate" :key="item.id">
        <el-card>
          <img :src="item.imgUrl" />
          <p>{{ item.text }}</p>
        </el-card>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script>
// @ is an alias to /src
import a1 from '../assets/show/1.png'
import a2 from '../assets/show/2.png'
import a3 from '../assets/show/3.png'
import a4 from '../assets/show/4.png'
import a5 from '../assets/show/5.png'
export default {
  name: 'HomeView',
  data() {
    return {
      carouselDate: [{
        id: 1,
        text: "文件归档无忧，高效管理，工作生活两不误",
        imgUrl: a1
      }, {
        id: 2,
        text: "精细编辑，完善信息，让个人资料更加完美",
        imgUrl: a2,
      }, {
        id: 3,
        text: "精心管理你的好友圈，分享快乐，传递知识",
        imgUrl: a3,
      }, {
        id: 4,
        text: "分享快乐，共享智慧，与好友共筑知识桥梁",
        imgUrl: a4,
      }, {
        id: 5,
        text: "标签助力，快速定位，文件查找更高效",
        imgUrl: a5,
      }],
    }
  },
  methods:{
    btn1(){
      this.$router.push('/login');
    },
    btn2(){
      this.$router.push('/register');
    },
  }
}
</script>
<style>
.el-carousel{
  width: 90%;
}
.el-carousel__item,
.el-carousel__mask,
.el-card,
.el-card__body {
  width: 100%;
  height: 100%;
}

.el-card__body img {
  margin: 10px;
  max-width: calc(100% - 20px);
  max-height: calc(100% - 120px);
}

.el-card__body p {
  font-size: 30px;
  font-weight: bold;
  font-family: "幼圆";
  color: #526656;
}

.el-card__body {
  padding: 0px !important;
}
.home{
  width: 100%;
  height: calc(100% - 40px);
  display: flex;
  justify-content: center;
  align-items: center;
    background-image: url(../assets/img/retouch_2024042521473917.png),url(../assets/img/zz.png);
    background-repeat: no-repeat;
    background-size: 213.5px 256px,1000px 1000px;
    background-position: right bottom,-250px -20px;
}
</style>
