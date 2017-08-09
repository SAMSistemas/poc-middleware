<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
	xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:xsd="http://www.w3.org/2001/XMLSchema">

	<xsl:param name="CamelHttpUrl" />

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	<xsl:template match="soap:address/@location">
		<xsl:attribute name="location">
			    <xsl:value-of select="$CamelHttpUrl" />
			</xsl:attribute>
	</xsl:template>
	<xsl:template match="xsd:import/@schemaLocation">
	    <xsl:variable name="SchemaLocation" select="current()" />
		<xsl:attribute name="schemaLocation">
			    <xsl:value-of select="$CamelHttpUrl" />
			    <xsl:text>?xsd=</xsl:text>
			    <xsl:value-of select="$SchemaLocation" />
			</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>