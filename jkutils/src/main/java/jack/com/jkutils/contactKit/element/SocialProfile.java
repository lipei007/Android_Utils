package jack.com.jkutils.contactKit.element;

public class SocialProfile extends BaseElement {

    public enum SocialProfileType {
        TYPE_CUSTOM,
        TYPE_HOME,
        TYPE_WORK,
        TYPE_OTHER
    }

    public enum SocialProfileProtocol {
        PROTOCOL_CUSTOM,
        PROTOCOL_AIM,
        PROTOCOL_MSN,
        PROTOCOL_YAHOO,
        PROTOCOL_SKYPE,
        PROTOCOL_QQ,
        PROTOCOL_GOOGLE_TALK,
        PROTOCOL_ICQ,
        PROTOCOL_JABBER,
        PROTOCOL_NETMEETING
    }

    private int imProtocol;

    public String value;
    public SocialProfileType type;
    public String localizedProtocol;
    public String label;
    public String localizedLabel;

    public void setSocialProtocol(SocialProfileProtocol protocol) {
        imProtocol = protocol.ordinal() - 1;
    }

    public SocialProfileProtocol getSocialProtocol() {
        return SocialProfileProtocol.values()[imProtocol + 1];
    }

    public int getImProtocol() {
        return imProtocol;
    }

    public SocialProfile(Integer id) {
        super(id);
    }
}
