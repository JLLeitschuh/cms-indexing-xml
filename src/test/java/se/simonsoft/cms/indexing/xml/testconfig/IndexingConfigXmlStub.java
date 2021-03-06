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
package se.simonsoft.cms.indexing.xml.testconfig;

import se.repos.indexing.item.ItemContentBufferStrategy;
import se.simonsoft.cms.indexing.xml.IndexingHandlersXml;
import se.simonsoft.cms.indexing.xml.XmlIndexFieldExtraction;
import se.simonsoft.cms.indexing.xml.XmlIndexWriter;
import se.simonsoft.cms.indexing.xml.custom.IndexFieldExtractionCustomXsl;
import se.simonsoft.cms.indexing.xml.custom.XmlMatchingFieldExtractionSource;
import se.simonsoft.cms.indexing.xml.custom.XmlMatchingFieldExtractionSourceDefault;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class IndexingConfigXmlStub extends AbstractModule {

	@Override
	protected void configure() {
		// This base config uses a stub to avoid a full setup.
		bind(XmlIndexWriter.class).to(XmlIndexWriterStub.class);

		// XML field extraction
		Multibinder<XmlIndexFieldExtraction> fieldExtraction = Multibinder.newSetBinder(binder(), XmlIndexFieldExtraction.class);
		IndexingHandlersXml.configureXmlFieldExtraction(fieldExtraction);
		// Used in field extraction. We don't have a strategy yet for placement of the custom xsl, read from jar
		bind(XmlMatchingFieldExtractionSource.class).to(XmlMatchingFieldExtractionSourceDefault.class);
		bind(IndexFieldExtractionCustomXsl.class).asEagerSingleton();

		// This base config uses a stub to avoid a full setup.
		bind(ItemContentBufferStrategy.class).to(ItemContentBufferStub.class);

	}

}
