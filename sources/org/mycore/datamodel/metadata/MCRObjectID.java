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

package org.mycore.datamodel.metadata;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.datamodel.common.MCRXMLTableManager;

/**
 * This class holds all informations and methods to handle the MyCoRe Object ID.
 * The MyCoRe Object ID is a special ID to identify each metadata object with
 * three parts, they are the project identifier, the type identifier and a
 * string with a number.
 * 
 * @author Jens Kupferschmidt
 * @author Thomas Scheffler (yagee)
 * @version $Revision$ $Date$
 */
public final class MCRObjectID {
    /**
     * public constant value for the MCRObjectID length
     */
    public static final int MAX_LENGTH = 64;

    // configuration values
    protected static MCRConfiguration CONFIG;

    private static final Logger LOGGER = Logger.getLogger(MCRObjectID.class);

    // counter for the next IDs per project base ID
    private static HashMap<String, Integer> lastnumber = new HashMap<String, Integer>();

    // data of the ID
    private String mcr_project_id = null;

    private String mcr_type_id = null;

    private String mcr_id = null;

    private int mcr_number = -1;

    private boolean mcr_valid_id = false;

    private static int number_distance = 1;

    private static String number_pattern = null;

    private static DecimalFormat number_format = null;

    /**
     * Static method to load the configuration.
     */
    static {
        CONFIG = MCRConfiguration.instance();
        number_distance = CONFIG.getInt("MCR.Metadata.ObjectID.NumberDistance", 1);
        number_pattern = CONFIG.getString("MCR.Metadata.ObjectID.NumberPattern", "0000000000");
        number_format = new DecimalFormat(number_pattern);
    }

    /**
     * The constructor for an empty MCRObjectId.
     */
    public MCRObjectID() {
    }

    /**
     * The constructor for MCRObjectID from a given string.
     * 
     * @exception MCRException
     *                if the given string is not valid.
     */
    public MCRObjectID(String id) throws MCRException {
        mcr_valid_id = false;

        if (!setID(id)) {
            throw new MCRException("The ID is not valid: " + id);
        }

        mcr_valid_id = true;
    }

    /**
     * The method set the MCRObjectID from a given base ID string. The number
     * was computed from this methode. It is the next free number of an item in
     * the database for the given project ID and type ID.
     * 
     * @exception MCRException
     *                if the given string is not valid or can't connect to the
     *                MCRXMLTableManager.
     */
    public void setNextFreeId() throws MCRException {
        setNextFreeId(getBase());
    }

    /**
     * The method set the MCRObjectID from a given base ID string. A base ID is
     * <em>project_id</em>_<em>type_id</em>. The number was computed from
     * this methode. It is the next free number of an item in the database for
     * the given project ID and type ID.
     * 
     * @param base_id
     *            the basic ID
     * @exception MCRException
     *                if the given string is not valid or can't connect to the
     *                MCRXMLTableManager.
     */
    public synchronized void setNextFreeId(String base_id) throws MCRException {
        setNextFreeId(base_id, 0);
    }
    
    public synchronized void setNextFreeId(String base_id, int maxInWorkflow) throws MCRException {
        // check the base_id
        mcr_valid_id = false;

        if( ! setID( base_id + "_1" ) ) {
            throw new MCRException("Error in project base string:" + base_id );
        }

        int last = Math.max(getLastID(base_id).getNumberAsInteger(), maxInWorkflow) + 1;
        int rest = last % number_distance;
        if (rest != 0)
            last += number_distance - rest;
        lastnumber.put(base_id, last);
        setID(base_id + "_" + String.valueOf(last));
    }

    /**
     * Returns the last ID used or reserved for the given object base type.
     */
    public static MCRObjectID getLastID(String base_id) {
        MCRObjectID oid = new MCRObjectID(base_id + "_1");
        int last = lastnumber.containsKey(base_id) ? lastnumber.get(base_id) : 0;
        int stored = MCRXMLTableManager.instance().getHighestStoredID(oid.getProjectId(), oid.getTypeId());
        oid.setNumber(Math.max(last, stored));
        return oid;
    }

    /**
     * This method set a new type in a existing MCRObjectID.
     * 
     * @param type
     *            the new type
     */
    public final boolean setType(String type) {
        if (type == null) {
            return false;
        }

        String test = type.toLowerCase().intern();

        if (!CONFIG.getBoolean("MCR.Metadata.Type." + test, false)) {
            return false;
        }

        mcr_type_id = test;

        return true;
    }

    /**
     * This method set a new number in a existing MCRObjectID.
     * 
     * @param num
     *            the new number
     */
    public final boolean setNumber(int num) {
        if (!mcr_valid_id) {
            return false;
        }

        if (num < 0) {
            return false;
        }

        mcr_number = num;
        mcr_id = null;

        return true;
    }

    /**
     * This method get the string with <em>project_id</em>. If the ID is not
     * valid, an empty string was returned.
     * 
     * @return the string of the project id
     */
    public final String getProjectId() {
        if (!mcr_valid_id) {
            return "";
        }

        return mcr_project_id;
    }

    /**
     * This method get the string with <em>type_id</em>. If the ID is not
     * valid, an empty string was returned.
     * 
     * @return the string of the type id
     */
    public final String getTypeId() {
        if (!mcr_valid_id) {
            return "";
        }

        return mcr_type_id;
    }

