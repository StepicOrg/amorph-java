/*
 * MIT License
 *
 * Copyright (c) 2017 Nikita Lapkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.stepik.amorph.matchers.optimal.rted;

import java.util.Hashtable;
import java.util.Map;

/**
 * This provides a way of using small int values to represent String labels,
 * as opposed to storing the labels directly.
 *
 * @author Denilson Barbosa, Nikolaus Augsten  from approxlib, available at http://www.inf.unibz.it/~augsten/src/
 */
public class LabelDictionary {
	public static final int KEY_DUMMY_LABEL = -1;
	private int count;
	private Map<String, Integer> strInt;
	private Map<Integer, String> intStr;
	private boolean newLabelsAllowed = true;

	/**
	 * Creates a new blank dictionary.
	 */
	public LabelDictionary() {
		count = 0;
		strInt = new Hashtable<String, Integer>();
		intStr = new Hashtable<Integer, String>();
	}


	/**
	 * Adds a new label to the dictionary if it has not been added yet.
	 * Returns the ID of the new label in the dictionary.
	 *
	 * @param label add this label to the dictionary if it does not exist yet
	 * @return ID of label in the dictionary
	 */
	public int store(String label) {
		if (strInt.containsKey(label)) {
			return (strInt.get(label).intValue());
		} else if (!newLabelsAllowed) {
			return KEY_DUMMY_LABEL;
		} else { // store label
			Integer intKey = new Integer(count++);
			strInt.put(label, intKey);
			intStr.put(intKey, label);

			return intKey.intValue();
		}
	}

	/**
	 * Returns the label with a given ID in the dictionary.
	 *
	 * @param labelID
	 * @return the label with the specified labelID, or null if this dictionary contains no label for labelID
	 */
	public String read(int labelID) {
		return intStr.get(new Integer(labelID));
	}

	/**
	 * @return true iff new labels can be stored into this label dictinoary
	 */
	public boolean isNewLabelsAllowed() {
		return newLabelsAllowed;
	}

	/**
	 * @param newLabelsAllowed the newLabelsAllowed to set
	 */
	public void setNewLabelsAllowed(boolean newLabelsAllowed) {
		this.newLabelsAllowed = newLabelsAllowed;
	}

}
