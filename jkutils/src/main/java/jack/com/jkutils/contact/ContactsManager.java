package jack.com.jkutils.contact;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

public class ContactsManager {

    private ContentResolver contentResolver;
    private static final String TAG = "ContactsManager";


    private static final String COLUMN_CONTACT_ID =
            ContactsContract.Data.CONTACT_ID;
    private static final String COLUMN_RAW_CONTACT_ID =
            ContactsContract.Data.RAW_CONTACT_ID;
    private static final String COLUMN_MIMETYPE =
            ContactsContract.Data.MIMETYPE;
    private static final String COLUMN_NAME =
            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME;
    private static final String COLUMN_NUMBER =
            ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String COLUMN_NUMBER_TYPE =
            ContactsContract.CommonDataKinds.Phone.TYPE;
    private static final String COLUMN_EMAIL =
            ContactsContract.CommonDataKinds.Email.DATA;
    private static final String COLUMN_EMAIL_TYPE =
            ContactsContract.CommonDataKinds.Email.TYPE;

    private static final String COLUMN_WEB =
            ContactsContract.CommonDataKinds.Website.DATA;
    private static final String COLUMN_WEB_TYPE =
            ContactsContract.CommonDataKinds.Website.TYPE;

    private static final String COLUMN_POSTAL =
            ContactsContract.CommonDataKinds.StructuredPostal.DATA;
    private static final String COLUMN_POSTAL_TYPE =
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE;

    private static final String COLUMN_PHOTO =
            ContactsContract.CommonDataKinds.Photo.PHOTO;


    private static final String MIMETYPE_STRING_NAME =
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
    private static final String MIMETYPE_STRING_PHONE =
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
    private static final String MIMETYPE_STRING_EMAIL =
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
    private static final String MIMETYPE_STRING_WEB =
            ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE;
    private static final String MIMETYPE_STRING_ADDRESS =
            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE;
    private static final String MIMETYPE_STRING_PHOTO =
            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE;

    public ContactsManager(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }


    public Contact searchContact(String name) {
        Log.w(TAG, "**search start**");
        Contact contact = new Contact();
        contact.setName(name);
        Log.d(TAG, "search name: " + contact.getName());
        String id = getContactID(contact.getName());
        contact.setId(id);

        if(id.equals("0")) {
            Log.d(TAG, contact.getName() + " not exist. exit.");
        } else {
            Log.d(TAG, "find id: " + id);
            //Fetch Phone Number
            Cursor cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{COLUMN_NUMBER, COLUMN_NUMBER_TYPE},
                    COLUMN_CONTACT_ID + "='" + id + "'", null, null);
            while(cursor.moveToNext()) {
                contact.setNumber(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)));
                contact.setNumberType(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER_TYPE)));
                Log.d(TAG, "find number: " + contact.getNumber());
                Log.d(TAG, "find numberType: " + contact.getNumberType());
            }
            //cursor.close();

            //Fetch email
            cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    new String[]{COLUMN_EMAIL, COLUMN_EMAIL_TYPE},
                    COLUMN_CONTACT_ID + "='" + id + "'", null, null);
            while(cursor.moveToNext()) {
                contact.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                contact.setEmailType(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL_TYPE)));
                Log.d(TAG, "find email: " + contact.getEmail());
                Log.d(TAG, "find emailType: " + contact.getEmailType());
            }
            cursor.close();
        }
        Log.w(TAG, "**search end**");
        return contact;
    }


    public String getContactID(String name) {
        String id = "0";

        String searchName = name;
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, searchName);

        //  Uri uri2 = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phoneNumber); 根据电话号码查找联系人

        String[] projection = new String[]{ContactsContract.Contacts._ID};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
        }
        return id;
    }
