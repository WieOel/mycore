/*
 * $Id$
 * $Revision: 5697 $ $Date: Dec 6, 2013 $
 *
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
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
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */

package org.mycore.common.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;

import org.apache.commons.io.input.NullInputStream;
import org.apache.log4j.Logger;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRFileContent;
import org.mycore.common.content.MCRURLContent;

/**
 * {@link InputStream} that includes all properties from {@link MCRRuntimeComponentDetector#getAllComponents()} and <strong>mycore.properties</strong>.
 * 
 * Use system property <code>MCR.Configuration.File</code> to configure alternative property file.
 * @author Thomas Scheffler (yagee)
 * @since 2013.12
 */
public class MCRConfigurationInputStream extends SequenceInputStream {

    private static final byte[] lbr = "\n".getBytes();

    public MCRConfigurationInputStream() throws IOException {
        super(getInputStreams());
    }

    private static Enumeration<? extends InputStream> getInputStreams() throws IOException {
        LinkedList<InputStream> cList = new LinkedList<>();
        for (MCRComponent component : MCRRuntimeComponentDetector.getAllComponents()) {
            InputStream is = component.getPropertyStream();
            if (is != null) {
                cList.add(is);
                //workaround if last property is not terminated with line break
                cList.add(new ByteArrayInputStream(lbr));
            }
        }
        InputStream propertyStream = getPropertyStream();
        if (propertyStream != null) {
            cList.add(propertyStream);
        }
        if (cList.isEmpty()) {
            cList.add(new NullInputStream(0));
        }
        return Collections.enumeration(cList);
    }

    private static InputStream getPropertyStream() throws IOException {
        String filename = System.getProperty("MCR.Configuration.File", "mycore.properties");
        File mycoreProperties = new File(filename);
        MCRContent input = null;
        if (mycoreProperties.canRead()) {
            input = new MCRFileContent(mycoreProperties);
        } else {
            URL url = MCRConfigurationInputStream.class.getClassLoader().getResource(filename);
            if (url == null) {
                String errorMsg = "Could not load: " + filename;
                if (Logger.getRootLogger() != null) {
                    Logger logger = Logger.getLogger(MCRConfigurationInputStream.class);
                    logger.warn(errorMsg);
                } else {
                    System.err.printf("WARN: %s\n", errorMsg);
                }
            } else {
                input = new MCRURLContent(url);
            }
        }
        return input == null ? null : input.getInputStream();
    }

}