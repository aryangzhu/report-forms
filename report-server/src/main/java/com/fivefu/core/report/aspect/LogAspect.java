package com.example.core.report.aspect;
import java.time.LocalDateTime;


import com.example.core.module.auth.utils.SecurityUtil;
import com.example.core.module.auth.vo.SysAuthUser;
import com.example.core.report.anno.LogOption;
import com.example.core.report.entity.TSysLog;
import com.example.core.report.service.TSysLogService;
import com.example.core.report.util.AsyncManager;
import com.example.core.report.util.RedisQueue;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    TSysLogService tSysLogService;


    @Autowired
    RedisQueue redisQueue;


    private static final String queueKey="sysLog";


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
//            SysAuthUser sysAuthUser = SecurityUtil.currentUser();
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
            //获取方法签名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取签名的方法
            Method method = signature.getMethod();
            //获取方法上的注解
            LogOption logOption = method.getAnnotation(LogOption.class);
            //设置注解上的参数
            int ordinal = logOption.businessType().ordinal();
            String title = logOption.title();
            String s = logOption.businessId();
            //使用Redis消息队列来完成异步操作
            TSysLog tSysLog=new TSysLog();
            tSysLog.setCreatedTime(LocalDateTime.now());
            tSysLog.setUpdatedTime(LocalDateTime.now());
            tSysLog.setInvokeId(0L);
            tSysLog.setInvokeName("sys");
            tSysLog.setBusiness(s);
            //将对象序列化
            String objectStr = redisQueue.toJson(tSysLog);
            //使用消息队列
            redisQueue.push(queueKey,objectStr);
            //异步写入调用日志
            //1.使用原生的线程池
            ExecutorService service = Executors.newFixedThreadPool(4);
            service.submit(new LogTask());
            //2.使用异步线程管理器
//            AsyncManager.me().execute();
        }catch (Exception exp){
            logger.error("==前置通知异常==");
            logger.error("异常信息：{}",exp.getMessage());
            exp.printStackTrace();
        }
    }

    class LogTask implements Runnable{

        @Override
        public void run() {
            //从消息队列中获取对象
            String objectStr = redisQueue.pop(queueKey);
            //数据入库
            TSysLog tSysLog = redisQueue.fromJson(objectStr, TSysLog.class);
            tSysLogService.save(tSysLog);
        }
    }

}
