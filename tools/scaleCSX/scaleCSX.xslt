<?xml version="1.0" encoding="UTF-8"?>
<!-- This stylesheet can be used to rescale all diagram in a ToscanaJ CSX file.

    Usage: set the parameter "scale" to whatever scaling factor you want and run this
    stylesheet on the CSX file using an XSLT processor -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:param name="scale">6</xsl:param>
	<xsl:template match="position|offset">
		<xsl:element name="{name()}">
			<xsl:attribute name="x">
				<xsl:value-of select="@x * $scale"/>
			</xsl:attribute>
			<xsl:attribute name="y">
				<xsl:value-of select="@y * $scale"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	<xsl:template match="*" priority="-5">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="node() | @*" priority="-10">
		<xsl:copy-of select="."/>
	</xsl:template>
</xsl:stylesheet>
