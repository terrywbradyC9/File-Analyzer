<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml"/>

  <xsl:param name="embargo-schema">local</xsl:param>
  <xsl:param name="embargo-element">embargo</xsl:param>
  <xsl:param name="embargo-terms">terms</xsl:param>
  <xsl:param name="embargo-custom-date">custom-date</xsl:param>
  <xsl:template match="/*">
    <dublin_core>
      <xsl:attribute name="schema"><xsl:value-of select="$embargo-schema"/></xsl:attribute>
      <xsl:apply-templates select="@embargo_code" mode="val"/>
      <xsl:apply-templates select="//DISS_restriction/DISS_sales_restriction/@remove" mode="val"/>
    </dublin_core>
  </xsl:template>

  <xsl:template match="@embargo_code[.=0]" mode="val">
  </xsl:template> 
  <xsl:template match="@embargo_code" mode="val">
    <dcvalue>
      <xsl:attribute name="element"><xsl:value-of select="$embargo-element"/></xsl:attribute>
      <xsl:attribute name="qualifier"><xsl:value-of select="$embargo-terms"/></xsl:attribute>
      <xsl:choose>
        <xsl:when test=". = 1">6-months</xsl:when>
        <xsl:when test=". = 2">1-year</xsl:when>
        <xsl:when test=". = 3">2-years</xsl:when>
        <xsl:when test=". = 4 and not(//DISS_restriction/DISS_sales_restriction/@remove)">forever</xsl:when>
        <xsl:when test=". = 4">custom</xsl:when>
      </xsl:choose>
    </dcvalue>
  </xsl:template>

  <xsl:template match="DISS_restriction/DISS_sales_restriction/@remove" mode="val">
    <xsl:if test="/*[@embargo_code=4]">
    <dcvalue>
      <xsl:attribute name="element"><xsl:value-of select="$embargo-element"/></xsl:attribute>
      <xsl:attribute name="qualifier"><xsl:value-of select="$embargo-custom-date"/></xsl:attribute>
      <xsl:choose>
        <xsl:when test="string-length(.) = 10">
          <xsl:value-of select="concat(substring(.,7,4),'-',substring(.,1,2),'-',substring(.,4,2))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </dcvalue>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
