import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
//import 'element-plus/dist/index.css'
import ElementIcons from '@element-plus/icons-vue'
import './them/index.scss'

createApp(App)
    .use(ElementPlus)
    .use(ElementIcons)
    .use(store).use(router).mount('#app')
