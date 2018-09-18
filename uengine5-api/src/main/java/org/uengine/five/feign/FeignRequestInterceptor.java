package org.uengine.five.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;

import org.metaworks.multitenancy.tenantawarefilter.TokenContext;
import org.springframework.util.StringUtils;

public class FeignRequestInterceptor implements RequestInterceptor {
    private String header;

    public FeignRequestInterceptor() {
        this.header = "access_token";
    }

    public FeignRequestInterceptor(String header) {
        this.header = header;
    }

    public void apply(RequestTemplate requestTemplate) {
        //오리지널 요청에 토큰이 있다면 토큰 전달
        String token = TokenContext.getThreadLocalInstance().getToken();
        if (!StringUtils.isEmpty(token)) {
            requestTemplate.header(header, token);
        }
    }
}
