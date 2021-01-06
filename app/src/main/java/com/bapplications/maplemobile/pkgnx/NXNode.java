/*
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Aaron Weiss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bapplications.maplemobile.pkgnx;

import com.bapplications.maplemobile.pkgnx.nodes.NXDoubleNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXNoChildrenNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The basic information container for the NX file format.
 *
 * @author Aaron Weiss
 * @version 2.0.1
 * @since 5/26/13
 */
public abstract class NXNode implements Iterable<NXNode> {
	private static final EmptyNodeIterator EMPTY_NODE_ITERATOR = new EmptyNodeIterator();
	private static final int MIN_COUNT_FOR_MAPS = 41;
	public static final int NODE_SIZE = 20;

	protected String name;
	protected NXFile file;
	protected int childCount;
	protected long childIndex;
	private NXNode[] children;
	private NXNoChildrenNode nullChildrens;
	private Map<String, NXNode> childMap;

	/**
	 * Sets up the basic information for the {@code NXNode}.
	 *
	 * @param name       the name of the node
	 * @param file       the file the node is from
	 * @param childIndex the index of the first child of the node
	 * @param childCount the number of children
	 */
	public NXNode(String name, NXFile file, long childIndex, int childCount) {
		this.name = name;
		this.file = file;
		this.childIndex = childIndex;
		this.childCount = childCount;
		this.nullChildrens = new NXNoChildrenNode();
		if (childCount >= MIN_COUNT_FOR_MAPS) {
			childMap = new HashMap<>();
			children = null;
		} else if (childCount > 0) {
			children = new NXNode[childCount];
		} else {
			children = null;
		}
	}

	public NXNode() {
	}

	/**
	 * Populates the children {@code Map} for this node.
	 */
	void populateChildren() {
		if (childCount == 0)
			return;
		if (childMap != null && childMap.isEmpty()) {
			for (int i = (int) childIndex; i < childIndex + childCount; i++) {
				childMap.put(file.getNode(i).getName(), file.getNode(i));
			}
		} else if (children[0] == null) {
			int k = 0;
			for (int i = (int) childIndex; i < childIndex + childCount; i++) {
				children[k++] = file.getNode(i);
			}
		}
	}

	/**
	 * Gets the value of this node universally.
	 *
	 * @return the value as an {@code Object}
	 */
	protected abstract Object get();

	public <T> T get(T def) {
		T res = (T) get();
		return res != null ? res : def;
	}

	public <T extends NXNode> T getChild(int name) {
		return getChild("" + name);
	}
	/**
	 * Gets a child node by {@code name}. Returns null if child is not present.
	 *
	 * @param name the name of the child
	 * @return the child {@code NXNode}
	 */
	@SuppressWarnings("unchecked")
	public <T extends NXNode> T getChild(String name) {
		if (childCount == 0 || isNotExist()) {
			return (T) nullChildrens;
		}
		T res = (T) searchChild(name);
		if (res == null) {
			return (T) nullChildrens;
		}
		return res;
	}

	public boolean isNotExist() {
		return this instanceof NXNoChildrenNode;
	}

	public boolean isExist() {
		return !isNotExist();
	}

	public boolean isChildExist(int name){
		return isChildExist("" + name);
	}
	public boolean isChildExist(String name){
		return getChild(name) != nullChildrens;
	}

	/**
	 * Determines whether or not this node has a child by the specified {@code name}.
	 *
	 * @param name the name of the child
	 * @return whether or not this node has a child by the specified {@code name}
	 */
	public boolean hasChild(String name) {
		if (childCount == 0)
			return false;
		return searchChild(name) != null;
	}

	/**
	 * Searches for a specific child node by {@code name}. Internally, this deals with how the children are stored.
	 *
	 * @param name the name of the child to find
	 * @return the found child or null, if it doesn't exist
	 */
	protected NXNode searchChild(String name) {
		if (childCount == 0)
			return null;
		else if ((children != null && children[0] == null) || (childMap != null && childMap.isEmpty()))
			populateChildren();
		if (childMap != null)
			return childMap.get(name);
		int min = 0, max = childCount - 1;
		String minVal = children[min].getName(), maxVal = children[max].getName();
		while (true) {
			if (name.compareTo(minVal) <= 0)
				return (name.equals(minVal)) ? children[min] : null;
			if (name.compareTo(maxVal) >= 0)
				return (name.equals(maxVal)) ? children[max] : null;
			int pivot = (min + max) >> 1;
			String pivotVal = children[pivot].getName();

			if (name.compareTo(pivotVal) > 0) {
				min = pivot + 1;
				max--;
			} else if (name.equals(pivotVal)) {
				return children[pivot];
			} else {
				min++;
				max = pivot - 1;
			}
			minVal = children[min].getName();
			maxVal = children[max].getName();
		}
	}

	/**
	 * Gets the name of the node.
	 *
	 * @return the name of this node
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the file that the node belonged to.
	 *
	 * @return the file owning this node
	 */
	public NXFile getFile() {
		return file;
	}

	/**
	 * Gets the number of children had by this node.
	 *
	 * @return number of child nodes
	 */
	public int getChildCount() {
		return childCount;
	}

	/**
	 * Gets the index of the first child of this node.
	 *
	 * @return first child node index
	 */
	public long getFirstChildIndex() {
		return childIndex;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (!(obj instanceof NXNode))
			return false;
		NXNode n = (NXNode) obj;
		return obj == this || (n.getName().equals(getName()) &&
					n.getChildCount() == getChildCount() &&
					n.getFirstChildIndex() == getFirstChildIndex() &&
					((n.get() == null && get() == null) || n.get().equals(get())));
	}

	@Override
	public Iterator<NXNode> iterator() {
		if (childCount == 0)
			return EMPTY_NODE_ITERATOR;
		else if ((children != null && children[0] == null) || (childMap != null && childMap.isEmpty()))
			populateChildren();
		return (childMap != null) ? childMap.values().iterator() : Arrays.asList(children).iterator();
	}

	public NXDoubleNode getDoubleChild(String name) {
		return (NXDoubleNode)getChild(name);
	}

	public boolean getBool() {
		return get(0L) > 0;
	}

    /**
	 * A silent, empty iterator for childless {@code NXNode}s.
	 *
	 * @author Aaron Weiss
	 * @version 1.0
	 * @since 5/26/13
	 */
	private static class EmptyNodeIterator implements Iterator<NXNode> {
		/**
		 * Creates an {@code EmptyNodeIterator}.
		 */
		private EmptyNodeIterator() {
			return;
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public NXNode next() {
			return null;
		}

		@Override
		public void remove() {
			return;
		}
	}
}
