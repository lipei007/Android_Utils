package jack.com.jkutils.contactKit.element;

public class Name extends BaseElement {

    public String givenName, middleName, familyName, displayName;

    public boolean isEmpty() {
        return givenName == null && middleName == null && familyName == null;
    }

    public Name(Integer id) {
        super(id);
    }

    public String fullName() {

        StringBuffer stringBuffer = new StringBuffer();
        if (givenName != null) {
            stringBuffer.append(givenName);
        }

        if (middleName != null) {
            stringBuffer.append(" " + middleName);
        }

        if (familyName != null) {
            stringBuffer.append(" " + familyName);
        }

        return stringBuffer.toString();
    }
}
