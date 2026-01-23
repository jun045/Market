package project.market.infra;

//테스트 쿼리 측정용
public class QueryCountContext {

    private static final ThreadLocal<Integer> COUNT = ThreadLocal.withInitial(() -> 0);

    private QueryCountContext() {}

    public static void reset() { COUNT.set(0); }

    public static void add(int n) { COUNT.set(COUNT.get() + n); }

    public static int get() { return COUNT.get(); }

    public static void clear() { COUNT.remove(); }
}
