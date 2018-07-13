import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 22:26
 */
public class MyTest {

    @Test
    public void tListAndMap() {
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        map.put("m1", null);
        ArrayList<String> list = new ArrayList<String>();
        List<String> m1 = map.get("m1");
        if (m1 == null || m1.size() == 0) {
            map.put("m1", list);
        }
        list.add("a");
        list.add("b");
        List<String> m11 = map.get("m1");
        for (String s : m11) {
            System.out.println(s);
        }
    }

    @Test
    public void tContain() {
        String a = "aaa";
        String b = "aaaw";
        boolean contains = a.contains(b);
        boolean contains1 = b.contains(a);
        System.out.println(contains);
        System.out.println(contains1);

    }
}
