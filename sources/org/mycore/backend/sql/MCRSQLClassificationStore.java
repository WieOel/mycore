/*
 * $RCSfile$
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

package org.mycore.backend.sql;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.mycore.common.MCRConfiguration;
import org.mycore.datamodel.classifications.MCRCategoryItem;
import org.mycore.datamodel.classifications.MCRClassificationInterface;
import org.mycore.datamodel.classifications.MCRClassificationItem;
import org.mycore.datamodel.classifications.MCRClassificationObject;
import org.mycore.datamodel.classifications.MCRLabel;
import org.mycore.datamodel.classifications.MCRLink;
import org.mycore.datamodel.metadata.MCRMetaClassification;

/**
 * This class implements the MCRClassificationInterface as persistence layer for
 * a SQL database.
 * 
 * @author Frank L�tzenkirchen
 * @author Jens Kupferschmidt
 * @version $Revision$ $Date$
 * @deprecated
 */
public class MCRSQLClassificationStore implements MCRClassificationInterface {
    // logger
    static Logger logger = Logger.getLogger(MCRSQLClassificationStore.class);

    // internal data
    private String tableClass;

    private String tableClassLabel;

    private String tableCateg;

    private String tableCategLabel;

    private int lengthClassID = MCRMetaClassification.MAX_CLASSID_LENGTH;

    private int lengthCategID = MCRMetaClassification.MAX_CATEGID_LENGTH;

    private int lengthLang = MCRClassificationObject.MAX_CLASSIFICATION_LANG;

    private int lengthText = MCRClassificationObject.MAX_CLASSIFICATION_TEXT;

    private int lengthURL = MCRClassificationObject.MAX_CATEGORY_URL;

    private int lengthDescription = MCRClassificationObject.MAX_CLASSIFICATION_DESCRIPTION;

    /**
     * The constructor for the class MCRSQLClassificationStore. It reads the
     * classification configuration and checks the table names.
     */
    public MCRSQLClassificationStore() {
        MCRConfiguration config = MCRConfiguration.instance();

        // set configuration
        tableClass = config.getString("MCR.classifications_store_sql_table_class", "MCRCLASS");
        tableClassLabel = config.getString("MCR.classifications_store_sql_table_classlabel", "MCRCLASSLABEL");
        tableCateg = config.getString("MCR.classifications_store_sql_table_categ", "MCRCATEG");
        tableCategLabel = config.getString("MCR.classifications_store_sql_table_categlabel", "MCRCATEGLABEL");

        if (!MCRSQLConnection.doesTableExist(tableClass)) {
            logger.info("Create table " + tableClass);
            createClass();
            logger.info("Done.");
        }

        if (!MCRSQLConnection.doesTableExist(tableClassLabel)) {
            logger.info("Create table " + tableClassLabel);
            createClassLabel();
            logger.info("Done.");
        }

        if (!MCRSQLConnection.doesTableExist(tableCateg)) {
            logger.info("Create table " + tableCateg);
            createCateg();
            logger.info("Done.");
        }

        if (!MCRSQLConnection.doesTableExist(tableCategLabel)) {
            logger.info("Create table " + tableCategLabel);
            createCategLabel();
            logger.info("Done.");
        }
    }

    /**
     * The method drop the classification and category tables.
     */
    public final void dropTables() {
        MCRSQLConnection c = MCRSQLConnectionPool.instance().getConnection();

        try {
            c.doUpdate("DROP TABLE " + tableClass);
            c.doUpdate("DROP TABLE " + tableClassLabel);
            c.doUpdate("DROP TABLE " + tableCateg);
            c.doUpdate("DROP TABLE " + tableClassLabel);
        } finally {
            c.release();
        }
    }

    /**
     * The method create a table for classification.
     */
    private final void createClass() {
        MCRSQLConnection c = MCRSQLConnectionPool.instance().getConnection();

        try {
            // the classification table
            c.doUpdate(new MCRSQLStatement(tableClass).addColumn("ID VARCHAR(" + Integer.toString(lengthClassID) + ") NOT NULL PRIMARY KEY").toCreateTableStatement());
        } finally {
            c.release();
        }
    }

