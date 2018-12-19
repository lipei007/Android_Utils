package jack.com.jkutils.contactKit.element;

public class BaseElement {

    public static final Integer NEW_ELEMENT_ID = 0;

    private Integer id;

    /**
     * 如果是新建的元素，那么ID应当传入 NEW_ELEMENT_ID
     * */
    public BaseElement(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
