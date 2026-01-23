package project.market;

import io.restassured.response.Response;

//쿼리 측정
public class PerfMetrics {

    public final long queryCount;
    public final long serverMs;
    public final long clientMs;

    private PerfMetrics(long queryCount, long serverMs, long clientMs) {
        this.queryCount = queryCount;
        this.serverMs = serverMs;
        this.clientMs = clientMs;
    }

    public static PerfMetrics from(Response res) {
        String qcHeader = res.getHeader("X-Query-Count");
        String msHeader = res.getHeader("X-Elapsed-Ms");

        if (qcHeader == null || msHeader == null) {
            throw new IllegalStateException("Metrics headers missing. Check filter registration.");
        }

        long queryCount = Long.parseLong(qcHeader);
        long serverMs = Long.parseLong(msHeader);
        long clientMs = res.time();

        return new PerfMetrics(queryCount, serverMs, clientMs);
    }

    public String format() {
        return String.format("[PERF] queries=%d | server=%dms | client=%dms", queryCount, serverMs, clientMs);
    }


}