    /**
     * The method create a table for category.
     */
    private final void createCateg() {
        MCRSQLConnection c = MCRSQLConnectionPool.instance().getConnection();

        try {
            // the category table
            c.doUpdate(new MCRSQLStatement(tableCateg).addColumn("ID VARCHAR(" + Integer.toString(lengthCategID) + ") NOT NULL").addColumn("CLID VARCHAR(" + Integer.toString(lengthClassID) + ") NOT NULL").addColumn("PID VARCHAR(" + Integer.toString(lengthCategID) + ")").addColumn("URL VARCHAR(" + Integer.toString(lengthURL) + ")").addColumn("PRIMARY KEY ( CLID, ID )").toCreateTableStatement());
            c.doUpdate(new MCRSQLStatement(tableCateg).addColumn("CLID").addColumn("ID").toIndexStatement());
        } finally {
            c.release();
        }
    }

    /**
     * The method create a label table for classification.
     */
    private final void createClassLabel() {
        MCRSQLConnection c = MCRSQLConnectionPool.instance().getConnection();

        try {
            // the classification table
            c.doUpdate(new MCRSQLStatement(tableClassLabel).addColumn("ID VARCHAR(" + Integer.toString(lengthClassID) + ") NOT NULL").addColumn("LANG VARCHAR(" + Integer.toString(lengthLang) + ") NOT NULL").addColumn("TEXT VARCHAR(" + Integer.toString(lengthText) + ")").addColumn("MCRDESC VARCHAR(" + Integer.toString(lengthDescription) + ")").addColumn("PRIMARY KEY ( ID, LANG )")
                    .toCreateTableStatement());
            c.doUpdate(new MCRSQLStatement(tableClassLabel).addColumn("ID").addColumn("LANG").toIndexStatement());
        } finally {
            c.release();
        }
    }

    /**
     * The method create a label table for category.
     */
    private final void createCategLabel() {
        MCRSQLConnection c = MCRSQLConnectionPool.instance().getConnection();

        try {
            // the category table
            c.doUpdate(new MCRSQLStatement(tableCategLabel).addColumn("ID VARCHAR(" + Integer.toString(lengthCategID) + ") NOT NULL").addColumn("CLID VARCHAR(" + Integer.toString(lengthClassID) + ") NOT NULL").addColumn("LANG VARCHAR(" + Integer.toString(lengthLang) + ") NOT NULL").addColumn("TEXT VARCHAR(" + Integer.toString(lengthText) + ")").addColumn(
                    "MCRDESC VARCHAR(" + Integer.toString(lengthDescription) + ")").addColumn("PRIMARY KEY ( CLID, ID, LANG )").toCreateTableStatement());
            c.doUpdate(new MCRSQLStatement(tableCategLabel).addColumn("CLID").addColumn("ID").addColumn("LANG").toIndexStatement());
        } finally {
            c.release();
        }
    }

    /**
     * The method create a new MCRClassificationItem in the datastore.
     * 
     * @param classification
     *            an instance of a MCRClassificationItem
     */
    public final void createClassificationItem(MCRClassificationItem classification) {
        MCRSQLConnection.justDoUpdate(new MCRSQLStatement(tableClass).setValue("ID", classification.getId()).toInsertStatement());
        ArrayList<MCRLabel> label = (ArrayList<MCRLabel>) classification.getLabels();
        for (int i = 0; i < label.size(); i++) {
            MCRSQLConnection.justDoUpdate(new MCRSQLStatement(tableClassLabel).setValue("ID", classification.getId()).setValue("LANG", ((MCRLabel) label.get(i)).getLang()).setValue("TEXT",((MCRLabel) label.get(i)).getText() ).setValue("MCRDESC", ((MCRLabel) label.get(i)).getDescription()).toInsertStatement());
        }
    }

    /**
     * The method remove a MCRClassificationItem from the datastore.
     * 
     * @param ID
     *            the ID of the MCRClassificationItem
     */
    public void deleteClassificationItem(String ID) {
        MCRSQLConnection.justDoUpdate(new MCRSQLStatement(tableClass).setCondition("ID", ID).toDeleteStatement());
        MCRSQLConnection.justDoUpdate(new MCRSQLStatement(tableClassLabel).setCondition("ID", ID).toDeleteStatement());
        MCRSQLConnection.justDoUpdate(new MCRSQLStatement(tableCateg).setCondition("CLID", ID).toDeleteStatement());
        MCRSQLConnection.justDoUpdate(new MCRSQLStatement(tableCategLabel).setCondition("CLID", ID).toDeleteStatement());
    }

