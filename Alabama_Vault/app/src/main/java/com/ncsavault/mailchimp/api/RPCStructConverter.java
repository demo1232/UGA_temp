package com.ncsavault.mailchimp.api;

import java.util.Map;

public interface RPCStructConverter {

	@SuppressWarnings({"unchecked", "RedundantThrows"})
	void populateFromRPCStruct(String key, Map struct) throws MailChimpApiException;
	
}
