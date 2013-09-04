<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:marc="http://www.loc.gov/MARC21/slim"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

  <xsl:template match="DISS_name">
    <!--  lfms, lfm, fmls -->
    <xsl:param name="format">lfms</xsl:param>
    <xsl:variable name="f" select="DISS_fname/text()"/>
    <xsl:variable name="m" select="DISS_middle/text()"/>
    <xsl:variable name="l">
      <xsl:choose>
        <xsl:when test="contains(DISS_surname/text(), ',')">
          <xsl:value-of select="substring-before(DISS_surname/text(), ',')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="DISS_surname/text()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="s">
      <xsl:choose>
        <xsl:when test="DISS_suffix/text() != ''">
          <xsl:value-of select="DISS_suffix/text()"/>
        </xsl:when>
        <xsl:when test="contains(DISS_surname/text(), ', ')">
          <xsl:value-of select="substring-after(DISS_surname/text(), ', ')"/>
        </xsl:when>
        <xsl:when test="contains(DISS_surname/text(), ',')">
          <xsl:value-of select="substring-after(DISS_surname/text(), ',')"/>
        </xsl:when>
	  </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$format='fmls' and $s != ''">
        <xsl:value-of select="concat($f, ' ', $m, ' ', $l, ', ', $s)"/>
      </xsl:when>
      <xsl:when test="$format='fmls'">
        <xsl:value-of select="concat($f, ' ', $m, ' ', $l)"/>
      </xsl:when>
      <xsl:when test="$format='s'">
        <xsl:value-of select="$s"/>
      </xsl:when>
      <xsl:when test="$format='lfms' and $s != ''">
        <xsl:value-of select="concat($l, ', ', $f, ' ', $m, ', ', $s)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat($l, ', ', $f, ' ', $m)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="notags">
    <xsl:param name="str"/>
    
    <xsl:variable name="pre" select="substring-before($str, '&lt;')"/>
    <xsl:choose>
      <xsl:when test="$pre = ''">
        <xsl:value-of select="$str"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$pre"/>
        <xsl:call-template name="notags">
          <xsl:with-param name="str" select="substring-after($str, '&gt;')"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
