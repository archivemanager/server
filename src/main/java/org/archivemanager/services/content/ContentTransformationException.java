/*
 * Copyright (C) 2011 Heed Technology Inc.
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
package org.archivemanager.services.content;

public class ContentTransformationException extends Exception {
	private static final long serialVersionUID = 7156479716935102827L;

	
	public ContentTransformationException(Exception exception) {
		super(exception);
	}
	public ContentTransformationException(String msg) {
		super(msg);
	}
	public ContentTransformationException(String msg, Exception exception) {
		super(msg, exception);
	}
	public ContentTransformationException(String msg, Throwable exception) {
		super(msg, exception);
		
	}
}
