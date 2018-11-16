package jack.com.jkutils.contact;

import android.graphics.Bitmap;
import android.provider.ContactsContract;

import java.io.ByteArrayOutputStream;

public class Contact {

    public static final String NUMBER_TYPE_WORK = String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
    public static final String NUMBER_TYPE_FAX_WORK = String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK);
    public static final String EMAIL_TYPE_WORK = String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK);
    public static final String WEB_TYPE_HOMEPAGE = String.valueOf(ContactsContract.CommonDataKinds.Website.TYPE_HOMEPAGE);
    public static final String POSTAL_TYPE_WORK = String.valueOf(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);

    private String id;
    private String name;
    private String number;
    private String numberType;
    private String fax;
    private String faxType;
    private String email;
    private String emailType;
    private String homePage;
    private String homePageType;
    private String address;
    private String addressType;
    private Bitmap photo;


    public Contact(){
    }

    public Contact(Contact contact){
        copyContact(contact);
    }

    public void copyContact(Contact contact) {
        this.name = contact.getName();
        this.number = contact.getNumber();
        this.numberType = contact.getNumberType();
        this.email = contact.getEmail();
        this.emailType = contact.getEmailType();
        this.fax = contact.getFax();
        this.faxType = contact.getFaxType();
        this.homePage = contact.getHomePage();
        this.homePageType = contact.getHomePageType();
        this.address = contact.getAddress();
        this.addressType = contact.getAddressType();
        this.photo = contact.getPhoto();
    }

    public String getEmail() {
        return email;
    }
    public String getEmailType() {
        if (emailType == null) {
            return EMAIL_TYPE_WORK;
        }
        return emailType;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getNumber() {
        return number;
    }
    public String getNumberType() {
        if (numberType == null) {
            return NUMBER_TYPE_WORK;
        }
        return numberType;
    }
    public String getFax() {
        return fax;
    }
    public String getFaxType() {
        if (faxType == null) {
            return NUMBER_TYPE_FAX_WORK;
        }
        return faxType;
    }
    public String getAddress() {
        return address;
    }

    public String getAddressType() {
        if (addressType == null) {
            return POSTAL_TYPE_WORK;
        }
        return addressType;
    }

    public String getHomePage() {
        if (homePage == null) {
            return WEB_TYPE_HOMEPAGE;
        }
        return homePage;
    }

    public String getHomePageType() {
        return homePageType;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public byte[] getPhotoData() {
        if (photo == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
    public void setFaxType(String faxType) {
        this.faxType = faxType;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public void setHomePageType(String homePageType) {
        this.homePageType = homePageType;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
