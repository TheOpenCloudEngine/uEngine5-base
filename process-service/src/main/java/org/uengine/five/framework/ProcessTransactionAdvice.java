package org.uengine.five.framework;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Created by uengine on 2017. 11. 10..
 */
@Aspect
@Component
public class ProcessTransactionAdvice {

    @Before("@annotation(processTransactional)")
    public void initiateTransaction(JoinPoint joinPoint, ProcessTransactional processTransactional) throws Exception {
        System.out.println("start tx");
        new ProcessTransactionContext();
        ProcessTransactionContext.getThreadLocalInstance().setCommitable(!processTransactional.readOnly());
    }

    @AfterReturning("@annotation(processTransactional)")
    public void commitTransaction(JoinPoint joinPoint, ProcessTransactional processTransactional) throws Exception {
        if (!processTransactional.readOnly()) {
            ProcessTransactionContext.getThreadLocalInstance().commit();
        }
        System.out.println("commit");
    }

    @AfterThrowing("@annotation(org.uengine.five.framework.ProcessTransactional)")
    public void rollbackTransaction() throws Exception {
        ProcessTransactionContext.getThreadLocalInstance().rollback();
        System.out.println("rollback");
    }
}