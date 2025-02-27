// import AutoImport from 'unplugin-auto-import/vite'
// import Components from 'unplugin-vue-components/vite'
// import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'
const { defineConfig } = require('@vue/cli-service');
// const AutoImport = require('unplugin-auto-import/webpack');
// const Components = require('unplugin-vue-components/webpack');
// const { ElementPlusResolver } = require('unplugin-vue-components/resolvers')
// const AutoImport = require('unplugin-auto-import/webpack')
// const Components = require('unplugin-vue-components/webpack')
// const Icons = require('unplugin-icons/webpack')
// const IconsResolver = require('unplugin-icons/resolver')

module.exports = defineConfig({
  transpileDependencies: true,
  // configureWebpack:{
  //   plugins:[
  //     AutoImport({
  //       resolvers:[ElementPlusResolver({
  //         importStyle:"sass",
  //       })]
  //     }),
  //     Components({
  //       resolvers:[ElementPlusResolver({
  //         importStyle:"sass",
  //       })]
  //     })
  //   ],
  // },
  // css: {
  //   loaderOptions: {
  //     scss: {
  //       additionalData: '@use "./src/them/index.scss" as *;',
  //     }
  //   }
  // },
  devServer: {
    port: 8081,
    client: {
      webSocketURL: 'ws://0.0.0.0:8081/ws',
    },
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
    // 配置服务器代理
    proxy: {
      '/api': { // 匹配访问路径中含有 '/api' 的路径
        target: 'http://localhost:9090', // 测试地址、目标地址
        changeOrigin: true,
        ws: true, // 是否开启 webSocket 代理
        pathRewrite: { // 请求路径重写
          '^/api': '',   //重写请求路径
        },
      }
    }
  },
  // configureWebpack:{
  //   plugins:[
  //     //配置webpack按需自动引入element-plus
  //     // require('unplugin-element-plus/webpack')({
  //     //   libs:[{
  //     //     libraryName:'element-plus',
  //     //     esModule:true,
  //     //     resolveStyle:(name)=>{
  //     //       return 'element-plus/them-chalk/${name}.css'
  //     //     },
  //     //   },]
  //     // }),
  //     //自动导入图标
  //     AutoImport({
  //       resolvers:[
  //         IconsResolver({
  //           prefix: 'Icon',
  //         }),
  //         //自动导入ElementPlus相关函数
  //         ElementPlusResolver(),
  //       ]
  //     }),
  //     Components({
  //       resolvers: [
  //         // Auto register icon components
  //         // 自动注册图标组件
  //         IconsResolver({
  //           enabledCollections: ['ep'],
  //         }),
  //         ElementPlusResolver()
  //       ]
  //     }),
  //     Icons({
  //       autoInstall: true,
  //     }),
  //   ],
  // },
})
