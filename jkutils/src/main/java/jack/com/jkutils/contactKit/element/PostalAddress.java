package jack.com.jkutils.contactKit.element;

public class PostalAddress extends BaseElement {

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

    public boolean isEmpty() {
        return country == null && state == null && city == null && street == null && postalCode == null;
    }

    public PostalAddress(Integer id) {
        super(id);
    }
}
