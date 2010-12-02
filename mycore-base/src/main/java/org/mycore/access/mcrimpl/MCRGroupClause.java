/*
 * 
 * $Revision$ $Date$
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

package org.mycore.access.mcrimpl;

import org.jdom.Attribute;
import org.jdom.Element;
import org.mycore.common.MCRSessionMgr;
import org.mycore.parsers.bool.MCRCondition;
import org.mycore.parsers.bool.MCRConditionVisitor;

/**
 * Implementation of a (group xy) clause
 * 
 * @author Matthias Kramm
 * @author Mathias Fricke
 */
class MCRGroupClause implements MCRCondition {

    private String groupname;

    private boolean not;

    MCRGroupClause(String group, boolean not) {
        groupname = group;
        this.not = not;
    }

    public boolean evaluate(Object o) {
        return MCRSessionMgr.getCurrentSession().getUserInformation().isUserInRole(groupname) ^ not;
    }

    @Override
    public String toString() {
        return "group" + (not ? " != " : " = ") + groupname + " ";
    }

    public Element toXML() {
        Element cond = new Element("condition");
        cond.setAttribute("field", "group");
        cond.setAttribute("operator", (not ? "!=" : "="));
        cond.setAttribute("value", groupname);
        return cond;
    }

    public Element info() {
        Element el = new Element("info");
        el.setAttribute(new Attribute("type", "GROUP"));
        return el;
    }

    public void accept(MCRConditionVisitor visitor) {
        visitor.visitType(info());
    }
};
