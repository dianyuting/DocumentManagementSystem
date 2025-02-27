package edu.wtbu.interceptor;

import edu.wtbu.entity.Result;
import edu.wtbu.service.UserService;
import edu.wtbu.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

//该注解标记该类为一个组件，spring能够在应用程序启动时自动扫描并加载这些组件，将其实例化为一个bean
@Component
@Slf4j
public class MyJwtInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    //预处理回调方法，第三个参数为响应的处理器，自定义Controller，返回为true表示继续流程，false表示流程中断
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = request.getHeader("token");
        //判断拦截到的请求是否是Controller里面的，不是就放行，预检请求就不会被拦
        if(!(handler instanceof HandlerMethod)) return true;
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        //检查是否有自定义PassToken注解，有则跳过认证检查
        if(method.isAnnotationPresent(PassToken.class)){
            PassToken passToken = method.getAnnotation(PassToken.class);
            if(passToken.required()) return true;
        }
        //不含自定义注解的进行token检查
        if(!JwtUtils.checkToken(request)){
            response.setStatus(401);
            //token无效
            return false;
        }
        Claims claims = JwtUtils.getMemberByJwtToken(request);
        Result r = userService.getUserById(Integer.parseInt(claims.get("id").toString()));
        if(r.getFlag().equals("fail")) {
            response.setStatus(401);
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    //后处理的回调方法，实现处理器的后处理
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    //请求处理完毕回调方法
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
