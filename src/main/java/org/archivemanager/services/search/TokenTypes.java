/*
 * Copyright (C) 2010 Heed Technology Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.archivemanager.services.search;

public interface TokenTypes {

	public static final int NULL = 0;
	public static final int ROOT = 1;
	public static final int OR = 2;
	public static final int AND = 3;
	public static final int NOT = 4;
	public static final int GRTR = 5;
	public static final int LESS = 6;
	public static final int EQAL = 7;
		
	public static final int ID = 9;
	public static final int TEXT = 10;
	public static final int ATTR = 11;
	public static final int CATG = 12;
	public static final int TERM = 13;
	public static final int COLM = 14; /*column/field value */
	public static final int NAME = 15; /*named entity relation */
	public static final int PROP = 16;
	public static final int DATE = 17;
	public static final int SUBJ = 18; /*subject entity relation */
	public static final int QNAM = 19; /*entity qualified name */
	public static final int ENTITY = 20;
	public static final int ALL = 21;
	
	public static final int MULT = 30;
	public static final int DRNG = 31; /*date range*/
	public static final int PATH = 32;
	
	public static final int NUMB = 40;
	public static final int YEAR = 41;
	public static final int MONTH = 42;
	public static final int DAY = 43;
}