    /**
     * The method return a MCRClassificationItem from the datastore.
     * 
     * @param ID
     *            the ID of the MCRClassificationItem
     */
    public final MCRClassificationItem retrieveClassificationItem(String ID) {
        String query = new MCRSQLStatement(tableClassLabel).setCondition("ID", ID).toSelectStatement();
        MCRSQLConnection conn = MCRSQLConnectionPool.instance().getConnection();

        MCRClassificationItem c = null;
        try {
            MCRSQLRowReader reader = conn.doQuery(query);
            while (reader.next()) {
                if (c == null) {
                    c = new MCRClassificationItem();
                    c.setId(ID);
                    c.setCatgegories(new ArrayList<MCRCategoryItem>());
                }
                MCRLabel label = new MCRLabel(reader.getString("LANG"), reader.getString("TEXT"), reader.getString("MCRDESC"));
                c.addLabel(label);
            }
            reader.close();
        } finally {
            conn.release();
        }

        return c;
    }

    /**
     * The method return if the MCRClassificationItem is in the datastore.
     * 
     * @param ID
     *            the ID of the MCRClassificationItem
     * @return true if the MCRClassificationItem was found, else false
     */
    public final boolean classificationItemExists(String ID) {
        return MCRSQLConnection.justCheckExists(new MCRSQLStatement(tableClass).setCondition("ID", ID).toRowSelector());
    }

    /**
     * The method create a new MCRCategoryItem in the datastore.
     * 
     * @param category
     *            an instance of a MCRCategoryItem
     */
    public final void createCategoryItem(MCRCategoryItem category) {
        try {
            MCRSQLConnection.justDoUpdate(new MCRSQLStatement(tableCateg).setValue("ID", category.getId()).setValue("CLID", category.getClassID()).setValue("PID", category.getParentID()).setValue("URL", category.getLink().getHref()).toInsertStatement());
        } catch (Exception e) {
            MCRSQLConnection.justDoUpdate(new MCRSQLStatement(tableCateg).setValue("ID", category.getId()).setValue("CLID", category.getClassID()).setValue("PID", category.getParentID()).toInsertStatement());
        }

        ArrayList<MCRLabel> label = (ArrayList<MCRLabel>) category.getLabels();
        for (int i = 0; i < label.size(); i++) {
            MCRSQLConnection.justDoUpdate(new MCRSQLStatement(tableCategLabel).setValue("ID", category.getId()).setValue("CLID", category.getClassID()).setValue("LANG", ((MCRLabel) label.get(i)).getLang()).setValue("TEXT", ((MCRLabel) label.get(i)).getText()).setValue("MCRDESC", ((MCRLabel) label.get(i)).getDescription()).toInsertStatement());
        }
    }

    /**
     * The method remove a MCRCategoryItem from the datastore.
     * 
     * @param CLID
     *            the ID of the MCRClassificationItem
     * @param ID
     *            the ID of the MCRCategoryItem
     */
    public final void deleteCategoryItem(String CLID, String ID) {
        MCRSQLConnection.justDoUpdate(new MCRSQLStatement(tableCateg).setCondition("CLID", CLID).setCondition("ID", ID).toDeleteStatement());
        MCRSQLConnection.justDoUpdate(new MCRSQLStatement(tableCategLabel).setCondition("CLID", CLID).setCondition("ID", ID).toDeleteStatement());
    }

