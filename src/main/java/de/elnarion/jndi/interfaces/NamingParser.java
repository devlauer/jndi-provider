/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.elnarion.jndi.interfaces;

import javax.naming.CompoundName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * The NamingParser for the jnp naming implementation
 *
 * @author Scott.Stark@jboss.org
 */
public class NamingParser implements NameParser, java.io.Serializable {
	// Constants -----------------------------------------------------

	/**
	 * The unsynchronized syntax properties
	 */
	static final Properties syntax = new FastNamingProperties();

	// Attributes ----------------------------------------------------

	// Static --------------------------------------------------------
	private static final long serialVersionUID = 2925203703371001031L;

	public static Properties getSyntax() {
		return syntax;
	}

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	// NameParser implementation -------------------------------------
	public Name parse(String name) throws NamingException {
		return new CompoundName(name, syntax);
	}

	// Y overrides ---------------------------------------------------

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------
}
