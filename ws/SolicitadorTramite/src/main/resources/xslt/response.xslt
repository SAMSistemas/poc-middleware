<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
	exclude-result-prefixes="xsl xs soap">

	<xsl:param name="status" />
	<xsl:param name="descripcion" />

	<xsl:template match="/">
		<soap:Envelope>
			<soap:Body>
				<tramiteResponse>
					<status><xsl:value-of select="$status" /></status>
					<descripcion><xsl:value-of select="$descripcion" /></descripcion>
				</tramiteResponse>
			</soap:Body>
		</soap:Envelope>
	</xsl:template>

</xsl:stylesheet>