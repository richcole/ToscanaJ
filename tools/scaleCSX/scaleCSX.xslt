<?xml version="1.0" encoding="UTF-8"?>
<!-- This stylesheet can be used to rescale all diagram in a ToscanaJ CSX file.

    Usage: set the parameter "scale" to whatever scaling factor you want and run this
    stylesheet on the CSX file using an XSLT processor.
    
    If the parameter "diagram" is set only the diagram with the given title will be changed. -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"></xsl:output>
        <xsl:param name="diagram"></xsl:param>
        <xsl:param name="scale">4</xsl:param>
	<xsl:template match="diagram">
		<xsl:copy>
			<xsl:choose>
				<xsl:when test="$diagram != '' ">
					<xsl:choose>
						<xsl:when test="@title = $diagram">
							<xsl:apply-templates select="node() | @*"></xsl:apply-templates>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="node() | @*"></xsl:copy-of>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="node() | @*"></xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="position|offset">
		<xsl:element name="{name()}">
			<xsl:attribute name="x"><xsl:value-of select="@x * $scale"></xsl:value-of></xsl:attribute>
			<xsl:attribute name="y"><xsl:value-of select="@y * $scale"></xsl:value-of></xsl:attribute>
		</xsl:element>
	</xsl:template>
	<xsl:template match="*" priority="-5">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*"></xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="node() | @*" priority="-10">
		<xsl:copy-of select="."></xsl:copy-of>
	</xsl:template>
</xsl:stylesheet>
