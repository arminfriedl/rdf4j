/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package org.eclipse.rdf4j.repository.manager.util;

import org.eclipse.rdf4j.repository.manager.RepositoryManager;

public interface RepositoryManagerListener {

	void initialized(RepositoryManager manager);

	void refreshed(RepositoryManager manager);

	void shutDown(RepositoryManager manager);
}
