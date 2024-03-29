/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package de.elnarion.jndi.server;

import de.elnarion.jndi.interfaces.Naming;
import de.elnarion.jndi.interfaces.NamingEvents;

import javax.naming.*;
import javax.naming.event.EventContext;
import javax.naming.event.NamingListener;
import java.util.Collection;

/**
 * A delegating wrapper that can be used to create a unique rmi server endpoint
 * that shares the an underlying Naming server implementation.
 *
 * @author Scott.Stark@jboss.org
 */
public class NamingServerWrapper implements Naming, NamingEvents {
	final Naming delegate;
	NamingEvents edelegate;

	NamingServerWrapper(Naming delegate) {
		this.delegate = delegate;
		if (delegate instanceof NamingEvents)
			edelegate = (NamingEvents) delegate;
	}

	public void bind(Name name, Object obj, String className) throws NamingException {
		delegate.bind(name, obj, className);
	}

	public Context createSubcontext(Name name) throws NamingException {
		return delegate.createSubcontext(name);
	}

	public Collection<NameClassPair> list(Name name) throws NamingException {
		return delegate.list(name);
	}

	public Collection<Binding> listBindings(Name name) throws NamingException {
		return delegate.listBindings(name);
	}

	public Object lookup(Name name) throws NamingException {
		return delegate.lookup(name);
	}

	public void rebind(Name name, Object obj, String className) throws NamingException {
		delegate.rebind(name, obj, className);
	}

	public void unbind(Name name) throws NamingException {
		delegate.unbind(name);
	}

	public void addNamingListener(EventContext context, Name target, int scope, NamingListener l)
			throws NamingException {
		edelegate.addNamingListener(context, target, scope, l);
	}

	public void removeNamingListener(NamingListener l) throws NamingException {
		edelegate.removeNamingListener(l);
	}

	public boolean targetMustExist() throws NamingException {
		return edelegate.targetMustExist();
	}
}
