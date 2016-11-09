package com.alexstyl.specialdates.events.peopleevents;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.alexstyl.specialdates.Optional;
import com.alexstyl.specialdates.contact.Contact;
import com.alexstyl.specialdates.contact.ContactNotFoundException;
import com.alexstyl.specialdates.contact.ContactProvider;
import com.alexstyl.specialdates.date.ContactEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.alexstyl.specialdates.events.peopleevents.EventType.BIRTHDAY;

class PeopleEventsRepository {

    private static final List<ContactEvent> NO_EVENTS = Collections.emptyList();
    private static final Optional<Contact> NO_CONTACT = Optional.absent();

    private final ContentResolver contentResolver;
    private final ContactProvider contactProvider;

    PeopleEventsRepository(ContentResolver contentResolver, ContactProvider contactProvider) {
        this.contentResolver = contentResolver;
        this.contactProvider = contactProvider;
    }

    List<ContactEvent> fetchPeopleWithEvents() {
        Cursor cursor = BirthdayQuery.query(contentResolver);
        if (isInvalid(cursor)) {
            return NO_EVENTS;
        }
        List<ContactEvent> events = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                int contactIdIndex = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
                long contactId = cursor.getLong(contactIdIndex);
                Optional<Contact> optionalContact = getDeviceContactWithId(contactId);
                if (optionalContact.isPresent()) {
                    Contact contact = optionalContact.get();
                    events.add(new ContactEvent(BIRTHDAY, contact.getDateOfBirth(), contact));
                }
            }
        } finally {
            cursor.close();
        }
        return events;
    }

    private Optional<Contact> getDeviceContactWithId(long id) {
        try {
            Contact contact = contactProvider.getOrCreateContact(id);
            return new Optional<>(contact);
        } catch (ContactNotFoundException e) {
            return NO_CONTACT;
        }
    }

    private boolean isInvalid(Cursor cursor) {
        return cursor == null || cursor.isClosed();
    }

    /**
     * Contract that queries birthdays only
     */
    private static final class BirthdayQuery {
        /**
         * Queries the contacts tables for birthdays with the default settings.
         */
        public static Cursor query(ContentResolver cr) {
            return cr.query(CONTENT_URI, PROJECTION, WHERE, WHERE_ARGS, SORT_ORDER);
        }

        private static final Uri CONTENT_URI = ContactsContract.Data.CONTENT_URI;
        private static final String COL_DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;

        static final String WHERE =
                "(" + ContactsContract.Data.MIMETYPE + " = ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE
                        + "="
                        + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + ")"
                        + " AND " + ContactsContract.Data.IN_VISIBLE_GROUP + " = 1";

        static final String[] WHERE_ARGS = {ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};
        final static String SORT_ORDER = COL_DISPLAY_NAME;
        static final String[] PROJECTION = {ContactsContract.Data.CONTACT_ID};
        public static final int ID = 0;

    }
}