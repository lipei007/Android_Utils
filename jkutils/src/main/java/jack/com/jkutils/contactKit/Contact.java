package jack.com.jkutils.contactKit;

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

public class Contact {

    public static final Integer NEW_CONTACT_ID = -1;

    private Integer ID;

    public Name name;
    public Photo photo;
    public Organization organization;
    public ArrayList<PhoneNumber>       phoneNumbers;
    public ArrayList<EmailAddress>      emailAddresses;
    public ArrayList<PostalAddress>     postalAddresses;
    public ArrayList<SocialProfile>     socialProfiles;
    public ArrayList<WebSite>           webSites;
    public Note note;

    public Contact() {
        setID(NEW_CONTACT_ID);
    }

    public Contact(Integer ID) {
        setID(ID);
    }

    private void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getID() {
        return ID;
    }

}
