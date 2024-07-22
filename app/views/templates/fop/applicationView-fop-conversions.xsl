<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

  <xsl:attribute-set name="print-document">
    <!--        <xsl:attribute name="padding-left">50px</xsl:attribute>-->
    <!--        <xsl:attribute name="padding-right">50px</xsl:attribute>-->
    <!--        <xsl:attribute name="padding-top">50px</xsl:attribute>-->
    <!--        <xsl:attribute name="padding-bottom">50px</xsl:attribute>-->
  </xsl:attribute-set>

  <xsl:attribute-set name="prevent-content-split">
    <!--        <xsl:attribute name="page-break-inside">avoid</xsl:attribute>-->
    <xsl:attribute name="keep-together.within-page">1</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="organisation-logo__container">
    <xsl:attribute name="margin-left">8px</xsl:attribute>
    <xsl:attribute name="border-left">2px solid #009390</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="stack">
    <xsl:attribute name="margin-right">5px</xsl:attribute> <!--new -->
  </xsl:attribute-set>

  <xsl:attribute-set name="organisation-logo__crest-hmrc">
    <xsl:attribute name="margin-left">8px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="organisation-logo__name">
    <xsl:attribute name="font-family">Helvetica,GDS Transport,arial,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">400</xsl:attribute>
    <xsl:attribute name="font-size">1.3em</xsl:attribute>
    <xsl:attribute name="margin-left">8px</xsl:attribute>
    <xsl:attribute name="margin-right">50px</xsl:attribute> <!--do more -->
    <xsl:attribute name="width">125px</xsl:attribute>
    <xsl:attribute name="border">2px solid red</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="print-header">
    <xsl:attribute name="color">#00703C</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,arial,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <xsl:attribute name="font-size">1.2em</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
    <xsl:attribute name="margin-top">5px</xsl:attribute>
    <!--        <xsl:attribute name="padding-bottom">80px</xsl:attribute>-->
    <xsl:attribute name="margin-bottom">0px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="heading">
    <xsl:attribute name="font-size">1.5em</xsl:attribute>
    <xsl:attribute name="margin-top">1em</xsl:attribute>
    <xsl:attribute name="margin-bottom">0.4em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="reference-number">
    <xsl:attribute name="font-size">2.25em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="hero-text">
    <xsl:attribute name="font-family">GDS Transport,arial,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <xsl:attribute name="font-size">1.4em</xsl:attribute>
    <xsl:attribute name="margin-bottom">0.26316em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="information-box">
    <!--        <xsl:attribute name="width">100%</xsl:attribute>-->
    <xsl:attribute name="border">1px solid #00703C</xsl:attribute> <!--changed looks better -->
    <xsl:attribute name="background-color">#e1f3f1</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,arial,sans-serif</xsl:attribute>
    <xsl:attribute name="padding-top">5px</xsl:attribute> <!--expanded padding again -->
    <xsl:attribute name="padding-bottom">5px</xsl:attribute>
    <xsl:attribute name="padding-left">10px</xsl:attribute>
    <xsl:attribute name="padding-right">10px</xsl:attribute>
    <xsl:attribute name="margin-left">0px</xsl:attribute>
    <xsl:attribute name="margin-right">0px</xsl:attribute>
    <xsl:attribute name="margin-top">0px</xsl:attribute>
    <xsl:attribute name="margin-bottom">1em</xsl:attribute>
    <!--TODO Some styles like this for example are unavailable due to CSS versions or no direct equivalent-->
    <!--<xsl:attribute name="box-sizing">border-box</xsl:attribute>-->
  </xsl:attribute-set>

  <xsl:attribute-set name="about-this-result">
    <xsl:attribute name="margin-top">1em</xsl:attribute>
    <xsl:attribute name="margin-bottom">1em</xsl:attribute>
    <xsl:attribute name="margin-left">0px</xsl:attribute>
    <xsl:attribute name="margin-right">0px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="about-this-result-box">
    <xsl:attribute name="background-color">#e1f3f1</xsl:attribute>
    <xsl:attribute name="padding-top">1px</xsl:attribute> <!--expanded padding again -->
    <xsl:attribute name="padding-bottom">1px</xsl:attribute>
    <xsl:attribute name="padding-left">10px</xsl:attribute>
    <xsl:attribute name="padding-right">10px</xsl:attribute>
    <!--        <xsl:attribute name="font-size">15px</xsl:attribute>--> <!--remove was doing nothing in css -->
    <xsl:attribute name="margin-left">0px</xsl:attribute>
    <xsl:attribute name="margin-right">0px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="about-this-result-box-do-not-send">
    <!--        <xsl:attribute name="height">480px</xsl:attribute>--> <!--unnecessary for fop -->
  </xsl:attribute-set>

  <xsl:attribute-set name="date-of-result">
    <xsl:attribute name="border-bottom">1px solid #00703C</xsl:attribute> <!-- changed looks better -->
    <xsl:attribute name="margin-top">1em</xsl:attribute>
    <xsl:attribute name="margin-bottom">1em</xsl:attribute>
  </xsl:attribute-set>
</xsl:stylesheet>