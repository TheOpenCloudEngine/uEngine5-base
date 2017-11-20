package org.uengine.five.framework;

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

    @Before("@annotation(org.uengine.five.framework.ProcessTransactional)")
    public void initiateTransaction() throws Exception {
        System.out.println("start tx");
        new ProcessTransactionContext();
    }

    @AfterReturning("@annotation(org.uengine.five.framework.ProcessTransactional)")
    public void commitTransaction() throws Exception {
        System.out.println("commit");
        ProcessTransactionContext.getThreadLocalInstance().commit();
    }

    @AfterThrowing("@annotation(org.uengine.five.framework.ProcessTransactional)")
    public void rollbackTransaction() throws Exception {
        System.out.println("rollback");
        ProcessTransactionContext.getThreadLocalInstance().rollback();
    }
}