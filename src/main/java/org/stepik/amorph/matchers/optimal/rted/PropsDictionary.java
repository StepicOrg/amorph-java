//    Copyright (C) 2012  Mateusz Pawlik and Nikolaus Augsten
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as
//    published by the Free Software Foundation, either version 3 of the
//    License, or (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.stepik.amorph.matchers.optimal.rted;

import java.util.Hashtable;
import java.util.Map;

/**
 * This provides a way of using small int values to represent String labels, 
 * as opposed to storing the labels directly.
 * 
 * @author Denilson Barbosa, Nikolaus Augsten  from approxlib, available at http://www.inf.unibz.it/~augsten/src/
 */
public class PropsDictionary {
	public static final int KEY_DUMMY_LABEL = -1;
	private int count;
	private Map<Map, Integer> propsInt;
	private Map<Integer, Map> intProps;
	private boolean newLabelsAllowed = true;

	/**
	 * Creates a new blank dictionary.
	 */
	public PropsDictionary() {
		count = 0;
		propsInt = new Hashtable<>();
		intProps = new Hashtable<>();
	}
	
	
	/**
	 * Adds a new props to the dictionary if it has not been added yet.
	 * Returns the ID of the new props in the dictionary.
	 * 
	 * @param props add this props to the dictionary if it does not exist yet
	 * @return ID of props in the dictionary
	 */	
	public int store(Map props) {
		if (propsInt.containsKey(props)) {
			return (propsInt.get(props));
		} else if (!newLabelsAllowed) { 
			return KEY_DUMMY_LABEL;
		} else { // store props
			Integer intKey = count++;
			propsInt.put(props, intKey);
			intProps.put(intKey, props);

			return intKey;
		}
	}
	
	/**
	 * Returns the label with a given ID in the dictionary.
	 *	
	 * @param labelID 
	 * @return the label with the specified labelID, or null if this dictionary contains no label for labelID
	 */
	public Map read(int labelID) {
		return intProps.get(labelID);
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

