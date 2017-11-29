package org.uengine.zuul;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.eureka.DomainExtractingServerList;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by uengine on 2017. 10. 9..
 */
public class BlueGreenRoundRobinRule extends AbstractLoadBalancerRule {

    @Autowired
    Environment environment;

    @Autowired
    BlueGreenProvider blueGreenProvider;

    private AtomicInteger nextServerCyclicCounter;
    private static final boolean AVAILABLE_ONLY_SERVERS = true;
    private static final boolean ALL_SERVERS = false;
    private static Logger log = LoggerFactory.getLogger(BlueGreenRoundRobinRule.class);

    public BlueGreenRoundRobinRule() {
        this.nextServerCyclicCounter = new AtomicInteger(0);
    }

    public BlueGreenRoundRobinRule(ILoadBalancer lb) {
        this();
        this.setLoadBalancer(lb);
    }

    private List getFilteredServers(List servers) {
        List<DiscoveryEnabledServer> filteredServers = new ArrayList();

        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String serverName = request.getServerName();
        String serviceId = (String) currentContext.get("serviceId");
        String prod_url = environment.getProperty("deployment.url.prod");
        String dev_url = environment.getProperty("deployment.url.dev");

        BlueGreenServiceState serviceState = blueGreenProvider.getService(serviceId);


        BlueGreen blueGreen = null;
        //프로덕트 url 로 접근한 경우  BLUE / GREEN
        if (serverName.equals(prod_url)) {
            blueGreen = serviceState.getProd();
        }
        //개발 url 로 접근한 경우 BLUE / GREEN
        else if (serverName.equals(dev_url)) {
            blueGreen = serviceState.getDev();
        }
        //그 외의 url 은 접근불가
        else {
            return filteredServers;
        }

        //인스턴스의 메타데이터 deployment 가 주어진 BLUE / GREEN 인 것만을 추린다.
        if (servers != null) {
            for (int i = 0; i < servers.size(); i++) {
                DiscoveryEnabledServer server = (DiscoveryEnabledServer) servers.get(i);
                InstanceInfo instanceInfo = server.getInstanceInfo();
                Map<String, String> metadata = instanceInfo.getMetadata();
                if (metadata != null && metadata.containsKey("deployment")) {
                    if (metadata.get("deployment").toString().equals(blueGreen.toString())) {
                        filteredServers.add(server);
                    }
                }
            }
        }
        return filteredServers;
    }

    private List getFilteredReachableServers(ILoadBalancer lb) {
        return this.getFilteredServers(lb.getReachableServers());
    }

    private List getFilteredAllServers(ILoadBalancer lb) {
        return this.getFilteredServers(lb.getAllServers());
    }

    public Server choose(ILoadBalancer lb, Object key) {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String serverName = request.getServerName();
        String serviceId = (String) currentContext.get("serviceId");

        if (lb == null) {
            log.warn("no load balancer");
            return null;
        } else {
            Server server = null;
            int count = 0;

            while (true) {
                if (server == null && count++ < 10) {
                    List reachableServers = this.getFilteredReachableServers(lb);
                    List allServers = this.getFilteredAllServers(lb);

                    int upCount = reachableServers.size();
                    int serverCount = allServers.size();
                    if (upCount != 0 && serverCount != 0) {
                        int nextServerIndex = this.incrementAndGetModulo(serverCount);
                        server = (Server) allServers.get(nextServerIndex);
                        if (server == null) {
                            Thread.yield();  // 없는 경우 다음 서버 찾아보기 위해 Thread 대기를 풀어
                        } else {
                            if (server.isAlive() && server.isReadyToServe()) { //건강한 서버라면 리턴해줌
                                return server;
                            }

                            server = null;
                        }
                        continue;
                    }

                    log.warn("No up servers available from load balancer: " + lb);
                    return null;
                }

                if (count >= 10) {
                    log.warn("No available alive servers after 10 tries from load balancer: " + lb);
                }

                return server;
            }
        }
    }

    private int incrementAndGetModulo(int modulo) {
        int current;
        int next;
        do {
            current = this.nextServerCyclicCounter.get();
            next = (current + 1) % modulo;  //modulo가  1이 아닌 이상... current와 next 가 같을 수 도 있나?
        } while (!this.nextServerCyclicCounter.compareAndSet(current, next));

        return next;
    }

    public Server choose(Object key) {      // 여기가 진입점임. IRule 의 정의중 핵심 프로토콜
        return this.choose(this.getLoadBalancer(), key);
    }

    public void initWithNiwsConfig(IClientConfig clientConfig) {
    }
}
