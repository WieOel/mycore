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

package mycore.datamodel;

import java.util.*;
import mycore.common.MCRConfiguration;
import mycore.common.MCRConfigurationException;
import mycore.common.MCRException;
import mycore.common.MCRUtils;

/**
 * This class implements all methode for handling one object metadata part.
 * This class uses only metadata type classes of the general 
 * datamodel code of MyCoRe.
 *
 * @author Jens Kupferschmidt
 * @author Mathias Hegner
 * @version $Revision$ $Date$
 **/
public class MCRObjectMetadata
{
// common data
private static String NL = 
  new String((System.getProperties()).getProperty("line.separator"));;
private String default_lang = null;

// metadata list
private ArrayList meta_list = null;
private ArrayList tag_names = null;

/**
 * This is the constructor of the MCRObjectMetadata class. It set the default
 * language for all metadata to the value from the configuration propertie
 * <em>MCR.metadata_default_lang</em>.
 *
 * @exception MCRConfigurationException
 *                              a special exception for configuartion data
 */
public MCRObjectMetadata() throws MCRConfigurationException
  {
  default_lang = MCRConfiguration.instance()
    .getString("MCR.metadata_default_lang");
  meta_list = new ArrayList();
  tag_names = new ArrayList();
  }

/**
 * <em>size</em> returns the number of tag names in the ArrayList.
 * 
 * @return int  number of tags and meta elements
 */
public int size () { return tag_names.size(); }

/**
 * <em>tagName</em> returns the tag name at a given index.
 * 
 * @param i           given index
 * @return String     the associated tag name
 */
public String tagName (int i) { return (String) tag_names.get(i); }

/**
 * <em>getHeritableMetadata</em> returns an instance of MCRObjectMetadata
 * containing all the heritable MetaElement's of this object.
 * 
 * @return MCRObjectMetadata    the heritable part of this MCRObjectMetadata
 * @exception MCRConfigurationException
 */
public MCRObjectMetadata getHeritableMetadata ()
	throws MCRConfigurationException
{
	MCRObjectMetadata heritMeta = new MCRObjectMetadata ();
	int i, n = size();
	MCRMetaElement metaElem = null;
	for (i = 0; i < n; ++i)
	{
		metaElem = (MCRMetaElement) meta_list.get(i);
		if (metaElem.getHeritable())
			heritMeta.setMetadataElement(metaElem, tagName(i));
	}
	return heritMeta;
}

/**
 * This methode return the MCRMetaElement selected by tag.
 * If this was not found, null was returned.
 *
 * @return the MCRMetaElement for the tag
 **/
public final MCRMetaElement getMetadataElement(String tag)
  {
  if ((tag == null) || ((tag = tag.trim()).length() ==0)) { return null; }
  int len = tag_names.size();
  for (int i = 0; i < len; i++) {
    if (((String)tag_names.get(i)).equals(tag)) {
      return (MCRMetaElement)meta_list.get(i); }
    }
  return  null;
  }

/**
 * This methode set the given MCRMetaElement to the list. If the
 * tag exists the MCRMetaElement was replaced.
 *
 * @param obj       the MCRMetaElement object
 * @param tag       the MCRMetaElement tag
 * @return true if set was succesful, otherwise false
 **/
public final boolean setMetadataElement(MCRMetaElement obj, String tag)
  { 
  if (obj == null) { return false; }
  if ((tag == null) || ((tag = tag.trim()).length() ==0)) { return false; }
  int len = tag_names.size();
  int fl = -1;
  for (int i = 0; i < len; i++) {
    if (((String)tag_names.get(i)).equals(tag)) { fl = i; }
    }
  if (fl == -1) {
    meta_list.add(obj);
    tag_names.add(tag);
    return true;
    }
  meta_list.remove(fl);
  meta_list.add(obj);
  return false;
  }

/**
 * This methode remove the MCRMetaElement selected by tag from the list.
 *
 * @return true if set was succesful, otherwise false
 **/
public final boolean removeMetadataElement(String tag)
  {
  if ((tag == null) || ((tag = tag.trim()).length() ==0)) { return false; }
  int len = tag_names.size();
  for (int i = 0; i < len; i++) {
    if (((String)tag_names.get(i)).equals(tag)) {
      meta_list.remove(i); return true; }
    }
  return false;
  }

/**
 * This methode read the XML input stream part from a DOM part for the
 * metadata of the document.
 *
 * @param element a list of relevant DOM elements for the metadata
 * @exception MCRException       if a problem is occured
 **/
public final void setFromDOM(org.jdom.Element element) throws MCRException
  {
  String temp_lang = element.getAttributeValue("lang");
  if ((temp_lang != null) && ((temp_lang = temp_lang.trim()).length() !=0)) {
    default_lang = temp_lang; }
  List elements_list = element.getChildren();
  int len = elements_list.size();
  String temp_tag = "";
  for (int i=0;i<len;i++) {
    org.jdom.Element subtag = (org.jdom.Element)elements_list.get(i);
    temp_tag = subtag.getName();
    if ((temp_tag == null) || ((temp_tag = temp_tag.trim()).length() ==0)) {
      throw new MCRException("MCRObjectMetadata : The tag is null or empty."); }
    tag_names.add(temp_tag);
    MCRMetaElement obj = new MCRMetaElement(default_lang);
    obj.setFromDOM(subtag);
    meta_list.add(obj);
    }
  }

/**
 * This methode create a XML stream for all metadata.
 *
 * @exception MCRException if the content of this class is not valid
 * @return a JDOM Element with the XML data of the metadata part
 **/
public final org.jdom.Element createXML() throws MCRException
  {
  if (!isValid()) {
    throw new MCRException("MCRObjectMetadata : The content is not valid."); }
  org.jdom.Element elm = new org.jdom.Element("metadata");
  elm.setAttribute("xml:lang",default_lang);
  int len = meta_list.size();
  for (int i = 0; i < len; i++) {
    elm.addContent(((MCRMetaElement)meta_list.get(i)).createXML()); }
  return elm;
  }

/**
 * This methode create a typed content list for all data in this instance.
 *
 * @param parametric true if the data should parametric searchable
 * @param textsearch true if the data should text searchable
 * @exception MCRException if the content of this class is not valid
 * @return a MCRTypedContent with the data of the MCRObject data
 **/
public final MCRTypedContent createTypedContent() throws MCRException
  {
  if (!isValid()) {
    throw new MCRException("MCRObjectMetadata : The content is not valid."); }
  MCRTypedContent tc = new MCRTypedContent();
  tc.addTagElement(tc.TYPE_MASTERTAG,"metadata");
  tc.addStringElement(tc.TYPE_ATTRIBUTE,"lang",default_lang,true,false);
  int len = meta_list.size();
  for (int i = 0; i < len; i++) {
    tc.addMCRTypedContent(((MCRMetaElement)meta_list.get(i))
      .createTypedContent()); }
  return tc;
  }

/**
 * This methode check the validation of the content of this class.
 * The methode returns <em>true</em> if
 * <ul>
 * <li> the array is empty
 * <li> the default lang value was supported
 * </ul>
 * otherwise the methode return <em>false</em>
 *
 * @return a boolean value
 **/
public final boolean isValid()
  {
  if (meta_list.size()==0) { return false; }
  if (!MCRUtils.isSupportedLang(default_lang)) { return false; }
  return true;
  }

/**
 * This metode print all data content from the internal data of the
 * metadata class.
 **/
public final void debug()
  {
  System.out.println("MCRObjectMetadata debug start");
  System.out.println("<lang>"+default_lang+"</lang>");
  System.out.println();
  for (int i = 0; i < meta_list.size(); i++) {
    ((MCRMetaElement)meta_list.get(i)).debug(); }
  System.out.println("MCRObjectMetadata debug end"+NL);
  }
}
