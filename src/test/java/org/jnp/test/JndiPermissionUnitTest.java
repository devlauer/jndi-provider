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
package org.jnp.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;

import javax.naming.CompositeName;
import javax.naming.Name;

import org.jnp.server.JndiPermission;
import org.junit.jupiter.api.Test;

/**
 * Tests of the JndiPermission/JndiPermissionCollection behavior
 * 
 * @author Scott.Stark@jboss.org
 * @author adrian@jboss.org
 */
class JndiPermissionUnitTest {
	private static final String ALL_BINDINGS = "<<ALL BINDINGS>>";

	private static final String[] ACTION_ARRAY = new String[] { JndiPermission.BIND_ACTION,
			JndiPermission.REBIND_ACTION, JndiPermission.UNBIND_ACTION, JndiPermission.LOOKUP_ACTION,
			JndiPermission.LIST_ACTION, JndiPermission.LIST_BINDINGS_ACTION, JndiPermission.CREATE_SUBCONTEXT_ACTION, };

	private static final String ALL_ACTIONS;

	static {
		StringBuilder builder = new StringBuilder();
		boolean comma = false;
		for (String action : ACTION_ARRAY) {
			if (comma)
				builder.append(',');
			builder.append(action);
			comma = true;
		}
		ALL_ACTIONS = builder.toString();
	}

	@Test
	void testBind() {
		JndiPermission any = new JndiPermission("<<ALL BINDINGS>>", "*");
		JndiPermission path1 = new JndiPermission("/path1/*", "bind");
		JndiPermission path1Recursive = new JndiPermission("/path1/-", "bind");

		assertTrue(any.implies(path1), "<<ALL BINDINGS>> implies /path1;bind");
		assertTrue(any.implies(path1Recursive), "<<ALL BINDINGS>> implies /path1;bind");
		JndiPermission p = new JndiPermission("/path1/", "bind");
		assertTrue(path1.implies(p), path1 + " implies /path1/ bind");
		assertTrue(path1Recursive.implies(p), path1Recursive + " implies /path1/ bind");
		// A directory permission does not imply access to the unqualified path
		p = new JndiPermission("/path1", "bind");
		assertFalse(path1.implies(p), path1 + " implies /path1;bind");
		assertFalse(path1Recursive.implies(p), path1Recursive + " implies /path1;bind");

	}

	@Test
	void testConstants() {
		assertEquals(0, JndiPermission.NONE);
		assertEquals(1, JndiPermission.BIND);
		assertEquals(2, JndiPermission.REBIND);
		assertEquals(4, JndiPermission.UNBIND);
		assertEquals(8, JndiPermission.LOOKUP);
		assertEquals(16, JndiPermission.LIST);
		assertEquals(32, JndiPermission.LIST_BINDINGS);
		assertEquals(64, JndiPermission.CREATE_SUBCONTEXT);
		assertEquals(127, JndiPermission.ALL);

		assertEquals("bind", JndiPermission.BIND_ACTION);
		assertEquals("rebind", JndiPermission.REBIND_ACTION);
		assertEquals("unbind", JndiPermission.UNBIND_ACTION);
		assertEquals("lookup", JndiPermission.LOOKUP_ACTION);
		assertEquals("list", JndiPermission.LIST_ACTION);
		assertEquals("listBindings", JndiPermission.LIST_BINDINGS_ACTION);
		assertEquals("createSubcontext", JndiPermission.CREATE_SUBCONTEXT_ACTION);
		assertEquals("*", JndiPermission.ALL_ACTION);
	}

	@Test
	void testBasicConstructorAllBindings() throws Exception {
		JndiPermission test = new JndiPermission(ALL_BINDINGS, JndiPermission.BIND_ACTION);
		assertEquals(ALL_BINDINGS, test.getName());
		assertEquals(JndiPermission.BIND_ACTION, test.getActions());
	}

	@Test
	void testBasicConstructorSimplePathString() throws Exception {
		JndiPermission test = new JndiPermission("simple", JndiPermission.BIND_ACTION);
		assertEquals("simple", test.getName());
		assertEquals(JndiPermission.BIND_ACTION, test.getActions());
	}

	@Test
	void testBasicConstructorHierarchcicalPathString() throws Exception {
		JndiPermission test = new JndiPermission("1/2/3", JndiPermission.BIND_ACTION);
		assertEquals("1/2/3", test.getName());
		assertEquals(JndiPermission.BIND_ACTION, test.getActions());
	}

	@Test
	void testBasicConstructorWildcardPathString() throws Exception {
		JndiPermission test = new JndiPermission("1/2/3/*", JndiPermission.BIND_ACTION);
		assertEquals("1/2/3/*", test.getName());
		assertEquals(JndiPermission.BIND_ACTION, test.getActions());
	}

