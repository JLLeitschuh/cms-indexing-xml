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
package se.simonsoft.cms.indexing.xml;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import se.repos.indexing.IndexingDoc;
import se.repos.indexing.item.IndexingItemProgress;
import se.repos.indexing.twophases.IndexingDocIncrementalSolrj;
import se.simonsoft.cms.item.CmsItemPath;
import se.simonsoft.cms.item.events.change.CmsChangesetItem;
import se.simonsoft.cms.xmlsource.handler.XmlNotWellFormedException;
import se.simonsoft.cms.xmlsource.handler.XmlSourceElement;
import se.simonsoft.cms.xmlsource.handler.XmlSourceReader;
import se.simonsoft.cms.xmlsource.handler.jdom.XmlSourceReaderJdom;

public class HandlerXmlTest {

	@Test
	public void testXmlSourceElementInvalid() {
		XmlSourceReader xmlReader = new XmlSourceReaderJdom();
		XmlIndexWriter indexWriter = mock(XmlIndexWriter.class);
		Set<XmlIndexFieldExtraction> fe = new LinkedHashSet<XmlIndexFieldExtraction>();
		final List<XmlSourceElement> calls = new LinkedList<XmlSourceElement>();
		fe.add(new XmlIndexFieldExtraction() {
			@Override
			public void extract(XmlSourceElement processedElement, IndexingDoc fields) throws XmlNotWellFormedException {
				calls.add(processedElement);
				throw new XmlNotWellFormedException("something went wrong", new RuntimeException("some xml lib's cause"));
			}

			@Override
			public void endDocument() {
				
			}
		});
		
		HandlerXml handlerXml = new HandlerXml();
		handlerXml.setDependenciesIndexing(indexWriter);
		handlerXml.setDependenciesXml(fe, xmlReader);
		
		CmsChangesetItem p1i = mock(CmsChangesetItem.class);
		when(p1i.isFile()).thenReturn(true);
		when(p1i.getPath()).thenReturn(new CmsItemPath("/some.xml"));
		IndexingDoc p1f = new IndexingDocIncrementalSolrj();
		IndexingItemProgress p1 = mock(IndexingItemProgress.class);
		when(p1.getItem()).thenReturn(p1i);
		when(p1.getFields()).thenReturn(p1f);
		when(p1.getContents()).thenReturn(new ByteArrayInputStream("<p>P</p>".getBytes()));
		handlerXml.handle(p1); // should catch and log the exception but proceed to next item
		
		assertEquals("Should have called the extract method", 1, calls.size());
	}

	@Test
	public void testXmlSourceElementFatalError() {
		XmlSourceReader xmlReader = new XmlSourceReaderJdom();
		XmlIndexWriter indexWriter = mock(XmlIndexWriter.class);
		Set<XmlIndexFieldExtraction> fe = new LinkedHashSet<XmlIndexFieldExtraction>();
		final List<XmlSourceElement> calls = new LinkedList<XmlSourceElement>();
		fe.add(new XmlIndexFieldExtraction() {
			@Override
			public void extract(XmlSourceElement processedElement, IndexingDoc fields) throws XmlNotWellFormedException {
				calls.add(processedElement);
				throw new RuntimeException("something went badly wrong");
			}

			@Override
			public void endDocument() {
				
			}
		});
		
		HandlerXml handlerXml = new HandlerXml();
		handlerXml.setDependenciesIndexing(indexWriter);
		handlerXml.setDependenciesXml(fe, xmlReader);
		
		CmsChangesetItem p1i = mock(CmsChangesetItem.class);
		when(p1i.isFile()).thenReturn(true);
		when(p1i.getPath()).thenReturn(new CmsItemPath("/some.xml"));
		IndexingDoc p1f = new IndexingDocIncrementalSolrj();
		IndexingItemProgress p1 = mock(IndexingItemProgress.class);
		when(p1.getItem()).thenReturn(p1i);
		when(p1.getFields()).thenReturn(p1f);
		when(p1.getContents()).thenReturn(new ByteArrayInputStream("<p>P</p>".getBytes()));
		try {
			handlerXml.handle(p1); // should catch and log the exception but proceed to next item
			fail("Should not proceed on unknown indexing errors, because we might unknowingly get an incomplete index");
		} catch (Exception e) {
			// expected
		}
		
		assertEquals("Should have called the extract method", 1, calls.size());
	}	
	
}