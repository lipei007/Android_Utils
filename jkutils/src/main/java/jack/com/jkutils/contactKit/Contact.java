package jack.com.jkutils.contactKit;

import java.util.ArrayList;

public class Contact {

    public Integer ID;

    public Name                         name;
    public Photo                        photo;
    public Organization                 organization;
    public ArrayList<PhoneNumber> phoneNumbers;
    public ArrayList<EmailAddress> emailAddresses;
    public ArrayList<PostalAddress> postalAddresses;
    public ArrayList<SocialProfile> socialProfiles;
    public ArrayList<String> webSites;

    public String note;
}
