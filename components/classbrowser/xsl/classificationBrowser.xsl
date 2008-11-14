<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- 
  XSL to include a classification browser into any MyCoReWebPage.

  Usage:
  <classificationBrowser 
    classification="{ClassificationID}" 
    category="{CategoryID to start with, optional}" 
    objectType="{MCRObject type, optional}"
    sortBy="{id|label, optional sort order of categories}"
    addURI="{true|false, whether to include URI from classification data}" 
    addDescription="{true|false, whether to include description from category label}" 
    class="{CSS class, default is 'classificationBrowser'}" 
    style="{XSL.Style to use, default is classificationBrowserData.xsl}" 
  />
 -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="classificationBrowser">
  <div>
    <xsl:attribute name="class">
      <xsl:choose>
        <xsl:when test="string-length(@class) &gt; 0">
          <xsl:value-of select="@class" />
        </xsl:when>
        <xsl:otherwise>classificationBrowser</xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
    
    <script type="text/javascript" src="/javascript/prototype.js"></script>
    <script language="JavaScript">
      /* &lt;![CDATA[ */
     
      function toogle(categID)
      {
        var childrenID = 'cbChildren_<xsl:value-of select="@classification" />_' + categID;
        var button = document.getElementById( 'cbButton_<xsl:value-of select="@classification" />_' + categID );
        var children = document.getElementById( childrenID );
        
        if( button.value == '-' )
        {
          button.value = '+';
          children.className='cbHidden';
          children.innerHTML = '';
        }
        else
        {
          button.value = '-';
          children.className='cbVisible';
          new Ajax.Updater( childrenID, '/servlets/ClassificationBrowser', 
          { parameters: { 
            classification : '<xsl:value-of select="@classification" />',
            category       : categID,
            sortBy         : '<xsl:value-of select="@sortBy" />',
            objectType     : '<xsl:value-of select="@objectType" />',
            field          : '<xsl:value-of select="@field" />',
            parameters     : '<xsl:value-of select="@parameters" />',
            restriction    : '<xsl:value-of select="@restriction" />',
            countResults   : '<xsl:value-of select="@countResults" />',
            countLinks     : '<xsl:value-of select="@countLinks" />',
            addURI         : '<xsl:value-of select="@addURI" />',
            addDescription : '<xsl:value-of select="@addDescription" />',
            style          : '<xsl:value-of select="@style" />'
          } } );      
        } 
      }
      /* ]]&gt; */
    </script>
    
    <xsl:variable name="id" select="generate-id(.)" />
    
    <div id="{$id}" class="cbVisible">
      <script language="JavaScript">
        new Ajax.Updater( '<xsl:value-of select="$id" />', '/servlets/ClassificationBrowser', 
        { parameters : { 
          classification : '<xsl:value-of select="@classification" />', 
          category       : '<xsl:value-of select="@category" />',
          sortBy         : '<xsl:value-of select="@sortBy" />',
          objectType     : '<xsl:value-of select="@objectType" />',
          field          : '<xsl:value-of select="@field" />',
          parameters     : '<xsl:value-of select="@parameters" />',
          restriction    : '<xsl:value-of select="@restriction" />',
          countResults   : '<xsl:value-of select="@countResults" />',
          countLinks     : '<xsl:value-of select="@countLinks" />',
          addURI         : '<xsl:value-of select="@addURI" />',
          addDescription : '<xsl:value-of select="@addDescription" />',
          style          : '<xsl:value-of select="@style" />'
        } } );
      </script>
    </div>
  </div>
</xsl:template>

</xsl:stylesheet>
