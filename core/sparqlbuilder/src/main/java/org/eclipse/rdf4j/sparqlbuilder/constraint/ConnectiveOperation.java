/*******************************************************************************
 Copyright (c) 2018 Eclipse RDF4J contributors.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Distribution License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/

package org.eclipse.rdf4j.sparqlbuilder.constraint;

/**
 * Represents a SPARQL connective operation, which takes an arbitrary number of arguments
 */
class ConnectiveOperation extends Operation<ConnectiveOperation> {
	ConnectiveOperation(ConnectiveOperator operator) {
		super(operator);
		parenthesize();
	}
}
