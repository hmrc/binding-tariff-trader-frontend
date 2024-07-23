<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

  <!-- These are Apache FOP representations of the 'application_view' CSS styles -->

  <xsl:attribute-set name="print-document">
    <!-- padding-* removed -->
  </xsl:attribute-set>

  <xsl:attribute-set name="prevent-content-split">
    <xsl:attribute name="keep-together.within-page">1</xsl:attribute>
    <!-- Changed page-break-inside to keep-together.within-page for long text segments -->
  </xsl:attribute-set>

  <xsl:attribute-set name="organisation-logo__container">
    <xsl:attribute name="margin-left">8px</xsl:attribute>
    <xsl:attribute name="border-left">2px solid #009390</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="organisation-logo__crest-hmrc">
    <xsl:attribute name="margin-left">8px</xsl:attribute>
    <!-- Removed width and height as image is best scaled by Apache -->
  </xsl:attribute-set>

  <xsl:attribute-set name="organisation-logo__name">
    <xsl:attribute name="font-family">Helvetica,GDS Transport,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">400</xsl:attribute>
    <xsl:attribute name="font-size">1.3em</xsl:attribute>
    <xsl:attribute name="margin-left">8px</xsl:attribute>
    <xsl:attribute name="margin-right">50px</xsl:attribute>
    <xsl:attribute name="width">125px</xsl:attribute>
    <!-- Added margin-right to help with text flow
         Changed padding-left to margin-left -->
  </xsl:attribute-set>

  <xsl:attribute-set name="print-header">
    <xsl:attribute name="color">#00703C</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,Helvetica,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <xsl:attribute name="font-size">1.2em</xsl:attribute>
    <xsl:attribute name="text-align">right</xsl:attribute>
    <xsl:attribute name="margin-top">5px</xsl:attribute>
    <xsl:attribute name="margin-left">0px</xsl:attribute>
    <xsl:attribute name="margin-right">15px</xsl:attribute>
    <xsl:attribute name="margin-bottom">0px</xsl:attribute>
    <!--Added font-family -->
  </xsl:attribute-set>

  <xsl:attribute-set name="heading">
    <xsl:attribute name="font-size">1.5em</xsl:attribute>
    <xsl:attribute name="margin-top">1em</xsl:attribute>
    <xsl:attribute name="margin-bottom">0.4em</xsl:attribute>
    <!-- All added for improved PDF layout -->
  </xsl:attribute-set>

  <xsl:attribute-set name="stack">
    <xsl:attribute name="margin-right">5px</xsl:attribute>
    <!-- Added for improved PDF layout -->
  </xsl:attribute-set>

  <xsl:attribute-set name="reference-number">
    <xsl:attribute name="font-size">2.25em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="hero-text">
    <xsl:attribute name="font-family">GDS Transport,Helvetica,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <xsl:attribute name="font-size">1.4em</xsl:attribute>
    <xsl:attribute name="margin-bottom">0.26316em</xsl:attribute>
    <!-- Added font-family -->
  </xsl:attribute-set>

  <xsl:attribute-set name="information-box">
    <xsl:attribute name="border">1px solid #00703C</xsl:attribute>
    <xsl:attribute name="background-color">#e1f3f1</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,Helvetica,sans-serif</xsl:attribute>
    <xsl:attribute name="padding-top">5px</xsl:attribute>
    <xsl:attribute name="padding-bottom">5px</xsl:attribute>
    <xsl:attribute name="padding-left">10px</xsl:attribute>
    <xsl:attribute name="padding-right">10px</xsl:attribute>
    <xsl:attribute name="margin-left">0px</xsl:attribute>
    <xsl:attribute name="margin-right">0px</xsl:attribute>
    <xsl:attribute name="margin-top">0px</xsl:attribute>
    <xsl:attribute name="margin-bottom">1em</xsl:attribute>
    <!-- Reduced border thickness
         Removed unsupported properties: display, box-sizing, width-->
  </xsl:attribute-set>

  <xsl:attribute-set name="about-this-result">
    <xsl:attribute name="margin-top">1em</xsl:attribute>
    <xsl:attribute name="margin-bottom">1em</xsl:attribute>
    <xsl:attribute name="margin-left">0px</xsl:attribute>
    <xsl:attribute name="margin-right">0px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="about-this-result-box">
    <xsl:attribute name="background-color">#e1f3f1</xsl:attribute>
    <xsl:attribute name="padding-top">1px</xsl:attribute>
    <xsl:attribute name="padding-bottom">1px</xsl:attribute>
    <xsl:attribute name="padding-left">10px</xsl:attribute>
    <xsl:attribute name="padding-right">10px</xsl:attribute>
    <xsl:attribute name="margin-left">0px</xsl:attribute>
    <xsl:attribute name="margin-right">0px</xsl:attribute>
    <!-- Removed unsupported properties height -->
  </xsl:attribute-set>

  <xsl:attribute-set name="date-of-result">
    <xsl:attribute name="border-bottom">1px solid #00703C</xsl:attribute>
    <xsl:attribute name="margin-top">1em</xsl:attribute>
    <xsl:attribute name="margin-bottom">1em</xsl:attribute>
    <!-- Reduced border thickness -->
  </xsl:attribute-set>
</xsl:stylesheet>