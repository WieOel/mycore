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

package org.mycore.frontend.cli;

import java.io.*;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.mycore.common.*;
import org.mycore.services.query.MCRQueryResult;
import org.mycore.common.xml.MCRXMLContainer;
import org.mycore.datamodel.classifications.MCRClassification;

/**
 * This class implements the query command to start a query to a local
 * Library Server or to any remote Library Server in a configuration
 * list or to a dedicated named remote Library Server.
 * The result was presided as a text output.
 *
 * @author Jens Kupferschmidt
 * @author Frank Lützenkirchen
 * @author Mathias Zarick
 * @version $Revision$ $Date$
 **/
final class MCRQueryCommands
{

// logger
static Logger logger=Logger.getLogger(MCRQueryCommands.class.getName());

/** Executes a local query */
public static void queryLocal( String type, String query )
{ query( "local", type, query ); }

/** Executes a remote query */
public static void queryRemote( String type, String query )
{ query( "remote", type, query ); }

/**
 * The query command
 *
 * @param host  either "local", "remote" or hostname
 * @param type  the document type, "class" or "document" or ...
 * @param query the query expression
 **/
public static void query( String host, String type, String query )
  {
  // Configuration
  MCRConfiguration config = MCRConfiguration.instance();
  // set the logger property
  PropertyConfigurator.configure(config.getLoggingProperties());

  // input parameters
  if (host==null) host = "local";
  if (type==null) { return; }
  type  = type .toLowerCase();
  if (query==null) query = "";
  logger.info("Query Host  = "+host);
  logger.info("Query Type  = "+type);
  logger.info("Query       = "+query);

  // classifications
  if (type.equals("class")) {
    MCRQueryResult result = new MCRQueryResult();
    String squence = config.getString("MCR.classifications_search_sequence",
      "remote-local");
    MCRXMLContainer resarray = new MCRXMLContainer();
    if (squence.equalsIgnoreCase("local-remote")) {
      resarray = result.setFromQuery("local",type,query );
      if (resarray.size()==0) {
        resarray = result.setFromQuery(host,type,query ); }
      }
    else {
      resarray = result.setFromQuery(host,type,query );
      if (resarray.size()==0) {
        resarray = result.setFromQuery("local",type,query ); }
      }
    if (resarray.size()==0) {
      logger.error("No classification or category exists" ); }
    // Stylesheet transforming
    String applpath = config.getString("MCR.appl_path");
    String xslfile = applpath + "/stylesheets/mcr_results-PlainText-"+
      "class.xsl";
    // Transformation
    try {
      TransformerFactory transfakt = TransformerFactory.newInstance();
      Transformer trans =
        transfakt.newTransformer(new StreamSource(xslfile));
      StreamResult sr = new StreamResult((OutputStream) System.out); 
      trans.transform(new javax.xml.transform.dom.DOMSource(
        (new org.jdom.output.DOMOutputter())
        .output(resarray.exportAllToDocument())),sr);
      }
    catch (Exception e) {
      logger.error("Error while tranformation the XML Result via XSLT.");
      logger.debug(e.getMessage());
      logger.info("Stop.");
      logger.info("");
      return;
      }
    logger.info("");
    logger.info("Ready.");
    logger.info("");
    return;
    }

  // other types
  MCRQueryResult result = new MCRQueryResult();
  MCRXMLContainer resarray = result.setFromQuery(host,type,query);

  // Configuration
  String applpath = config.getString("MCR.appl_path");
  String xslfile = applpath + "/stylesheets/mcr_results-PlainText-"+
    type.toLowerCase()+".xsl";
  try {
    TransformerFactory transfakt = TransformerFactory.newInstance();
    // Indexlist
    Transformer trans =
      transfakt.newTransformer(new StreamSource(xslfile));
    StreamResult sr = new StreamResult((OutputStream) System.out); 
    trans.transform(new javax.xml.transform.dom.DOMSource(
      (new org.jdom.output.DOMOutputter())
      .output(resarray.exportAllToDocument())),sr);
    }
  catch (Exception e) {
    logger.error("Error while tranformation the XML Result via XSLT.");
    logger.debug(e.getMessage());
    logger.info("Stop.");
    logger.info("");
    return;
    }
  logger.info("");
  logger.info("Ready.");
  logger.info("");
  }

}

