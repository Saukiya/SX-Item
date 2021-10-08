package github.saukiya.sxitem.nms;

public abstract class Test extends NMS {

    public abstract void A();

    public static Test getInst() {
        return NMS.getInst(Test.class);
    }
}
