/*******************************************************************************
 * Copyright (c) 2020 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.rdf4j.sail.shacl.ast;

import static org.eclipse.rdf4j.model.util.Values.bnode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.rdf4j.common.annotation.InternalUseOnly;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 * Internal utility methods for the SHACL AST to quickly convert between java collections and RDF collections. These
 * utilities are not meant for general use as they make several simplifying assumptions for performance reasons:
 *
 * <ul>
 * <li>they add type coercion
 * <Li>they rely on the use of "stable" identifiers for blank nodes, so that duplicates can be quickly detected and
 * discarded
 * </ul>
 *
 * @apiNote This feature is for internal use only: its existence, signature or behavior may change without warning from
 *          one release to the next.
 */
@InternalUseOnly
public class ShaclAstLists {

	public static void listToRdf(Collection<? extends Value> values, Resource head, Model model) {
		// The Turtle parser does not add "a rdf:List" statements when parsing the shorthand list format,
		// so we don't add rdf:List when writing it out either.

		Iterator<? extends Value> iter = values.iterator();
		while (iter.hasNext()) {
			Value value = iter.next();
			model.add(head, RDF.FIRST, value);

			if (iter.hasNext()) {
				Resource next = bnode();
				model.add(head, RDF.REST, next);
				head = next;
			} else {
				model.add(head, RDF.REST, RDF.NIL);
			}
		}
	}

	private static List<Value> toList(RepositoryConnection connection, Resource head) {
		List<Value> ret = new ArrayList<>();
		while (!head.equals(RDF.NIL)) {
			try (Stream<Statement> stream = connection.getStatements(head, RDF.FIRST, null).stream()) {
				Value value = stream.map(Statement::getObject).findAny().get();
				ret.add(value);
			}

			try (Stream<Statement> stream = connection.getStatements(head, RDF.REST, null).stream()) {
				head = stream.map(Statement::getObject).map(v -> (Resource) v).findAny().get();
			}

		}

		return ret;

	}

	public static <T extends Value> List<T> toList(RepositoryConnection connection, Resource head, Class<T> type) {
		if (type == Value.class) {
			return (List<T>) toList(connection, head);
		}

		List<T> ret = new ArrayList<>();
		while (!head.equals(RDF.NIL)) {
			try (Stream<Statement> stream = connection.getStatements(head, RDF.FIRST, null).stream()) {
				Value value = stream.map(Statement::getObject).findAny().get();
				if (type.isInstance(value)) {
					ret.add(((T) value));
				} else {
					throw new IllegalStateException("RDF list should contain only type " + type.getSimpleName()
							+ ", but found " + value.getClass().getSimpleName());
				}
			}

			try (Stream<Statement> stream = connection.getStatements(head, RDF.REST, null).stream()) {
				head = stream.map(Statement::getObject).map(v -> (Resource) v).findAny().get();
			}

		}

		return ret;

	}

}
