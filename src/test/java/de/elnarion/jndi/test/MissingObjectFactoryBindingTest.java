/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package de.elnarion.jndi.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Hashtable;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.elnarion.jndi.interfaces.MissingObjectFactoryException;
import de.elnarion.jndi.server.SingletonNamingServer;

/**
 * Ensures that requests to {@link Context#lookup(Name)} where the stored value
 * is a {@link Reference} pointing to an unknown {@link ObjectFactory} result in
 * a reliable and informative exception
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
class MissingObjectFactoryBindingTest {

	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(MissingObjectFactoryBindingTest.class.getName());

	/**
	 * Naming Context for bind/lookup
	 */
	private static Context namingContext;

	/**
	 * Creates the new server
	 */
	@BeforeAll
	static void createServerAndNamingContext() throws Exception {
		// Start the naming server
		new SingletonNamingServer();

		// Create a naming context
		namingContext = new InitialContext();
	}

	/**
	 * Control; ensures that we can bind into JNDI and look up a value via an
	 * {@link ObjectFactory}
	 */
	@Test
	void testControl() throws NamingException {
		// Define a bind name
		final String name = "/control";

		// Bind an object factory into JNDI
		final String factoryClassName = ContractedValueObjectFactory.class.getName();
		this.bindObjectFactory(name, factoryClassName);

		// Look up
		final Object obj = namingContext.lookup(name);

		// Test expected value
		final String expectedValue = ContractedValueObjectFactory.VALUE;
		assertEquals(expectedValue, obj, "Object from JNDI at \"" + name + "\" was not expected");
	}

	/**
	 * Ensures that calls to lookup a
	 * 
	 * @link Reference} pointing to an unknown {@link ObjectFactory} result in a
	 *       reliable and informative exception
	 */
	@Test()
	void testExpectedExceptionOnMissingObjectFactory() throws NamingException {
		assertThrows(MissingObjectFactoryException.class, () -> {
			// Define a bind name
			final String name = "/missingObjectFactory";

			// Bind a missing object factory into JNDI
			final String factoryClassName = "org.jboss.MissingObjectFactory";
			this.bindObjectFactory(name, factoryClassName);

			// Look up
			try {
				namingContext.lookup(name);
			}
			// Expected
			catch (final MissingObjectFactoryException mofe) {
				// Just log
				log.info(mofe.toString());
				throw mofe;
			}
		});
		// If we've reached here, no good, we should have gotten an exception
	}

	/**
	 * Binds an {@link ObjectFactory} with the specified class name into JNDI at the
	 * specified location
	 */
	private void bindObjectFactory(final String location, final String factoryClassName) throws NamingException {
		final Reference ref = new Reference(String.class.getName(), factoryClassName, null); // Expected type not
																								// important
		namingContext.bind(location, ref);
	}

	/**
	 * {@link ObjectFactory} implementation to always return the same contracted
	 * value of {@link ContractedValueObjectFactory#VALUE}
	 */
	public static class ContractedValueObjectFactory implements ObjectFactory {

		/**
		 * Value which will be returned for every lookup
		 */
		static final String VALUE = "ExpectedValue";

		/**
		 * {@inheritDoc}
		 * 
		 * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object,
		 *      javax.naming.Name, javax.naming.Context, java.util.Hashtable)
		 */
		public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
				throws Exception {
			return VALUE;
		}

	}
}
