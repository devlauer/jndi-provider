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

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 * A variation of the de.elnarion.jndi.interfaces.NamingContextFactory
 * InitialContextFactory implementation that maintains the last envrionment used
 * to create an InitialContext in a thread local variable for access within the
 * scope of the InitialContext. This can be used by the EJB handles to save the
 * context that should be used to perform the looks when the handle is restored.
 *
 * @see de.elnarion.jndi.interfaces.NamingContextFactory
 *
 * @author Scott.Stark@jboss.org
 */
public class VmNamingContextFactory extends de.elnarion.jndi.interfaces.NamingContextFactory {
	public static final ThreadLocal<Hashtable<?,?>> lastInitialContextEnv = new ThreadLocal<>();

	// InitialContextFactory implementation --------------------------
	@Override
	public Context getInitialContext(Hashtable<?,?> env) throws NamingException {
		lastInitialContextEnv.set(env);
		return super.getInitialContext(env);
	}
}
