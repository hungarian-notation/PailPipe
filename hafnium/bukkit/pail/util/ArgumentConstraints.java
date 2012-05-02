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
package hafnium.bukkit.pail.util;

public class ArgumentConstraints {

	public interface IntegerConstraint {
		public boolean isAcceptable(int val);

		public String andBeString();
	}

	public final static class IntMaxConstraint implements IntegerConstraint {
		private final int max;

		public IntMaxConstraint(int max) {
			this.max = max;
		}

		@Override
		public boolean isAcceptable(int val) {
			return val <= this.max;
		}

		@Override
		public String andBeString() {
			return "^nless than ^c" + this.max + " ^ninclusive";
		}
	}

	public final static class IntMinConstraint implements IntegerConstraint {
		private final int min;

		public IntMinConstraint(int min) {
			this.min = min;
		}

		@Override
		public boolean isAcceptable(int val) {
			return val >= this.min;
		}

		@Override
		public String andBeString() {
			return "^ngreater than ^c" + this.min + " ^ninclusive";
		}
	}

	public final static class IntRangeConstraint implements IntegerConstraint {
		private final int min, max;

		public IntRangeConstraint(int min, int max) {
			this.min = min;
			this.max = max;
		}

		@Override
		public boolean isAcceptable(int val) {
			return (val >= this.min) && (val <= this.max);
		}

		@Override
		public String andBeString() {
			return "^nbetween ^c" + this.min + " ^nand ^c" + this.max + " ^ninclusive";
		}
	}
}