//    public String getContactID(String name) {
//        String id = "0";
//
//
////        ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE
//
//        Cursor cursor = contentResolver.query(
//                ContactsContract.Contacts.CONTENT_URI,
//                new String[]{ContactsContract.Contacts._ID},
//                ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE +
//                        "='" + name + "'", null, null);
//        if(cursor.moveToNext()) {
//            id = cursor.getString(cursor.getColumnIndex(
//                    ContactsContract.Contacts._ID));
//        }
//        return id;
//    }

    public boolean contactExist(String name) {
        if (name == null) {
            return false;
        }
        String id = getContactID(name);
        if(!id.equals("0")) {
            return true;
        } else {
            return false;
        }
    }


    public void addContact(Contact contact,boolean checkExist) {
        Log.w(TAG, "**add start**");

        if (checkExist) {
            String id = getContactID(contact.getName());
            if(!id.equals("0")) {
                Log.d(TAG, "contact already exist. exit.");
            } else if(contact.getName().trim().equals("")){
                Log.d(TAG, "contact name is empty. exit.");
            } else {
                addContact(contact);
            }
        } else {
            addContact(contact);
        }

        Log.w(TAG, "**add end**");

    }

    private void addContact(Contact contact) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_NAME)
                .withValue(COLUMN_NAME, contact.getName())
                .build());
        Log.d(TAG, "add name: " + contact.getName());

        // photo
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_PHOTO)
                .withValue(COLUMN_PHOTO, contact.getPhotoData())
                .build());

        // web
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_WEB)
                .withValue(COLUMN_WEB,contact.getHomePage())
                .withValue(COLUMN_WEB_TYPE, contact.getHomePage())
                .build());

        // postal
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_ADDRESS)
                .withValue(COLUMN_POSTAL,contact.getAddress())
                .withValue(COLUMN_POSTAL_TYPE, contact.getAddressType())
                .build());

        if(!contact.getNumber().trim().equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                    .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_PHONE)
                    .withValue(COLUMN_NUMBER, contact.getNumber())
                    .withValue(COLUMN_NUMBER_TYPE, contact.getNumberType())
                    .build());
            Log.d(TAG, "add number: " + contact.getNumber());
        }

        if(!contact.getFax().trim().equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                    .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_PHONE)
                    .withValue(COLUMN_NUMBER, contact.getFax())
                    .withValue(COLUMN_NUMBER_TYPE, contact.getFaxType())
                    .build());
            Log.d(TAG, "add fax: " + contact.getFax());
        }

        if(!contact.getEmail().trim().equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                    .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_EMAIL)
                    .withValue(COLUMN_EMAIL, contact.getEmail())
                    .withValue(COLUMN_EMAIL_TYPE, contact.getEmailType())
                    .build());
            Log.d(TAG, "add email: " + contact.getEmail());
        }

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            Log.d(TAG, "add contact success.");
        } catch (Exception e) {
            Log.d(TAG, "add contact failed.");
            Log.e(TAG, e.getMessage());
        }
    }


    public void deleteContact(Contact contact) {
        Log.w(TAG, "**delete start**");
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        String id = getContactID(contact.getName());
        //delete contact
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID+"="+id, null)
                .build());
        //delete contact information such as phone number,email
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(COLUMN_CONTACT_ID + "=" + id, null)
                .build());
        Log.d(TAG, "delete contact: " + contact.getName());

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            Log.d(TAG, "delete contact success");
        } catch (Exception e) {
            Log.d(TAG, "delete contact failed");
            Log.e(TAG, e.getMessage());
        }
        Log.w(TAG, "**delete end**");
    }


    public void updateContact(Contact contactOld, Contact contactNew) {
        Log.w(TAG, "**update start**");
        String id = getContactID(contactOld.getName());
        if(id.equals("0")) {
            Log.d(TAG, contactOld.getName()+" not exist.");
        }/* else if(contactNew.getName().trim().equals("")){
            Log.d(TAG, "contact name is empty. exit.");
        } else if(!getContactID(contactNew.getName()).equals("0")){
            Log.d(TAG, "new contact name already exist. exit.");
        }*/ else {

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            //update name
//            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
//                    .withSelection(COLUMN_CONTACT_ID + "=? AND " + COLUMN_MIMETYPE + "=?",
//                            new String[]{id, MIMETYPE_STRING_NAME})
//                    .withValue(COLUMN_NAME, contactNew.getName())
//                    .build());
            Log.d(TAG, "update name: " + contactNew.getName());

            // photo
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(COLUMN_CONTACT_ID + "=? AND " + COLUMN_MIMETYPE + "=?",
                            new String[]{id, MIMETYPE_STRING_PHOTO})
                    .withValue(COLUMN_PHOTO, contactNew.getPhotoData())
                    .build());

            // web
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(COLUMN_CONTACT_ID + "=? AND " + COLUMN_MIMETYPE + "=?",
                            new String[]{id, MIMETYPE_STRING_WEB})
                    .withValue(COLUMN_WEB,contactNew.getHomePage())
                    .withValue(COLUMN_WEB_TYPE, contactNew.getHomePage())
                    .build());

            // postal
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(COLUMN_CONTACT_ID + "=? AND " + COLUMN_MIMETYPE + "=?",
                            new String[]{id, MIMETYPE_STRING_ADDRESS})
                    .withValue(COLUMN_POSTAL,contactNew.getAddress())
                    .withValue(COLUMN_POSTAL_TYPE, contactNew.getAddressType())
                    .build());

            //update number
            if(!contactNew.getNumber().trim().equals("")) {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(COLUMN_CONTACT_ID + "=? AND " + COLUMN_MIMETYPE + "=? AND " + COLUMN_NUMBER_TYPE + "=?",
                                new String[]{id, MIMETYPE_STRING_PHONE,contactNew.getNumberType()})
                        .withValue(COLUMN_NUMBER, contactNew.getNumber())
                        .withValue(COLUMN_NUMBER_TYPE, contactNew.getNumberType())
                        .build());
                Log.d(TAG, "update number: " + contactNew.getNumber());
            }

            // fax
            if(!contactNew.getNumber().trim().equals("")) {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(COLUMN_CONTACT_ID + "=? AND " + COLUMN_MIMETYPE + "=? AND " + COLUMN_NUMBER_TYPE + "=?",
                                new String[]{id, MIMETYPE_STRING_PHONE,contactNew.getFaxType()})
                        .withValue(COLUMN_NUMBER, contactNew.getFax())
                        .withValue(COLUMN_NUMBER_TYPE, contactNew.getFaxType())
                        .build());
                Log.d(TAG, "update fax: " + contactNew.getNumber());
            }


            //update email if mail
            if(!contactNew.getEmail().trim().equals("")) {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(COLUMN_CONTACT_ID + "=? AND " + COLUMN_MIMETYPE + "=?",
                                new String[]{id, MIMETYPE_STRING_EMAIL})
                        .withValue(COLUMN_EMAIL, contactNew.getEmail())
                        .withValue(COLUMN_EMAIL_TYPE, contactNew.getEmailType())
                        .build());
                Log.d(TAG, "update email: " + contactNew.getEmail());
            }

            try {
                contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                Log.d(TAG, "update success");
            } catch (Exception e) {
                Log.d(TAG, "update failed");
                Log.e(TAG, e.getMessage());
            }
        }
        Log.w(TAG, "**update end**");
    }


    public Uri getContactUri(String contactId, Context context)  {

        if (context == null || contactId == null) {
            return null;
        }

        //获取联系人信息的Uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //获取ContentResolver
        ContentResolver contentResolver = context.getContentResolver();
        //查询数据，返回Cursor
        Cursor cursor = contentResolver.query(uri, null, null, null, null);


        while (cursor.moveToNext()) {

            //获取联系人的ID
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人的姓名
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

            if (id != null && id.equals(contactId)) {

                return ContactsContract.Contacts.getLookupUri(Integer.valueOf(contactId), lookupKey);

            }
        }
        return null;
    }

    /**
     * @brief 显示联系人编辑界面
     * */
    public void editContact(String contactId, Context context) {

        if (context == null || contactId == null) {
            return ;
        }

        Uri uri = getContactUri(contactId,context);
        if (uri != null) {

            Intent editIntent = new Intent(Intent.ACTION_EDIT);
            editIntent.setDataAndType(uri,ContactsContract.Contacts.CONTENT_ITEM_TYPE);
//            editIntent.putExtra("finishActivityOnSaveCompleted", true);
            context.startActivity(editIntent);

        } else {

        }
    }

    /**
     * @brief 新建联系人显示编辑界面
     * */
    public void insertContact(Contact contact,Context context) {

        if (context == null) {
            return;
        }

        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, contact.getName());

        intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getNumber());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);

        intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, contact.getFax());
        intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK);

        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contact.getEmail());
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);

        intent.putExtra(ContactsContract.Intents.Insert.POSTAL, contact.getAddress());
        intent.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);

        // Insert中没有的类型，通过Data写入
        ArrayList<ContentValues> data = new ArrayList<ContentValues>();

        ContentValues row1 = new ContentValues();
        row1.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
        row1.put(ContactsContract.CommonDataKinds.Website.DATA, contact.getHomePage());
        data.add(row1);

        ContentValues row2 = new ContentValues();
        row2.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
        row2.put(ContactsContract.CommonDataKinds.Photo.PHOTO, contact.getPhotoData());
        data.add(row2);

//		Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);

        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);

        context.startActivity(intent);

    }
}

