package yellow.mongo.proxy.element;

import java.util.List;

public class ElementArray extends Element<Integer> {
    
    private final Integer value;

    public ElementArray(final int type, final String name, final Integer value) {
        super(type, name);
        
        this.value = value;
    }

    

    @Override
    public Integer value() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int size() {
        return super.size();
    }
}
