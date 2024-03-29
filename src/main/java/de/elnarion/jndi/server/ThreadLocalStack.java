/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
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

import java.util.ArrayList;
import java.util.List;

/**
 * Comment
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 */
public class ThreadLocalStack<T> {
	private final ThreadLocal<ArrayList<T>> stack = new ThreadLocal<>(); //NOSONAR - ignore

	public void push(T obj) {
		ArrayList<T> list = stack.get();
		if (list == null) {
			list = new ArrayList<>(1);
			stack.set(list);
		}
		list.add(obj);
	}

	public T pop() {
		ArrayList<T> list = stack.get();
		if (list == null) {
			return null;
		}
		T rtn = list.remove(list.size() - 1);
		if (list.isEmpty()) {
			stack.set(null); //NOSONAR - ignore
			list.clear();
		}
		return rtn;
	}

	public T get() {
		ArrayList<T> list = stack.get();
		if (list == null) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	public List<T> getList() {
		return stack.get();
	}
}
