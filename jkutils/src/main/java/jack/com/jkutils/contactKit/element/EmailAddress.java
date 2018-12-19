package jack.com.jkutils.contactKit.element;

public class EmailAddress extends BaseElement {

    public enum EmailType {
        TYPE_CUSTOM,
        TYPE_HOME,
        TYPE_WORK,
        TYPE_OTHER,
        TYPE_MOBILE
    }

    public String value;
    public EmailType type;
    public String label;
    public String localizedLabel;

    public EmailAddress(Integer id) {
        super(id);
    }
}
