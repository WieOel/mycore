/**
 * $RCSfile: MCROAIQueryService.java,v $
 * $Revision: 1.0 $ $Date: 2003/01/21 10:18:25 $
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

package org.mycore.services.oai;

import org.mycore.services.oai.MCROAIQuery;

/**
 * @author Werner Gresshoff
 *
 * @version $Revision: 1.0 $ $Date: 2003/01/21 10:18:25 $
 *
 * This is the MyCoRe-Implementation of the <i>MCROAIQuery</i>-Interface.
 */
public class MCROAIQueryService implements MCROAIQuery {

	/**
	 * Method exists. Checks if the given ID exists in the data repository
	 * @param id The ID to be checked
	 * @return boolean
	 */
	public boolean exists(String id) {
		return MCRObject.existInDatastore(id);
	}
}
