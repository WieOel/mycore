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

package org.mycore.common.content.transformer;

import org.jdom.output.Format;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.common.content.MCRXMLContent;

/**
 * Transforms xml content in pretty, UTF-8 encoded format.
 *  
 * @author Frank L\u00FCtzenkirchen
 */
public class MCRToPrettyXML extends MCRContentTransformer {

    @Override
    public MCRContent transform(MCRContent source) throws Exception {
        MCRXMLContent content = (source instanceof MCRXMLContent ? (MCRXMLContent) source : new MCRJDOMContent(source.asXML()));
        content.setFormat(Format.getPrettyFormat().setEncoding("UTF-8"));
        return content;
    }
    
    @Override
    protected String getDefaultExtension() {
        return "xml";
    }
}
