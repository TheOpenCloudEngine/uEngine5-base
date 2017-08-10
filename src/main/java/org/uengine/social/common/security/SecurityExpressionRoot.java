package org.uengine.social.common.security;

/**
 * Created by uengine on 2017. 8. 1..
 */
public class SecurityExpressionRoot {

    String loggedUserId;

    public String getLoggedUserId() {
        return loggedUserId;
    }

    public void setLoggedUserId(String loggedUserId) {
        this.loggedUserId = loggedUserId;
    }

    public SecurityExpressionRoot(String loggedUserId) {
        this.loggedUserId = loggedUserId;
    }
}
