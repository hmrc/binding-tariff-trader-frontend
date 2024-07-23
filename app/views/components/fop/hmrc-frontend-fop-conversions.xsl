<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

  <!-- These are Apache FOP representations of the 'hmrc-frontend' CSS styles -->

  <xsl:attribute-set name="govuk-i-font-weight-bold">
    <xsl:attribute name="font-weight">700</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-i-margin-left-0">
    <xsl:attribute name="margin-left">0px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-grid-row">
    <!-- Removed margin-* -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-grid-column-full">
    <xsl:attribute name="padding-top">0px</xsl:attribute>
    <xsl:attribute name="padding-bottom">0px</xsl:attribute>
    <xsl:attribute name="padding-left">15px</xsl:attribute>
    <xsl:attribute name="padding-right">15px</xsl:attribute>
    <!-- Removed unsupported properties: box-sizing, width-->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-grid-column-one-half">
    <xsl:attribute name="padding-top">0px</xsl:attribute>
    <xsl:attribute name="padding-bottom">0px</xsl:attribute>
    <xsl:attribute name="padding-left">15px</xsl:attribute>
    <xsl:attribute name="padding-right">15px</xsl:attribute>
    <!-- Removed unsupported properties: box-sizing, width, float-->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-grid-column-one-third">
    <xsl:attribute name="padding-top">0px</xsl:attribute>
    <xsl:attribute name="padding-bottom">0px</xsl:attribute>
    <xsl:attribute name="padding-left">15px</xsl:attribute>
    <xsl:attribute name="padding-right">15px</xsl:attribute>
    <!-- Removed unsupported properties: box-sizing, width, float-->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-grid-column-two-thirds">
    <xsl:attribute name="padding-top">0px</xsl:attribute>
    <xsl:attribute name="padding-bottom">0px</xsl:attribute>
    <xsl:attribute name="padding-left">15px</xsl:attribute>
    <xsl:attribute name="padding-right">15px</xsl:attribute>
    <!-- Removed unsupported properties: box-sizing, width, float-->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-body">
    <xsl:attribute name="color">#0b0c0c</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,Helvetica,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">400</xsl:attribute>
    <xsl:attribute name="font-size">1em</xsl:attribute>
    <xsl:attribute name="line-height">1.25</xsl:attribute>
    <xsl:attribute name="margin-top">0px</xsl:attribute>
    <xsl:attribute name="margin-bottom">15px</xsl:attribute>
    <!-- font-size rem unit converted to em -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-heading-m">
    <xsl:attribute name="color">#0b0c0c</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,Helvetica,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <xsl:attribute name="font-size">1.125em</xsl:attribute>
    <xsl:attribute name="line-height">1.11111</xsl:attribute>
    <xsl:attribute name="margin-top">0px</xsl:attribute>
    <xsl:attribute name="margin-bottom">15px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-inset-text">
    <xsl:attribute name="color">#0b0c0c</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,Helvetica,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">400</xsl:attribute>
    <xsl:attribute name="font-size">1em</xsl:attribute>
    <xsl:attribute name="line-height">1.25</xsl:attribute>
    <xsl:attribute name="padding-left">15px</xsl:attribute>
    <xsl:attribute name="padding-right">15px</xsl:attribute>
    <xsl:attribute name="padding-top">15px</xsl:attribute>
    <xsl:attribute name="padding-bottom">15px</xsl:attribute>
    <xsl:attribute name="margin-top">20px</xsl:attribute>
    <xsl:attribute name="margin-bottom">20px</xsl:attribute>
    <xsl:attribute name="margin-left">0px</xsl:attribute>
    <xsl:attribute name="margin-right">0px</xsl:attribute>
    <xsl:attribute name="clear">both</xsl:attribute>
    <xsl:attribute name="border-left">7px solid #b1b4b6</xsl:attribute>
    <!-- font-size rem unit converted to em
         Reduced border thickness -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-warning-text">
    <xsl:attribute name="font-family">GDS Transport,Helvetica,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">400</xsl:attribute>
    <xsl:attribute name="font-size">1em</xsl:attribute>
    <xsl:attribute name="line-height">1.25</xsl:attribute>
    <xsl:attribute name="margin-bottom">20px</xsl:attribute>
    <xsl:attribute name="position">relative</xsl:attribute> <!--check -->
    <xsl:attribute name="padding-left">0px</xsl:attribute>
    <xsl:attribute name="padding-right">0px</xsl:attribute>
    <xsl:attribute name="padding-top">10px</xsl:attribute>
    <xsl:attribute name="padding-bottom">10px</xsl:attribute>
    <!-- font-size rem unit converted to em -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-warning-text__icon">
    <!-- Replaced by png image -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-warning-text__text">
    <xsl:attribute name="color">#0b0c0c</xsl:attribute>
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <!-- Added font-weight as element is <strong>
         Removed unsupported properties: display -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-summary-list">
    <xsl:attribute name="color">#0b0c0c</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,Helvetica,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">400</xsl:attribute>
    <xsl:attribute name="font-size">1em</xsl:attribute>
    <xsl:attribute name="line-height">1.25</xsl:attribute>
    <xsl:attribute name="margin-top">0px</xsl:attribute>
    <xsl:attribute name="margin-bottom">20px</xsl:attribute>
    <xsl:attribute name="table-layout">fixed</xsl:attribute>
    <!-- font-size rem unit converted to em -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-summary-list--no-border">
      <xsl:attribute name="border">0px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-summary-list__row">
    <xsl:attribute name="border-bottom">1px solid #b1b4b6</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-summary-list__key">
    <xsl:attribute name="padding-top">10px</xsl:attribute>
    <xsl:attribute name="padding-right">20px</xsl:attribute>
    <xsl:attribute name="padding-bottom">10px</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,Helvetica,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <!-- Replaced margin-* with padding-*
         Added font-family -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-summary-list__value">
    <xsl:attribute name="padding-top">10px</xsl:attribute>
    <xsl:attribute name="padding-right">20px</xsl:attribute>
    <xsl:attribute name="padding-bottom">10px</xsl:attribute>
    <!-- Replaced margin-* with padding-* -->
  </xsl:attribute-set>

</xsl:stylesheet>
