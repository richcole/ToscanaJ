/*
 * User: peter
 * Date: Feb 17, 2002
 * Time: 8:59:06 PM
 */
package util;


public class Stopwatch {
    static final int SECONDS_PER_MINUTE = 60;
    static final int MINUTES_PER_HOUR = 60;
    static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

    static final String MINUTES_MARK = " min. ";
    static final String HOURS_MARK = " h. ";
    static final String SECONDS_MARK = " sec.";

    public Stopwatch() {
        restart();
    }

    public long getTotalTime() {
        return getCurrentTime() - getStartTime();
    }

    public long getDelayTime() {
        long lDelay = getCurrentTime() - m_nPreviousTime;
        m_nPreviousTime = getCurrentTime();
        return lDelay;
    }

    public String getDelayAsString() {
        long lDelayTime = getDelayTime();

        if (lDelayTime < SECONDS_PER_MINUTE) {
            return "" + lDelayTime + SECONDS_MARK;
        }
        if (lDelayTime < SECONDS_PER_HOUR) {
            long lMinutes = getMinutes(lDelayTime);
            long lSeconds = getSeconds(lDelayTime);
            return "" + lMinutes + MINUTES_MARK + lSeconds + SECONDS_MARK;
        }
        long lHours = getHours(lDelayTime);
        long lMinutes = getMinutes(lDelayTime);
        long lSeconds = getSeconds(lDelayTime);
        return "" + lHours + HOURS_MARK + lMinutes + MINUTES_MARK + lSeconds + SECONDS_MARK;
    }

    public static long getSeconds(long lDelayTime) {
        return lDelayTime % SECONDS_PER_MINUTE;
    }

    public static long getMinutes(long lDelayTime) {
        return (lDelayTime % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
    }

    public static long getHours(long lDelayTime) {
        return lDelayTime / SECONDS_PER_HOUR;
    }

    public void restart() {
        m_nStartTime = getCurrentTime();
        m_nPreviousTime = m_nStartTime;
    }

    public String getReport() {
        return " [total time " + getTotalTime() + " seconds] [delay " + getDelayTime() + " seconds]";
    }

    private static long getCurrentTime() {
        return (System.currentTimeMillis() / 1000);
    }

    private long getStartTime() {
        return m_nStartTime;
    }

    private long m_nStartTime = 0;
    private long m_nPreviousTime = 0;
}
