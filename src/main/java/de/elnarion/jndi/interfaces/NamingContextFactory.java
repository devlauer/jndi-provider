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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

/**
 * The jnp naming provider InitialContextFactory implementation.
 * 
 * @see javax.naming.spi.InitialContextFactory
 * 
 * @author Scott.Stark@jboss.org
 */
public class NamingContextFactory implements InitialContextFactory, ObjectFactory {
	// InitialContextFactory implementation --------------------------
	@SuppressWarnings("unchecked")
	public Context getInitialContext(Hashtable<?,?> env) throws NamingException {
		return new NamingContext((Hashtable<String, Object>) env, null, null);
	}

	// ObjectFactory implementation ----------------------------------
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?,?> environment) throws Exception {
		Context ctx = getInitialContext(environment);
		Reference ref = (Reference) obj;
		return ctx.lookup((String) ref.get("URL").getContent());
	}

}
