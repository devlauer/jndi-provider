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
package de.elnarion.jndi.server;

import javax.naming.NamingException;

import de.elnarion.jndi.interfaces.NamingContext;

/**
 * An extension of the NamingServer that installs itself as the
 * {@link NamingContext#setLocal(de.elnarion.jndi.interfaces.Naming)} instancee used as
 * the JVM local server.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 */
public class SingletonNamingServer extends NamingServer {
	static final long serialVersionUID = 7405111143081053466L;

	public SingletonNamingServer() throws NamingException {
		NamingContext.setLocal(this);
	}

	public void destroy() {
		NamingContext.setLocal(null);
	}
}
