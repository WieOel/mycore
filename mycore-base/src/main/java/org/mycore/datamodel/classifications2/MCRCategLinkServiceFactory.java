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
package org.mycore.datamodel.classifications2;

import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.classifications2.impl.MCRCategLinkServiceImpl;

/**
 * @author Thomas Scheffler (yagee)
 * 
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class MCRCategLinkServiceFactory {
    private static final String STANDARD_IMPL = MCRCategLinkServiceImpl.class.getCanonicalName();

    private static MCRCategLinkService instance = MCRConfiguration.instance().<MCRCategLinkService> getInstanceOf("MCR.Category.LinkService", STANDARD_IMPL);

    /**
     * Returns an instance of a MCRCategoryDAO implementator.
     */
    public static MCRCategLinkService getInstance() {
        return instance;
    }

}
