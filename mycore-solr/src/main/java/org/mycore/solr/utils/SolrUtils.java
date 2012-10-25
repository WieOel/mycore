/**
 * 
 */
package org.mycore.solr.utils;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.mycore.solr.SolrServerFactory;

/**
 * @author shermann
 *
 */
public class SolrUtils {

    private static Logger LOGGER = Logger.getLogger(SolrUtils.class);

    /**
     * Get the number of hits for a given solr query.
     * 
     * @param solrQuery the solr query to execute
     * 
     * @return the number of hits or 0 in case there are 0 hits or an error occured
     */
    public static long getNumHits(String solrQuery) {
        SolrQuery q = new SolrQuery(solrQuery);
        q.setRows(0);
        CommonsHttpSolrServer solrServer = SolrServerFactory.getSolrServer();
        long numFound = 0;
        try {
            QueryResponse queryResponse = solrServer.query(q);
            numFound = queryResponse.getResults().getNumFound();
        } catch (SolrServerException e) {
            LOGGER.error("Could not execute query", e);
        }
        return numFound;
    }

    /**
     * @param query
     * @param rows
     * @return
     * @throws SolrServerException
     */
    public static SolrDocumentList getQueryResults(String query, int rows) throws SolrServerException {
        SolrQuery q = new SolrQuery(query);
        q.setRows(rows);
        return SolrServerFactory.getSolrServer().query(q).getResults();
    }
}