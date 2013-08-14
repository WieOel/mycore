<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- General MyCoRe XSL Parameter -->
  <xsl:param name="CurrentLang" select="'de'"/>
  <xsl:param name="WebApplicationBaseURL"/>
  <xsl:param name="returnURL" select="$WebApplicationBaseURL"/>
  <!-- Classeditor XSL Parameter -->
  <xsl:param name="classeditor.debug" select="false()" />
  <xsl:param name="classeditor.class" select="''"/>
  <xsl:param name="classeditor.categ" select="''"/>
  <xsl:param name="classeditor.showId" select="false()" />

  <!-- Variables -->
  <xsl:variable name="classeditor.dojoVersion" select="'1.8.3'" />

  <xsl:variable name="classeditor.resourceURL" select="concat($WebApplicationBaseURL, 'rsc/classifications/')" />
  <xsl:variable name="classeditor.webURL" select="concat($WebApplicationBaseURL, 'modules/classeditor')"/>
  <xsl:variable name="classeditor.jsURL" select="concat($classeditor.webURL, '/js')"/>
  <xsl:variable name="classeditor.imgURL" select="concat($classeditor.webURL, '/img')"/>
  <xsl:variable name="classeditor.cssURL" select="concat($classeditor.webURL, '/css')"/>

  <!-- Call this template before you include dojo. Because of dojoConfig! -->
  <xsl:template name="classeditor.loadSettings">
    <xsl:param name="classeditor.class" select="$classeditor.class"/>
    <xsl:param name="classeditor.categ" select="$classeditor.categ"/>
    <xsl:param name="classeditor.showId" select="$classeditor.showId"/>
    <script type="text/javascript">
      var classeditor = classeditor || {
        dojoVersion: "<xsl:value-of select='$classeditor.dojoVersion' />",
        settings: {
          webAppBaseURL: "<xsl:value-of select='$WebApplicationBaseURL' />",
          resourceURL: "<xsl:value-of select='$classeditor.resourceURL' />",
          webURL: "<xsl:value-of select='$classeditor.webURL' />",
          jsURL: "<xsl:value-of select='$classeditor.jsURL' />",
          imgURL: "<xsl:value-of select='$classeditor.imgURL' />",
          cssURL: "<xsl:value-of select='$classeditor.cssURL' />",
          returnURL: "<xsl:value-of select='$returnURL' />",
          showId: "<xsl:value-of select='$classeditor.showId' />" === "true",
          language: "<xsl:value-of select='$CurrentLang' />",
          editable: true,
          debug: "<xsl:value-of select='$classeditor.debug' />" === "true"
        },
        classId: "<xsl:value-of select='$classeditor.class' />",
        categoryId: "<xsl:value-of select='$classeditor.categ' />"
      };

      dojoConfig = {
        async: true,
        isDebug: false,
        parseOnLoad: true,
        packages: [
          {name: "mycore", location: classeditor.settings.jsURL + "/mycore"}
        ],
        dojoBlankHtmlUrl: classeditor.settings.webURL + "/blank.html"
      };
    </script>
  </xsl:template>

  <xsl:template name="classeditor.includeDojoJS">
    <xsl:choose>
      <xsl:when test="$classeditor.debug = true()">
        <script src="http://ajax.googleapis.com/ajax/libs/dojo/{$classeditor.dojoVersion}/dojo/dojo.js.uncompressed.js"></script>  
      </xsl:when>
      <xsl:otherwise>
        <script src="http://ajax.googleapis.com/ajax/libs/dojo/{$classeditor.dojoVersion}/dojo/dojo.js"></script>  
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="classeditor.includeJS">
    <xsl:choose>
      <xsl:when test="$classeditor.debug = true()">
<!--    <script type="text/javascript" src="{$classeditor.jsURL}/classificationEditor.js"></script> -->
        <script type="text/javascript" src="{$classeditor.jsURL}/lib/mycore-dojo.js.uncompressed.js"></script>
      </xsl:when>
      <xsl:otherwise>
<!--    <script type="text/javascript" src="{$classeditor.jsURL}/classificationEditor.min.js"></script> -->
        <script type="text/javascript" src="{$classeditor.jsURL}/lib/mycore-dojo.js"></script>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="classeditor.includeDojoCSS">
    <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/{$classeditor.dojoVersion}/dijit/themes/claro/claro.css"></link>
  </xsl:template>

  <xsl:template name="classeditor.includeCSS">
    <link rel="stylesheet" type="text/css" href="{$classeditor.webURL}/css/classificationEditor.css"></link>
  </xsl:template>

</xsl:stylesheet>