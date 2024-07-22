<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

  <xsl:attribute-set name="govuk-i-font-weight-bold">
    <xsl:attribute name="font-weight">700</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-i-margin-left-0">
    <xsl:attribute name="margin-left">0px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-grid-row">
    <!--        <xsl:attribute name="width">100%</xsl:attribute>-->
    <!--        <xsl:attribute name="margin-right">-15px</xsl:attribute>-->
    <!--        <xsl:attribute name="margin-left">-15px</xsl:attribute>-->
    <!--        <xsl:attribute name="padding-right">15px</xsl:attribute>-->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-grid-column-full">
    <!--        <xsl:attribute name="width">100%</xsl:attribute>-->
    <xsl:attribute name="padding-top">0px</xsl:attribute>
    <xsl:attribute name="padding-bottom">0px</xsl:attribute>
    <xsl:attribute name="padding-left">15px</xsl:attribute>
    <xsl:attribute name="padding-right">15px</xsl:attribute>
    <!--        <xsl:attribute name="border-sizing">border-box</xsl:attribute>-->
    <!--        <xsl:attribute name="float">left</xsl:attribute>-->
    <!--        <xsl:attribute name="margin-left">15px</xsl:attribute>-->

  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-grid-column-one-half">
    <!--        <xsl:attribute name="width">50%</xsl:attribute>-->
    <xsl:attribute name="padding-top">0px</xsl:attribute>
    <xsl:attribute name="padding-bottom">0px</xsl:attribute>
    <xsl:attribute name="padding-left">15px</xsl:attribute>
    <xsl:attribute name="padding-right">15px</xsl:attribute>
    <!--        <xsl:attribute name="border-sizing">border-box</xsl:attribute>-->
    <!--        <xsl:attribute name="float">left</xsl:attribute>-->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-grid-column-one-third">
    <!--        <xsl:attribute name="width">33.33333%</xsl:attribute>-->
    <xsl:attribute name="padding-top">0px</xsl:attribute>
    <xsl:attribute name="padding-bottom">0px</xsl:attribute>
    <xsl:attribute name="padding-left">15px</xsl:attribute>
    <xsl:attribute name="padding-right">15px</xsl:attribute> <!-- property form not supported -->
    <!--        <xsl:attribute name="border-sizing">border-box</xsl:attribute>-->
    <!--        <xsl:attribute name="float">left</xsl:attribute>-->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-grid-column-two-thirds">
    <!--        <xsl:attribute name="width">66.66667%</xsl:attribute>-->
    <xsl:attribute name="padding-top">0px</xsl:attribute>
    <xsl:attribute name="padding-bottom">0px</xsl:attribute>
    <xsl:attribute name="padding-left">15px</xsl:attribute>
    <xsl:attribute name="padding-right">15px</xsl:attribute>
    <!--        <xsl:attribute name="border-sizing">border-box</xsl:attribute>-->
    <!--        <xsl:attribute name="float">left</xsl:attribute>-->
    <!--        <xsl:attribute name="margin-left">-15px</xsl:attribute>-->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-body">
    <xsl:attribute name="color">#0b0c0c</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,arial,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">400</xsl:attribute>
    <xsl:attribute name="font-size">1em</xsl:attribute>
    <xsl:attribute name="line-height">1.25</xsl:attribute>
    <xsl:attribute name="margin-top">0cm</xsl:attribute>
    <xsl:attribute name="margin-bottom">15px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-heading-m">
    <xsl:attribute name="color">#0b0c0c</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,arial,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <xsl:attribute name="font-size">1.125em</xsl:attribute>
    <xsl:attribute name="line-height">1.11111</xsl:attribute>
    <xsl:attribute name="space-before">0cm</xsl:attribute>
    <xsl:attribute name="space-after">15px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-inset-text">
    <xsl:attribute name="color">#0b0c0c</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,arial,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">400</xsl:attribute>
    <xsl:attribute name="font-size">1em</xsl:attribute>
    <xsl:attribute name="line-height">1.25</xsl:attribute>
    <xsl:attribute name="padding-left">15px</xsl:attribute> <!-- has to be all sides not just padding-->
    <xsl:attribute name="padding-right">15px</xsl:attribute>
    <xsl:attribute name="padding-top">15px</xsl:attribute>
    <xsl:attribute name="padding-bottom">15px</xsl:attribute>
    <xsl:attribute name="margin-top">20px</xsl:attribute>
    <xsl:attribute name="margin-bottom">20px</xsl:attribute>
    <xsl:attribute name="margin-left">0px</xsl:attribute>
    <xsl:attribute name="margin-right">0px</xsl:attribute>
    <xsl:attribute name="clear">both</xsl:attribute>
    <xsl:attribute name="border-left">7px solid #b1b4b6</xsl:attribute> <!-- halfed -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-warning-text">
    <xsl:attribute name="font-family">GDS Transport,arial,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">400</xsl:attribute>
    <xsl:attribute name="font-size">1em</xsl:attribute>
    <xsl:attribute name="line-height">1.25</xsl:attribute>
    <xsl:attribute name="margin-bottom">20px</xsl:attribute>
    <xsl:attribute name="position">relative</xsl:attribute>
    <xsl:attribute name="padding-left">0px</xsl:attribute>
    <xsl:attribute name="padding-right">0px</xsl:attribute>
    <xsl:attribute name="padding-top">10px</xsl:attribute>
    <xsl:attribute name="padding-bottom">10px</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-warning-text__icon"> <!--to check -->
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <!--        <xsl:attribute name="-webkit-box-sizing">border-box</xsl:attribute>-->
    <xsl:attribute name="box-sizing">border-box</xsl:attribute>
    <xsl:attribute name="display">inline-block</xsl:attribute>
    <xsl:attribute name="position">absolute</xsl:attribute>
    <xsl:attribute name="left">0</xsl:attribute>
    <xsl:attribute name="min-width">35px</xsl:attribute>
    <xsl:attribute name="min-height">35px</xsl:attribute>
    <xsl:attribute name="margin-top">-7px</xsl:attribute>
    <xsl:attribute name="border">3px solid #0b0c0c</xsl:attribute>
    <xsl:attribute name="border-radius">50%</xsl:attribute>
    <xsl:attribute name="color">#fff</xsl:attribute>
    <xsl:attribute name="background">#0b0c0c</xsl:attribute>
    <xsl:attribute name="font-size">30px</xsl:attribute>
    <xsl:attribute name="line-height">29px</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
    <!--        <xsl:attribute name="-webkit-user-select">none</xsl:attribute>-->
    <!--        <xsl:attribute name="-ms-user-select">none</xsl:attribute>-->
    <!--        <xsl:attribute name="-moz-user-select">none</xsl:attribute>-->
    <xsl:attribute name="user-select">none</xsl:attribute>
    <xsl:attribute name="forced-color-adjust">none</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-warning-text__text">
    <xsl:attribute name="color">#0b0c0c</xsl:attribute>
    <!--        <xsl:attribute name="display">block</xsl:attribute>-->
    <!--        <xsl:attribute name="padding-left">45px</xsl:attribute>--> <!--removed for inline -->
    <!--            <xsl:attribute name="margin-left">45px</xsl:attribute>-->
    <xsl:attribute name="font-weight">700</xsl:attribute> <!--additional -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-summary-list">
    <xsl:attribute name="color">#0b0c0c</xsl:attribute>
    <xsl:attribute name="font-family">GDS Transport,arial,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">400</xsl:attribute>
    <xsl:attribute name="font-size">1em</xsl:attribute>
    <xsl:attribute name="line-height">1.25</xsl:attribute>
    <xsl:attribute name="margin-top">0px</xsl:attribute>
    <xsl:attribute name="margin-bottom">20px</xsl:attribute>
    <xsl:attribute name="table-layout">fixed</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-summary-list--no-border">
    <!--        <xsl:attribute name="border">0</xsl:attribute>--> <!-- TODO -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-summary-list__row">
    <xsl:attribute name="border-bottom">1px solid #b1b4b6</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-summary-list__key">
    <xsl:attribute name="padding-top">10px</xsl:attribute>
    <xsl:attribute name="padding-right">20px</xsl:attribute>
    <xsl:attribute name="padding-bottom">10px</xsl:attribute>
    <!--        <xsl:attribute name="width">30%</xsl:attribute> &lt;!&ndash;TODO: width is ignored&ndash;&gt;-->
    <xsl:attribute name="font-family">GDS Transport,arial,sans-serif</xsl:attribute>
    <xsl:attribute name="font-weight">700</xsl:attribute>
    <!--        <xsl:attribute name="margin-bottom">5px</xsl:attribute>--> <!-- remove because it was for the webpage -->
  </xsl:attribute-set>

  <xsl:attribute-set name="govuk-summary-list__value">
    <xsl:attribute name="padding-top">10px</xsl:attribute>
    <xsl:attribute name="padding-right">20px</xsl:attribute>
    <xsl:attribute name="padding-bottom">10px</xsl:attribute>
    <xsl:attribute name="margin-top">0px</xsl:attribute>
    <xsl:attribute name="margin-bottom">0px</xsl:attribute>
  </xsl:attribute-set>

  <!-- NON GOVUK STYLES -->


  <xsl:attribute-set name="debug-red">
    <xsl:attribute name="border">1px solid red</xsl:attribute> <!-- Red border for visibility -->
  </xsl:attribute-set>

  <xsl:attribute-set name="debug-blue">
    <xsl:attribute name="border">1px solid blue</xsl:attribute> <!-- Blue border for visibility -->
  </xsl:attribute-set>



</xsl:stylesheet>