    /**
     * The method return a MCRCategoryItem from the datastore.
     * 
     * @param CLID
     *            the ID of the MCRClassificationItem
     * @param ID
     *            the ID of the MCRCategoryItem
     */
    public final MCRCategoryItem retrieveCategoryItem(String CLID, String ID) {
        MCRSQLConnection conn = MCRSQLConnectionPool.instance().getConnection();
        try {
            MCRSQLRowReader reader = conn.doQuery(new MCRSQLStatement(tableCateg).setCondition("ID", ID).setCondition("CLID", CLID).toSelectStatement());

            if (!reader.next()) {
                return null;
            }

            String PID = reader.getString("PID");
            String URL = "";

            try {
                URL = reader.getString("URL");
            } catch (Exception e) {
                URL = "";
            }

            if (URL == null) {
                URL = "";
            }
            reader.close();

            MCRCategoryItem c = new MCRCategoryItem();
            c.setId(ID);
            c.setClassID(CLID);
            c.setParentID(PID);
            c.setCatgegories(new ArrayList<MCRCategoryItem>());
            MCRLink link = new MCRLink("locator", URL, URL, "");
            c.setLink(link);
            reader = conn.doQuery(new MCRSQLStatement(tableCategLabel).setCondition("ID", ID).setCondition("CLID", CLID).toSelectStatement());

            while (reader.next()) {
                MCRLabel label = new MCRLabel(reader.getString("LANG"), reader.getString("TEXT"), reader.getString("MCRDESC"));
                c.addLabel(label);
            }
            reader.close();

            return c;
        } finally {
            conn.release();
        }
    }

    /**
     * The method return a MCRCategoryItem from the datastore.
     * 
     * @param CLID
     *            the ID of the MCRClassificationItem
     * @param labeltext
     *            the label text of the MCRCategoryItem
     */
    public MCRCategoryItem retrieveCategoryItemForLabelText(String CLID, String labeltext) {
        MCRSQLConnection conn = MCRSQLConnectionPool.instance().getConnection();

        try {
            MCRSQLRowReader reader = conn.doQuery(new MCRSQLStatement(tableCategLabel).setCondition("TEXT", labeltext).setCondition("CLID", CLID).toSelectStatement());
            boolean found = reader.next();
            reader.close();

            if (!found) {
                return null;
            }

            String ID = reader.getString("ID");
            MCRCategoryItem c = new MCRCategoryItem();
            c.setId(ID);
            c.setClassID(CLID);
            c.setParentID("");
            c.setCatgegories(new ArrayList<MCRCategoryItem>());
            MCRLabel label = new MCRLabel(reader.getString("LANG"), reader.getString("TEXT"), reader.getString("MCRDESC"));
            c.addLabel(label);
            reader = conn.doQuery(new MCRSQLStatement(tableCateg).setCondition("ID", ID).setCondition("CLID", CLID).toSelectStatement());
            found = reader.next();

            if (!found) {
                reader.close();
                return null;
            }

            String URL = "";
            try {
                URL = reader.getString("URL");
            } catch (Exception e) {
                URL = "";
            }
            if (URL == null) {
                URL = "";
            }
            reader.close();
            MCRLink link = new MCRLink("locator", URL, URL, "");
            c.setLink(link);

            return c;
        } finally {
            conn.release();
        }
    }

    /**
     * The method return if the MCRCategoryItem is in the datastore.
     * 
     * @param CLID
     *            the ID of the MCRClassificationItem
     * @param ID
     *            the ID of the MCRCategoryItem
     * @return true if the MCRCategoryItem was found, else false
     */
    public final boolean categoryItemExists(String CLID, String ID) {
        return MCRSQLConnection.justCheckExists(new MCRSQLStatement(tableCateg).setCondition("ID", ID).setCondition("CLID", CLID).toRowSelector());
    }

    /**
     * The method return an Vector of MCRCategoryItems from the datastore.
     * 
     * @param CLID
     *            the ID of the MCRClassificationItem
     * @param PID
     *            the parent ID of the MCRCategoryItem
     * @return a list of MCRCategoryItem children
     */
    public final ArrayList retrieveChildren(String CLID, String PID) {
        ArrayList<MCRCategoryItem> children = new ArrayList<MCRCategoryItem>();
        MCRSQLConnection conn = MCRSQLConnectionPool.instance().getConnection();
        try {
            MCRSQLRowReader reader = conn.doQuery(new MCRSQLStatement(tableCateg).setCondition("PID", PID).setCondition("CLID", CLID).toSelectStatement());

            while (reader.next()) {
                String ID = reader.getString("ID");
                String URL = "";

                try {
                    URL = reader.getString("URL");
                } catch (Exception e) {
                    URL = "";
                }

                if (URL == null) {
                    URL = "";
                }

                MCRCategoryItem child = new MCRCategoryItem();
                child.setId(ID);
                child.setClassID(CLID);
                child.setParentID(PID);
                MCRLink link = new MCRLink("locator", URL, URL, "");
                child.setLink(link);
                children.add(child);
            }
            reader.close();

            for (int i = 0; i < children.size(); i++) {
                reader = conn.doQuery(new MCRSQLStatement(tableCategLabel).setCondition("ID", ((MCRCategoryItem) children.get(i)).getId()).setCondition("CLID", CLID).toSelectStatement());

                while (reader.next()) {
                    MCRLabel label = new MCRLabel(reader.getString("LANG"), reader.getString("TEXT"), reader.getString("MCRDESC"));
                    ((MCRCategoryItem) children.get(i)).addLabel(label);
                }
                reader.close();
            }
        } finally {
            conn.release();
        }

        return children;
    }

