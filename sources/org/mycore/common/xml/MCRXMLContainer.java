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

package org.mycore.common.xml;

import java.io.*;
import java.util.*;
import org.mycore.common.MCRException;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRConfigurationException;
import org.mycore.datamodel.metadata.MCRObject;

/**
 * This class is the cache of one result list included all XML files
 * as result of one query. They holds informations about host, rank and
 * the XML byte stream. You can get the complete result or elements of them
 * as XML JDOM Document for transforming with XSLT.
 *
 * @author Jens Kupferschmidt
 * @author Mathias Zarick
 * @version $Revision$ $Date$
 **/
public class MCRXMLContainer
{

// data
private ArrayList host;
private ArrayList mcr_id;
private ArrayList rank;
private ArrayList xml;
private ArrayList status;
private String default_encoding;

/** The tag for the result collection **/
public static final String TAG_RESULTS = "mcr_results";
/** The tag for one result **/
public static final String TAG_RESULT = "mcr_result";
/** The attribute of the host name **/
public static final String ATTR_HOST = "host";
/** The attribute of the MCRObjectId **/
public static final String ATTR_ID = "id";
/** The attribute of the rank **/
public static final String ATTR_RANK = "rank";
/** The attribute of the neighbour status **/
public static final String ATTR_STATUS = "status";

/**
 * This constructor create the MCRXMLContainer class with an empty
 * query result list.
 **/
public MCRXMLContainer()
  {
  MCRConfiguration config = MCRConfiguration.instance();
  default_encoding = config.getString("MCR.metadata_default_encoding","UTF-8");
  host = new ArrayList();
  mcr_id = new ArrayList();
  rank = new ArrayList();
  xml = new ArrayList();
  status = new ArrayList();
  }

/**
 * This constructor create the MCRXMLContainer class with a given
 * query result list.
 *
 * @param in a MCRXMLContainer as input
 **/
public MCRXMLContainer(MCRXMLContainer in)
  {
  MCRConfiguration config = MCRConfiguration.instance();
  default_encoding = config.getString("MCR.metadata_default_encoding","UTF-8");
  host = new ArrayList();
  mcr_id = new ArrayList();
  rank = new ArrayList();
  xml = new ArrayList();
  status = new ArrayList();

  for (int i=0;i<in.size();i++) {
    host.add(in.getHost(i));
    mcr_id.add(in.getId(i));
    rank.add(new Integer(in.getRank(i)));
    xml.add(in.getXML(i));
    }
  fillStatus();
  }

/**
 * This methode return the size of the result list.
 *
 * @return the size of the result list
 **/
public final int size()
  { return host.size(); }

/**
 * This methode return the host of an element index.
 *
 * @param index   the index in the list
 * @return an empty string if the index is outside the border, else return
 * the host name
 **/
public final String getHost(int index)
  {
  if ((index<0)||(index>host.size())) { return ""; }
  return (String) host.get(index);
  }

/**
 * This method sets a host element at a specified position.
 *
 * @param index   the index in the list
 * @param newhost the new value in the list
 **/
public final void setHost(int index,String newhost)
  {
  host.set(index,newhost);
  }

/**
 * This methode return the MCRObjectId of an element index as string.
 *
 * @param index   the index in the list
 * @return an empty string if the index is outside the border, else return
 * the MCRObjectId as string.
 **/
public final String getId(int index)
  {
  if ((index<0)||(index>mcr_id.size())) { return ""; }
  return (String) mcr_id.get(index);
  }

/**
 * This methode return the rank of an element index.
 *
 * @param index   the index in the list
 * @return -1 if the index is outside the border, else return the rank
 **/
public final int getRank(int index)
  {
  if ((index<0)||(index>rank.size())) { return -1; }
  return ((Integer)rank.get(index)).intValue();
  }

/**
 * This method returns the neighbour status of an element index.
 *
 * @param index   the index in the list
 * @return -1 if the index is outside the border, else return the rank
 **/
public final int getStatus(int index)
  {
  if ((index<0)||(index>status.size())) { return -1; }
  return ((Integer)status.get(index)).intValue();
  }
/**
 * This method sets the neighbour status of an element index.
 *
 * @param index   the index in the list
 * @param status  the new value in the list
 **/
public final void setStatus(int index,int instatus)
  {
  status.set(index, new Integer(instatus));
  }

/**
 * This methode return the mycoreobject as JDOM Element of an element index.
 *
 * @param index   the index in the list
 * @return an null if the index is outside the border, else return
 * the mycoreobject as JDOM Element
 **/
public final org.jdom.Element getXML(int index)
  {
  if ((index<0)||(index>host.size())) { return null; }
  return (org.jdom.Element) xml.get(index);
  }

/**
 * This methode add one element to the result list.
 *
 * @param in_host    the host input as a string
 * @param in_id      the MCRObjectId input as a string
 * @param in_rank    the rank input as an integer
 * @param in_xml     the JDOM Element of a mycoreobject
 **/
public final synchronized void add(String in_host, String in_id, int in_rank,
  org.jdom.Element in_xml)
  {
  host.add(in_host);
  mcr_id.add(in_id);
  rank.add(new Integer(in_rank));
  xml.add(in_xml);
  int in_status=0;
  for (int i=0; i < size(); i++){
	if (status.size()>0){
	  status.set((status.size()-1), new Integer(((Integer)status.get(status.size()-1)).intValue()+1));
	  in_status=2;
	}
  }
  status.add(new Integer(in_status));
  }

/**
 * This methode add one element to the result list.
 *
 * @param in_host    the host input as a string
 * @param in_id      the MCRObjectId input as a string
 * @param in_rank    the rank input as an integer
 * @param in_xml     the well formed XML stream as a byte array
 * @exception org.jdom.JDOMException if a JDOm error was occured
 **/
public final synchronized void add(String in_host, String in_id, int in_rank, 
  byte[] in_xml) throws org.jdom.JDOMException
  {
  ByteArrayInputStream bin = new ByteArrayInputStream(in_xml);
  org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
  org.jdom.Document jdom = builder.build(bin);
  org.jdom.Element root = jdom.getRootElement();
  add(in_host,in_id,in_rank,root);
  }

/**
 * This methode return a well formed XML stream of the result collection
 * as a JDOM document.<br>
 * &lt;?xml version="1.0" encoding="..."?&gt;<br>
 * &lt;mcr_results&gt;<br>
 * &lt;mcr_result host="<em>host</em> id="<em>MCRObjectId</em>"
 *  rank="<em>rank</em>" &gt;<br>
 * &lt;mycore...&gt;<br>
 * ...<br>
 * &lt;/mycore...&gt;<br>
 * &lt;/mcr_result&gt;<br>
 * &lt;/mcr_results&gt;<br>
 *
 * @return the result collection as a JDOM document.
 **/
public final org.jdom.Document exportAllToDocument()
  {
  org.jdom.Element root = new org.jdom.Element(TAG_RESULTS);
  org.jdom.Document doc = new org.jdom.Document(root);
  for (int i=0;i<rank.size();i++) {
    org.jdom.Element res = new org.jdom.Element(TAG_RESULT);
    res.setAttribute(ATTR_HOST,((String)host.get(i)).trim());
    res.setAttribute(ATTR_ID,((String)mcr_id.get(i)).trim());
    res.setAttribute(ATTR_RANK,rank.get(i).toString());
    res.setAttribute(ATTR_STATUS,status.get(i).toString());
    org.jdom.Element tmp = 
      (org.jdom.Element)((org.jdom.Element)xml.get(i)).clone();
    res.addContent(tmp);
    root.addContent(res);
    }
  return doc;
  }

/**
 * This methode return a well formed XML stream of the result collection
 * as a JDOM document.<br>
 * &lt;?xml version="1.0" encoding="..."?&gt;<br>
 * &lt;mcr_results&gt;<br>
 * &lt;mcr_result host="<em>host</em> id="<em>MCRObjectId</em>"
 *  rank="<em>rank</em>" &gt;<br>
 * &lt;mycore...&gt;<br>
 * ...<br>
 * &lt;/mycore...&gt;<br>
 * &lt;/mcr_result&gt;<br>
 * &lt;/mcr_results&gt;<br>
 *
 * @return the result collection as a JDOM document.
 * @exception IOException if an error in the XMLOutputter was occured
 **/
public final byte [] exportAllToByteArray() throws IOException
  {
  org.jdom.Document doc = exportAllToDocument();
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  org.jdom.output.XMLOutputter op = new org.jdom.output.XMLOutputter();
  op.setEncoding(default_encoding);
  op.output(doc,os);
  return os.toByteArray();
  }

/**
 * This methode return a well formed XML stream as a JDOM document.<br>
 * &lt;?xml version="1.0" encoding="..."?&gt;<br>
 * &lt;mcr_results&gt;<br>
 * &lt;mcr_result host="<em>host</em> id="<em>MCRObjectId</em>"
 *  rank="<em>rank</em>" &gt;<br>
 * &lt;mycore...&gt;<br>
 * ...<br>
 * &lt;/mycore...&gt;<br>
 * &lt;/mcr_result&gt;<br>
 * &lt;/mcr_results&gt;<br>
 *
 * @param index the index number of the element
 * @return one result as a JDOM document. If index is out of border an
 * empty body was returned.
 **/
public final org.jdom.Document exportElementToDocument(int index)
  {
  org.jdom.Element root = new org.jdom.Element(TAG_RESULTS);
  org.jdom.Document doc = new org.jdom.Document(root);
  if ((index>=0)&&(index<=rank.size())) {
    org.jdom.Element res = new org.jdom.Element(TAG_RESULT);
    res.setAttribute(ATTR_HOST,((String)host.get(index)).trim());
    res.setAttribute(ATTR_ID,((String)mcr_id.get(index)).trim());
    res.setAttribute(ATTR_RANK,rank.get(index).toString());
	res.setAttribute(ATTR_STATUS,status.get(index).toString());
    org.jdom.Element tmp = 
      (org.jdom.Element)((org.jdom.Element)xml.get(index)).clone();
    res.addContent(tmp);
    root.addContent(res);
    }
  return doc;
  }

/**
 * This methode return a well formed XML stream as a byte array.<br>
 * &lt;?xml version="1.0" encoding="..."?&gt;<br>
 * &lt;mcr_results&gt;<br>
 * &lt;mcr_result host="<em>host</em> id="<em>MCRObjectId</em>"
 *  rank="<em>rank</em>" &gt;<br>
 * &lt;mycore...&gt;<br>
 * ...<br>
 * &lt;/mycore...&gt;<br>
 * &lt;/mcr_result&gt;<br>
 * &lt;/mcr_results&gt;<br>
 *
 * @param index the index number of the element
 * @exception IOException if an error in the XMLOutputter was occured
 * @return one result as a JDOM document. If index is out of border an
 * empty body was returned.
 **/
public final byte [] exportElementToByteArray(int index) throws IOException
  {
  org.jdom.Document doc = exportElementToDocument(index);
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  org.jdom.output.XMLOutputter op = new org.jdom.output.XMLOutputter();
  op.setEncoding(default_encoding);
  op.output(doc,os);
  return os.toByteArray();
  }

private static String ERRORTEXT =
  "The stream for the MCRXMLContainer import is false.";

/**
 * This methode import a well formed XML stream of results as byte array and add it to an
 * existing list.
 * in form of<br>
 * &lt;?xml version="1.0" encoding="..."?&gt;<br>
 * &lt;mcr_results&gt;<br>
 * &lt;mcr_result host="<em>host</em> id="<em>MCRObjectId</em>"
 *  rank="<em>rank</em>" &gt;<br>
 * &lt;mycore...&gt;<br>
 * ...<br>
 * &lt;/mycore...&gt;<br>
 * &lt;/mcr_result&gt;<br>
 * &lt;/mcr_results&gt;<br>
 *
 * @param the XML input stream as byte array
 * @exception MCRException a MyCoRe error is occured
 * @exception org.jdom.JDOMException cant read the byte array as XML
 **/
public final synchronized void importElements(byte [] in) 
  throws MCRException, org.jdom.JDOMException
  {
  ByteArrayInputStream bin = new ByteArrayInputStream(in);
  org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
  org.jdom.Document jdom = builder.build(bin);
  org.jdom.Element root = jdom.getRootElement();
  if (!root.getName().equals(TAG_RESULTS)) {
    throw new MCRException("The input is not an MCRXMLContainer."); }
  List list = root.getChildren(TAG_RESULT);
  int irank = 0;
  int istatus = 0;
  for (int i=0;i<list.size();i++) {
    org.jdom.Element res = (org.jdom.Element) list.get(i);
    String inhost = res.getAttributeValue(ATTR_HOST);
    String inid = res.getAttributeValue(ATTR_ID);
    String inrank = res.getAttributeValue(ATTR_RANK);
    String instatus = res.getAttributeValue(ATTR_STATUS);
    try { irank = Integer.parseInt(inrank); }
    catch (NumberFormatException e) {
      throw new MCRException(ERRORTEXT); }
    try { istatus = Integer.parseInt(instatus); }
    catch (NumberFormatException e) {
      throw new MCRException(ERRORTEXT); }
    List childlist = res.getChildren();
    org.jdom.Element inxml = (org.jdom.Element)childlist.get(0);
    host.add(inhost);
    mcr_id.add(inid);
    rank.add(new Integer(irank));
    status.add(new Integer(istatus));
    System.out.print("MCRXMLContainer.importElements(byte[]): status="+istatus);
    xml.add(inxml);
    }
  }

/**
 * This method imports another MCRXMLContainer and add it to the existing list.
 *
 * @param the other list as input
 **/
public final synchronized void importElements(MCRXMLContainer in)
  { 
  for (int i=0; i<in.size(); i++) {
    add(in.getHost(i),in.getId(i),in.getRank(i),in.getXML(i));}
  }

/**
 * This methode print the content of this MCRXMLContainer as
 * an XML String.
 *
 * @exception IOException if an error in the XMLOutputter was occured
 **/
public final void debug() throws IOException
  { 
  System.out.println("Debug of MCRXMLContainer");
  System.out.println("============================");
  System.out.println("Size = "+size()); 
  System.out.println(new String(exportAllToByteArray())); 
  }

private final void fillStatus()
  {
    if (size() != status.size()){
      System.out.print("MCRXMLContainer: refreshing status :");
      int in_status=0;
	  status.clear();
	  for (int i=0; i < size(); i++){
	    if (status.size()>0){
		  status.set((status.size()-1), new Integer(((Integer)status.get(status.size()-1)).intValue()+1));
		  in_status=2;
		}
	  status.add(new Integer(in_status));
	  System.out.print(" "+in_status);
	  }
	  System.out.print("\n");
  	}
  }

}

