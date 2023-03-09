package com.fivefu.core.report.aspect;


import com.fivefu.core.module.auth.utils.SecurityUtil;
import com.fivefu.core.module.auth.vo.SysAuthUser;
import com.fivefu.core.report.anno.LogOption;
import com.fivefu.core.report.constant.BusinessType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    /** 排除敏感属性字段 */
    public static final String[] EXCLUDE_PROPERTIES = { "password", "oldPassword", "newPassword", "confirmPassword" };


    @AfterReturning(pointcut = "@annotation(controllerLog)")
    public void doAfterReturning(JoinPoint joinPoint, LogOption controllerLog){
        handleLog(joinPoint,controllerLog,null);
    }

    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, LogOption controllerLog, Exception e)
    {
        handleLog(joinPoint, controllerLog, e);
    }



    public void handleLog(final JoinPoint joinPoint, LogOption controllerLog, final Exception e){
        try{
            //获取当前用户
            SysAuthUser sysAuthUser = SecurityUtil.currentUser();

            //利用反射获取参数
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            //获取request
            HttpServletRequest request = sra.getRequest();
            Enumeration<String> parameterNames = request.getParameterNames();
            Map<String, Object> resultMap = new HashMap<>();
            while (parameterNames.hasMoreElements()) {
                String s = parameterNames.nextElement();
                String parameter = request.getParameter(s);
                resultMap.put(s, parameter);
            }
            //获取方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            LogOption logOption = method.getAnnotation(LogOption.class);
            //设置注解上的参数
            int ordinal = logOption.businessType().ordinal();
            String title = logOption.title();
            System.out.println(ordinal+title);
            //使用消息队列
            //异步写入调用日志
        }catch (Exception exp){
            logger.error("==前置通知异常==");
            logger.error("异常信息：{}",e.getMessage());
            e.printStackTrace();
        }
    }

}
