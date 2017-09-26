<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
	exclude-result-prefixes="xsl xs soap">

	<xsl:template match="/">
		<xsl:copy-of select="//tramite" />
	</xsl:template>

</xsl:stylesheet>