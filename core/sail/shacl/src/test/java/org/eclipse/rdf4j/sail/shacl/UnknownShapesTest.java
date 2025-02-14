/*******************************************************************************
 * Copyright (c) 2019 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package org.eclipse.rdf4j.sail.shacl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class UnknownShapesTest {

	@Test
	public void testPropertyShapes() throws IOException, InterruptedException {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

		MyAppender newAppender = new MyAppender();
		root.addAppender(newAppender);

		SailRepository shaclRepository = Utils.getInitializedShaclRepository("unknownProperties.ttl", false);

		try (SailRepositoryConnection connection = shaclRepository.getConnection()) {
			connection.begin();
			connection.add(RDF.TYPE, RDF.TYPE, RDFS.RESOURCE);
			connection.commit();
		}

		Thread.sleep(100);

		Set<String> relevantLog = newAppender.logged.stream()
				.filter(m -> m.startsWith("Unsupported SHACL feature"))
				.collect(Collectors.toSet());

		Set<String> expected = new HashSet<>(Arrays.asList(
				"Unsupported SHACL feature detected sh:unknownTarget in statement (http://example.com/ns#PersonShape, http://www.w3.org/ns/shacl#unknownTarget, http://www.w3.org/2000/01/rdf-schema#Class) [null]",
				"Unsupported SHACL feature detected sh:unknownShaclProperty in statement (http://example.com/ns#PersonPropertyShape, http://www.w3.org/ns/shacl#unknownShaclProperty, \"1\"^^<http://www.w3.org/2001/XMLSchema#integer>) [null]"));

		assertEquals(expected, relevantLog);

		shaclRepository.shutDown();

	}

	@Ignore
	@Test
	public void testComplexPath() throws IOException, InterruptedException {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

		MyAppender newAppender = new MyAppender();
		root.addAppender(newAppender);

		SailRepository shaclRepository = Utils.getInitializedShaclRepository("complexPath.ttl", false);

		try (SailRepositoryConnection connection = shaclRepository.getConnection()) {
			connection.begin();
			connection.add(RDF.TYPE, RDF.TYPE, RDFS.RESOURCE);
			connection.commit();
		}

		Thread.sleep(100);

		Set<String> relevantLog = newAppender.logged.stream()
				.filter(m -> m.startsWith("Unsupported SHACL feature"))
				.map(s -> s.replaceAll("\r\n|\r|\n", " "))
				.map(String::trim)
				.collect(Collectors.toSet());

		Set<String> expected = new HashSet<>(Arrays.asList(
				"Unsupported SHACL feature with complex path. Only single predicate paths, or single predicate inverse paths are supported. <http://example.com/ns#alternativePath> shape has been deactivated!  @prefix sh: <http://www.w3.org/ns/shacl#> .  <http://example.com/ns#alternativePath> sh:path [       sh:alternativePath <http://example.com/ns#father>     ] .",
				"Unsupported SHACL feature with complex path. Only single predicate paths, or single predicate inverse paths are supported. <http://example.com/ns#pathSequence> shape has been deactivated!  @prefix sh: <http://www.w3.org/ns/shacl#> .  <http://example.com/ns#pathSequence> sh:path (<http://example.com/ns#parent> <http://example.com/ns#firstName>) .",
				"Unsupported SHACL feature with complex path. Only single predicate paths, or single predicate inverse paths are supported. <http://example.com/ns#inverseOfWithComplex> shape has been deactivated!  @prefix sh: <http://www.w3.org/ns/shacl#> .  <http://example.com/ns#inverseOfWithComplex> sh:path [       sh:inversePath [           sh:zeroOrMorePath <http://example.com/ns#inverseThis>         ]     ] ."));

		assertEquals(expected, relevantLog);

		shaclRepository.shutDown();
	}

	class MyAppender extends AppenderBase<ILoggingEvent> {

		List<String> logged = new ArrayList<>();

		@Override
		public synchronized void doAppend(ILoggingEvent eventObject) {
			logged.add(eventObject.getFormattedMessage());
		}

		@Override
		protected void append(ILoggingEvent iLoggingEvent) {

		}
	}
}
