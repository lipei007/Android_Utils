package jack.com.jkutils.contactKit;

public class PostalAddress {

    public enum AddressType {
        TYPE_CUSTOM,
        TYPE_HOME,
        TYPE_WORK,
        TYPE_OTHER
    }

    public String country, state, city, street, postalCode;
    public String formatAddress;
    public AddressType type;
    public String label;
    public String localizedLabel;

}
