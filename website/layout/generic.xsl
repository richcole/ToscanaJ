<?xml version="1.0" encoding="UTF-8"?>
<!-- A generic stylesheet to convert XHTML with XWeb navigation information below the main html tag into HTML 4 transitional with navigation at the top or to the left.

Parameters control the behaviour (see parameter declarations after this comment), some elements will be searched in the XWeb makefile and used if found and CSS is used for the details.

Elements in XWeb Makefile
========================

Section buttons:
- if a section has an image with a type starting with "normalSectionButton" attached to it, it will be used as button, otherwise text will be used
- if it has an image with a type starting with "activeSectionButton" this will be used for the currently active section, otherwise it will look the same as the other sections
- if an image with a type starting with "mouseOverSectionButton" is attached, this will be used for the mouseOver effect, if not the "activeSectionButton" might be used, if this doesn't exist there will be no mouseOver effect

Entry buttons:
- as above, just with images of types starting with "normalEntryButton", "activeEntryButton" and "mouseOverEntryButton"

Banner:
- if a section has an image with a type starting with "sectionBanner" attached, it will be used as banner for each page in the section
- if a page has an image with a type starting with "entryBanner" attached, it will be used as banner for the page
- if both are available the section banner will be above the entry banner

Logos:
- there are two logos possible: "firstLogo" and "secondLogo", all are searched as file entries with the given ids. The first logo will always be in the top left corner, the second behind the navigation, i.e. below if the navigation is to the left, and to the right if the navigation is at the top

CSS stylesheet:
- if a file entry with the id matching the value of $stylesheet.id (defaults to "style") is found, it will be linked as stylesheet to each page

CSS Classes Used
==================