	@Test
	void testBasicConstructorRecursivePathString() throws Exception {
		JndiPermission test = new JndiPermission("1/2/3/-", JndiPermission.BIND_ACTION);
		assertEquals("1/2/3/-", test.getName());
		assertEquals(JndiPermission.BIND_ACTION, test.getActions());
	}

	@Test
	void testBasicConstructorSimplePathName() throws Exception {
		JndiPermission test = new JndiPermission(new CompositeName("simple"), JndiPermission.BIND_ACTION);
		assertEquals("simple", test.getName());
		assertEquals(JndiPermission.BIND_ACTION, test.getActions());
	}

	@Test
	void testBasicConstructorHierarchcicalPathName() throws Exception {
		JndiPermission test = new JndiPermission(new CompositeName("1/2/3"), JndiPermission.BIND_ACTION);
		assertEquals("1/2/3", test.getName());
		assertEquals(JndiPermission.BIND_ACTION, test.getActions());
	}

	@Test
	void testBasicConstructorWildcardPathName() throws Exception {
		JndiPermission test = new JndiPermission(new CompositeName("1/2/3/*"), JndiPermission.BIND_ACTION);
		assertEquals("1/2/3/*", test.getName());
		assertEquals(JndiPermission.BIND_ACTION, test.getActions());
	}

	@Test
	void testBasicConstructorRecursivePathName() throws Exception {
		JndiPermission test = new JndiPermission(new CompositeName("1/2/3/-"), JndiPermission.BIND_ACTION);
		assertEquals("1/2/3/-", test.getName());
		assertEquals(JndiPermission.BIND_ACTION, test.getActions());
	}

	@Test
	void testBasicConstructorBadString() throws Exception {
		assertThrows(NullPointerException.class, () -> {
			new JndiPermission((String) null, JndiPermission.ALL_ACTION);
		});
	}

	@Test
	void testBasicConstructorBadName() throws Exception {
		assertThrows(NullPointerException.class, () -> {
			new JndiPermission((Name) null, JndiPermission.ALL_ACTION);
		});
	}

	@Test
	void testBasicConstructorActions() throws Exception {
		HashSet<String> previous = new HashSet<String>();
		testBasicConstructorActions(previous);
	}

	void testBasicConstructorActions(HashSet<String> previous) throws Exception {
		if (previous.size() == ACTION_ARRAY.length)
			return;

		for (String current : ACTION_ARRAY) {
			if (previous.contains(current) == false) {
				HashSet<String> newTest = new HashSet<String>(previous);
				newTest.add(current);

				StringBuilder builder = new StringBuilder();
				boolean comma = false;
				for (String action : newTest) {
					if (comma)
						builder.append(',');
					builder.append(action);
					comma = true;
				}

				JndiPermission test = new JndiPermission("simple", builder.toString());
				assertEquals("simple", test.getName());

				String foundActions = test.getActions();
				assertNotNull(foundActions);
				HashSet<String> actual = new HashSet<String>(Arrays.asList(foundActions.split(",")));
				assertEquals(newTest, actual);

				// Recurse
				testBasicConstructorActions(newTest);
			}
		}
	}

	@Test
	void testBasicConstructorAllAction() throws Exception {
		JndiPermission all = new JndiPermission("simple", JndiPermission.ALL_ACTION);
		JndiPermission test = new JndiPermission("simple", ALL_ACTIONS);
		assertEquals(test, all);
	}

