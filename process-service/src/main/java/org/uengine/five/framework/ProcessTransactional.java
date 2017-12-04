package org.uengine.five.framework;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by uengine on 2017. 11. 10..
 */

@Transactional
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessTransactional {
    boolean readOnly() default false;
}
