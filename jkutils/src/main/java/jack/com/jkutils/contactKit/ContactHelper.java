package jack.com.jkutils.contactKit;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import jack.com.jkutils.contactKit.element.BaseElement;
import jack.com.jkutils.contactKit.element.EmailAddress;
import jack.com.jkutils.contactKit.element.Name;
import jack.com.jkutils.contactKit.element.Note;
import jack.com.jkutils.contactKit.element.Organization;
import jack.com.jkutils.contactKit.element.PhoneNumber;
import jack.com.jkutils.contactKit.element.Photo;
import jack.com.jkutils.contactKit.element.PostalAddress;
import jack.com.jkutils.contactKit.element.SocialProfile;
import jack.com.jkutils.contactKit.element.WebSite;

import java.util.ArrayList;

public class ContactHelper {


    private static volatile ContactHelper sharedHelper;
    private ContentResolver mContentResolver;
    private Context mContext;

    //region Construction
    /**
     * context should be ApplicationContext
     * */
    public static ContactHelper sharedHelper(Context context) {
        if (context == null) {
            return null;
        }

        if (sharedHelper == null) {
            synchronized (ContactHelper.class) {
                if (sharedHelper == null) {
                    sharedHelper = new ContactHelper(context);
                }
            }
        }
        return sharedHelper;
    }

    private ContactHelper(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }
    // endregion