	@Test
	void testBasicConstructorBadActions() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> {
			new JndiPermission("simple", "rubbish");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new JndiPermission("simple", "bind,rubbish");
		});
	}

	@Test
	void testImpliesSimplePath() throws Exception {
		JndiPermission test1 = new JndiPermission("simple", JndiPermission.ALL_ACTION);
		JndiPermission test2 = new JndiPermission("simple", JndiPermission.ALL_ACTION);
		assertTrue(test2.implies(test1));

		test2 = new JndiPermission("notsimple", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));

		test2 = new JndiPermission(ALL_BINDINGS, JndiPermission.ALL_ACTION);
		assertTrue(test2.implies(test1));
	}

	@Test
	void testImpliesHierarchy() throws Exception {
		JndiPermission test1 = new JndiPermission("1/2/3", JndiPermission.ALL_ACTION);
		JndiPermission test2 = new JndiPermission("1/2/3", JndiPermission.ALL_ACTION);
		assertTrue(test2.implies(test1));

		test2 = new JndiPermission("1", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));

		test2 = new JndiPermission("1/2", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));

		test2 = new JndiPermission("1/2/4", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));

		test2 = new JndiPermission("1/2/3/4", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));

		test2 = new JndiPermission(ALL_BINDINGS, JndiPermission.ALL_ACTION);
		assertTrue(test2.implies(test1));
	}

	@Test
	void testImpliesWildcard() throws Exception {
		JndiPermission test1 = new JndiPermission("1/2/3", JndiPermission.ALL_ACTION);
		JndiPermission test2 = new JndiPermission("1/2/*", JndiPermission.ALL_ACTION);
		assertTrue(test2.implies(test1));

		test2 = new JndiPermission("1/2/3/*", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));

		test2 = new JndiPermission("1/4/*", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));

		test2 = new JndiPermission("1/*", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));

		/*
		 * test2 = new JndiPermission("*", JndiPermission.ALL_ACTION);
		 * assertFalse(test2.implies(test1));
		 * 
		 * test1 = new JndiPermission("1", JndiPermission.ALL_ACTION);
		 * assertTrue(test2.implies(test1));
		 */

		test1 = new JndiPermission("1/2/34", JndiPermission.ALL_ACTION);
		test2 = new JndiPermission("1/2/3*", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));
	}

	@Test
	void testImpliesRecursive() throws Exception {
		JndiPermission test1 = new JndiPermission("1/2/3", JndiPermission.ALL_ACTION);
		JndiPermission test2 = new JndiPermission("1/2/-", JndiPermission.ALL_ACTION);
		assertTrue(test2.implies(test1));

		test2 = new JndiPermission("1/-", JndiPermission.ALL_ACTION);
		assertTrue(test2.implies(test1));

		/*
		 * test2 = new JndiPermission("-", JndiPermission.ALL_ACTION);
		 * assertTrue(test2.implies(test1));
		 */

		test2 = new JndiPermission("1/2/3/-", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));

		test2 = new JndiPermission("1/4/-", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));

		test1 = new JndiPermission("1/2/34", JndiPermission.ALL_ACTION);
		test2 = new JndiPermission("1/2/3-", JndiPermission.ALL_ACTION);
		assertFalse(test2.implies(test1));
	}

	@Test
	void testImpliesActions() throws Exception {
		JndiPermission all = new JndiPermission("simple", JndiPermission.ALL_ACTION);

		for (String current : ACTION_ARRAY) {
			HashSet<String> actions = new HashSet<String>();
			actions.add(current);

			JndiPermission test = new JndiPermission("simple", current);
			assertTrue(all.implies(test), "All implies " + current);
			testImpliesActions(actions, test);
		}
	}

	public void testImpliesActions(HashSet<String> previous, JndiPermission current) throws Exception {
		if (previous.size() == ACTION_ARRAY.length)
			return;

		for (String action : ACTION_ARRAY) {
			if (previous.contains(action) == false) {
				HashSet<String> newTest = new HashSet<String>(previous);
				newTest.add(action);

				StringBuilder builder = new StringBuilder();
				boolean comma = false;
				for (String element : newTest) {
					if (comma)
						builder.append(',');
					builder.append(element);
					comma = true;
				}
				String actions = builder.toString();

				JndiPermission test = new JndiPermission("simple", actions);
				assertTrue(test.implies(current), actions + " implies " + current.getActions());

				// Recurse
				testImpliesActions(newTest, current);
			}
		}
	}

	@Test
	void testEqualsPath() throws Exception {
		JndiPermission one = new JndiPermission("1/2/3", JndiPermission.ALL_ACTION);
		JndiPermission two = new JndiPermission("1/2/3", JndiPermission.ALL_ACTION);
		assertEquals(one, two);
		assertEquals(two, one);

		two = new JndiPermission(new CompositeName("1/2/3"), JndiPermission.ALL_ACTION);
		assertEquals(one, two);
		assertEquals(two, one);

		two = new JndiPermission("1/2/4", JndiPermission.ALL_ACTION);
		assertNotSame(one, two);
		assertNotSame(two, one);

		two = new JndiPermission("1/2/*", JndiPermission.ALL_ACTION);
		assertNotSame(one, two);
		assertNotSame(two, one);

		two = new JndiPermission("1/2/-", JndiPermission.ALL_ACTION);
		assertNotSame(one, two);
		assertNotSame(two, one);

		two = new JndiPermission(ALL_BINDINGS, JndiPermission.ALL_ACTION);
		assertNotSame(one, two);
		assertNotSame(two, one);

		one = new JndiPermission("1/2/*", JndiPermission.ALL_ACTION);
		two = new JndiPermission("1/2/*", JndiPermission.ALL_ACTION);
		assertEquals(one, two);
		assertEquals(two, one);

		one = new JndiPermission("1/2/-", JndiPermission.ALL_ACTION);
		two = new JndiPermission("1/2/-", JndiPermission.ALL_ACTION);
		assertEquals(one, two);
		assertEquals(two, one);

		one = new JndiPermission(ALL_BINDINGS, JndiPermission.ALL_ACTION);
		two = new JndiPermission(ALL_BINDINGS, JndiPermission.ALL_ACTION);
		assertEquals(one, two);
		assertEquals(two, one);
	}

	@Test
	void testEqualsActions() throws Exception {
		for (String action1 : ACTION_ARRAY) {
			for (String action2 : ACTION_ARRAY) {
				JndiPermission one = new JndiPermission("1/2/3", action1);
				JndiPermission two = new JndiPermission("1/2/3", action1);
				if (action1.equals(action2)) {
					assertEquals(one, two);
					assertEquals(two, one);
				} else {
					assertNotSame(one, two);
					assertNotSame(two, one);
				}
			}
		}

		JndiPermission one = new JndiPermission("1/2/3", ALL_ACTIONS);
		JndiPermission two = new JndiPermission("1/2/3", JndiPermission.ALL_ACTION);
		assertEquals(one, two);
		assertEquals(two, one);

		one = new JndiPermission("1/2/3", "bind,unbind");
		two = new JndiPermission("1/2/3", "unbind,bind");
		assertEquals(one, two);
		assertEquals(two, one);

		one = new JndiPermission("1/2/3", "bind,unbind");
		two = new JndiPermission("1/2/3", "unbind");
		assertNotSame(one, two);
		assertNotSame(two, one);
	}

	@Test
	void testSerialization() throws Exception {
		testSerialization(new JndiPermission("simple", JndiPermission.ALL_ACTION));
		testSerialization(new JndiPermission(new CompositeName("simple"), JndiPermission.ALL_ACTION));
		testSerialization(new JndiPermission("1/2/3", JndiPermission.ALL_ACTION));
		testSerialization(new JndiPermission("1/2/*", JndiPermission.ALL_ACTION));
		testSerialization(new JndiPermission("1/2/-", JndiPermission.ALL_ACTION));
		testSerialization(new JndiPermission(ALL_BINDINGS, JndiPermission.ALL_ACTION));
		for (String action : ACTION_ARRAY)
			testSerialization(new JndiPermission("simple", action));
	}

	void testSerialization(JndiPermission expected) throws Exception {
		JndiPermission actual = serializeDeserialize(expected, JndiPermission.class);
		assertEquals(expected, actual);
	}

	@Test
	void testPermissionsCollection() throws Exception {
		JndiPermission one = new JndiPermission("1/2/3/*", "bind");
		JndiPermission two = new JndiPermission("1/2/3/4", "unbind");

		PermissionCollection permissions = one.newPermissionCollection();
		assertFalse(permissions.implies(new JndiPermission("1/2/3/4", "bind")));
		assertFalse(permissions.implies(new JndiPermission("1/2/3/4", "unbind")));
		assertFalse(permissions.elements().hasMoreElements());

		permissions.add(one);
		permissions.add(two);

		JndiPermission test = new JndiPermission("1/2/3/4", "bind");
		assertTrue(permissions.implies(test));

		test = new JndiPermission("1/2/3/5", "bind");
		assertTrue(permissions.implies(test));

		test = new JndiPermission("1/2/3/4", "unbind");
		assertTrue(permissions.implies(test));

		test = new JndiPermission("1/2/3/5", "unbind");
		assertFalse(permissions.implies(test));

		test = new JndiPermission("1/2/3", "bind");
		assertFalse(permissions.implies(test));

		assertThrows(IllegalArgumentException.class, () -> {
			permissions.add(null);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			permissions.add(new RuntimePermission("createClassLoader"));
		});
		HashSet<Permission> expected = new HashSet<Permission>();
		expected.add(one);
		expected.add(two);

		HashSet<Permission> actual = new HashSet<Permission>();
		for (Enumeration<Permission> enumeration = permissions.elements(); enumeration.hasMoreElements();)
			actual.add(enumeration.nextElement());
		assertEquals(expected, actual);
	}

	/**
	 * Serialize an object
	 * 
	 * @param object the object
	 * @return the bytes
	 * @throws Exception for any error
	 */
	protected byte[] serialize(Serializable object) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		oos.close();
		return baos.toByteArray();
	}

	/**
	 * Serialize an object
	 *
	 * @param bytes - the raw serialzied object data
	 * @return the bytes
	 * @throws Exception for any error
	 */
	protected Object deserialize(byte[] bytes) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		return ois.readObject();
	}

	/**
	 * Serialize/deserialize
	 * 
	 * @param <T>      the expected type
	 * @param value    the value
	 * @param expected the expected type
	 * @return the result
	 * @throws Exception for any problem
	 */
	protected <T> T serializeDeserialize(Serializable value, Class<T> expected) throws Exception {
		byte[] bytes = serialize(value);
		Object result = deserialize(bytes);
		return assertInstanceOf(expected, result);
	}

}