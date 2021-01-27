/**
 * @author Jack_Ou  created on 2021/1/21.
 */
public class JvmStackOpt {

    public int work(int x) throws Exception{
        int z =(x+5)*10;
        Thread.sleep(Integer.MAX_VALUE);
        return  z;
    }
    public static void main(String[] args)throws Exception {
        JvmStackOpt jvmStack = new JvmStackOpt();
        jvmStack.work(10);
    }
}
