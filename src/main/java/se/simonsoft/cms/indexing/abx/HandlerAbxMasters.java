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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.simonsoft.cms.item.indexing.IdStrategy;
import se.repos.indexing.IndexingDoc;
import se.repos.indexing.IndexingItemHandler;
import se.repos.indexing.item.HandlerPathinfo;
import se.repos.indexing.item.IndexingItemProgress;
import se.repos.indexing.item.HandlerProperties;
import se.simonsoft.cms.item.impl.CmsItemIdArg;
import se.simonsoft.cms.item.impl.CmsItemIdBase;

/**
 * Uses the abx.*Master properties, splitting on newline, to add fields ref_abx.*Master.
 */
public class HandlerAbxMasters extends HandlerAbxFolders {

	private static final Logger logger = LoggerFactory.getLogger(HandlerAbxMasters.class);
	
	private static final String HOSTFIELD = "repohost";

	private IdStrategy idStrategy;
	
	/**
	 * @param idStrategy to fill the refid field
	 */
	@Inject
	public HandlerAbxMasters(IdStrategy idStrategy) {
		super(idStrategy);
		this.idStrategy = idStrategy;
	}
	
	@Override
	public void handle(IndexingItemProgress progress) {
		
		logger.trace("handle(IndexItemProgress progress)");
		
		IndexingDoc fields = progress.getFields();
		String host = (String) fields.getFieldValue(HOSTFIELD);
		if (host == null) {
			throw new IllegalStateException("Depending on indexer that adds host field " + HOSTFIELD);
		}
		
		Set<CmsItemIdBase> masterIds = new HashSet<CmsItemIdBase>();
		String[] abxProperties = {"abx.ReleaseMaster", "abx.AuthorMaster", "abx.TranslationMaster"};
		for (String propertyName : abxProperties) {
			masterIds.addAll(handleAbxProperty(host, propertyName, (String) fields.getFieldValue("prop_" + propertyName)));
		}
		
		for (CmsItemIdBase masterId : masterIds) {
			fields.addField("ref_abx.Masters", masterId.getLogicalId());
		}
		
		handleFolders(fields, "ref_abx.Masters_pathparents", masterIds);
		
	}

	@Override
	public Set<Class<? extends IndexingItemHandler>> getDependencies() {
		return new HashSet<Class<? extends IndexingItemHandler>>() {private static final long serialVersionUID = 1L;{
			add(HandlerPathinfo.class);
			add(HandlerProperties.class);
		}};
	}
	
	/**
	 * Helper method for extracting master ids and adding them to a reference
	 * field. Assumes that the propvided property is found in a field with the
	 * prefix "prop_".
	 *
	 * @param host
	 * @param propertyName name of the property field to copy master ref from
	 * @param abxprop value of the property field.
	 * @return 
	 */
	protected Set<CmsItemIdBase> handleAbxProperty(String host, String propertyName, String abxprop) {

		Set<CmsItemIdBase> result = new HashSet<CmsItemIdBase>();

		if (abxprop != null) {
			
			if (abxprop.length() != 0) {
				
				for (String d : abxprop.split("\n")) {
					CmsItemIdArg id = new CmsItemIdArg(d);
					id.setHostname(host);
					result.add(id);
				}
				
			} else {
				logger.debug("{} property exists but is empty", propertyName);
			}
			
		}
		
		return result;
		
	}

}