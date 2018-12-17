package jack.com.jkutils.contactKit;

public class EmailAddress {

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
}
