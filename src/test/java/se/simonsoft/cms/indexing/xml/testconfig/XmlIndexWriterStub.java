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

import se.simonsoft.cms.indexing.xml.XmlIndexAddSession;
import se.simonsoft.cms.indexing.xml.XmlIndexWriter;
import se.simonsoft.cms.item.CmsRepository;
import se.simonsoft.cms.item.events.change.CmsChangesetItem;

public class XmlIndexWriterStub implements XmlIndexWriter {

	public XmlIndexWriterStub() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public XmlIndexAddSession get() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePath(CmsRepository repository, CmsChangesetItem c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void commit(boolean expungeDeletes) {
		// TODO Auto-generated method stub

	}

}
