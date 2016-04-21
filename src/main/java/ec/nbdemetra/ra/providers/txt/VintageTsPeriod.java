package ec.nbdemetra.ra.providers.txt;

import ec.nbdemetra.ra.timeseries.VintageFrequency;
import ec.tstoolkit.design.Development;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.IPeriod;
import ec.tstoolkit.timeseries.Month;
import ec.tstoolkit.timeseries.TsException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Represents a regular period (corresponding to a TsFrequency) of the year. A
 * TsPeriod is fully identified by its frequency, its (0-based) position in the
 * year and its year. It should be noted that the frequency of a period cannot
 * be changed after its creation
 *
 * @author bennouha
 */
@Development(status = Development.Status.Alpha)
public class VintageTsPeriod implements Serializable, Cloneable, IPeriod,
        Comparable<VintageTsPeriod> {

    /**
     *
     */
    private static final long serialVersionUID = 7847770015060071968L;
    private static String[] g_sm = {"jan", "feb", "mar", "apr", "may", "jun",
        "jul", "aug", "sep", "oct", "nov", "dec"};

    static long calcEndMonth(final int year, final int month) {
        int day = Day.getNumberOfDaysByMonth(year, month);
        return new GregorianCalendar(year, month, day).getTime().getTime();
    }

    static int calcId(final int freq, final int year, final int placeinyear) {
        return (year - 1970) * freq + placeinyear;
    }

    static long calcStartMonth(final int year, final int month) {
        return new GregorianCalendar(year, month, 1).getTime().getTime();
    }

    /**
     * Gets a description (independent of the year) of the period corresponding
     * to a frequency and to a 0-based position. For example: January.. for
     * monthly periods Q1... for quarterly periods
     *
     * @param freq The frequency of the period
     * @param pos Its 0-based position in the year
     * @return The corresponding text
     */
    public static String formatPeriod(VintageFrequency freq, int pos) {
        if (freq == VintageFrequency.Monthly) {
            return Month.valueOf(pos).toString();
        } else {
            StringBuilder builder = new StringBuilder();
            switch (freq) {
                case Quarterly:
                    builder.append('Q');
                    break;
                case Yearly:
                    builder.append('Y');
                    break;

            }
            builder.append(pos + 1);
            return builder.toString();
        }
    }

    /**
     * Gets a short description (independent of the year) of the period
     * corresponding to a frequency and a 0-based position. For example: "jan"
     * for the first monthly period of the year. "Q1" for the first quarter...
     *
     * @param freq The given frequency
     * @param pos The 0-based position of the period
     * @return The short description
     * @see #formatPeriod(TSFrequency, int).
     */
    public static String formatShortPeriod(VintageFrequency freq, int pos) {
        if (freq == VintageFrequency.Monthly) {
            return g_sm[pos];
        } else {
            return formatPeriod(freq, pos);
        }
    }
    private final VintageFrequency m_freq;
    private int m_id;

    /**
     * Creates a new TsPeriod, corresponding to a given frequency. By default,
     * the year and period are 1970, 0
     *
     * @param frequency Frequency of the period
     */
    public VintageTsPeriod(final VintageFrequency frequency) {
        m_freq = frequency;
    }

    /**
     * Creates a new TsPeriod, having a given frequency and containing a given
     * date.
     *
     * @param frequency Frequency of the period.
     * @param date A date in the period
     */
    public VintageTsPeriod(final VintageFrequency frequency, final Date date) {
        m_freq = frequency;
        set(date);
    }

    /**
     * Creates a new TsPeriod, having a given frequency and containing a given
     * day.
     *
     * @param frequency Frequency of the period.
     * @param day A day that belongs to the period. Can be any day of the
     * period.
     */
    public VintageTsPeriod(final VintageFrequency frequency, final Day day) {
        m_freq = frequency;
        set(day);
    }

    /**
     * *
     * Creates a period corresponding to the given year
     *
     * @param y Year
     * @return The year
     */
    public static VintageTsPeriod year(int y) {
        return new VintageTsPeriod(VintageFrequency.Yearly, y, 0);
    }

    VintageTsPeriod(final VintageFrequency frequency, final int id) {
        m_freq = frequency;
        m_id = id;
    }

    /**
     * Creates a new TsPeriod.
     *
     * @param frequency Frequency of the period
     * @param year Year of the period
     * @param position 0-based position of the period in the year
     */
    public VintageTsPeriod(final VintageFrequency frequency, final int year,
            final int position) {
        m_freq = frequency;
        set(year, position);
    }

    /**
     * Creates a new TsPeriod, having a given frequency and containing a given
     * period.
     *
     * @param frequency The frequency of the new object
     * @param p The period that is contained in the new object.
     * @exception An exception is thrown when the frequencies are incompatible
     * (i.e. the new period can't contain the given period).
     */
    public VintageTsPeriod(final VintageFrequency frequency, final VintageTsPeriod p) {
        int freq = frequency.intValue(), pfreq = p.m_freq.intValue();
        if (pfreq % freq != 0) {
            throw new TsException(TsException.INCOMPATIBLE_FREQ);
        }
        m_freq = frequency;
        m_id = p.m_id * freq / pfreq;
    }

    @Override
    public VintageTsPeriod clone() {
        VintageTsPeriod period = null;
        try {
            period = (VintageTsPeriod) super.clone();
        } catch (CloneNotSupportedException err) {
        }
        return period;
    }

    /**
     * Compare to periods. The periods can have different frequencies.
     *
     * @param other The compared period.
     * @return 0 is returned if the periods are equal. 1 is returned if the
     * second period is strictly before the current period. -1 is returned if
     * the second period is strictly after the current period.
     */
    @Override
    public int compareTo(final VintageTsPeriod other) {
        if (other.m_freq == m_freq) {
            if (m_id == other.m_id) {
                return 0;
            } else if (m_id < other.m_id) {
                return -1;
            } else {
                return 1;
            }
        }
        if (lastday().compareTo(other.firstday()) < 0) {
            return -1;
        }
        if (other.firstday().compareTo(lastday()) > 0) {
            return 1;
        }
        throw new TsException(TsException.INCOMPATIBLE_FREQ);
    }

    /**
     * Verifies that a date belongs to the period.
     *
     * @param dt Tested date
     * @return true if the given date is inside the period, false otherwise.
     */
    @Override
    public boolean contains(final Date dt) {
        VintageTsPeriod tmp = new VintageTsPeriod(m_freq);
        tmp.set(dt);
        return tmp.m_id == m_id;
    }

    /**
     * Verifies that a day belongs to the period.
     *
     * @param day Tested day
     * @return true if the given day belongs to the period (including first and
     * last day), false otherwise.
     */
    public boolean contains(final Day day) {
        VintageTsPeriod tmp = new VintageTsPeriod(m_freq);
        tmp.set(day);
        return tmp.m_id == m_id;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof VintageTsPeriod && equals((VintageTsPeriod) obj));
    }

    public boolean equals(VintageTsPeriod other) {
        return (m_freq == other.m_freq) && (m_id == other.m_id);
    }

    /**
     * Returns the first day of the period
     *
     * @return The first day
     */
    @Override
    public Day firstday() {
        int ifreq = m_freq.intValue();
        int c = 12 / ifreq;
        return new Day(getYear(), Month.valueOf(getPosition() * c), 0);
    }

    /**
     * Gets the frequency of the period.
     *
     * @return The frequency.
     */
    public VintageFrequency getFrequency() {
        return m_freq;
    }

    /**
     * Gets a description (independent of the year) of the period corresponding
     * to a frequency and a position.
     *
     * @return The description
     * @see #formatShortPeriod(TSFrequency, int).
     */
    public String getPeriodString() {
        return formatPeriod(m_freq, getPosition());
    }

    /**
     * Gets the 0-based position of the period in the year (for instance
     * February has position 1 in a monthly frequency).
     *
     * @return 0-based position of the period in the year. The returned value is
     * in the range [0, getFrequency().intValue()[
     */
    public int getPosition() {
        int ifreq = m_freq.intValue();
        if (m_id >= 0) {
            return m_id % ifreq;
        } else {
            return ifreq - 1 + (1 + m_id) % ifreq;
        }
    }

    /**
     * Gets the year the period belongs to.
     *
     * @return Year of the period.
     */
    public int getYear() {
        if (m_id >= 0) {
            return 1970 + m_id / m_freq.intValue();
        } else {
            return 1969 + (1 + m_id) / m_freq.intValue();
        }
    }

    @Override
    public int hashCode() {
        return m_id;
    }

    int id() {
        return m_id;
    }

    /**
     * Checks that the period is after a given date.
     *
     * @param date The reference date.
     * @return true if the start of the period is strictly after the given date,
     * false otherwise
     * @see #isBefore(Date)
     */
    public boolean isAfter(final Date date) {
        return firstday().toCalendar().getTime().compareTo(date) > 0;
    }

    /**
     * Checks that the period is after a given day.
     *
     * @param day The given day
     * @return true if the start of the period is strictly after the given date,
     * false otherwise
     */
    public boolean isAfter(final Day day) {
        return firstday().compareTo(day) > 0;
    }

    /**
     * Verifies that a period is after another one
     *
     * @param rp The reference period
     * @return Is equivalent to compareTo(rp) &gt 0;
     * @see #compareTo(TsPeriod)
     */
    public boolean isAfter(final VintageTsPeriod rp) {
        return compareTo(rp) > 0;
    }

    /**
     * Checks that the period is before a given date.
     *
     * @param date The reference date.
     * @return true if the end of the period is strictly before the given date,
     * false otherwise
     * @see #isAfter(Date)
     */
    public boolean isBefore(final Date date) {
        return lastday().toCalendar().getTime().compareTo(date) < 0;
    }

    /**
     * Checks that the period is before a given day.
     *
     * @param day The reference day.
     * @return true if the end of the period is strictly before the given day,
     * false otherwise
     */
    public boolean isBefore(final Day day) {
        return lastday().compareTo(day) < 0;
    }

    /**
     * Verifies that a period is before another one.
     *
     * @param rp The reference period
     * @return Is equivalent to compareTo(rp) &lt 0;
     * @see #compareTo(TsPeriod)
     */
    public boolean isBefore(final VintageTsPeriod rp) {
        return compareTo(rp) < 0;
    }

    /**
     * Checks that the current period is inside a given period
     *
     * @param p The containing period.
     * @return true if the current period is inside p, else otherwise
     */
    public boolean isInside(final VintageTsPeriod p) {
        int ifreq = m_freq.intValue(), pfreq = p.m_freq.intValue();
        if (pfreq > ifreq) {
            return false;
        }
        // express in months.
        if (ifreq == pfreq) {
            return m_id == p.m_id;
        }
        int id0 = m_id * 12 / ifreq;
        int id1 = p.m_id * 12 / pfreq;
        if (id0 < id1) {
            return false;
        }
        return id0 + 12 / ifreq <= id1 + 12 / pfreq;
    }

    /**
     * Verifies that a period is not after the second one
     *
     * @param rp The reference period
     * @return Is equivalent to compareTo(rp) &lt= 0;
     * @see #compareTo(TsPeriod)
     */
    public boolean isNotAfter(final VintageTsPeriod rp) {
        return compareTo(rp) <= 0;
    }

    /**
     * Verifies that a period is not before the second one
     *
     * @param rp The reference period
     * @return Is equivalent to compareTo(rp) &gt= 0;
     * @see #compareTo(TsPeriod)
     */
    public boolean isNotBefore(final VintageTsPeriod rp) {
        // if (lp == null)
        // throw new ArgumentNullException("lp");
        return compareTo(rp) >= 0;
    }

    @Override
    public Day lastday() {
        int ifreq = m_freq.intValue();
        int c = 12 / ifreq;
        int y = getYear();
        int month = getPosition() * c + c - 1;
        return new Day(y, Month.valueOf(month), Day.getNumberOfDaysByMonth(y,
                month) - 1);
    }

    /**
     * Gets the middle of the period
     *
     * @return The date corresponding to the middle of the period. The date can
     * be in the middle of a day.
     */
    public Date middle() {
        int ifreq = m_freq.intValue();
        int c = 12 / ifreq;
        int y = getYear(), p = getPosition();
        long l0 = calcStartMonth(y, p * c);
        long l1 = calcEndMonth(y, p * c + c - 1);
        return new Date((l0 + l1) / 2);
    }

    /**
     * Subtracts a number of periods to the current one. A new object is
     * created. The current object is not modified.
     *
     * @param nperiods The number of periods
     * @return Returns this - nperiods.
     */
    public VintageTsPeriod minus(int nperiods) {
        return new VintageTsPeriod(m_freq, m_id - nperiods);
    }

    /**
     * Number of periods between two periods with the same frequency.
     *
     * @return Number of period between the current Object and p. > 0 if the
     * current Object is after p, = 0 if both objects are equals, < 0 if the
     * current Object is before p. @param p The period used in the comparison.
     * @throws A TsException is thrown when the frequencies of the periods are
     * di
     * fferent .
     */
    public int minus(final VintageTsPeriod p) {
        if (m_freq != p.m_freq) {
            throw new TsException(TsException.INCOMPATIBLE_FREQ);
        }
        return m_id - p.m_id;
    }

    /**
     * Moves a period by n periods.
     *
     * @param nperiods Any integer. The number of periods added (+) or removed
     * (-) from the initial position.
     */
    public void move(final int nperiods) {
        m_id += nperiods;
    }

    /**
     * Adds a number of periods to the current one. A new object is created. The
     * current object is not modified.
     *
     * @param nperiods
     * @return Returns this + nperiods.
     */
    public VintageTsPeriod plus(int nperiods) {
        return new VintageTsPeriod(m_freq, m_id + nperiods);
    }

    /**
     * Initialises a period of a given frequency with a date
     *
     * @param date Date that the period must contain.
     */
    public final void set(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int ifreq = m_freq.intValue();
        m_id = (cal.get(Calendar.YEAR) - 1970) * ifreq
                + cal.get(Calendar.MONTH) / (12 / ifreq);
    }

    /**
     * Initialises a period of a given frequency with a day
     *
     * @param day Day that the period must contain.
     */
    public final void set(final Day day) {
        set(day.toCalendar().getTime());
    }

    /**
     * Initialises a period of a given frequency with its year and its position
     * in the year.
     *
     * @param year Year the period belongs to.
     * @param position Place of the period in the year. 0-indexed value (must be
     * in the range [0, getFrequency()[.
     */
    public final void set(int year, final int position) {
        int ifreq = m_freq.intValue();
        if ((position < 0) || (position >= ifreq)) {
            throw new TsException(TsException.INVALID_PERIOD);
        }
        m_id = calcId(ifreq, year, position);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(32);
        int p = getPosition();
        int y = getYear();

        int ifreq = m_freq.intValue();

        switch (ifreq) {
            case 2:
            case 3:
            case 4: {
                switch (p) {
                    case 0:
                        buffer.append('I');
                        break;
                    case 1:
                        buffer.append("II");
                        break;
                    case 2:
                        buffer.append("III");
                        break;
                    case 3:
                        buffer.append("IV");
                        break;
                }
                buffer.append('-');
                break;
            }
            case 6:
            case 12:
                buffer.append(1 + p).append('-');
                break;

        }
        buffer.append(y);
        return buffer.toString();
    }
}
