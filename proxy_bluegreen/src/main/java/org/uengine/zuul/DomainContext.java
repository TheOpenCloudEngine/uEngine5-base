package org.uengine.zuul;

/**
 * Created by uengine on 2017. 10. 9..
 */
public class DomainContext {
    static ThreadLocal<DomainContext> local = new ThreadLocal();
    String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public DomainContext(String domain) {
        this.domain = domain;
        local.set(this);
    }

    public static DomainContext getThreadLocalInstance() {
        DomainContext dc = (DomainContext) local.get();
        return dc != null ? dc : new DomainContext((String) null);
    }
}
