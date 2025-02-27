import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/main',
    name: 'main',
    component: () => import('../views/MainView.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/RegisterView.vue')
  },
  {
    path: '/mydocument',
    name: 'mydocument',
    component: () => import('../views/MainView.vue'),
    children: [{
      path: '',
      component: () => import('../components/MyDocument.vue'),
    }]
  },
  {
    path: '/updateUserInfo',
    name: 'updateUserInfo',
    component: () => import('../views/UpdateUserInfoView.vue'),
  },
  {
    path: '/friendFile',
    name: 'friendFile',
    component: () => import('../views/MainView.vue'),
    children: [{
      path: '',
      component: () => import('../components/FriendFile.vue'),
    }]
  },
  {
    path: '/labelManagement',
    name: 'labelManagement',
    component: () => import('../views/MainView.vue'),
    children: [{
      path: '',
      component: () => import('../components/LabelManagement.vue'),
    }]
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.path.indexOf('userManageAside-') !== -1) return
  if (to.path === '/login' || to.path === '/register' || to.path === '/') { //若是进入登录与注册界面
    next()
  } else {
    let userToken = localStorage.getItem('token');
    if (userToken == null || userToken == '') {
      alert("请先登录");
      return next('/login');
    } else {
      next();
    }
  }
})


export default router
