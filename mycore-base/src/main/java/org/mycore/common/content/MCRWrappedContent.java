/*
 * $Id$
 * $Revision: 5697 $ $Date: Jan 2, 2013 $
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

package org.mycore.common.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Source;

import org.apache.commons.vfs2.FileObject;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.mycore.datamodel.ifs.MCRContentInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * @author Thomas Scheffler (yagee)
 *
 */
public abstract class MCRWrappedContent extends MCRContent {

    private static final Logger LOGGER = Logger.getLogger(MCRWrappedContent.class);

    private MCRContent baseContent;

    public MCRContent getBaseContent() {
        return baseContent;
    }

    protected void setBaseContent(MCRContent baseContent) {
        LOGGER.debug("Wrapped " + baseContent.getClass().getCanonicalName() + ": " + baseContent.getSystemId());
        this.baseContent = baseContent;
    }

    /* (non-Javadoc)
     * @see org.mycore.common.content.MCRContent#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return getBaseContent().getInputStream();
    }

    @Override
    void setSystemId(String systemId) {
        getBaseContent().setSystemId(systemId);
    }

    @Override
    public String getSystemId() {
        return getBaseContent().getSystemId();
    }

    @Override
    public MCRContentInputStream getContentInputStream() throws IOException {
        return getBaseContent().getContentInputStream();
    }

    @Override
    public Source getSource() throws IOException {
        return getBaseContent().getSource();
    }

    @Override
    public void sendTo(OutputStream out) throws IOException {
        getBaseContent().sendTo(out);
    }

    @Override
    public void sendTo(OutputStream out, boolean close) throws IOException {
        getBaseContent().sendTo(out, close);
    }

    @Override
    public InputSource getInputSource() throws IOException {
        return getBaseContent().getInputSource();
    }

    @Override
    public void sendTo(File target) throws IOException {
        getBaseContent().sendTo(target);
    }

    @Override
    public void sendTo(FileObject target) throws IOException {
        getBaseContent().sendTo(target);
    }

    @Override
    public byte[] asByteArray() throws IOException {
        return getBaseContent().asByteArray();
    }

    @Override
    public String asString() throws IOException, UnsupportedEncodingException {
        return getBaseContent().asString();
    }

    @Override
    public String asString(String encoding) throws IOException, UnsupportedEncodingException {
        return getBaseContent().asString(encoding);
    }

    @Override
    public Document asXML() throws JDOMException, IOException, SAXParseException {
        return getBaseContent().asXML();
    }

    @Override
    public MCRContent ensureXML() throws IOException, JDOMException, SAXParseException {
        return getBaseContent().ensureXML();
    }

    @Override
    public String getDocType() throws IOException {
        return getBaseContent().getDocType();
    }

    @Override
    public boolean isReusable() {
        return getBaseContent().isReusable();
    }

    @Override
    public MCRContent getReusableCopy() throws IOException {
        return getBaseContent().getReusableCopy();
    }

}