span.firstLetter: the first letter in each paragraph ("p" elements, available if $feature.firstLetter is set to "on")
span.paragraphNumber: the number behind a paragraph if $feature.numberParagraphs is "on"
td.marginInSection: the table cell used as margin behind the last entry of a section
td.navNormalSection, td.navActiveSection, td.navNormalEntry, td.navActiveEntry: the table cells containing the buttons
td.firstLogo, td.secondLogo: the table cells containg the logos (if found)
tr.spacingBehindSections: the cell behind the sections if navigation is at the left side and secondary is below
table.navigation: the table containing the navigation
div.sectionBanner, div.entryBanner: the banner sections (if banners were found)
a.xwebLink: an internal link resolved by XWeb (if $feature.internalLink.token is set)
a.*Link: (where * is a protocol name like http, ftp, etc): a link using some specific protocol (if $style.markup.linkTypes is turned on)
a.localLink: a local link, without protocol given (if $style.markup.linkTypes is turned on)
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" indent="yes" doctype-system="http://www.w3.org/TR/html4/loose.dtd"></xsl:output>
	<!-- Setting up the navigation -->
	<!-- The position of the main (i.e. toplevel) navigation: left or top -->
	<xsl:param name="nav.main.pos">left</xsl:param>
	<!-- The position of the secondary navigation: either "nested" (between the sections) or "below". -->
	<xsl:param name="nav.sec.pos">nested</xsl:param>
	<!-- Determines if all second level entries are visible ("all") or only the ones in the currently selected toplevel element ("current"). "all" currently works only in nested navigation. -->
	<xsl:param name="nav.sec.visible">current</xsl:param>
	<!-- If set to "normal", all entries in a section will be shown below it, the sections themself are not linked. If set to "section" the section image/text will link to the first entry, which is not shown again. TODO: fix -->
	<xsl:param name="nav.sec.firstEntry">normal</xsl:param>
	<!-- Changing the detailed look of the site -->
	<!-- If stylesheet.url is not given, this determines the id we look for to find the CSS file (on files in the structure)-->
	<xsl:param name="stylesheet.id">style</xsl:param>
	<!-- This can be used to specify a url for the external CSS file directly, stylesheet.id will be ignored if this is given -->
	<xsl:param name="stylesheet.url"></xsl:param>
	<!-- Additional features -->
	<!-- If set to some string, the string will be used as designator for internal links pointing to an entry in the makefile with the given id. E.g. if set to "int:" a link with href="int:homepage" will be resolved to an entry with id "homepage". If this fails the link will be dropped. -->
	<xsl:param name="feature.internalLink.token"></xsl:param>
	<!-- If set to "on" the first letter in each paragraph ("p") will be enclosed in a span using the class "firstLetter" for CSS markup. This can be used as replacement for the CSS pseudo class first-letter, which causes problems in many browsers. -->
	<xsl:param name="style.markup.firstLetter">off</xsl:param>
	<!-- If set to "on" the each paragraph ("p") will start with an anchor having its number as name and it will have the number in parentheses behind it, linking to itself. -->
	<xsl:param name="feature.numberParagraphs">off</xsl:param>
	<!-- If set to "on" links ("a") will get a CSS class identifier for the protocol: if a colon is found in the href, the substring before the colon is used to create a class like "httpLink" for http links, if no colon is found it will be called "localLink". If a class attribute already exists it will not be changed.-->
	<xsl:param name="style.markup.linkTypes">off</xsl:param>
	<!-- Can be used to turn "off" all JavaScript (mouseOver effects). -->
	<xsl:param name="feature.javascript">on</xsl:param>
	<!-- If nonempty the given file name (relative to the stylesheet position) will be included as header. The input file has to be XML with the root element "header" and XHTML code in between. -->
	<xsl:param name="feature.include.header"></xsl:param>
	<!-- If nonempty the given file name (relative to the stylesheet position) will be included as footer. The input file has to be XML with the root element "footer" and XHTML code in between. -->
	<xsl:param name="feature.include.footer"></xsl:param>
	<!-- Internationalisation features -->
	<!-- The text put behind the alt text for normal pages -->
	<xsl:param name="i18n.normalEntryMarker"></xsl:param>
	<!-- The text put behind the alt text for active pages -->
	<xsl:param name="i18n.activeEntryMarker"> (active)</xsl:param>
	<!-- The text put behind the alt text for normal sections -->
	<xsl:param name="i18n.normalSectionMarker"> (section)</xsl:param>
	<!-- The text put behind the alt text for active sections -->
	<xsl:param name="i18n.activeSectionMarker"> (active section)</xsl:param>
	<!-- The alt text for a banner -->
	<xsl:param name="i18n.bannerMarker"> (banner)</xsl:param>
	<!-- Calculate some stuff -->
	<!-- Calculate length of internal link token -->
	<xsl:variable name="internalLinkTokenLength" select="string-length($feature.internalLink.token)"></xsl:variable>
	<!-- TODO: add parameter checks -->
	<!-- The start template -->
	<xsl:template match="html">
		<html>
			<head>
				<!-- copy the head children to get title and meta data -->
				<xsl:copy-of select="head/node()"></xsl:copy-of>
				<!-- attach stylesheet -->
				<xsl:choose>
					<xsl:when test="$stylesheet.url != '' ">
						<link rel="stylesheet" href="{$stylesheet.url}" type="text/css"></link>
					</xsl:when>
					<xsl:when test="//file[@id=$stylesheet.id]">
						<link rel="stylesheet" href="{//file[@id=$stylesheet.id]/@src}" type="text/css"></link>
					</xsl:when>
				</xsl:choose>
				<!-- preload all needed xweb images if JavaScript is used -->
				<xsl:if test="$feature.javascript='on'">
					<script language="JavaScript" type="text/javascript">
						<xsl:comment>Begin
                        <!-- the first level mouse over buttons -->
							<xsl:for-each select="/html/section">
								<xsl:choose>
									<xsl:when test="img[starts-with(@xwebtype, 'mouseOverSectionButton')]">
										<xsl:apply-templates mode="insertImagePreload" select="img[starts-with(@xwebtype, 'mouseOverSectionButton')]"></xsl:apply-templates>
									</xsl:when>
									<xsl:when test="img[starts-with(@xwebtype, 'activeSectionButton')]">
										<xsl:apply-templates mode="insertImagePreload" select="img[starts-with(@xwebtype, 'activeSectionButton')]"></xsl:apply-templates>
									</xsl:when>
								</xsl:choose>
							</xsl:for-each>
							<!-- second level mouse over buttons -->
							<!-- TODO: restrict -->
							<xsl:if test="$nav.sec.visible='all'">
								<!-- the page buttons for all sections -->
								<xsl:for-each select="/html/section/entry">
									<xsl:choose>
										<xsl:when test="img[starts-with(@xwebtype, 'mouseOverEntryButton')]">
											<xsl:apply-templates mode="insertImagePreload" select="img[starts-with(@xwebtype, 'mouseOverEntryButton')]"></xsl:apply-templates>
										</xsl:when>
										<xsl:when test="img[starts-with(@xwebtype, 'activeEntryButton')]">
											<xsl:apply-templates mode="insertImagePreload" select="img[starts-with(@xwebtype, 'activeEntryButton')]"></xsl:apply-templates>
										</xsl:when>
									</xsl:choose>
								</xsl:for-each>
							</xsl:if>
							<xsl:if test="$nav.sec.visible='current'">
								<!-- the page buttons for the currently active section -->
								<xsl:for-each select="/html/section[@active='true']/entry">
									<xsl:choose>
										<xsl:when test="img[starts-with(@xwebtype, 'mouseOverEntryButton')]">
											<xsl:apply-templates mode="insertImagePreload" select="img[starts-with(@xwebtype, 'mouseOverEntryButton')]"></xsl:apply-templates>
										</xsl:when>
										<xsl:when test="img[starts-with(@xwebtype, 'activeEntryButton')]">
											<xsl:apply-templates mode="insertImagePreload" select="img[starts-with(@xwebtype, 'activeEntryButton')]"></xsl:apply-templates>
										</xsl:when>
									</xsl:choose>
								</xsl:for-each>
							</xsl:if>
                   // End</xsl:comment>
					</script>
				</xsl:if>
			</head>
			<body>
				<!-- first insert the header if wanted (above everything) -->
				<xsl:if test="$feature.include.header != '' ">
					<xsl:copy-of select="document($feature.include.header)/header/node()"></xsl:copy-of>
				</xsl:if>
				<!-- The table part contains a number of weird tricks to get Netscape 4 to display this in a useful way.
                           The algorithm for calculating the width of columns in Netscape 4 is weird. Some pages on the
                           internet claim that they tried to reengineer it but failed. Basic concept: values are minimum
                           widths and the rest is distributed across the columns. If the sum of the widths is more than
                           the screen width, a scrollbar appears (and printouts are cut) -->
				<xsl:choose>
					<xsl:when test="$nav.main.pos = 'left' ">
						<table cellspacing="0" cellpadding="10" border="0" width="100%">
							<tr valign="top">
								<!-- the navigation -->
								<td align="center">
									<table cellspacing="0" cellpadding="0" border="0" class="navigation">
										<xsl:if test="//file[@id='firstLogo']">
											<tr>
												<td valign="top" class="firstLogo">
													<img src="{//file[@id='firstLogo']/@src}" border="0" alt="{//file[@id='firstLogo']/@name}"></img>
												</td>
											</tr>
										</xsl:if>
										<xsl:apply-templates mode="navLeft" select="section"></xsl:apply-templates>
										<xsl:if test="$nav.sec.pos = 'below' ">
											<!-- empty cell behind sections -->
											<tr class="spacingBehindSections">
												<td>&#160;</td>
											</tr>
											<xsl:for-each select="section[@active='true']/entry">
												<xsl:if test="position() !=1 or $nav.sec.firstEntry = 'normal' ">
													<tr>
														<xsl:apply-templates select="." mode="insertButton"></xsl:apply-templates>
													</tr>
												</xsl:if>
											</xsl:for-each>
										</xsl:if>
										<xsl:if test="//file[@id='secondLogo']">
											<tr>
												<td valign="bottom" class="secondLogo">
													<img src="{//file[@id='secondLogo']/@src}" border="0" alt="{//file[@id='secondLogo']/@name}"></img>
												</td>
											</tr>
										</xsl:if>
									</table>
								</td>
								<!-- the main body -->
								<td width="90%">
									<!-- insert banners if found -->
									<!-- check for section banner -->
									<xsl:if test="/html/section[@active='true']/img[@xwebtype='sectionBanner']">
										<div class="sectionBanner">
											<xsl:for-each select="/html/section[@active='true']/img[@xwebtype='sectionBanner']">
												<!-- should select exactly one -->
												<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.bannerMarker)}" width="{@width}" height="{@height}"></img>
											</xsl:for-each>
										</div>
									</xsl:if>
									<!-- check for page banner -->
									<xsl:if test="/html/section[@active='true']/entry[@active='true']/img[@xwebtype='entryBanner']">
										<div class="entryBanner">
											<xsl:for-each select="/html/section[@active='true']/entry[@active='true']/img[@xwebtype='entryBanner']">
												<!-- should select exactly one -->
												<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.bannerMarker)}" width="{@width}" height="{@height}"></img>
											</xsl:for-each>
										</div>
									</xsl:if>
									<!-- recurse for first letter and internal linking support (could be dropped if both not needed) -->
									<xsl:apply-templates select="body/node()" mode="body"></xsl:apply-templates>
								</td>
							</tr>
						</table>
					</xsl:when>
					<xsl:when test="$nav.main.pos = 'top'">
						<!-- insert banners if found -->
						<!-- check for section banner -->
						<xsl:if test="/html/section[@active='true']/img[@xwebtype='sectionBanner']">
							<div class="sectionBanner">
								<xsl:for-each select="/html/section[@active='true']/img[@xwebtype='sectionBanner']">
									<!-- should select exactly one -->
									<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.bannerMarker)}" width="{@width}" height="{@height}"></img>
								</xsl:for-each>
							</div>
						</xsl:if>
						<!-- check for page banner -->
						<xsl:if test="/html/section[@active='true']/entry[@active='true']/img[@xwebtype='entryBanner']">
							<div class="entryBanner">
								<xsl:for-each select="/html/section[@active='true']/entry[@active='true']/img[@xwebtype='entryBanner']">
									<!-- should select exactly one -->
									<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.bannerMarker)}" width="{@width}" height="{@height}"></img>
								</xsl:for-each>
							</div>
						</xsl:if>
						<table class="navigation">
							<tr valign="top">
								<!-- the navigation -->
								<xsl:if test="//file[@id='firstLogo']">
									<td valign="top" class="firstLogo">
										<img src="{//file[@id='firstLogo']/@src}" border="0" alt="{//file[@id='firstLogo']/@name}"></img>
									</td>
								</xsl:if>
								<xsl:apply-templates mode="navTop" select="section"></xsl:apply-templates>
								<xsl:if test="//file[@id='secondLogo']">
									<td class="secondLogo">
										<img src="{//file[@id='secondLogo']/@src}" border="0" alt="{//file[@id='secondLogo']/@name}"></img>
									</td>
								</xsl:if>
							</tr>
							<xsl:if test="$nav.sec.pos = 'below' ">
								<tr>
									<xsl:for-each select="section[@active='true']/entry">
										<xsl:if test="position() !=1 or $nav.sec.firstEntry = 'normal' ">
											<xsl:apply-templates select="." mode="insertButton"></xsl:apply-templates>
										</xsl:if>
									</xsl:for-each>
								</tr>
							</xsl:if>
						</table>
						<!-- the main body -->
						<!-- recurse for first letter and internal linking support (could be dropped if both not needed) -->
						<xsl:apply-templates select="body/node()" mode="body"></xsl:apply-templates>
					</xsl:when>
				</xsl:choose>
				<!-- insert the footer -->
				<xsl:if test="$feature.include.footer != '' ">
					<xsl:copy-of select="document($feature.include.footer)/footer/node()"></xsl:copy-of>
				</xsl:if>
			</body>
		</html>
	</xsl:template>
	<!-- left navigation mode: the section buttons -->
	<xsl:template match="section" mode="navLeft">
		<tr>
			<xsl:apply-templates mode="insertButton" select="."></xsl:apply-templates>
		</tr>
		<xsl:if test="$nav.sec.pos = 'nested' and ($nav.sec.visible = 'all' or @active = 'true') ">
			<xsl:apply-templates mode="navLeft" select="entry"></xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	<!-- left navigation mode: the page buttons -->
	<xsl:template match="entry" mode="navLeft">
		<xsl:if test="position() !=1 or $nav.sec.firstEntry = 'normal' ">
			<tr>
				<xsl:apply-templates select="." mode="insertButton"></xsl:apply-templates>
			</tr>
		</xsl:if>
		<!-- put an empty row below the last entry of a section to get some spacing -->
		<xsl:if test="position() = last()">
			<tr>
				<td class="marginInSection">
            &#160;
          </td>
			</tr>
		</xsl:if>
	</xsl:template>
	<!-- top navigation mode: the section buttons -->
	<xsl:template match="section" mode="navTop">
		<xsl:apply-templates mode="insertButton" select="."></xsl:apply-templates>
		<xsl:if test="$nav.sec.pos = 'nested' and ($nav.sec.visible = 'all' or @active = 'true' )">
			<xsl:apply-templates mode="navTop" select="entry"></xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	<!-- top navigation mode: the page buttons -->
	<xsl:template match="entry" mode="navTop">
		<xsl:if test="position() !=1 or $nav.sec.firstEntry = 'normal' ">
			<xsl:apply-templates select="." mode="insertButton"></xsl:apply-templates>
		</xsl:if>
		<!-- put an empty row behind the last entry of a section to get some spacing -->
		<xsl:if test="position() = last()">
			<td class="marginInSection">
            &#160;
          </td>
		</xsl:if>
	</xsl:template>
	<!-- Body mode: if first letter should be highlighted, switch into paragraph mode for paragraphs -->
	<xsl:template match="p" mode="body">
		<xsl:copy>
			<xsl:for-each select="@*">
				<xsl:copy-of select="."></xsl:copy-of>
			</xsl:for-each>
			<xsl:if test="$feature.numberParagraphs = 'on' ">
				<a>
					<xsl:attribute name="name"><xsl:number count="p" format="1"></xsl:number></xsl:attribute>
				</a>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$style.markup.firstLetter = 'on' ">
					<xsl:apply-templates select="node()" mode="par"></xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="node()" mode="body"></xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="$feature.numberParagraphs = 'on' and name(..) = 'body'">
				<span class="paragraphNumber">
					<xsl:text>&#32;</xsl:text>
					<a>
						<xsl:attribute name="href">#<xsl:number count="p" format="1"></xsl:number></xsl:attribute>
						<xsl:number count="p" format="(1)"></xsl:number>
					</a>
				</span>
			</xsl:if>
		</xsl:copy>
	</xsl:template>
	<!-- Paragraph mode: if text, check if first and highlight first letter -->
	<xsl:template match="text()" mode="par">
		<xsl:choose>
			<xsl:when test="position()=1">
				<!-- first node is text (or comment :-( ) -->
				<xsl:choose>
					<!-- just highlight latin letters -->
					<xsl:when test="contains('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ',substring(normalize-space(.),1,1))">
						<span class="firstLetter">
							<xsl:value-of select="substring(normalize-space(.),1,1)"></xsl:value-of>
						</span>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="substring(normalize-space(.),1,1)"></xsl:value-of>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="substring(normalize-space(.),2)"></xsl:value-of>
				<!-- check if we lost whitespace at the end -->
				<xsl:if test="substring(.,string-length(.)-1,1) != substring(normalize-space(.),string-length(normalize-space(.))-1,1)">
					<!-- the entity is just to avoid stupid programs from removing the whitespace within the xsl:text. TODO: fix problem -->
					<xsl:text>&#32;</xsl:text>
				</xsl:if>
				<xsl:apply-templates select="node()[position()!=1]" mode="body"></xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="." mode="body"></xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- Paragraph mode: simple recursion for non-text-elements -->
	<xsl:template match="*" mode="par">
		<xsl:apply-templates select="." mode="body"></xsl:apply-templates>
	</xsl:template>
	<!-- Body mode: resolve internal links if wanted -->
	<xsl:template match="a" mode="body">
		<xsl:choose>
			<xsl:when test="@href">
				<!-- we have a link -->
				<xsl:choose>
					<xsl:when test="$feature.internalLink.token = '' or substring(@href,1,$internalLinkTokenLength) != $feature.internalLink.token">
						<xsl:copy>
							<xsl:for-each select="@*">
								<xsl:copy-of select="."></xsl:copy-of>
							</xsl:for-each>
							<xsl:if test="$style.markup.linkTypes and not(@class)">
								<xsl:choose>
									<xsl:when test="contains(@href,':')">
										<!-- we have a protocol (or a WIndows drive letter) -->
										<xsl:attribute name="class"><xsl:value-of select="substring-before(@href,':')"></xsl:value-of>Link</xsl:attribute>
									</xsl:when>
									<xsl:otherwise>
										<!-- a local link -->
										<xsl:attribute name="class">localLink</xsl:attribute>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:if>
							<xsl:apply-templates select="node()" mode="body"></xsl:apply-templates>
						</xsl:copy>
					</xsl:when>
					<xsl:otherwise>
						<!-- we want internal links, have @href and it starts with the token -->
						<xsl:variable name="id" select="substring(@href,$internalLinkTokenLength+1)"></xsl:variable>
						<xsl:choose>
							<xsl:when test="//entry[@id=$id]">
								<a href="{//entry[@id=$id]/@src}" class="xwebLink">
									<xsl:apply-templates select="node()" mode="body"></xsl:apply-templates>
								</a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:message>Could not find id '<xsl:value-of select="$id"></xsl:value-of>' on any entry page.</xsl:message>
								<xsl:apply-templates select="node()" mode="body"></xsl:apply-templates>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
					<xsl:for-each select="@*">
						<xsl:copy-of select="."></xsl:copy-of>
					</xsl:for-each>
					<xsl:apply-templates select="node()" mode="body"></xsl:apply-templates>
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- Body mode: just keep recursing -->
	<xsl:template match="*" mode="body">
		<xsl:copy>
			<xsl:for-each select="@*">
				<xsl:copy-of select="."></xsl:copy-of>
			</xsl:for-each>
			<xsl:apply-templates select="node()" mode="body"></xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	<!-- Templates without recursion-->
	<xsl:template match="img" mode="insertImagePreload">
		<xsl:value-of select="@name"></xsl:value-of>
		<xsl:text> = new Image( </xsl:text>
		<xsl:value-of select="@width"></xsl:value-of>
		<xsl:text>, </xsl:text>
		<xsl:value-of select="@height"></xsl:value-of>
		<xsl:text>);</xsl:text>
		<xsl:value-of select="@name"></xsl:value-of>
		<xsl:text>.src = "</xsl:text>
		<xsl:value-of select="@src"></xsl:value-of>
		<xsl:text>";</xsl:text>
	</xsl:template>
	<xsl:template match="section" mode="insertButton">
		<!-- determine which buttons exist -->
		<xsl:variable name="normalImageExists" select="count(img[starts-with(@xwebtype, 'normalSectionButton')]) = 1"></xsl:variable>
		<xsl:variable name="activeImageExists" select="count(img[starts-with(@xwebtype, 'activeSectionButton')]) = 1"></xsl:variable>
		<xsl:variable name="mouseOverImageExists" select="count(img[starts-with(@xwebtype, 'mouseOverSectionButton')]) = 1"></xsl:variable>
		<!-- the table cell with get a class attribute depending on the status of the section -->
		<td>
			<xsl:choose>
				<!-- active section has to be linked to first entry -->
				<xsl:when test="$nav.sec.firstEntry = 'section' and @active and not(entry[1]/@active)">
					<xsl:attribute name="class">navActiveSection</xsl:attribute>
					<a>
						<xsl:attribute name="href"><xsl:value-of select="entry[1]/@src"></xsl:value-of></xsl:attribute>
						<xsl:choose>
							<xsl:when test="$activeImageExists">
								<xsl:for-each select="img[starts-with(@xwebtype, 'activeSectionButton')]">
									<!-- should select exactly one -->
									<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.activeSectionMarker)}" width="{@width}" height="{@height}"></img>
								</xsl:for-each>
							</xsl:when>
							<xsl:when test="$normalImageExists">
								<xsl:for-each select="img[starts-with(@xwebtype, 'normalSectionButton')]">
									<!-- should select exactly one -->
									<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.activeSectionMarker)}" width="{@width}" height="{@height}"></img>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@name"></xsl:value-of>
							</xsl:otherwise>
						</xsl:choose>
					</a>
				</xsl:when>
				<!-- section is active and does not need a link-->
				<xsl:when test="@active">
					<xsl:attribute name="class">navActiveSection</xsl:attribute>
					<xsl:choose>
						<xsl:when test="$activeImageExists">
							<xsl:for-each select="img[starts-with(@xwebtype, 'activeSectionButton')]">
								<!-- should select exactly one -->
								<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.activeSectionMarker)}" width="{@width}" height="{@height}"></img>
							</xsl:for-each>
						</xsl:when>
						<xsl:when test="$normalImageExists">
							<xsl:for-each select="img[starts-with(@xwebtype, 'normalSectionButton')]">
								<!-- should select exactly one -->
								<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.activeSectionMarker)}" width="{@width}" height="{@height}"></img>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@name"></xsl:value-of>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<!-- section has to be linked to first entry -->
				<xsl:when test="$nav.sec.firstEntry = 'section' or $nav.sec.visible = 'current' or $nav.sec.pos ='below' ">
					<xsl:attribute name="class">navNormalSection</xsl:attribute>
					<a>
						<xsl:attribute name="href"><xsl:value-of select="entry[1]/@src"></xsl:value-of></xsl:attribute>
						<xsl:choose>
							<xsl:when test="$normalImageExists">
								<xsl:if test="$feature.javascript = 'on' and ($mouseOverImageExists or $activeImageExists)">
									<xsl:choose>
										<xsl:when test="$mouseOverImageExists">
											<xsl:attribute name="onMouseOver"><xsl:text>document.</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'normalSectionButton')]/@name"></xsl:value-of><xsl:text>.src='</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'mouseOverSectionButton')]/@src"></xsl:value-of><xsl:text>';</xsl:text></xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="onMouseOver"><xsl:text>document.</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'normalSectionButton')]/@name"></xsl:value-of><xsl:text>.src='</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'activeSectionButton')]/@src"></xsl:value-of><xsl:text>';</xsl:text></xsl:attribute>
										</xsl:otherwise>
									</xsl:choose>
									<xsl:attribute name="onMouseOut"><xsl:text>document.</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'normalSectionButton')]/@name"></xsl:value-of><xsl:text>.src='</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'normalSectionButton')]/@src"></xsl:value-of><xsl:text>';</xsl:text></xsl:attribute>
								</xsl:if>
								<xsl:for-each select="img[starts-with(@xwebtype, 'normalSectionButton')]">
									<!-- should select exactly one -->
									<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.normalSectionMarker)}" width="{@width}" height="{@height}"></img>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@name"></xsl:value-of>
							</xsl:otherwise>
						</xsl:choose>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">navNormalSection</xsl:attribute>
					<xsl:choose>
						<xsl:when test="$normalImageExists">
							<xsl:for-each select="img[starts-with(@xwebtype, 'normalSectionButton')]">
								<!-- should select exactly one -->
								<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.normalSectionMarker)}" width="{@width}" height="{@height}"></img>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@name"></xsl:value-of>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>
	<xsl:template match="entry" mode="insertButton">
		<!-- determine which buttons exist -->
		<xsl:variable name="normalImageExists" select="count(img[starts-with(@xwebtype, 'normalEntryButton')]) = 1"></xsl:variable>
		<xsl:variable name="activeImageExists" select="count(img[starts-with(@xwebtype, 'activeEntryButton')]) = 1"></xsl:variable>
		<xsl:variable name="mouseOverImageExists" select="count(img[starts-with(@xwebtype, 'mouseOverEntryButton')]) = 1"></xsl:variable>
		<!-- the table cell with get a class attribute depending on the active or not status of the entry -->
		<td>
			<xsl:choose>
				<xsl:when test="@active">
					<xsl:attribute name="class">navActiveEntry</xsl:attribute>
					<xsl:choose>
						<xsl:when test="$activeImageExists">
							<xsl:for-each select="img[starts-with(@xwebtype, 'activeEntryButton')]">
								<!-- should select exactly one -->
								<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.activeEntryMarker)}" width="{@width}" height="{@height}"></img>
							</xsl:for-each>
						</xsl:when>
						<xsl:when test="$normalImageExists">
							<xsl:for-each select="img[starts-with(@xwebtype, 'normalEntryButton')]">
								<!-- should select exactly one -->
								<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.activeEntryMarker)}" width="{@width}" height="{@height}"></img>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@name"></xsl:value-of>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">navNormalEntry</xsl:attribute>
					<a>
						<xsl:attribute name="href"><xsl:value-of select="@src"></xsl:value-of></xsl:attribute>
						<xsl:choose>
							<xsl:when test="$normalImageExists">
								<xsl:if test="$feature.javascript = 'on' and ($mouseOverImageExists or $activeImageExists)">
									<xsl:choose>
										<xsl:when test="$mouseOverImageExists">
											<xsl:attribute name="onMouseOver"><xsl:text>document.</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'normalEntryButton')]/@name"></xsl:value-of><xsl:text>.src='</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'mouseOverEntryButton')]/@src"></xsl:value-of><xsl:text>';</xsl:text></xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="onMouseOver"><xsl:text>document.</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'normalEntryButton')]/@name"></xsl:value-of><xsl:text>.src='</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'activeEntryButton')]/@src"></xsl:value-of><xsl:text>';</xsl:text></xsl:attribute>
										</xsl:otherwise>
									</xsl:choose>
									<xsl:attribute name="onMouseOut"><xsl:text>document.</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'normalEntryButton')]/@name"></xsl:value-of><xsl:text>.src='</xsl:text><xsl:value-of select="img[starts-with(@xwebtype, 'normalEntryButton')]/@src"></xsl:value-of><xsl:text>';</xsl:text></xsl:attribute>
								</xsl:if>
								<xsl:for-each select="img[starts-with(@xwebtype, 'normalEntryButton')]">
									<!-- should select exactly one -->
									<img src="{@src}" name="{@name}" border="0" alt="{concat(@alt,$i18n.normalEntryMarker)}" width="{@width}" height="{@height}"></img>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@name"></xsl:value-of>
							</xsl:otherwise>
						</xsl:choose>
					</a>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>
</xsl:stylesheet>
