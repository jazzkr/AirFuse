package ca.krisztiankurucz.iotfuse.iotfusebox;

/**
 * Created by krisz on 2017-07-26.
 */

public class FuseObject {
    public int id;
    public String name;
    public String desc;
    public double current_limit;

    FuseObject(int id, String name, String desc, double current_limit) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.current_limit = current_limit;
    }
}
