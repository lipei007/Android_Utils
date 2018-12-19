package jack.com.jkutils.contactKit.element;

public class PhoneNumber extends BaseElement {


    public enum PhoneNumberType {
        TYPE_CUSTOM,
        TYPE_HOME,
        TYPE_MOBILE,
        TYPE_WORK,
        TYPE_FAX_WORK,
        TYPE_FAX_HOME,
        TYPE_PAGER,
        TYPE_OTHER,
        TYPE_CALLBACK,
        TYPE_CAR,
        TYPE_COMPANY_MAIN,
        TYPE_ISDN,
        TYPE_MAIN,
        TYPE_OTHER_FAX,
        TYPE_RADIO,
        TYPE_TELEX,
        TYPE_TTY_TDD,
        TYPE_WORK_MOBILE,
        TYPE_WORK_PAGER,
        TYPE_ASSISTANT,
        TYPE_MMS
    }

    public PhoneNumberType type;
    public String label;
    public String value;
    public String localizedLabel;

    public PhoneNumber(Integer id) {
        super(id);
    }

}
