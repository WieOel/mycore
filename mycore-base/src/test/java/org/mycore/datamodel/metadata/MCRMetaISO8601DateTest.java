/**
 * 
 * $Revision$ $Date$
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/

package org.mycore.datamodel.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRTestCase;
import org.mycore.datamodel.common.MCRISO8601Date;
import org.mycore.datamodel.common.MCRISO8601Format;
import org.mycore.datamodel.common.MCRISO8601FormatChooser;

/**
 * This class is a JUnit test case for org.mycore.datamodel.metadata.MCRMeta8601Date.
 * 
 * @author Thomas Scheffler
 * @version $Revision$ $Date$
 *
 */
public class MCRMetaISO8601DateTest extends MCRTestCase {
    private static Logger LOGGER;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();//org.mycore.datamodel.metadata.MCRMetaISO8601Date
        setProperty(MCRISO8601Date.PROPERTY_STRICT_PARSING, "true", true);
        if (setProperty("log4j.logger.org.mycore.datamodel.metadata.MCRMetaISO8601Date", "INFO", false)) {
            //DEBUG will print a Stacktrace if we test for errors, but that's O.K.
            MCRConfiguration.instance().configureLogging();
        }
        if (LOGGER == null) {
            LOGGER = Logger.getLogger(MCRMetaISO8601DateTest.class);
        }
    }

    @Test
    public void formatChooser() {
        // test year
        String duration = "-16";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.YEAR_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        duration = "2006";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.YEAR_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        // test year-month
        duration = "2006-01";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.YEAR_MONTH_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        // test complete
        duration = "2006-01-18";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.COMPLETE_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        // test complete with hour and minutes
        duration = "2006-01-18T11:08Z";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.COMPLETE_HH_MM_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        duration = "2006-01-18T11:08+02:00";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.COMPLETE_HH_MM_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        // test complete with hour, minutes and seconds
        duration = "2006-01-18T11:08:20Z";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.COMPLETE_HH_MM_SS_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        duration = "2006-01-18T11:08:20+02:00";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.COMPLETE_HH_MM_SS_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        // test complete with hour, minutes, seconds and fractions of a second
        duration = "2006-01-18T11:08:20.1Z";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.COMPLETE_HH_MM_SS_SSS_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        duration = "2006-01-18T11:08:20.12Z";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.COMPLETE_HH_MM_SS_SSS_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        duration = "2006-01-18T11:08:20.123Z";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.COMPLETE_HH_MM_SS_SSS_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        duration = "2006-01-18T11:08:20.1+02:00";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.COMPLETE_HH_MM_SS_SSS_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        duration = "2006-01-18T11:08:20.12+02:00";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.COMPLETE_HH_MM_SS_SSS_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
        duration = "2006-01-18T11:08:20.123+02:00";
        assertEquals(duration + " test failed", getFormat(MCRISO8601FormatChooser.COMPLETE_HH_MM_SS_SSS_FORMAT),
            getFormat(MCRISO8601FormatChooser.getFormatter(duration, null)));
    }

    /*
     * Test method for
     * 'org.mycore.datamodel.metadata.MCRMetaTimestamp.getSecond()'
     */
    @Test
    public void setDate() {
        MCRMetaISO8601Date ts = new MCRMetaISO8601Date();
        String timeString = "1997-07-16T19:20:30.452300+01:00";
        LOGGER.debug(timeString);
        ts.setDate(timeString);
        assertNotNull("Date is null", ts.getDate());
        // this can be a different String, but point in time should be the same
        LOGGER.debug(ts.getISOString());
        ts.setFormat(MCRISO8601Format.COMPLETE_HH_MM);
        LOGGER.debug(ts.getISOString());
        // wrong date format for the following string should null the internal
        // date.
        timeString = "1997-07-16T19:20:30+01:00";
        LOGGER.debug(timeString);
        ts.setDate(timeString);
        assertNull("Date is not null", ts.getDate());
        ts.setFormat((String) null); // check auto format determination
        ts.setDate(timeString);
        assertNotNull("Date is null", ts.getDate());
        // check if shorter format declarations fail if String is longer
        ts.setFormat(MCRISO8601Format.YEAR);
        timeString = "1997-07";
        ts.setDate(timeString);
        assertNull("Date is not null", ts.getDate());
        LOGGER.debug(ts.getISOString());
        timeString = "01.12.1986";
        ts.setFormat((String) null);
        ts.setDate(timeString);
        assertNull("Date is not null", ts.getDate());
        setProperty("MCR.Metadata.SimpleDateFormat.StrictParsing", "false", true);
        setProperty("MCR.Metadata.SimpleDateFormat.Locales", "de_DE,en_US", true);
        ts.setFormat((String) null);
        ts.setDate(timeString);
        LOGGER.debug(ts.getISOString());
        timeString = "12/01/1986";
        ts.setDate(timeString);
        LOGGER.debug(ts.getISOString());
        //assertNotNull("Date is null", ts.getDate());
        setProperty("MCR.Metadata.SimpleDateFormat.StrictParsing", "true", true);
    }

    /*
     * Test method for
     * 'org.mycore.datamodel.metadata.MCRMetaTimestamp.getSecond()'
     */
    @Test
    public void createXML() {
        MCRMetaISO8601Date ts = new MCRMetaISO8601Date("servdate", "createdate", 0);
        String timeString = "1997-07-16T19:20:30.452300+01:00";
        ts.setDate(timeString);
        assertNotNull("Date is null", ts.getDate());
        Element export = ts.createXML();
        if (LOGGER.isDebugEnabled()) {
            XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
            StringWriter sw = new StringWriter();
            try {
                xout.output(export, sw);
                LOGGER.debug(sw.toString());
            } catch (IOException e) {
                LOGGER.warn("Failure printing xml result", e);
            }
        }
    }

    @Test
    public void getFormat() {
        MCRMetaISO8601Date ts = new MCRMetaISO8601Date();
        assertNull("Format used is not Null", ts.getFormat());
        ts.setFormat(MCRISO8601Format.COMPLETE);
        assertEquals("Set format differs from get format", MCRISO8601Format.COMPLETE, ts.getFormat());
    }

    @Test
    public void getDate() {
        MCRMetaISO8601Date ts = new MCRMetaISO8601Date();
        assertNull("Date is not Null", ts.getDate());
        Date dt = new Date();
        ts.setDate(dt);
        assertNotNull("Date is Null", ts.getDate());
        assertEquals("Set date differs from get date", dt, ts.getDate());
    }

    @Test
    public void getISOString() {
        MCRMetaISO8601Date ts = new MCRMetaISO8601Date();
        assertNull("Date is not Null", ts.getISOString());
        Date dt = new Date();
        ts.setDate(dt);
        assertNotNull("Date is Null", ts.getISOString());
    }

    @Test
    public void setFromDOM() {
        MCRMetaISO8601Date ts = new MCRMetaISO8601Date();
        Element datum = new Element("datum");
        datum.setAttribute("inherited", "0").setText("2006-01-23");
        ts.setFromDOM(datum);
        assertEquals("Dates not equal", "2006-01-23", ts.getISOString());
        datum.setAttribute("format", MCRISO8601Format.COMPLETE_HH_MM.toString());
        ts.setFromDOM(datum);
        assertNull("Date should be null", ts.getDate());
        assertEquals("Format should be set by jdom", MCRISO8601Format.COMPLETE_HH_MM, ts.getFormat());
    }

    private String getFormat(DateTimeFormatter df) {
        if (df == null || df == MCRISO8601FormatChooser.COMPLETE_HH_MM_SS_SSS_FORMAT
            || df == MCRISO8601FormatChooser.UTC_COMPLETE_HH_MM_SS_SSS_FORMAT) {
            return MCRISO8601Format.COMPLETE_HH_MM_SS_SSS.toString();
        } else if (df == MCRISO8601FormatChooser.COMPLETE_HH_MM_SS_FORMAT
            || df == MCRISO8601FormatChooser.UTC_COMPLETE_HH_MM_SS_FORMAT) {
            return MCRISO8601Format.COMPLETE_HH_MM_SS.toString();
        } else if (df == MCRISO8601FormatChooser.COMPLETE_HH_MM_FORMAT
            || df == MCRISO8601FormatChooser.UTC_COMPLETE_HH_MM_FORMAT) {
            return MCRISO8601Format.COMPLETE_HH_MM.toString();
        } else if (df == MCRISO8601FormatChooser.COMPLETE_FORMAT || df == MCRISO8601FormatChooser.UTC_COMPLETE_FORMAT) {
            return MCRISO8601Format.COMPLETE.toString();
        } else if (df == MCRISO8601FormatChooser.YEAR_MONTH_FORMAT
            || df == MCRISO8601FormatChooser.UTC_YEAR_MONTH_FORMAT) {
            return MCRISO8601Format.YEAR_MONTH.toString();
        } else if (df == MCRISO8601FormatChooser.YEAR_FORMAT || df == MCRISO8601FormatChooser.UTC_YEAR_FORMAT) {
            return MCRISO8601Format.YEAR.toString();
        } else {
            return MCRISO8601Format.COMPLETE_HH_MM_SS_SSS.toString();
        }
    }

}