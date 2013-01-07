/*
 * $Revision$ 
 * $Date$
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

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Source;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMSource;

/**
 * Reads MCRContent from a JDOM XML document.
 * 
 * @author Frank L\u00FCtzenkichen
 */
public class MCRJDOMContent extends MCRXMLContent {

    private Document jdom;

    /**
     * @param jdom the JDOM XML document to read from 
     */
    public MCRJDOMContent(Document jdom) {
        this.jdom = jdom;
        super.docType = jdom.getDocType() == null ? jdom.getRootElement().getName() : jdom.getDocType().getElementName();
    }

    /**
     * Alternative constructor for newly created root elements
     * that do not have a Document parent yet, which is a very 
     * common use case.
     * 
     * @param jdom the JDOM XML root element to read from 
     */
    public MCRJDOMContent(Element jdom) {
        this(new Document(jdom));
    }

    @Override
    public Source getSource() {
        JDOMSource source = new JDOMSource(jdom);
        source.setSystemId(systemId);
        return source;
    }

    @Override
    public void sendTo(OutputStream out) throws IOException {
        if (jdom == null) {
            throw new IOException("JDOM document is null and cannot be written to OutputStream");
        }
        new XMLOutputter(format).output(jdom, out);
    }

    @Override
    public Document asXML() {
        return jdom;
    }
}
