import axios from "axios";
import router from "@/router";
import store from '@/store';
//为拦截器报错所用的弹框，如不需要可以不导入
//import { ElMessage } from "element-plus";

const service = axios.create({
    baseURL: '', //基本路径，后面可直接写/方法即可
    withCredentials: false, // 异步请求携带cookie
    // 设置请求头
    headers: {
        // 设置后端需要的传参类型
        // 'Content-Type': 'application/json;charset=UTF-8',
        // 'token': 'your token',
        // 'X-Requested-With': 'XMLHttpRequest',  
        "Access-Control-Allow-Origin": "*",
    },
    //设置请求超时时间
    timeout: 1000 * 60 * 5,
});

//*可选
//请求拦截器
service.interceptors.request.use((request) => {
    console.log("request：", request);
    //配置请求头
    //request.headers.token=window.localStorage.getItem('token')===null? null : window.localStorage.getItem('token')
    request.headers.Authorization = window.localStorage.getItem('token') === null ? null : window.localStorage.getItem('token')
    //一定要把处理过的request返回
    console.log(request);
    return request;
},
    err => Promise.reject(err)
);
//响应拦截器
service.interceptors.response.use((response) => {
    //获取接口返回结果
    const res = response.data
    console.log("response：", response);
    if (response.status == 200) {
        console.log("响应拦截",response.headers.getContentType())
        if (response.headers.getContentType() === 'application/octet-stream;charset=UTF-8') {
            console.log("响应拦截器："+response.headers['content-disposition'])
            return Promise.resolve({
                //获取文件名
                fileName: response.headers['content-disposition'],
                data: res
            });
        }
        return res;
    } else {
        //ElMessage.error(res.data || '网络异常')
        return res;
    }
},
    (err) => {
        if (err.response.status == 401) {
            router.push("/login");
            store.commit('logout');
            return Promise.reject(new Error("登录信息超时"));
        }else if(err.response.status == 404){
            alert("请求资源不存在");
            return Promise.reject(err);
        }
        return Promise.reject(err)
    }
);
export default service;