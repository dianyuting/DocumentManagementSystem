const path = require('path')
const AutoImport = require('unplugin-auto-import/vite');
const Components = require('unplugin-vue-components/vite');
const { ElementPlusResolver } = require('unplugin-vue-components/resolvers')
module.exports = {
    mode: 'development',
    entry: path.resolve(__dirname, 'src', 'index.js'),
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'bundle.js'
    },
    plugins: [
        AutoImport({
            resolvers: [ElementPlusResolver({
                importStyle: "sass",
            })]
        }),
        Components({
            resolvers: [ElementPlusResolver({
                importStyle: "sass",
            })]
        })
    ],
    css: {
        loaderOptions: {
            scss: {
                additionalData: '@use"./src/them/index.scss" as *;',
            }
        }
    },
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
}