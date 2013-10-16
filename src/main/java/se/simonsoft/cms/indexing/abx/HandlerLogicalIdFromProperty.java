/**
 * Copyright (C) 2009-2013 Simonsoft Nordic AB
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
package se.simonsoft.cms.indexing.abx;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.repos.indexing.IndexingItemHandler;
import se.repos.indexing.item.HandlerPathinfo;
import se.repos.indexing.item.HandlerProperties;
import se.repos.indexing.item.IndexingItemProgress;
import se.simonsoft.cms.item.CmsItemId;
import se.simonsoft.cms.item.events.change.CmsChangesetItem;
import se.simonsoft.cms.item.impl.CmsItemIdArg;

/**
 * Without cms-logicalid module we can't encode a logical id based on {@link CmsChangesetItem},
 * so we'll use the value from svn property field {@value #PROPERTY_FIELD} but with the
 * commit revision from the changeset item.
 */
public class HandlerLogicalIdFromProperty extends HandlerLogicalId {
	
	public static final String PROPERTY_FIELD = "prop_abx.BaseLogicalId";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Override
	protected CmsItemId getItemId(IndexingItemProgress progress) {
		CmsChangesetItem item = progress.getItem();
		String repohost = (String) progress.getFields().getFieldValue("repohost");
		if (repohost == null) {
			throw new AssertionError("Missing repohost field for " + item + ", can not set logical ID");
		}
		String base = (String) progress.getFields().getFieldValue(PROPERTY_FIELD);
		if (base == null) {
			if ("xml".equals(item.getPath().getExtension())) {
				logger.warn("No BaseLogicalId for {}, logical ID field will not be set");
			} else {
				logger.trace("No BaseLogicalId for {}, logical ID field will not be set");
			}
			return null;
		}
		CmsItemIdArg id = new CmsItemIdArg(base);
		id.setHostnameOrValidate(repohost);
		return id.withPegRev(item.getRevisionChanged().getNumber());
	}
	
	@SuppressWarnings("serial")
	@Override
	public Set<Class<? extends IndexingItemHandler>> getDependencies() {
		return new HashSet<Class<? extends IndexingItemHandler>>() {{
			add(HandlerPathinfo.class);
			add(HandlerProperties.class);
		}};
	}

}
