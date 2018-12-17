package jack.com.jkutils.contactKit;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class ContactHelper {


    private static volatile ContactHelper sharedHelper;
    private ContentResolver mContentResolver;
    private Context mContext;

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

    /**
     * 查询信息
     * */
    private ArrayList<PhoneNumber> traversePhoneNumbersByID(Integer ID) {

        Uri phoneNumberUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] phoneNumberProjections = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.LABEL
        };

        String phoneNumberSelection = ContactsContract.Data.CONTACT_ID + "='" + ID + "'";

        Cursor phoneCur = mContentResolver.query(phoneNumberUri, phoneNumberProjections, phoneNumberSelection, null, null);
        if (phoneCur != null) {

            ArrayList<PhoneNumber> phoneNumberArray = new ArrayList<>();
            while(phoneCur.moveToNext()) {

                String phoneNumber = phoneCur.getString(0);
                Integer phoneType = phoneCur.getInt(1);
                String phoneLabel = phoneCur.getString(2);

                PhoneNumber phone = new PhoneNumber();
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
                ContactsContract.CommonDataKinds.Email.LABEL
        };

        String emailSelection = ContactsContract.Data.CONTACT_ID + "='" + ID + "'";

        Cursor emailCur = mContentResolver.query(emailUri, emailProjections, emailSelection, null, null);

        if (emailCur != null) {

            ArrayList<EmailAddress> emailAddressArray = new ArrayList<>();
            while(emailCur.moveToNext()) {

                String email = emailCur.getString(0);
                Integer emailType = emailCur.getInt(1);
                String emailLabel = emailCur.getString(2);

                EmailAddress emailAddress = new EmailAddress();
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

                PostalAddress address = new PostalAddress();
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
                ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL
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

                SocialProfile socialProfile = new SocialProfile();
                socialProfile.value = socialValue;
                socialProfile.label = socialLabel;
                socialProfile.type = SocialProfile.SocialProfileType.values()[socialType];
                socialProfile.localizedLabel = ContactsContract.CommonDataKinds.Im.getTypeLabel(mContext.getResources(), socialType, socialLabel).toString();
                socialProfile.setSocialProtocol(SocialProfile.SocialProfileProtocol.values()[socialProtocol]);
                socialProfile.localizedProtocol = ContactsContract.CommonDataKinds.Im.getProtocolLabel(mContext.getResources(), socialProtocol, socialCustomProtocol).toString();

                socialProfileArray.add(socialProfile);
            }
            socialCur.close();
            return socialProfileArray;
        }

        return null;
    }

    private ArrayList<String> traverseWebSiteByID(Integer ID) {

        Uri webUri = ContactsContract.Data.CONTENT_URI;

        String[] webProjections = new String[] {ContactsContract.CommonDataKinds.Website.URL};

        String webSelection = ContactsContract.Data.CONTACT_ID + "='" + ID + "'" + " And " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE + "'";

        Cursor webCur = mContentResolver.query(webUri, webProjections, webSelection, null, null);
        if (webCur != null) {

            ArrayList<String> webArray = new ArrayList<>();
            while (webCur.moveToNext()) {
                String web = webCur.getString(0);
                webArray.add(web);
            }
            webCur.close();
            return webArray;
        }

        return null;
    }

    private String traverseNoteByID(Integer ID) {

        Uri noteUri = ContactsContract.Data.CONTENT_URI;
        String[] noteProjections = new String[] {ContactsContract.CommonDataKinds.Note.NOTE};
        String noteSelection = ContactsContract.Data.CONTACT_ID + "='" + ID + "'" + " And " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "'";

        Cursor noteCur = mContentResolver.query(noteUri, noteProjections, noteSelection, null, null);
        if (noteCur != null) {
            String note = null;
            if (noteCur.moveToFirst()) {
                note = noteCur.getString(0);
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
                ContactsContract.CommonDataKinds.Organization.TITLE
        };

        String jobSelection = ContactsContract.Data.RAW_CONTACT_ID + "='" + ID + "'" + " AND " + ContactsContract.Contacts.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "'";

        Cursor jobCur = mContentResolver.query(jobUri, jobProjections, jobSelection, null, null);

        if (jobCur != null) {

            Organization organization = null;
            if (jobCur.moveToFirst()) {

                String organizationName = jobCur.getString(0);
                String department = jobCur.getString(1);
                String jobTitle = jobCur.getString(2);

                organization = new Organization();
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
                ContactsContract.CommonDataKinds.Photo.PHOTO
        };

        String photoSelection = ContactsContract.Data.RAW_CONTACT_ID + "='" + ID + "'" + " AND " + ContactsContract.Contacts.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";

        Cursor photoCur = mContentResolver.query(photoUri, photoProjections, photoSelection, null, null);

        if (photoCur != null) {

            Photo photo = null;
            if (photoCur.moveToFirst()) {

                String photoFileID = photoCur.getString(0);
                byte[] thumbnail = photoCur.getBlob(1);

                Uri uri = Uri.withAppendedPath(ContactsContract.DisplayPhoto.CONTENT_URI, photoFileID);

                photo = new Photo();
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
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME
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

                name = new Name();
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

    private Contact searchContactByID(Integer ID) {

        Contact contact = new Contact();

        // ID
        contact.ID = ID;

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

    public void insertContact(Contact contact) {

        if (contact != null) {

        }
    }

    public void deleteContact(Contact contact) {

    }
}
