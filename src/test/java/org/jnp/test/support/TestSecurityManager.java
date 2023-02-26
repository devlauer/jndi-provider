/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jnp.test.support;

import java.security.Permission;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Scott.Stark@jboss.org
 */
public class TestSecurityManager extends SecurityManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestSecurityManager.class);
	/** The set of allowed test permissions */
	HashSet<Permission> testPermissions = new HashSet<Permission>();

	public void addPermission(Permission p) {
		testPermissions.add(p);
	}

	public Set<Permission> getPermissions() {
		return testPermissions;
	}

	/**
	* 
	*/
	@Override
	public void checkPermission(Permission perm) {
		for (Permission p : testPermissions) {
			if (p.implies(perm))
				return;
		}
		LOGGER.info("checkPermission, " + perm);
		super.checkPermission(perm);
	}
}