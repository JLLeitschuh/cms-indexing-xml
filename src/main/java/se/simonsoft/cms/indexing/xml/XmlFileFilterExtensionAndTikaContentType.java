/**
 * Copyright (C) 2009-2016 Simonsoft Nordic AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.simonsoft.cms.indexing.xml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import se.repos.indexing.IndexingDoc;
import se.simonsoft.cms.item.events.change.CmsChangesetItem;

/**
 * Identifies XML on file extension and svn:mime-type property.
 * An alternative would be to resort to trial-and-error
 * (which we need anyway because some XML is not valid)
 * or detected content type from fulltext/metadata extraction.
 */
public class XmlFileFilterExtensionAndTikaContentType implements XmlFileFilter {
	
	private static final String CONTENT_TYPE_KEY = "embd_Content-Type";

	private Set<String> extensionsToTry = new HashSet<String>(Arrays.asList("xml", "dita", "ditamap", 
			"xlf", 
			"xhtml", "html", "htm", 
			"x-svg" // #871 Disabling SVG until we can protect SolR from whatever.
			));
	private Set<String> contentTypesToTry = new HashSet<String>(Arrays.asList(
			"application/xml",
			"application/xhtml+xml",
			"application/dita+xml"
			));

	@Override
	public boolean isXml(CmsChangesetItem c, IndexingDoc fields) {
		// TODO legacy behavior now, add check for svn prop
		return extensionsToTry.contains(c.getPath().getExtension()) && (!fields.containsKey(CONTENT_TYPE_KEY) ||
				contentTypesToTry.contains(parseContentType((String)fields.getFieldValue(CONTENT_TYPE_KEY))));	
	}
	
	protected String parseContentType(String contentType) {
		if (contentType != null) {
			contentType = contentType.contains(";") ? contentType.split(";")[0].trim() : contentType.trim();
		}
		return contentType;
	}
	
}
