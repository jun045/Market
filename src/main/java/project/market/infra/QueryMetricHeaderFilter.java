package project.market.infra;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

//테스트 쿼리 측정용 필터
@Component
@Profile({"test", "local"})
public class QueryMetricHeaderFilter extends OncePerRequestFilter {

    private final Statistics statistics;

    public QueryMetricHeaderFilter(EntityManagerFactory emf) {
        SessionFactory sf = emf.unwrap(SessionFactory.class);
        this.statistics = sf.getStatistics();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("[QueryMetricHeaderFilter] invoked");
        ContentCachingResponseWrapper wrapped = new ContentCachingResponseWrapper(response);

        long startNs = System.nanoTime();
        QueryCountContext.reset();

        try {
            filterChain.doFilter(request, wrapped);
        } finally {
            long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;

            wrapped.setHeader("X-Query-Count", String.valueOf(QueryCountContext.get()));
            wrapped.setHeader("X-Elapsed-Ms", String.valueOf(elapsedMs));

            QueryCountContext.clear();
            wrapped.copyBodyToResponse();
        }
    }

}