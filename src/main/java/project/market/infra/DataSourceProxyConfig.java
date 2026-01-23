package project.market.infra;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

// 테스트 쿼리 측적용 config
@Configuration
public class DataSourceProxyConfig {

    @Bean
    public static BeanPostProcessor dataSourceProxyPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (!(bean instanceof DataSource ds)) return bean;

                // 이미 프록시면 패스 (클래스명으로 대충 방지)
                String cls = bean.getClass().getName();
                if (cls.contains("ttddyy") || cls.contains("dsproxy")) return bean;

                // ✅ builder API 안 타는 방식: ProxyDataSource 직접 생성 + addListener
                net.ttddyy.dsproxy.support.ProxyDataSource proxy =
                        new net.ttddyy.dsproxy.support.ProxyDataSource(ds);

                proxy.setDataSourceName("DS-PROXY");

                proxy.addListener(new net.ttddyy.dsproxy.listener.QueryExecutionListener() {
                    @Override
                    public void beforeQuery(net.ttddyy.dsproxy.ExecutionInfo execInfo,
                                            java.util.List<net.ttddyy.dsproxy.QueryInfo> queryInfoList) {
                        // no-op
                    }

                    @Override
                    public void afterQuery(net.ttddyy.dsproxy.ExecutionInfo execInfo,
                                           java.util.List<net.ttddyy.dsproxy.QueryInfo> queryInfoList) {
                        // size() 안 먹는 환경 대비: for-each로 카운트
                        int n = 0;
                        for (net.ttddyy.dsproxy.QueryInfo ignored : queryInfoList) n++;
                        QueryCountContext.add(n);
                    }
                });

                return proxy;
            }
        };
    }
}