    /**
     * The method return the number of MCRCategoryItems from the datastore.
     * 
     * @param CLID
     *            the ID of the MCRClassificationItem
     * @param PID
     *            the parent ID of the MCRCategoryItem
     * @return the number of MCRCategoryItem children
     */
    public final int retrieveNumberOfChildren(String CLID, String PID) {
        return MCRSQLConnection.justCountRows(new MCRSQLStatement(tableCateg).setCondition("PID", PID).setCondition("CLID", CLID).toRowSelector());
    }

    /**
     * The method returns all availiable classification ID's they are loaded.
     * 
     * @return a list of classification ID's as String array
     */
    public final String[] getAllClassificationID() {
        int len = MCRSQLConnection.justCountRows(new MCRSQLStatement(tableClass).addColumn("ID").toRowSelector());
        logger.debug("Number of classifications = " + Integer.toString(len));

        MCRSQLConnection conn = MCRSQLConnectionPool.instance().getConnection();
        String[] ID = new String[len];
        try {
            MCRSQLRowReader reader = conn.doQuery(new MCRSQLStatement(tableClass).addColumn("ID").toSelectStatement());
            int i = 0;

            while (reader.next()) {
                ID[i] = reader.getString("ID");
                logger.debug("ID of classifications[" + Integer.toString(i) + "] = " + ID[i]);
                i++;
            }
            reader.close();
        } finally {
            conn.release();
        }

        return ID;
    }
    
    /**
     * The method returns all availiable classification's they are loaded.
     * 
     * @return a list of classification ID's as String array
     */
    public final MCRClassificationItem[] getAllClassification() {
    	logger.debug("List of classifications");
    	// 	Select count(*) FROM MCRCLASS 
    	MCRSQLConnection conn = MCRSQLConnectionPool.instance().getConnection();
    	
        int len = MCRSQLConnection
        		.justCountRows(new MCRSQLStatement(tableClass).addColumn("ID")
                .toRowSelector());
        
        MCRClassificationItem[] classList = new MCRClassificationItem[len];
        
    	//SELECT id, lang, text, mcrdesc FROM MCRCLASSLABEL M order by id, lang
    	StringBuffer querySB = new StringBuffer("SELECT ID, LANG, TEXT, MCRDESC FROM MCRCLASSLABEL ORDER BY 1,2");  	        
    	MCRSQLRowReader reader = conn.doQuery(querySB.toString());
        
        int k = -1;
        while (reader.next()) {
        	String ID = reader.getString("ID");
        	if ( k==-1 || !classList[k].getId().equalsIgnoreCase(ID) ){
        		k++;
                //logger.debug("next ID of classList[" + Integer.toString(k) + "] = " + ID);
        		classList[k] = new MCRClassificationItem();
                classList[k].setId(ID);
           		//logger.debug("add first data of classList[" + Integer.toString(k) + "] = " + ID);
                MCRLabel label = new MCRLabel(reader.getString("LANG"), reader.getString("TEXT"), reader.getString("MCRDESC"));
           	 	classList[k].addLabel(label);
        	} else {
        		//logger.debug("add more data of classList[" + Integer.toString(k) + "] = " + ID);
                MCRLabel label = new MCRLabel(reader.getString("LANG"), reader.getString("TEXT"), reader.getString("MCRDESC"));
        		classList[k].addLabel(label);
        	}
        }
        return classList;
    }

}
