package jack.com.jkutils.contactKit.element;

public class Organization extends BaseElement {

    public String organizationName, department, jobTitle;

    public boolean isEmpty() {
        return organizationName == null && department == null && jobTitle == null;
    }

    public Organization(Integer id) {
        super(id);
    }
}
