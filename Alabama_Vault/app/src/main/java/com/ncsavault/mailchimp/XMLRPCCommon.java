package com.ncsavault.mailchimp;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

class XMLRPCCommon {

	protected final XmlSerializer serializer;
	protected final IXMLRPCSerializer iXMLRPCSerializer;
	
	XMLRPCCommon() {
		serializer = Xml.newSerializer();
		iXMLRPCSerializer = new XMLRPCSerializer();
	}

	protected void serializeParams(Object[] params) throws IllegalArgumentException, IllegalStateException, IOException {
		if (params != null && params.length != 0)
		{
			// set method params
			serializer.startTag(null, Tag.PARAMS);
			for (Object param : params) {
				serializer.startTag(null, Tag.PARAM).startTag(null, IXMLRPCSerializer.TAG_VALUE);
				iXMLRPCSerializer.serialize(serializer, param);
				serializer.endTag(null, IXMLRPCSerializer.TAG_VALUE).endTag(null, Tag.PARAM);
			}
			serializer.endTag(null, Tag.PARAMS);
		}
	}

}
