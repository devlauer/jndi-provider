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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.elnarion.jndi.server.NamingBeanImpl;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * 
 */
class OptionalJavaCompTest {
	private NamingBeanImpl naming;

	@AfterEach
	public void after() {
		if (naming != null)
			naming.stop();
		naming = null;
	}

	@Test
	void testWithJavaComp() throws Exception {
		naming = new NamingBeanImpl();
		naming.start();

		InitialContext ctx = new InitialContext();
		Object comp = ctx.lookup("java:comp");
		assertNotNull(comp);
	}

	@Test
	void testWithoutJavaComp() throws Exception {
		naming = new NamingBeanImpl();
		naming.setInstallJavaComp(false);
		naming.start();

		InitialContext ctx = new InitialContext();
		try {
			ctx.lookup("java:comp");
			fail("java:comp should not exist");
		} catch (NameNotFoundException e) {
			// good
		}
	}
}
