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
package com.bapplications.maplemobile.pkgnx.nodes;


import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXFile;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.util.SeekableLittleEndianAccessor;


/**
 * An {@code NXNode} representing a 2D vector as a {@code Point}.
 *
 * @author Aaron Weiss
 * @version 2.0.0
 * @since 5/27/13
 */
public class NXPointNode extends NXNode {
	private final Point point;

	/**
	 * Creates a new {@code NXPointNode}.
	 *
	 * @param name       the name of the node
	 * @param file       the file the node is from
	 * @param childIndex the index of the first child of the node
	 * @param childCount the number of children
	 * @param slea       the {@code SeekableLittleEndianAccessor} to read from
	 */
	public NXPointNode(String name, NXFile file, long childIndex, int childCount, SeekableLittleEndianAccessor slea) {
		super(name, file, childIndex, childCount);
		point = new Point(slea.getInt(), slea.getInt());
	}

	@Override
	public Point get() {
		return point;
	}


	/**
	 * Gets the value of this node as a {@code Point}.
	 *
	 * @return the node value
	 */
	public Point getPoint() {
		return point;
	}
}
