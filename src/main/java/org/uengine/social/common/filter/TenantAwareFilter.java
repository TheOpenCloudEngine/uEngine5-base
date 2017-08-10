package org.uengine.social.common.filter;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpMethod;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by uengine on 2017. 6. 12..
 */
@WebFilter
//@Component
public class TenantAwareFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //String token = ((HttpServletRequest)servletRequest).getParameter("access_token");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
//            String token = ((HttpServletRequest) servletRequest).getHeader("access_token");
//
//            //java 처리 후
//            String jwt = token;
//
//            JWSObject jwsObject = null;
//            String tenantId = null;
//            try {
//                jwsObject = JWSObject.parse(token);
//
//                JSONObject jsonPayload = jwsObject.getPayload().toJSONObject();
//                JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(jsonPayload);
//
//                JSONObject contexts = (JSONObject) jwtClaimsSet.getClaim("context");
//                String userName = (String) contexts.get("userName");
//
//                //new User(userName);
//
//                tenantId = userName.split("@")[1];
//
//            } catch (Exception e) {
//                throw new RuntimeException("Invalid login ", e);
//            }

            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
