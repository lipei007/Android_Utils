package jack.com.jkutils.contactKit;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;

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

public class ContactUIHelper {

    // region Content Values

    /**
     * name
     * */
    private static ContentValues valuesForName(Name name) {

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

            return values;
        }
        return null;
    }

    /**
     * Photo
     * */
    private static ContentValues valuesForPhoto(Photo photo) {
        if (photo != null && photo.thumbnail != null) {

            ContentValues values = new ContentValues();

            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, photo.thumbnail);

            return values;
        }

        return null;
    }

    /**
     * Job
     * */

    private static ContentValues valuesForJob(Organization organization) {
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

            return values;
        }

        return null;
    }


    /**
     * Phone
     * */
    private static ContentValues valuesForPhoneNumber(PhoneNumber phone) {

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

            return values;
        }

        return null;
    }

    /**
     * Email
     * */
    private static ContentValues valuesForEmailAddress(EmailAddress email) {

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

            return values;
        }
        return null;
    }

    /**
     * Postal Address
     * */
    private static ContentValues valuesForPostalAddress(PostalAddress address) {

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

            return values;
        }

        return null;
    }

    /**
     * Social Profile
     * */
    private static ContentValues valuesForSocialProfile(SocialProfile socialProfile) {

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

            return values;
        }
        return null;
    }

    /**
     * Website
     * */
    private static ContentValues valuesForWebsite(WebSite web) {

        if (web != null && web.url != null) {

            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);

            values.put(ContactsContract.CommonDataKinds.Website.URL, web.url);

            return values;
        }
        return null;
    }

    /**
     * Note
     * */
    private static ContentValues valuesForNote(Note note) {
        if (note != null && note.note != null) {
            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);

            values.put(ContactsContract.CommonDataKinds.Note.NOTE, note.note);

            return values;
        }
        return null;
    }

    // endregion

    // region New Contact
    /**
     * 新建联系人
     * */
    public static void startActivityForNewContact(Activity context, int requestCode) {

        if (context != null) {
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            context.startActivityForResult(intent, requestCode);
        }
    }

    public static void startActivityForNewContact(Activity context, Contact contact, int requestCode) {

        if (context != null) {

            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

            if (contact != null) {

                // info
                if (contact.name != null) {
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, contact.name.fullName());
                }
//
//            intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getNumber());
//            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
//
//            intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, contact.getFax());
//            intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK);
//
//            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contact.getEmail());
//            intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
//
//            intent.putExtra(ContactsContract.Intents.Insert.POSTAL, contact.getAddress());
//            intent.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);

                // Insert中没有的类型，通过Data写入
                ArrayList<ContentValues> data = new ArrayList<>();

                // 不起作用
//                ContentValues nameValues = valuesForName(contact.name);
//                if (nameValues != null) {
//                    data.add(nameValues);
//                }

                ContentValues photoValues = valuesForPhoto(contact.photo);
                if (photoValues != null) {
                    data.add(photoValues);
                }

                ContentValues jobValues = valuesForJob(contact.organization);
                if (jobValues != null) {
                    data.add(jobValues);
                }

                if (contact.phoneNumbers != null && contact.phoneNumbers.size() > 0) {
                    for (PhoneNumber phoneNumber : contact.phoneNumbers) {

                        ContentValues phoneValues = valuesForPhoneNumber(phoneNumber);
                        if (phoneValues != null) {
                            data.add(phoneValues);
                        }

                    }
                }

                if (contact.emailAddresses != null && contact.emailAddresses.size() > 0) {

                    for (EmailAddress email : contact.emailAddresses) {

                        ContentValues emailValues = valuesForEmailAddress(email);
                        if (emailValues != null) {
                            data.add(emailValues);
                        }
                    }
                }

                if (contact.postalAddresses != null && contact.postalAddresses.size() > 0) {

                    for (PostalAddress address : contact.postalAddresses) {

                        ContentValues addressValues = valuesForPostalAddress(address);
                        if (addressValues != null) {
                            data.add(addressValues);
                        }
                    }
                }

                if (contact.socialProfiles != null && contact.socialProfiles.size() > 0) {

                    for (SocialProfile socialProfile : contact.socialProfiles) {

                        ContentValues socialProfileValues = valuesForSocialProfile(socialProfile);
                        if (socialProfileValues != null) {
                            data.add(socialProfileValues);
                        }
                    }
                }

                if (contact.webSites != null && contact.webSites.size() > 0) {

                    for (WebSite webSite : contact.webSites) {

                        ContentValues webValues = valuesForWebsite(webSite);
                        if (webValues != null) {
                            data.add(webValues);
                        }
                    }
                }

                if (contact.note != null) {
                    ContentValues noteValues = valuesForNote(contact.note);
                    if (noteValues != null) {
                        data.add(noteValues);
                    }
                }

                intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);

            } // if contact != null

            context.startActivityForResult(intent, requestCode);
        }

    }

    // endregion


    // region Edit Contact
    /**
     * 编辑联系人
     * */
    public static void startActivityForEditContact(Context context, Contact contact) {

        if (contact != null) {

            Uri uri = ContactHelper.sharedHelper(context).getUriOfContact(contact);
            if (uri != null) {
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                editIntent.setDataAndType(uri,ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                context.startActivity(editIntent);
            }
        }
    }

    // endregion


    // region Contact Picker
    /**
     * 选择联系人
     * */
    public static void startContactPickerActivity(Activity activity, int requestCode) {

        if (activity != null) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(ContactsContract.Contacts.CONTENT_URI);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static Contact pickContactForIntent(Context context,Intent data) {
        if (data != null && context != null) {
            return ContactHelper.sharedHelper(context).searchContactByUri(data.getData());
        }
        return null;
    }
    // endregion
}