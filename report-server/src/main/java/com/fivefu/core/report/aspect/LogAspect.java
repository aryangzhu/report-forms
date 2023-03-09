package com.fivefu.core.report.aspect;


import com.fivefu.core.module.auth.utils.SecurityUtil;
import com.fivefu.core.module.auth.vo.SysAuthUser;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    /** 排除敏感属性字段 */
    public static final String[] EXCLUDE_PROPERTIES = { "password", "oldPassword", "newPassword", "confirmPassword" };


    @AfterReturning("@annotation(com.fivefu.core.report.anno.LogOption)")
    public void doAfterReturning(){
        handleLog();
    }


    public void handleLog(){
        try{
            //获取当前用户
            SysAuthUser sysAuthUser = SecurityUtil.currentUser();

            //

        }catch (Exception e){
            logger.error("==前置通知异常==");
            logger.error("异常信息：{}",e.getMessage());
            e.printStackTrace();
        }
    }

}
