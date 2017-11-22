package com.ncsavault.mailchimp.api.lists;

import com.ncsavault.mailchimp.XMLRPCSerializable;
import com.ncsavault.mailchimp.api.Constants;

import java.util.Date;
import java.util.Hashtable;


/**
 * Utility class for building up a list of merge fields.  This has some utility to handle some of the specific types
 * like dates and addresses.

 *
 */
public class MergeFieldListUtil extends Hashtable<Object, Object> implements XMLRPCSerializable {

	private static final long serialVersionUID = 2340843397084407707L;
	

	/**
	 * This will make sure the date is formatted properly, this date should be in GMT
     * @param date date
     */
	public void addDateField(Date date) {
		put("BIRTHDAY", Constants.TIME_FMT.format(date));
	}
	

	/**
	 * Adds a field of any type.  If you are trying to add a complex object here, make sure it implements
	 * XMLRPCSerializable or is a primitive type
	 * @param key key
	 * @param value value
	 */
	public void addField(String key, Object value) {
		put(key, value);
	}
	
	/**
	 * Implements XMLRPCSerializable
	 * @return the serialized version of everything built-up as an XMLRPC struct
	 */
	public Object getSerializable() {
		return this;
	}

	/**
	 * Convenience to add an email address
	 * @param string email
	 */
	public void addEmail(String string) {
		put("EMAIL", string);
	}

	
}
