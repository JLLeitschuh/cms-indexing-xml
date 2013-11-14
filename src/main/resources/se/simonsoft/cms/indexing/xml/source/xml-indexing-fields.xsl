<?xml version="1.0" encoding="UTF-8"?>
<!--

    Copyright (C) 2009-2013 Simonsoft Nordic AB

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:cms="http://www.simonsoft.se/namespace/cms"
	>

	<!-- document's status -->
	<xsl:param name="status"/>

	<xsl:template match="/">
	
		<!-- <xsl:variable name="whitespace" select="'&#x20;&#xD;&#xA;&#x9;'"/>-->
		<xsl:variable name="whitespace" select="' '"/>
	
		<!-- Tokenize the text nodes before concat:ing them to avoid issue with missing space (btw e.g. a title and a p) -->
		<!-- Inspired by: http://stackoverflow.com/questions/12784190/xslt-tokenize-nodeset -->
		<xsl:variable name="text" select="for $elemtext in //text() return tokenize(normalize-space($elemtext), $whitespace)"/>
	
		<doc>

			<!-- skip name and attributes, already extracted -->

			<!-- skip plain source, already extracted -->
			
			<!-- What about number of elements? -->	
			
			
			<!-- Just concat of the tokens/words. Somehow becomes space-separated. -->
			<field name="text"><xsl:value-of select="$text"/></field>
			
			<!-- Word count is simple when each word is a text node. -->
			<field name="words_text"><xsl:value-of select="count($text)"/></field>
			
			<field name="source_reuse">
				<xsl:apply-templates select="." mode="source-reuse-root"/>
			</field>
			
			<field name="reusevalue">
				<xsl:apply-templates select="." mode="rule-reusevalue"/>
			</field>

			<field name="reuseready">
				<xsl:apply-templates select="." mode="rule-reuseready"/>
			</field>
			
		</doc>
		
	</xsl:template>

	<xsl:template match="*" mode="source-reuse-root">
		<xsl:text>&lt;</xsl:text>
		<xsl:value-of select="name()" />
		<!-- no attributes for root node -->
		<xsl:text>&gt;</xsl:text>
		<xsl:apply-templates mode="source-reuse" />
		<xsl:text>&lt;/</xsl:text>
		<xsl:value-of select="name()" />
		<xsl:text>&gt;</xsl:text>
	</xsl:template>

	<xsl:template match="*" mode="source-reuse">
		<xsl:text>&lt;</xsl:text>
		<xsl:value-of select="name()" />
		<!-- filtering of attributes is done in match statements of templates, easier to customize)-->
		<xsl:apply-templates mode="source-reuse" select="@*"/>
		<xsl:text>&gt;</xsl:text>
		<xsl:apply-templates mode="source-reuse" />
		<xsl:text>&lt;/</xsl:text>
		<xsl:value-of select="name()" />
		<xsl:text>&gt;</xsl:text>
	</xsl:template>
	
	<xsl:template match="@cms:rid | @cms:rlogicalid | @cms:rwords| @cms:twords | @cms:tstatus | @cms:trid | @cms:tpos | @cms:tlogicalid | @cms:tmatch" mode="source-reuse">
        <!-- Simply doing nothing to suppress the CMS attributes. -->
        <!-- Does not include cms:tvalidate because it should be considered in checksums. -->
    </xsl:template>
	
	<xsl:template match="@*" mode="source-reuse">
		<xsl:value-of select="' '"/>
		<xsl:value-of select="name()"/>
		<xsl:text>="</xsl:text>
		<xsl:value-of select="."/>
		<xsl:text>"</xsl:text>
	</xsl:template>

	<xsl:template match="*[@xml:space='preserve']/text()" mode="source-reuse" priority="5">
        <!-- Not normalizing space when xml:space is set. -->
        <!-- Can not determine normalize from schema, will have to be acceptable -->
        <!-- Important to use source instead of source-reuse for replacement. -->
        <xsl:value-of select="." />
    </xsl:template>
    
    <xsl:template match="text()" mode="source-reuse" priority="1">
        <!-- Text: Normalize each text node. -->
        <xsl:value-of select="normalize-space(.)" />
    </xsl:template>
	
	<xsl:template match="processing-instruction()" mode="source-reuse">
        <xsl:text>&lt;?</xsl:text>
		<xsl:value-of select="name()"/>
		<xsl:text> </xsl:text>
		<xsl:value-of select="normalize-space(.)" />
		<xsl:text>?&gt;</xsl:text>
    </xsl:template>
	
	<!-- Ranks elements according to how useful they would be as replacement, >0 to be at all useful -->
	<xsl:template match="*" mode="rule-reusevalue">
		<xsl:choose>
			<!-- Rid should not have been removed from the current node -->
			<xsl:when test="not(@cms:rid)">-2</xsl:when>	
			<!-- Rid should not have been removed from a child, but note that removal must always be done on complete includes -->
			<!-- Some installations don't set rids below certain stop tags -->
			<xsl:when test="//*[@cms:rlogicalid and not(@cms:rid)]">-3</xsl:when>
			<!-- Marking a document Obsolete means we don't want to reuse from it -->
			<xsl:when test="$status = 'Obsolete'">-1</xsl:when>
			<!-- Anything else is a candidate for reuse, with tstatus set on the best match and replacements done if reuseready>0 -->
			<xsl:otherwise>1</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Decides if a match is valid for use as replacement, >0 means true -->
	<xsl:template match="*" mode="rule-reuseready">
		<xsl:choose>
			<xsl:when test="$status = 'Released'">1</xsl:when>
			<xsl:otherwise>0</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>