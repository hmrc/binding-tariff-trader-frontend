<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:fo="http://www.w3.org/1999/XSL/Format"
        xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
        xmlns:custom="urn:custom-functions"
        version="2.0">

  <!-- This stylesheet is used to convert 'hmrc-frontend' HTML to FOP elements for Apache FOP pdf generation.
       It provides generic methods for converting HTML tags and specific implementations to match
       'hmrc-frontend' components. -->

  <xsl:variable name="hmrc-frontend-attributes" select="'*/hmrc-frontend-fop-conversions.xsl'"/>

  <xsl:key name="attr-by-name" match="attr" use="@name" />

  <xsl:param name="attributeSets"
      select="document($hmrc-frontend-attributes)//xsl:attribute-set | $documentAttributeSets"/>
  <xsl:param name="log" select="false()"/>

  <xsl:template
      match="*[self::h1 or self::h2 or self::h3 or self::p or self::strong or self::div or self::dt or self::dd]"
      mode="pdf"
      priority="0">
    <xsl:variable name="class" select="@class"/>
    <xsl:call-template name="log">
      <xsl:with-param name="message" select="concat('Processing node: ', name(), '[$class=&quot;', $class, '&quot;]', '&#xA;', '    Content: ', string(.))"/>
    </xsl:call-template>
    <xsl:call-template name="remapClass">
      <xsl:with-param name="class" select="$class"/>
      <xsl:with-param name="tag" select="name()"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="img" mode="pdf" priority="0">
    <xsl:variable name="collectAttributes">
      <xsl:call-template name="combineAttributes">
        <xsl:with-param name="class" select="@class"/>
      </xsl:call-template>
    </xsl:variable>
    <fo:block>
      <xsl:call-template name="applySortedAttributes">
        <xsl:with-param name="collectAttributes" select="$collectAttributes"/>
      </xsl:call-template>
      <fo:external-graphic content-width="{@width}" content-height="{@height}" scaling="uniform" src="{@src}" fox:alt-text="{@alt}"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="span" mode="pdf" priority="0">
    <xsl:variable name="class" select="@class"/>
    <xsl:call-template name="remapClass">
      <xsl:with-param name="class" select="$class"/>
      <xsl:with-param name ="tag" select="name()"/>
    </xsl:call-template>
  </xsl:template>

  <!-- Representation of 'Summary list' component in the GOV.UK Design system https://design-system.service.gov.uk/components/summary-list/ -->

  <xsl:template match="dl" mode="pdf" priority="0.5">
    <xsl:variable name="collectAttributes">
      <xsl:call-template name="combineAttributes">
        <xsl:with-param name="class" select="@class"/>
      </xsl:call-template>
    </xsl:variable>
    <fo:table table-layout="fixed" width="100%">
      <xsl:call-template name="applySortedAttributes">
        <xsl:with-param name="collectAttributes" select="$collectAttributes"/>
      </xsl:call-template>
      <xsl:choose>
        <xsl:when test="@data-column = '1'">
          <fo:table-column column-width="proportional-column-width(100)"/>
        </xsl:when>
        <xsl:when test="@data-column = '3'">
          <fo:table-column column-width="proportional-column-width(30)"/>
          <fo:table-column column-width="proportional-column-width(50)"/>
          <fo:table-column column-width="proportional-column-width(20)"/>
        </xsl:when>
        <xsl:otherwise>
          <fo:table-column column-width="proportional-column-width(3)"/>
          <fo:table-column column-width="proportional-column-width(7)"/>
        </xsl:otherwise>
      </xsl:choose>
      <fo:table-body>
        <xsl:apply-templates select="node()" mode="pdf"/>
      </fo:table-body>
    </fo:table>
  </xsl:template>

  <!-- <dd> & <dt> HTML tags are using the FOP matcher for Summary List -->

  <xsl:template match="div[@class='govuk-summary-list__row']" mode="pdf" priority="0.5">
    <fo:table-row xsl:use-attribute-sets="govuk-summary-list__row">
      <xsl:comment>Applying govuk-uk-summary-list__row template</xsl:comment>
      <xsl:apply-templates select="node()" mode="pdf"/>
    </fo:table-row>
  </xsl:template>

  <!-- Representation of 'Warning text' component in the GOV.UK Design system https://design-system.service.gov.uk/components/warning-text/ -->

  <xsl:template match="div[@class='govuk-warning-text']" mode="pdf" priority="0.5">
    <fo:block xsl:use-attribute-sets="govuk-warning-text">
      <fo:table table-layout="fixed" width="100%">
        <fo:table-column column-width="30px"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-body>
          <fo:table-row>
            <xsl:apply-templates select="node()" mode="pdf"/>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>

  <xsl:template match="span[@class='govuk-warning-text__assistive']" mode="pdf" priority="0.5">
    <xsl:comment>Ignoring accessibility content</xsl:comment>
  </xsl:template>

  <xsl:template match="span[@class='govuk-warning-text__icon']" mode="pdf" priority="0.5">
    <fo:table-cell>
      <fo:block>
        <fo:external-graphic content-width="25px"
                             content-height="scale-to-fit"
                             scaling="uniform"
                             src="data:image/png;base64,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"
                             fox:alt-text="Warning"/>
      </fo:block>
    </fo:table-cell>>
  </xsl:template>

  <xsl:template match="strong[@class='govuk-warning-text__text']" mode="pdf" priority="0.5">
    <fo:table-cell xsl:use-attribute-sets="govuk-warning-text__text">
      <fo:block padding-top="8px">
        <xsl:apply-templates select="node()" mode="pdf"/>
      </fo:block>
    </fo:table-cell>
  </xsl:template>

  <!-- Representation of 'Common Layouts' page structures in the GOV.UK Design system https://design-system.service.gov.uk/styles/layout/#common-layouts -->

  <xsl:template match="div[contains(@class, 'govuk-grid-row')]" mode="pdf" priority="0.5">
    <xsl:variable name="collectAttributes">
      <xsl:call-template name="combineAttributes">
        <xsl:with-param name="class" select="@class"/>
      </xsl:call-template>
    </xsl:variable>
    <fo:block>
      <xsl:call-template name="applySortedAttributes">
        <xsl:with-param name="collectAttributes" select="$collectAttributes"/>
      </xsl:call-template>
      <fo:table table-layout="fixed" width="100%">
        <xsl:if test="@data-column = 'half'">
          <fo:table-column column-width="proportional-column-width(5)"/>
          <fo:table-column column-width="proportional-column-width(5)"/>
        </xsl:if>
        <xsl:if test="@data-column = 'full'">
          <fo:table-column column-width="proportional-column-width(100)"/>
        </xsl:if>
        <xsl:if test="@data-column = 'one-third'">
          <fo:table-column column-width="proportional-column-width(33.33333)"/>
          <fo:table-column column-width="proportional-column-width(66.66667)"/>
        </xsl:if>
        <xsl:if test="@data-column = 'two-thirds'">
          <fo:table-column column-width="proportional-column-width(66.66667)"/>
          <fo:table-column column-width="proportional-column-width(33.33333)"/>
        </xsl:if>
        <fo:table-body>
          <fo:table-row>
            <xsl:comment>Applying govuk-grid-row template</xsl:comment>
            <xsl:apply-templates select="node()" mode="pdf"/>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>

  <xsl:template match="div[@class='govuk-grid-column-one-half']" mode="pdf" priority="0.5">
    <fo:table-cell>
      <fo:block xsl:use-attribute-sets="govuk-grid-column-one-half">
        <xsl:comment>Applying govuk-grid-column-one-half template</xsl:comment>
        <xsl:apply-templates select="node()" mode="pdf"/>
      </fo:block>
    </fo:table-cell>
  </xsl:template>

  <xsl:template match="div[@class='govuk-grid-column-full']" mode="pdf" priority="0.5">
    <fo:table-cell>
      <fo:block xsl:use-attribute-sets="govuk-grid-column-full">
        <xsl:comment>Applying govuk-grid-column-one-half template</xsl:comment>
        <xsl:apply-templates select="node()" mode="pdf"/>
      </fo:block>
    </fo:table-cell>
  </xsl:template>

  <xsl:template match="div[@class='govuk-grid-column-one-third']" mode="pdf" priority="0.5">
    <fo:table-cell>
      <fo:block xsl:use-attribute-sets="govuk-grid-column-one-third">
        <xsl:comment>Applying govuk-grid-column-one-half template</xsl:comment>
        <xsl:apply-templates select="node()" mode="pdf"/>
      </fo:block>
    </fo:table-cell>
  </xsl:template>

  <xsl:template match="div[@class='govuk-grid-column-two-thirds']" mode="pdf" priority="0.5">
    <fo:table-cell>
      <fo:block xsl:use-attribute-sets="govuk-grid-column-two-thirds">
        <xsl:comment>Applying govuk-grid-column-one-half template</xsl:comment>
        <xsl:apply-templates select="node()" mode="pdf"/>
      </fo:block>
    </fo:table-cell>
  </xsl:template>

  <xsl:template name="remapClass">
    <xsl:param name="class"/>
    <xsl:param name="tag"/>
    <xsl:variable name="collectAttributes">
      <xsl:call-template name="combineAttributes">
        <xsl:with-param name="class" select="$class"/>
        <xsl:with-param name="logAttributes" select="true()"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$tag = 'dt' or $tag = 'dd'">
        <fo:table-cell>
          <fo:block>
            <xsl:call-template name="applySortedAttributes">
              <xsl:with-param name="collectAttributes" select="$collectAttributes"/>
            </xsl:call-template>
            <xsl:apply-templates select="node()" mode="pdf"/>
          </fo:block>
        </fo:table-cell>
      </xsl:when>
      <xsl:when test="$tag = 'span'">
        <fo:inline>
          <xsl:call-template name="applySortedAttributes">
            <xsl:with-param name="collectAttributes" select="$collectAttributes"/>
          </xsl:call-template>
          <xsl:apply-templates select="node()" mode="pdf"/>
        </fo:inline>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message><xsl:value-of select="$collectAttributes"/></xsl:message>
        <fo:block>
          <xsl:call-template name="applySortedAttributes">
            <xsl:with-param name="collectAttributes" select="$collectAttributes"/>
          </xsl:call-template>
          <xsl:apply-templates select="node()" mode="pdf"/>
        </fo:block>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Allows attributes from different CSS classes to be combined and applied to the FOP element -->

  <xsl:template name="combineAttributes">
    <xsl:param name="class"/>
    <xsl:param name="logAttributes" select="false()"/>
    <xsl:for-each select="tokenize($class, ' ')">
      <xsl:variable name="currentClass" select="."/>
      <xsl:variable name="formatClassName" select="replace($currentClass, '!', 'i')"/>
      <xsl:if test="$logAttributes = true()">
        <xsl:call-template name="log">
          <xsl:with-param name="message" select="concat('    Attributes from Class: ', $formatClassName)"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:for-each select="$attributeSets[@name=$formatClassName]/xsl:attribute">
        <xsl:element name="attr">
          <xsl:attribute name="name">
            <xsl:value-of select="@name"/>
          </xsl:attribute>
          <xsl:attribute name="value">
            <xsl:value-of select="."/>
          </xsl:attribute>
        </xsl:element>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="applySortedAttributes">
    <xsl:param name="collectAttributes"/>
    <xsl:for-each select="$collectAttributes/attr[generate-id() = generate-id(key('attr-by-name', @name)[last()])]">
      <xsl:sort select="@name"/>
      <xsl:attribute name="{@name}">
        <xsl:value-of select="@value"/>
      </xsl:attribute>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="log">
    <xsl:param name="message"/>
    <xsl:if test="$log = true()">
      <xsl:message>
        <xsl:value-of select="$message"/>
      </xsl:message>
    </xsl:if>
  </xsl:template>


  <!-- Normal text -->
  <xsl:template match="text()" mode="pdf">
    <xsl:value-of select="custom:insert-soft-breaks(.)"/>
  </xsl:template>


</xsl:stylesheet>
