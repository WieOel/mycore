/**
 * $RCSfile$
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

package org.mycore.backend.cm8;

import com.ibm.mm.sdk.server.*;
import com.ibm.mm.sdk.common.*;

import org.apache.log4j.Logger;

import org.mycore.common.MCRPersistenceException;
import org.mycore.datamodel.metadata.MCRMetaDefault;

/**
 * This class implements the interface for the CM8 persistence layer for
 * the data model type MetaCorporation.
 *
 * @author Jens Kupferschmidt
 * @version $Revision$ $Date$
 **/

public class MCRCM8MetaCorporation implements DKConstantICM, MCRCM8MetaInterface
{

/**
 * This method create a DKComponentTypeDefICM to create a complete
 * ItemType from the configuration.
 *
 * @param element  a MCR datamodel element as JDOM Element
 * @param connection the connection to the CM8 datastore
 * @param dsDefICM the datastore definition
 * @param prefix the prefix name for the item type
 * @param textindex the definition of the text search index
 * @param textserach the flag to use textsearch as string
 * @return a DKComponentTypeDefICM for the MCR datamodel element
 * @exception MCRPersistenceException general Exception of MyCoRe
 **/
public DKComponentTypeDefICM createItemType(org.jdom.Element element,
  DKDatastoreICM connection, DKDatastoreDefICM dsDefICM, String prefix,
  DKTextIndexDefICM textindex, String textsearch) throws MCRPersistenceException
  {
  Logger logger = MCRCM8ConnectionPool.getLogger();
  String subtagname = prefix+(String)element.getAttribute("name").getValue();
  // String length
  String subtaglen = (String)element.getAttribute("length").getValue();
  int len = MCRMetaDefault.DEFAULT_STRING_LENGTH;
  try {
    len = Integer.parseInt(subtaglen); }
  catch (NumberFormatException e) {
    throw new MCRPersistenceException(e.getMessage(),e); }
  // Text search option
  boolean ts = false;
  try {
    if (textsearch.toLowerCase().equals("true")) { ts = true; } }
  catch (Exception e) { }
  logger.debug("Set TextSearch for "+subtagname+" to "+ts);

  DKComponentTypeDefICM lt = new DKComponentTypeDefICM(connection);
  try {
    // create component child
    lt.setName(subtagname);
    lt.setDeleteRule(DK_ICM_DELETE_RULE_CASCADE);
    // add lang attribute
    DKAttrDefICM attr = (DKAttrDefICM) dsDefICM.retrieveAttr(prefix+"lang");
    attr.setNullable(true);
    attr.setUnique(false);
    lt.addAttr(attr);
    // add type attribute
    attr = (DKAttrDefICM) dsDefICM.retrieveAttr(prefix+"type");
    attr.setNullable(true);
    attr.setUnique(false);
    lt.addAttr(attr);

    // add the corporation child component name
    String name = prefix+"name";
    DKComponentTypeDefICM it = new DKComponentTypeDefICM(connection);
    it.setName(name);
    it.setDeleteRule(DK_ICM_DELETE_RULE_CASCADE);
    if (!MCRCM8ItemType.createAttributeVarChar(connection,name,len,ts)) {
      logger.warn( "CM8 Datastore Creation attribute "+
        name+" already exists."); }
    attr = (DKAttrDefICM) dsDefICM.retrieveAttr(name);
    attr.setNullable(true);
    attr.setUnique(false);
    it.addAttr(attr);
    lt.addSubEntity(it);

    // add the corporation child component nickname
    name = prefix+"nickname";
    it = new DKComponentTypeDefICM(connection);
    it.setName(name);
    it.setDeleteRule(DK_ICM_DELETE_RULE_CASCADE);
    if (!MCRCM8ItemType.createAttributeVarChar(connection,name,len,ts)) {
      logger.warn( "CM8 Datastore Creation attribute "+
        name+" already exists."); }
    attr = (DKAttrDefICM) dsDefICM.retrieveAttr(name);
    attr.setNullable(true);
    attr.setUnique(false);
    it.addAttr(attr);
    lt.addSubEntity(it);

    // add the corporation child component parent
    name = prefix+"parent";
    it = new DKComponentTypeDefICM(connection);
    it.setName(name);
    it.setDeleteRule(DK_ICM_DELETE_RULE_CASCADE);
    if (!MCRCM8ItemType.createAttributeVarChar(connection,name,len,ts)) {
      logger.warn( "CM8 Datastore Creation attribute "+
        name+" already exists."); }
    attr = (DKAttrDefICM) dsDefICM.retrieveAttr(name);
    attr.setNullable(true);
    attr.setUnique(false);
    it.addAttr(attr);
    lt.addSubEntity(it);

    // add the corporation child component property
    name = prefix+"property";
    it = new DKComponentTypeDefICM(connection);
    it.setName(name);
    it.setDeleteRule(DK_ICM_DELETE_RULE_CASCADE);
    if (!MCRCM8ItemType.createAttributeVarChar(connection,name,len,ts)) {
      logger.warn( "CM8 Datastore Creation attribute "+
        name+" already exists."); }
    attr = (DKAttrDefICM) dsDefICM.retrieveAttr(name);
    attr.setNullable(true);
    attr.setUnique(false);
    it.addAttr(attr);
    lt.addSubEntity(it);
    }
  catch (Exception e) {
    throw new MCRPersistenceException(e.getMessage(),e); }
  return lt;
  }

}
