package com.ncsavault.mailchimp.api.lists;

import com.ncsavault.mailchimp.XMLRPCException;
import com.ncsavault.mailchimp.api.Constants;
import com.ncsavault.mailchimp.api.MailChimpApi;
import com.ncsavault.mailchimp.api.MailChimpApiException;

/**
 * Provides the list related API functions
 */
public class ListMethods extends MailChimpApi {

    public ListMethods(CharSequence apiKey) {
        super(apiKey);
    }


    /**
     * Convince for subscribing a user, same as calling listSubscribe(listId, emailAddress, mergeFields, null, null, null, null, null);
     *
     * @param listId listId
     * @param emailAddress emailAddress
     * @param mergeFields mergeFields
     * @return list
     * @throws MailChimpApiException Exception
     */
    public boolean listSubscribe(String listId, String emailAddress, MergeFieldListUtil mergeFields) throws MailChimpApiException, XMLRPCException {
        return listSubscribe(listId, emailAddress, mergeFields, null, null, null, null, null);
    }

    /**
     * Subscribe a single user to a list
     *
     * @param listId           the id of the list, see getLists()
     * @param emailAddress     the email address to subscribe
     * @param mergeFields      the merge fields and values for the person to subscribe
     * @return list list
     * @throws MailChimpApiException Exception
     */
    @SuppressWarnings("SameParameterValue")
    public boolean listSubscribe(String listId, String emailAddress, MergeFieldListUtil mergeFields, Constants.EmailType emailType, Boolean doubleOptIn, Boolean updateExisting,
                                 Boolean replaceInterests, Boolean sendWelcome) throws MailChimpApiException, XMLRPCException {

        return (Boolean) callMethod("listSubscribe", listId,
                emailAddress,
                mergeFields == null ? new String[]{""} : mergeFields, // API Note: must send at least an empty array for the merge fields
                emailType == null ? "" : emailType.toString(),
                doubleOptIn == null ? "" : doubleOptIn,
                updateExisting == null ? "" : updateExisting,
                replaceInterests == null ? "" : replaceInterests,
                sendWelcome == null ? "" : sendWelcome);
    }

}