    /**
     * This method get the string with <em>number</em>. If the ID is not
     * valid, an empty string was returned.
     * 
     * @return the string of the number
     */
    public final String getNumberAsString() {
        if (!mcr_valid_id) {
            return "";
        }

        return number_format.format(mcr_number);
    }

    /**
     * This method get the integer with <em>number</em>. If the ID is not
     * valid, a -1 was returned.
     * 
     * @return the number as integer
     */
    public final int getNumberAsInteger() {
        if (!mcr_valid_id) {
            return -1;
        }

        return mcr_number;
    }

    /**
     * This method get the basic string with <em>project_id</em>_
     * <em>type_id</em>. If the Id is not valid, an empty string was
     * returned.
     * 
     * @return the string of the schema name
     */
    public String getBase() {
        if (!mcr_valid_id) {
            return "";
        }

        return mcr_project_id + "_" + mcr_type_id;
    }

    /**
     * This method get the ID string with <em>project_id</em>_
     * <em>type_id</em>_<em>number</em>. If the ID is not valid, an empty
     * string was returned.
     * 
     * @return the string of the schema name
     */
    public String getId() {
        if (!mcr_valid_id) {
            return "";
        }

        if (mcr_id == null) {
            mcr_id = new StringBuffer(MAX_LENGTH).append(mcr_project_id).append('_').append(mcr_type_id).append('_').append(number_format.format(mcr_number)).toString();
        }

        return mcr_id;
    }

    /**
     * This method return the validation value of a MCRObjectId. The MCRObjectID
     * is valid if:
     * <ul>
     * <li>The syntax of the ID is <em>project_id</em>_<em>type_id</em>_
     * <em>number</em> as <em>String_String_Integer</em>.
     * <li>The ID is not longer as MAX_LENGTH.
     * </ul>
     * 
     * @return the validation value, true if the MCRObjectId is correct,
     *         otherwise return false
     */
    public boolean isValid() {
        return mcr_valid_id;
    }

    /**
     * This method return the validation value of a MCRObjectId and store the
     * components in this class. The <em>type_id</em> was set to lower case.
     * The MCRObjectID is valid if:
     * <ul>
     * <li>The argument is not null.
     * <li>The syntax of the ID is <em>project_id</em>_<em>type_id</em>_
     * <em>number</em> as <em>String_String_Integer</em>.
     * <li>The ID is not longer as MAX_LENGTH.
     * <li>The ID has only characters, they must not encoded.
     * </ul>
     * 
     * @param id
     *            the MCRObjectId
     * @return the validation value, true if the MCRObjectId is correct,
     *         otherwise return false
     */
    public final boolean setID(String id) {
        mcr_valid_id = false;

        if ((id == null) || ((id = id.trim()).length() == 0)) {
            return false;
        }

        if (id.length() > MAX_LENGTH) {
            return false;
        }

        String mcr_id;

        try {
            mcr_id = URLEncoder.encode(id, CONFIG.getString("MCR.Request.CharEncoding", "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            LOGGER.error("MCR.Request.CharEncoding property does not contain a valid encoding:", e1);

            return false;
        }

        if (!mcr_id.equals(id)) {
            return false;
        }

        int len = mcr_id.length();
        int i = mcr_id.indexOf("_");

        if (i == -1) {
            return false;
        }

        mcr_project_id = mcr_id.substring(0, i).intern();

        int j = mcr_id.indexOf("_", i + 1);

        if (j == -1) {
            return false;
        }

        mcr_type_id = mcr_id.substring(i + 1, j).toLowerCase().intern();

        if (!CONFIG.getBoolean("MCR.Metadata.Type." + mcr_type_id.toLowerCase(), false)) {
            return false;
        }

        mcr_number = -1;

        try {
            mcr_number = Integer.parseInt(mcr_id.substring(j + 1, len));
        } catch (NumberFormatException e) {
            return false;
        }

        if (mcr_number < 0) {
            return false;
        }

        this.mcr_id = null;
        mcr_valid_id = true;

        return mcr_valid_id;
    }

    /**
     * This method checks the value of a MCRObjectId. The MCRObjectId is valid
     * if:
     * <ul>
     * <li>The argument is not null.
     * <li>The syntax of the ID is <em>project_id</em>_<em>type_id</em>_
     * <em>number</em> as <em>String_String_Integer</em>.
     * <li>The ID is not longer as MAX_LENGTH. >li> The ID has only characters,
     * they must not encoded.
     * </ul>
     * 
     * @param id
     *            the MCRObjectId
     * @throws MCRException
     *             if ID is not valid
     */
    public static void isValidOrDie(String id) {
        new MCRObjectID(id);
    }

    /**
     * This method check this data again the input and retuns the result as
     * boolean.
     * 
     * @param in
     *            the MCRObjectID to check
     * @return true if all parts are equal, else return false.
     */
    public boolean equals(MCRObjectID in) {
        return (mcr_project_id.equals(in.mcr_project_id) && (mcr_type_id.equals(in.mcr_type_id)) && (mcr_number == in.mcr_number));
    }

    /**
     * This method check this data again the input and retuns the result as
     * boolean.
     * 
     * @param in
     *            the MCRObjectID to check
     * @return true if all parts are equal, else return false.
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object in) {
        if (!(in instanceof MCRObjectID)) {
            return false;
        }
        return equals((MCRObjectID) in);
    }

    /**
     * @see #getId()
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getId();
    }

    /**
     * returns getId().hashCode()
     * 
     * @see #getId()
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getId().hashCode();
    }
}
