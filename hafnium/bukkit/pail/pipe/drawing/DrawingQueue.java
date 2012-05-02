/* 
 * Copyright (c) 2012 Chris Bode
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 * documentation files (the "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the 
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions 
 * of the Software.
 *
 * No affiliation with PailPipe or any related projects is claimed.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */
package hafnium.bukkit.pail.pipe.drawing;

import java.util.LinkedList;
import java.util.Queue;

public class DrawingQueue implements Runnable {
	private final Queue<DrawingOperation> ops;

	public DrawingQueue() {
		this.ops = new LinkedList<DrawingOperation>();
	}

	public void enqueue(DrawingOperation op) {
		this.ops.add(op);
	}

	public boolean draw() {
		return this.draw(25);
	}

	public boolean draw(int duration) {
		long start = time();

		if (this.ops.peek() == null)
			return true;

		do
			if (!this.ops.peek().isDone())
				this.ops.peek().drawBlock();
			else
				this.ops.poll();
		while ((this.ops.peek() != null) && (time() < start + duration));

		return this.ops.peek() == null;
	}

	private static long time() {
		return System.currentTimeMillis();
	}

	@Override
	public void run() {
		this.draw();
	}
}