    //region Look Up
    /**
     * 查询信息
     * */
    private ArrayList<PhoneNumber> traversePhoneNumbersByID(Integer ID) {

        Uri phoneNumberUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] phoneNumberProjections = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.LABEL,
                ContactsContract.Data._ID
        };

        String phoneNumberSelection = ContactsContract.Data.CONTACT_ID + "='" + ID + "'";

        Cursor phoneCur = mContentResolver.query(phoneNumberUri, phoneNumberProjections, phoneNumberSelection, null, null);
        if (phoneCur != null) {

            ArrayList<PhoneNumber> phoneNumberArray = new ArrayList<>();
            while(phoneCur.moveToNext()) {

                String phoneNumber = phoneCur.getString(0);
                Integer phoneType = phoneCur.getInt(1);
                String phoneLabel = phoneCur.getString(2);
                Integer id = phoneCur.getInt(3);

                PhoneNumber phone = new PhoneNumber(id);
                phone.value = phoneNumber;
                phone.label = phoneLabel;
                phone.type = PhoneNumber.PhoneNumberType.values()[phoneType];
                phone.localizedLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(mContext.getResources(), phoneType, phoneLabel).toString();

                phoneNumberArray.add(phone);

            }
            phoneCur.close();
            return phoneNumberArray;
        }

        return null;
    }

    private ArrayList<EmailAddress> traverseEmailAddressByID(Integer ID) {

        Uri emailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;

        String[] emailProjections = new String[]{
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Email.TYPE,
                ContactsContract.CommonDataKinds.Email.LABEL,
                ContactsContract.Data._ID
        };

        String emailSelection = ContactsContract.Data.CONTACT_ID + "='" + ID + "'";

        Cursor emailCur = mContentResolver.query(emailUri, emailProjections, emailSelection, null, null);

        if (emailCur != null) {

            ArrayList<EmailAddress> emailAddressArray = new ArrayList<>();
            while(emailCur.moveToNext()) {

                String email = emailCur.getString(0);
                Integer emailType = emailCur.getInt(1);
                String emailLabel = emailCur.getString(2);
                Integer id = emailCur.getInt(3);

                EmailAddress emailAddress = new EmailAddress(id);
                emailAddress.value = email;
                emailAddress.type = EmailAddress.EmailType.values()[emailType];
                emailAddress.label = emailLabel;
                emailAddress.localizedLabel = ContactsContract.CommonDataKinds.Email.getTypeLabel(mContext.getResources(), emailType, emailLabel).toString();

                emailAddressArray.add(emailAddress);
            }
            emailCur.close();
            return emailAddressArray;
        }
        return null;
    }

    private ArrayList<PostalAddress> traversePostalAddressByID(Integer ID) {

        Uri addressUri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;

        String[] addressProjections = null;

        String addressSelection = ContactsContract.Data.CONTACT_ID + "='" + ID + "'";

        Cursor addressCur = mContentResolver.query(addressUri, addressProjections, addressSelection, null, null);

        if (addressCur != null) {

            ArrayList<PostalAddress> postalAddressArray = new ArrayList<>();
            while (addressCur.moveToNext()) {

                String country = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                String state = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                String city = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String street = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String postalCode = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                String formatAddress = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));

                Integer addressType = addressCur.getInt(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                String addressLabel = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL));

                Integer id = addressCur.getInt(addressCur.getColumnIndex(ContactsContract.Data._ID));

                PostalAddress address = new PostalAddress(id);
                address.country = country;
                address.state = state;
                address.city = city;
                address.street = street;
                address.postalCode = postalCode;
                address.formatAddress = formatAddress;
                address.type = PostalAddress.AddressType.values()[addressType];
                address.label = addressLabel;
                address.localizedLabel = ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabel(mContext.getResources(), addressType, addressLabel).toString();

                postalAddressArray.add(address);
            }
            addressCur.close();
            return postalAddressArray;
        }

        return null;
    }

    private ArrayList<SocialProfile> traverseSocialProfileByID(Integer ID) {

        Uri socialUri = ContactsContract.Data.CONTENT_URI;

        String[] socialProjections = new String[] {
                ContactsContract.CommonDataKinds.Im.DATA,
                ContactsContract.CommonDataKinds.Im.TYPE,
                ContactsContract.CommonDataKinds.Im.LABEL,
                ContactsContract.CommonDataKinds.Im.PROTOCOL,
                ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL,
                ContactsContract.Data._ID
        };

        String socialSelection = ContactsContract.Data.CONTACT_ID + "='" + ID + "'" + " And " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE + "'";

        Cursor socialCur = mContentResolver.query(socialUri, socialProjections, socialSelection, null, null);
        if (socialCur != null) {

            ArrayList<SocialProfile> socialProfileArray = new ArrayList<>();
            while (socialCur.moveToNext()) {


                String socialValue = socialCur.getString(0);
                Integer socialType = socialCur.getInt(1);
                String socialLabel = socialCur.getString(2);
                Integer socialProtocol = socialCur.getInt(3);
                String socialCustomProtocol = socialCur.getString(4);
                Integer id = socialCur.getInt(5);

                SocialProfile socialProfile = new SocialProfile(id);
                socialProfile.value = socialValue;
                socialProfile.label = socialLabel;
                socialProfile.type = SocialProfile.SocialProfileType.values()[socialType];
                socialProfile.localizedLabel = ContactsContract.CommonDataKinds.Im.getTypeLabel(mContext.getResources(), socialType, socialLabel).toString();
                socialProfile.setSocialProtocol(SocialProfile.SocialProfileProtocol.values()[socialProtocol + 1]);
                socialProfile.localizedProtocol = ContactsContract.CommonDataKinds.Im.getProtocolLabel(mContext.getResources(), socialProtocol, socialCustomProtocol).toString();

                socialProfileArray.add(socialProfile);
            }
            socialCur.close();
            return socialProfileArray;
        }

        return null;
    }

    private ArrayList<WebSite> traverseWebSiteByID(Integer ID) {

        Uri webUri = ContactsContract.Data.CONTENT_URI;

        String[] webProjections = new String[] {
                ContactsContract.CommonDataKinds.Website.URL,
                ContactsContract.Data._ID
        };

        String webSelection = ContactsContract.Data.CONTACT_ID + "='" + ID + "'" + " And " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE + "'";

        Cursor webCur = mContentResolver.query(webUri, webProjections, webSelection, null, null);
        if (webCur != null) {

            ArrayList<WebSite> webArray = new ArrayList<>();
            while (webCur.moveToNext()) {

                String web = webCur.getString(0);
                Integer id = webCur.getInt(1);

                WebSite webSite = new WebSite(id);
                webSite.url = web;

                webArray.add(webSite);
            }
            webCur.close();
            return webArray;
        }

        return null;
    }

    private Note traverseNoteByID(Integer ID) {

        Uri noteUri = ContactsContract.Data.CONTENT_URI;
        String[] noteProjections = new String[] {
                ContactsContract.CommonDataKinds.Note.NOTE,
                ContactsContract.Data._ID
        };

        String noteSelection = ContactsContract.Data.CONTACT_ID + "='" + ID + "'" + " And " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "'";

        Cursor noteCur = mContentResolver.query(noteUri, noteProjections, noteSelection, null, null);
        if (noteCur != null) {
            Note note = null;
            if (noteCur.moveToFirst()) {

                String noteStr = noteCur.getString(0);
                Integer id = noteCur.getInt(1);

                note = new Note(id);
                note.note = noteStr;
            }
            noteCur.close();
            return note;
        }

        return null;
    }

    private Organization traverseOrganizationByID(Integer ID) {

        Uri jobUri = ContactsContract.Data.CONTENT_URI;

        String[] jobProjections = new String[]{
                ContactsContract.CommonDataKinds.Organization.COMPANY,
                ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
                ContactsContract.CommonDataKinds.Organization.TITLE,
                ContactsContract.Data._ID
        };

        String jobSelection = ContactsContract.Data.RAW_CONTACT_ID + "='" + ID + "'" + " AND " + ContactsContract.Contacts.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "'";

        Cursor jobCur = mContentResolver.query(jobUri, jobProjections, jobSelection, null, null);

        if (jobCur != null) {

            Organization organization = null;
            if (jobCur.moveToFirst()) {

                String organizationName = jobCur.getString(0);
                String department = jobCur.getString(1);
                String jobTitle = jobCur.getString(2);
                Integer id = jobCur.getInt(3);

                organization = new Organization(id);
                organization.organizationName = organizationName;
                organization.department = department;
                organization.jobTitle = jobTitle;
            }
            jobCur.close();
            return organization;
        }
        return null;
    }

    private Photo traversePhotoByID(Integer ID) {

        Uri photoUri = ContactsContract.Data.CONTENT_URI;

        String[] photoProjections = new String[]{
                ContactsContract.CommonDataKinds.Photo.PHOTO_FILE_ID,
                ContactsContract.CommonDataKinds.Photo.PHOTO,
                ContactsContract.Data._ID
        };

        String photoSelection = ContactsContract.Data.RAW_CONTACT_ID + "='" + ID + "'" + " AND " + ContactsContract.Contacts.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";

        Cursor photoCur = mContentResolver.query(photoUri, photoProjections, photoSelection, null, null);

        if (photoCur != null) {

            Photo photo = null;
            if (photoCur.moveToFirst()) {

                String photoFileID = photoCur.getString(0);
                byte[] thumbnail = photoCur.getBlob(1);
                Integer id = photoCur.getInt(2);

                Uri uri = Uri.withAppendedPath(ContactsContract.DisplayPhoto.CONTENT_URI, photoFileID);

                photo = new Photo(id);
                photo.uri = uri;
                photo.thumbnail = thumbnail;
            }
            photoCur.close();
            return photo;
        }

        return null;
    }

    private Name traverseNameByID(Integer ID) {

        Uri nameUri = ContactsContract.Data.CONTENT_URI;

        String[] nameProjections = new String[]{
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                ContactsContract.Data._ID
        };

        String nameSelection = ContactsContract.Data.RAW_CONTACT_ID + "='" + ID + "'" + " AND " + ContactsContract.Contacts.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'";

        Cursor nameCur = mContentResolver.query(nameUri, nameProjections, nameSelection, null, null);

        if (nameCur != null) {

            Name name = null;
            if (nameCur.moveToFirst()) {

                String givenName = nameCur.getString(0);
                String middleName = nameCur.getString(1);
                String familyName = nameCur.getString(2);
                String displayName = nameCur.getString(3);
                Integer id = nameCur.getInt(4);

                name = new Name(id);
                name.displayName = displayName;
                name.givenName = givenName;
                name.middleName = middleName;
                name.familyName = familyName;
            }
            nameCur.close();
            return name;
        }

        return null;
    }

    public Contact searchContactByID(Integer ID) {

        Contact contact = new Contact(ID);

        // name
        contact.name = traverseNameByID(ID);

        // Photo
        contact.photo =  traversePhotoByID(ID);

        // Fetch Job
        contact.organization = traverseOrganizationByID(ID);

        //Fetch Phone Number
        contact.phoneNumbers = traversePhoneNumbersByID(ID);

        //Fetch email
        contact.emailAddresses = traverseEmailAddressByID(ID);

        // Fetch Address
        contact.postalAddresses = traversePostalAddressByID(ID);

        // Fetch Social
        contact.socialProfiles = traverseSocialProfileByID(ID);

        // Fetch URL
        contact.webSites = traverseWebSiteByID(ID);

        // Fetch Note
        contact.note = traverseNoteByID(ID);

        return contact;
    }

    public Contact searchContactByUri(Uri uri) {
        if (uri != null) {

            String[] projections = new String[] {
                    ContactsContract.Contacts._ID
            };

            Cursor cursor = mContentResolver.query(uri, projections, null, null, null);
            if (cursor != null) {

                if (cursor.moveToFirst()) {
                    Integer ID = cursor.getInt(0);
                    cursor.close();
                    return searchContactByID(ID);
                }

            }

        }
        return null;
    }

    public Uri getUriOfContact(Contact contact) {

        if (contact != null && contact.getID() != Contact.NEW_CONTACT_ID) {

            Integer ID = contact.getID();

            Uri uri = ContactsContract.Contacts.CONTENT_URI;

            String[] projections = new String[]{
                    ContactsContract.Contacts.LOOKUP_KEY
            };

            String selection = ContactsContract.Data._ID + "='" + ID + "'";

            Cursor cursor = mContentResolver.query(uri, projections, selection, null, null);
            if (cursor != null) {

                if (cursor.moveToFirst()) {
                    String lookupKey = cursor.getString(0);
                    cursor.close();
                    return ContactsContract.Contacts.getLookupUri(ID, lookupKey);
                }
            }

        }
        return null;
    }

    private Integer getRawContactID(Contact contact) {

        if (contact != null && contact.getID() != Contact.NEW_CONTACT_ID) {

            Integer ID = contact.getID();

            Uri uri = ContactsContract.Data.CONTENT_URI;

            String[] projections = new String[]{
                    ContactsContract.Data.RAW_CONTACT_ID
            };

            String selection = ContactsContract.Data._ID + "='" + ID + "'";

            Cursor cursor = mContentResolver.query(uri, projections, selection, null, null);
            if (cursor != null) {

                if (cursor.moveToFirst()) {
                    Integer rawContactID = cursor.getInt(0);
                    cursor.close();
                    return rawContactID;
                }
            }

        }
        return Contact.NEW_CONTACT_ID;
    }

    /**
     * 遍历所有联系人
     * */
    public ArrayList<Contact> traverseAllContacts() {

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projections = new String[] {
                ContactsContract.Contacts._ID
        };

        Cursor cursor = mContentResolver.query(uri, projections, null, null,null);
        if (cursor != null) {

            ArrayList<Contact> contactArray = new ArrayList<>();
            while (cursor.moveToNext()) {

                Integer ID = cursor.getInt(0);

                Contact contact = searchContactByID(ID);

                contactArray.add(contact);
            }
            cursor.close();
            return contactArray;
        }

        return null;
    }

    /**
     * 查询联系人
     * */
    public ArrayList<Contact> searchContactsByKeyword(String keyword) {

        if (keyword != null) {

            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, keyword);
            String[] projection = new String[]{
                    ContactsContract.Contacts._ID
            };

            Cursor cursor = mContentResolver.query(uri, projection, null, null, null);
            if (cursor != null) {

                ArrayList<Contact> contacts = new ArrayList<>();
                while (cursor.moveToNext()) {

                    Integer ID = cursor.getInt(0);
                    Contact contact = searchContactByID(ID);
                    if (contact != null) {
                        contacts.add(contact);
                    }
                }
                cursor.close();
                return contacts;
            }
        }

        return null;
    }
    // endregion


    //region Insert
    /**
     * 插入信息
     * */

    /**
     * name
     * */
    private ContentProviderOperation insertNameForContact(Name name, int rawContactID) {

        if (name != null && !name.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);

            // given name
            if (name.givenName != null) {
                values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name.givenName);
            }

            // middle name
            if (name.middleName != null) {
                values.put(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, name.middleName);
            }

            // family name
            if (name.familyName != null) {
                values.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, name.familyName);
            }

            return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, rawContactID) // RAW_CONTACT_ID是第一个事务添加得到的，因此这里传入0，applyBatch返回的ContentProviderResult[]数组中第一项
                    .withValues(values)
                    .build();
        }
        return null;
    }

    private ContentProviderOperation insertName(Name name) {
        return insertNameForContact(name, 0);
    }

    /**
     * Photo
     * */
    private  ContentProviderOperation insertPhotoForContact(Photo photo, int rawContactID) {
        if (photo != null && photo.thumbnail != null) {

            return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Photo.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photo.thumbnail)
                    .build();

        }

        return null;
    }

    private ContentProviderOperation insertPhoto(Photo photo) {
        return insertPhotoForContact(photo, 0);
    }

    /**
     * Job
     * */

    private ContentProviderOperation insertJobForContact(Organization organization, int rawContactID) {
        if (organization != null && !organization.isEmpty()) {

            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);

            if (organization.organizationName != null) {
                values.put(ContactsContract.CommonDataKinds.Organization.COMPANY, organization.organizationName);
            }

            if (organization.department != null) {
                values.put(ContactsContract.CommonDataKinds.Organization.DEPARTMENT, organization.department);
            }

            if (organization.jobTitle != null) {
                values.put(ContactsContract.CommonDataKinds.Organization.TITLE, organization.jobTitle);
            }

            return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Organization.RAW_CONTACT_ID, rawContactID)
                    .withValues(values)
                    .build();
        }

        return null;
    }

    private ContentProviderOperation insertJob(Organization organization) {

        return insertJobForContact(organization, 0);
    }

    /**
     * Phone
     * */
    private ContentProviderOperation insertPhoneNumberForContact(PhoneNumber phone, int rawContactID) {

        if (phone != null) {

            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);

            if (phone.type == null) {
                phone.type = PhoneNumber.PhoneNumberType.TYPE_MAIN;
            }
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, phone.type.ordinal());

            if (phone.type == PhoneNumber.PhoneNumberType.TYPE_CUSTOM && phone.localizedLabel != null) {
                values.put(ContactsContract.CommonDataKinds.Phone.LABEL, phone.localizedLabel);
            }

            if (phone.value != null) {
                values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.value);
            }

            if (phone.label != null) {
                values.put(ContactsContract.CommonDataKinds.Phone.LABEL, phone.label);
            }

            return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID, rawContactID)
                    .withValues(values)
                    .build();
        }

        return null;
    }

    private ArrayList<ContentProviderOperation> insertPhoneNumbers(ArrayList<PhoneNumber> phoneNumbers) {

        if (phoneNumbers != null && phoneNumbers.size() > 0) {

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            for (PhoneNumber phone : phoneNumbers) {
                ops.add(insertPhoneNumberForContact(phone, 0));
            }

            return ops;
        }

        return null;
    }

    /**
     * Email
     * */
    private ContentProviderOperation insertEmailAddressForContact(EmailAddress email, int rawContactID) {

        if (email != null) {

            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);

            if (email.type == null) {
                email.type = EmailAddress.EmailType.TYPE_HOME;
            }
            values.put(ContactsContract.CommonDataKinds.Email.TYPE, email.type.ordinal());

            if (email.type == EmailAddress.EmailType.TYPE_CUSTOM && email.localizedLabel != null) {
                values.put(ContactsContract.CommonDataKinds.Email.LABEL, email.localizedLabel);
            }

            if (email.value != null) {
                values.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email.value);
            }

            if (email.label != null) {
                values.put(ContactsContract.CommonDataKinds.Email.LABEL, email.label);
            }

            return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID, rawContactID)
                    .withValues(values)
                    .build();
        }
        return null;
    }

    private ArrayList<ContentProviderOperation> insertEmailAddresses(ArrayList<EmailAddress> emailAddresses) {

        if (emailAddresses != null && emailAddresses.size() > 0) {

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            for (EmailAddress email : emailAddresses) {
                ops.add(insertEmailAddressForContact(email, 0));
            }

            return ops;
        }

        return null;
    }

    /**
     * Postal Address
     * */
    private ContentProviderOperation insertPostalAddressForContact(PostalAddress address, int rawContactID) {

        if (address != null && !address.isEmpty()) {

            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);

            if (address.type == null) {
                address.type = PostalAddress.AddressType.TYPE_HOME;
            }
            values.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, address.type.ordinal());

            if (address.type == PostalAddress.AddressType.TYPE_CUSTOM && address.localizedLabel != null) {
                values.put(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, address.localizedLabel);
            }

            if (address.country != null) {
                values.put(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, address.country);
            }

            if (address.state != null) {
                values.put(ContactsContract.CommonDataKinds.StructuredPostal.REGION, address.state);
            }

            if (address.city != null) {
                values.put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, address.city);
            }

            if (address.street != null) {
                values.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address.street);
            }

            if (address.postalCode != null) {
                values.put(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, address.postalCode);
            }

            return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.StructuredPostal.RAW_CONTACT_ID, rawContactID)
                    .withValues(values)
                    .build();
        }

        return null;
    }

    private ArrayList<ContentProviderOperation> insertPostalAddresses(ArrayList<PostalAddress> postalAddresses) {

        if (postalAddresses != null && postalAddresses.size() > 0) {

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            for (PostalAddress address : postalAddresses) {

                if (!address.isEmpty()) {
                    ops.add(insertPostalAddressForContact(address, 0));
                }

            }

            return ops;

        }
        return null;
    }

    /**
     * Social Profile
     * */
    private ContentProviderOperation insertSocialProfileForContact(SocialProfile socialProfile, int rawContactID) {

        if (socialProfile != null && socialProfile.value != null) {

            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);

            values.put(ContactsContract.CommonDataKinds.Im.DATA, socialProfile.value);

            if (socialProfile.type == null) {
                socialProfile.type = SocialProfile.SocialProfileType.TYPE_HOME;
            }
            values.put(ContactsContract.CommonDataKinds.Im.TYPE, socialProfile.type.ordinal());

            if (socialProfile.type == SocialProfile.SocialProfileType.TYPE_CUSTOM && socialProfile.localizedLabel != null) {
                values.put(ContactsContract.CommonDataKinds.Im.LABEL, socialProfile.localizedLabel);
            }

            int protocol = socialProfile.getImProtocol();
            values.put(ContactsContract.CommonDataKinds.Im.PROTOCOL, protocol);
            if (protocol == ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM && socialProfile.localizedProtocol != null) {
                values.put(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL, socialProfile.localizedProtocol);
            }

            return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Im.RAW_CONTACT_ID, rawContactID)
                    .withValues(values)
                    .build();
        }
        return null;
    }

    private ArrayList<ContentProviderOperation> insertSocialProfiles(ArrayList<SocialProfile> socialProfiles) {

        if (socialProfiles != null && socialProfiles.size() > 0) {

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            for (SocialProfile socialProfile : socialProfiles) {

                if (socialProfile.value != null) {
                    ops.add(insertSocialProfileForContact(socialProfile, 0));
                }

            }

            return ops;

        }

        return null;
    }

    /**
     * Website
     * */
    private ContentProviderOperation insertWebSiteForContact(WebSite web, int rawContactID) {

        if (web != null && web.url != null) {

            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);

            values.put(ContactsContract.CommonDataKinds.Website.URL, web.url);

            return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Website.RAW_CONTACT_ID, rawContactID)
                    .withValues(values)
                    .build();
        }
        return null;
    }

    private ArrayList<ContentProviderOperation> insertWebSites(ArrayList<WebSite> websites) {
        if (websites != null && websites.size() > 0) {

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            for (WebSite web : websites) {

                if (web.url != null) {
                    ops.add(insertWebSiteForContact(web, 0));
                }
            }

            return ops;
        }
        return null;
    }

    /**
     * Note
     * */
    private ContentProviderOperation insertNoteForContact(Note note, int rawContactID) {
        if (note != null && note.note != null) {
            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);

            values.put(ContactsContract.CommonDataKinds.Note.NOTE, note.note);

            return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Note.RAW_CONTACT_ID, rawContactID)
                    .withValues(values)
                    .build();
        }
        return null;
    }

    private ContentProviderOperation insertNote(Note note) {
        if (note != null && note.note != null) {
            return insertNoteForContact(note, 0);
        }
        return null;
    }

    /**
     * 新增联系人
     * */
    public void insertContact(ArrayList<Contact> contacts) {

        if (contacts != null && contacts.size() > 0) {

            for (Contact contact : contacts) {

                if (contact.getID() == Contact.NEW_CONTACT_ID) {

                    ArrayList<ContentProviderOperation> operations = new ArrayList<>();


                    // 添加到账号
                    operations.add(
                            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                    .build()
                    );

                    // insert name
                    ContentProviderOperation nameOp = insertName(contact.name);
                    if (nameOp != null) {
                        operations.add(nameOp);
                    }

                    // insert photo
                    ContentProviderOperation photoOperation = insertPhoto(contact.photo);
                    if (photoOperation != null) {
                        operations.add(photoOperation);
                    }

                    // insert job
                    ContentProviderOperation jobOp = insertJob(contact.organization);
                    if (jobOp != null) {
                        operations.add(jobOp);
                    }

                    // insert phone number
                    ArrayList<ContentProviderOperation> phoneOps = insertPhoneNumbers(contact.phoneNumbers);
                    if (phoneOps != null && phoneOps.size() > 0) {
                        operations.addAll(phoneOps);
                    }

                    // insert email
                    ArrayList<ContentProviderOperation> emailOps = insertEmailAddresses(contact.emailAddresses);
                    if (emailOps != null && emailOps.size() > 0) {
                        operations.addAll(emailOps);
                    }

                    // insert postal address
                    ArrayList<ContentProviderOperation> addressOps = insertPostalAddresses(contact.postalAddresses);
                    if (addressOps != null && addressOps.size() > 0) {
                        operations.addAll(addressOps);
                    }

                    // insert social profile
                    ArrayList<ContentProviderOperation> socialProfileOps = insertSocialProfiles(contact.socialProfiles);
                    if (socialProfileOps != null && socialProfileOps.size() > 0) {
                        operations.addAll(socialProfileOps);
                    }

                    // insert url
                    ArrayList<ContentProviderOperation> urlOps = insertWebSites(contact.webSites);
                    if (urlOps != null && urlOps.size() > 0) {
                        operations.addAll(urlOps);
                    }

                    // insert note
                    ContentProviderOperation noteOp = insertNote(contact.note);
                    if (noteOp != null) {
                        operations.add(noteOp);
                    }


                    try {
                        ContentProviderResult[] results = mContentResolver.applyBatch(ContactsContract.AUTHORITY, operations);
                        for (ContentProviderResult result : results) {
                            Log.i("Insert Contact", result.uri.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } // if

            }  // for
        }
    }

    /**
     * 已有联系人新增信息
     * */
    public void insertContactElements(Contact contact, ArrayList<BaseElement> elements) {

        if (contact != null && contact.getID() != null && contact.getID() != Contact.NEW_CONTACT_ID && elements != null && elements.size() > 0) {

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            for (BaseElement element : elements) {

                if (element.getId().intValue() == BaseElement.NEW_ELEMENT_ID.intValue()) {

                    ContentProviderOperation op = null;

                    if (element instanceof Name) {

                        Name name = (Name)element;
                        op = insertNameForContact(name, contact.getID());

                    } else if (element instanceof Photo) {

                        Photo photo = (Photo)element;
                        op = insertPhotoForContact(photo, contact.getID());

                    } else if (element instanceof Organization) {

                        Organization organization = (Organization)element;
                        op = insertJobForContact(organization, contact.getID());

                    } else if (element instanceof PhoneNumber) {

                        PhoneNumber phone = (PhoneNumber)element;
                        op = insertPhoneNumberForContact(phone, contact.getID());

                    } else if (element instanceof EmailAddress) {

                        EmailAddress email = (EmailAddress)element;
                        op = insertEmailAddressForContact(email, contact.getID());

                    } else if (element instanceof PostalAddress) {

                        PostalAddress postalAddress = (PostalAddress)element;
                        op = insertPostalAddressForContact(postalAddress, contact.getID());

                    } else if (element instanceof SocialProfile) {

                        SocialProfile socialProfile = (SocialProfile)element;
                        op = insertSocialProfileForContact(socialProfile, contact.getID());

                    } else if (element instanceof WebSite) {

                        WebSite web = (WebSite)element;
                        op = insertWebSiteForContact(web, contact.getID());

                    } else if (element instanceof Note) {

                        Note note = (Note)element;
                        op = insertNoteForContact(note, contact.getID());
                    }

                    if (op != null) {
                        ops.add(op);
                    }

                }// if
            } // for

            if (ops.size() > 0) {
                try {
                    ContentProviderResult[] results = mContentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    for (ContentProviderResult result : results) {
                        Log.i("Update Contact Name", result.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    // endregion

    //region Delete
    /**
     * 删除联系人
     * */
    public void deleteContact(ArrayList<Contact> contacts) {
        if (contacts != null && contacts.size() > 0) {

            ArrayList<ContentProviderOperation> operations = new ArrayList<>();
            for (Contact contact : contacts) {

                Integer ID = contact.getID();
                if (ID != Contact.NEW_CONTACT_ID) {

                    //delete contact
                    operations.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=" + ID, null).build());

                    //delete contact information such as phone number,email
                    operations.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI).withSelection(ContactsContract.Data.CONTACT_ID + "=" + ID, null).build());
                }

            }

            try {
                mContentResolver.applyBatch(ContactsContract.AUTHORITY, operations);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // endregion

    //region Update
    /**
     * 更新联系人
     *
     * 方式一：
     *  Uri uri = ContactsContract.Data.CONTENT_URI;
     *
     *  String nameSelection = ContactsContract.Data._ID + "='" + name.id + "'" + " AND " + ContactsContract.Contacts.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'";
     *
     *  int result = mContentResolver.update(uri, values, nameSelection, null);
     *
     *  方式二：
     *  ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
     *                     .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(name.getId())})
     *                     .withValues(values)
     *                     .build();
     * */

    private ContentProviderOperation updateName(Name name) {
        if (name != null && name.getId() != null && name.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

            ContentValues values = new ContentValues();

            // given name
            if (name.givenName == null) {
                name.givenName = "";
            }
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name.givenName);

            // middle name
            if (name.middleName == null) {
                name.middleName = "";
            }
            values.put(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, name.middleName);

            // family name
            if (name.familyName == null) {
                name.familyName = "";
            }
            values.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, name.familyName);

            return ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(name.getId())})
                    .withValues(values)
                    .build();


//            Uri uri = ContactsContract.Data.CONTENT_URI;
//
//            String nameSelection = ContactsContract.Data._ID + "='" + name.id + "'" + " AND " + ContactsContract.Contacts.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'";
//
//            int result = mContentResolver.update(uri, values, nameSelection, null);
//
//            Log.d("Update Contact", "updateName: " + result);
        }
        return null;
    }

    private ContentProviderOperation updatePhoto(Photo photo) {

        if (photo != null && photo.getId() != null && photo.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

            if (photo.thumbnail != null) {
                return ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(photo.getId())})
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photo.thumbnail)
                        .build();
            } else {
                return ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(photo.getId())})
                        .build();
            }

        }

        return null;
    }

    private ContentProviderOperation updateJob(Organization organization) {

        if (organization != null && organization.getId() != null && organization.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

            if (organization.isEmpty()) {

                return ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(organization.getId())})
                        .build();

            } else {
                ContentValues values = new ContentValues();

                if (organization.organizationName != null) {
                    values.put(ContactsContract.CommonDataKinds.Organization.COMPANY, organization.organizationName);
                }

                if (organization.department != null) {
                    values.put(ContactsContract.CommonDataKinds.Organization.DEPARTMENT, organization.department);
                }

                if (organization.jobTitle != null) {
                    values.put(ContactsContract.CommonDataKinds.Organization.TITLE, organization.jobTitle);
                }

                return ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(organization.getId())})
                        .withValues(values)
                        .build();
            }
        }

        return null;
    }

    private ContentProviderOperation updatePhoneNumber(PhoneNumber phone) {

        if (phone != null && phone.getId() != null && phone.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

            if (phone.value != null) {

                ContentValues values = new ContentValues();

                if (phone.type == null) {
                    phone.type = PhoneNumber.PhoneNumberType.TYPE_MAIN;
                }
                values.put(ContactsContract.CommonDataKinds.Phone.TYPE, phone.type.ordinal());

                if (phone.type == PhoneNumber.PhoneNumberType.TYPE_CUSTOM && phone.localizedLabel != null) {
                    values.put(ContactsContract.CommonDataKinds.Phone.LABEL, phone.localizedLabel);
                }

                if (phone.value != null) {
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.value);
                }

                if (phone.label != null) {
                    values.put(ContactsContract.CommonDataKinds.Phone.LABEL, phone.label);
                }

                return ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(phone.getId())})
                        .withValues(values)
                        .build();

            } else {

                return ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(phone.getId())})
                        .build();

            }
        }

        return null;
    }

    private ContentProviderOperation updateEmailAddress(EmailAddress emailAddress) {

        if (emailAddress != null && emailAddress.getId() != null && emailAddress.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

            if (emailAddress.value != null) {

                ContentValues values = new ContentValues();

                if (emailAddress.type == null) {
                    emailAddress.type = EmailAddress.EmailType.TYPE_HOME;
                }
                values.put(ContactsContract.CommonDataKinds.Email.TYPE, emailAddress.type.ordinal());

                if (emailAddress.type == EmailAddress.EmailType.TYPE_CUSTOM && emailAddress.localizedLabel != null) {
                    values.put(ContactsContract.CommonDataKinds.Email.LABEL, emailAddress.localizedLabel);
                }

                if (emailAddress.value != null) {
                    values.put(ContactsContract.CommonDataKinds.Email.ADDRESS, emailAddress.value);
                }

                if (emailAddress.label != null) {
                    values.put(ContactsContract.CommonDataKinds.Email.LABEL, emailAddress.label);
                }

                return ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(emailAddress.getId())})
                        .withValues(values)
                        .build();

            } else {

                return ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(emailAddress.getId())})
                        .build();
            }

        }

        return null;
    }

    private ContentProviderOperation updatePostalAddress(PostalAddress postalAddress) {

        if (postalAddress != null && postalAddress.getId() != null && postalAddress.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

            if (!postalAddress.isEmpty()) {

                ContentValues values = new ContentValues();

                if (postalAddress.type == null) {
                    postalAddress.type = PostalAddress.AddressType.TYPE_HOME;
                }
                values.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, postalAddress.type.ordinal());

                if (postalAddress.type == PostalAddress.AddressType.TYPE_CUSTOM && postalAddress.localizedLabel != null) {
                    values.put(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, postalAddress.localizedLabel);
                }

                if (postalAddress.country != null) {
                    values.put(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, postalAddress.country);
                }

                if (postalAddress.state != null) {
                    values.put(ContactsContract.CommonDataKinds.StructuredPostal.REGION, postalAddress.state);
                }

                if (postalAddress.city != null) {
                    values.put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, postalAddress.city);
                }

                if (postalAddress.street != null) {
                    values.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET, postalAddress.street);
                }

                if (postalAddress.postalCode != null) {
                    values.put(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, postalAddress.postalCode);
                }

                return ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(postalAddress.getId())})
                        .withValues(values)
                        .build();
            } else {

                return ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(postalAddress.getId())})
                        .build();
            }

        }
        return null;
    }

    private ContentProviderOperation updateSocialProfile(SocialProfile socialProfile) {

        if (socialProfile != null && socialProfile.getId() != null && socialProfile.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

            if (socialProfile.value != null) {

                ContentValues values = new ContentValues();

                values.put(ContactsContract.CommonDataKinds.Im.DATA, socialProfile.value);

                if (socialProfile.type == null) {
                    socialProfile.type = SocialProfile.SocialProfileType.TYPE_HOME;
                }
                values.put(ContactsContract.CommonDataKinds.Im.TYPE, socialProfile.type.ordinal());

                if (socialProfile.type == SocialProfile.SocialProfileType.TYPE_CUSTOM && socialProfile.localizedLabel != null) {
                    values.put(ContactsContract.CommonDataKinds.Im.LABEL, socialProfile.localizedLabel);
                }

                int protocol = socialProfile.getImProtocol();
                values.put(ContactsContract.CommonDataKinds.Im.PROTOCOL, protocol);
                if (protocol == ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM && socialProfile.localizedProtocol != null) {
                    values.put(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL, socialProfile.localizedProtocol);
                }

                return ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(socialProfile.getId())})
                        .withValues(values)
                        .build();
            } else {

                return ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(socialProfile.getId())})
                        .build();
            }

        }

        return null;
    }

    private ContentProviderOperation updateWebSite(WebSite website) {
        if (website != null && website.getId() != null && website.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

            if (website.url != null) {
                ContentValues values = new ContentValues();
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);

                values.put(ContactsContract.CommonDataKinds.Website.URL, website.url);

                return ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(website.getId())})
                        .withValues(values)
                        .build();
            } else {

                return ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(website.getId())})
                        .build();
            }
        }
        return null;
    }

    private ContentProviderOperation updateNote(Note note) {
        if (note != null && note.getId() != null && note.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

            if (note.note != null) {

                ContentValues values = new ContentValues();
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);

                values.put(ContactsContract.CommonDataKinds.Note.NOTE, note.note);

                return ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(note.getId())})
                        .withValues(values)
                        .build();
            } else {

                return ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(note.getId())})
                        .build();
            }

        }
        return null;
    }

    /**
     * 如果要删除，只需将其值设置为null
     * 比如：删除note， note.note = null
     * */
    public void updateContact(Contact contact) {
        if (contact != null && contact.getID() != Contact.NEW_CONTACT_ID) {

            Integer ID = getRawContactID(contact);
            if (ID.intValue() == Contact.NEW_CONTACT_ID) {
                return;
            }

            ArrayList<ContentProviderOperation> operations = new ArrayList<>();

            // update name
            if (contact.name != null) {
                ContentProviderOperation nameOp;
                if (contact.name.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {
                    nameOp = updateName(contact.name);
                } else {
                    nameOp = insertNameForContact(contact.name, contact.getID());
                }
                if (nameOp != null) {
                    operations.add(nameOp);
                }
            }

            // update photo
            if (contact.photo != null) {
                ContentProviderOperation photoOperation;
                if (contact.photo.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {
                    photoOperation = updatePhoto(contact.photo);
                } else {
                    photoOperation = insertPhotoForContact(contact.photo, contact.getID());
                }
                if (photoOperation != null) {
                    operations.add(photoOperation);
                }
            }

            // update job
            if (contact.organization != null) {

                ContentProviderOperation jobOp;

                if (contact.organization.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {
                    jobOp = updateJob(contact.organization);
                } else {
                    jobOp = insertJobForContact(contact.organization, contact.getID());
                }

                if (jobOp != null) {
                    operations.add(jobOp);
                }
            }

            // update phone number
            if (contact.phoneNumbers != null && contact.phoneNumbers.size() > 0) {
                for (PhoneNumber phone : contact.phoneNumbers) {

                    ContentProviderOperation phoneOp;

                    if (phone.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

                        phoneOp = updatePhoneNumber(phone);
                    } else {

                        phoneOp = insertPhoneNumberForContact(phone, contact.getID());
                    }

                    if (phoneOp != null) {
                        operations.add(phoneOp);
                    }
                }
            }


            // update email
            if (contact.emailAddresses != null && contact.emailAddresses.size() > 0) {
                for (EmailAddress emailAddress : contact.emailAddresses) {

                    ContentProviderOperation emailOp;
                    if (emailAddress.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

                        emailOp = updateEmailAddress(emailAddress);
                    } else {

                        emailOp = insertEmailAddressForContact(emailAddress, contact.getID());
                    }

                    if (emailOp != null) {
                        operations.add(emailOp);
                    }
                }
            }

            // update postal address
            if (contact.postalAddresses != null && contact.postalAddresses.size() > 0) {
                for (PostalAddress postalAddress : contact.postalAddresses) {

                    ContentProviderOperation postalAddressOp;
                    if (postalAddress.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

                        postalAddressOp = updatePostalAddress(postalAddress);
                    } else {
                        postalAddressOp = insertPostalAddressForContact(postalAddress, contact.getID());
                    }
                    if (postalAddressOp != null) {
                        operations.add(postalAddressOp);
                    }
                }
            }

            // update social profile
            if (contact.socialProfiles != null && contact.socialProfiles.size() > 0) {
                for (SocialProfile socialProfile : contact.socialProfiles) {

                    ContentProviderOperation socialProfileOp;
                    if (socialProfile.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

                        socialProfileOp = updateSocialProfile(socialProfile);
                    } else {
                        socialProfileOp = insertSocialProfileForContact(socialProfile, contact.getID());
                    }

                    if (socialProfileOp != null) {
                        operations.add(socialProfileOp);
                    }
                }
            }

            // update url
            if (contact.webSites != null && contact.webSites.size() > 0) {
                for (WebSite webSite : contact.webSites) {

                    ContentProviderOperation webSiteOp;
                    if (webSite.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

                        webSiteOp = updateWebSite(webSite);
                    } else {

                        webSiteOp = insertWebSiteForContact(webSite, contact.getID());
                    }

                    if (webSiteOp != null) {
                        operations.add(webSiteOp);
                    }
                }
            }

            // update note
            ContentProviderOperation noteOp;
            if (contact.note.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

                noteOp = updateNote(contact.note);
            } else {

                noteOp = insertNoteForContact(contact.note, contact.getID());
            }
            if (noteOp != null) {
                operations.add(noteOp);
            }

            try {
                ContentProviderResult[] results = mContentResolver.applyBatch(ContactsContract.AUTHORITY, operations);
                for (ContentProviderResult result : results) {
                    Log.i("Update Contact", result.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void updateContactElements(ArrayList<BaseElement> elements) {

        if (elements != null && elements.size() > 0) {

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            for (BaseElement element : elements) {

                if (element.getId().intValue() != BaseElement.NEW_ELEMENT_ID.intValue()) {

                    ContentProviderOperation op = null;

                    if (element instanceof Name) {

                        Name name = (Name)element;
                        op = updateName(name);

                    } else if (element instanceof Photo) {

                        Photo photo = (Photo)element;
                        op = updatePhoto(photo);

                    } else if (element instanceof Organization) {

                        Organization organization = (Organization)element;
                        op = updateJob(organization);

                    } else if (element instanceof PhoneNumber) {

                        PhoneNumber phone = (PhoneNumber)element;
                        op = updatePhoneNumber(phone);

                    } else if (element instanceof EmailAddress) {

                        EmailAddress email = (EmailAddress)element;
                        op = updateEmailAddress(email);

                    } else if (element instanceof PostalAddress) {

                        PostalAddress postalAddress = (PostalAddress)element;
                        op = updatePostalAddress(postalAddress);

                    } else if (element instanceof SocialProfile) {

                        SocialProfile socialProfile = (SocialProfile)element;
                        op = updateSocialProfile(socialProfile);

                    } else if (element instanceof WebSite) {

                        WebSite web = (WebSite)element;
                        op = updateWebSite(web);

                    } else if (element instanceof Note) {

                        Note note = (Note)element;
                        op = updateNote(note);
                    }

                    if (op != null) {
                        ops.add(op);
                    }

                }// if
            } // for

            if (ops.size() > 0) {
                try {
                    ContentProviderResult[] results = mContentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    for (ContentProviderResult result : results) {
                        Log.i("Update Contact Name", result.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    // endregion

}
