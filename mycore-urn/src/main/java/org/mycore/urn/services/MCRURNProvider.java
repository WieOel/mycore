/**
 * 
 */
package org.mycore.urn.services;

import java.util.List;
import java.util.Vector;


/**
 * @author shermann
 * 
 */
public class MCRURNProvider extends MCRAbstractURNProvider {

    /* (non-Javadoc)
     * @see fsu.archiv.mycore.urn.IURNProvider#generateURN()
     */
    public MCRURN generateURN() {
        String urn = MCRURNManager.buildURN("Default");
        return MCRURN.valueOf(urn);
    }

    /* (non-Javadoc)
     * @see fsu.archiv.mycore.urn.IURNProvider#generateURN(int)
     */
    public MCRURN[] generateURN(int amount) {
        List<MCRURN> list = new Vector<MCRURN>(amount);
        while (amount != 0) {
            list.add(generateURN());
            amount--;
        }
        return list.toArray(new MCRURN[list.size()]);
    }
}
