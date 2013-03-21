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

package org.mycore.frontend.xeditor.target;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.frontend.xeditor.MCREditorSession;

/**
 * @author Frank L\u00FCtzenkirchen
 */
public class MCRDebugTarget implements MCREditorTarget {

    @Override
    public void handleSubmission(ServletContext context, MCRServletJob job, MCREditorSession session, String parameter) throws IOException {
        job.getResponse().setContentType("text/html; charset=UTF-8");

        PrintWriter out = job.getResponse().getWriter();
        out.println("<html><body>");
        out.println("<p><pre>");

        for (Enumeration<String> parameters = job.getRequest().getParameterNames(); parameters.hasMoreElements();) {
            String name = parameters.nextElement();
            for (String value : job.getRequest().getParameterValues(name)) {
                out.println(name + " = " + value);
            }
        }

        out.println("</pre></p>");
        out.println("<p><pre>");

        Format fmt = Format.getPrettyFormat().setLineSeparator("\n").setOmitDeclaration(false);
        XMLOutputter outputter = new XMLOutputter(fmt);
        out.println(outputter.outputString(session.getEditedXML()));

        out.println("</pre></p>");
        out.println("</body></html>");
        out.close();
    }
}
