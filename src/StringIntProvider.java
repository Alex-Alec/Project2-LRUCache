import java.util.HashMap;
import java.util.Map;

public class StringIntProvider implements DataProvider <String, Integer>{
    private Map<String, Integer> data = new HashMap<>();

    public Integer get (String key) {
        return data.get(key);
    }

    public void add (String key, Integer val) {
        data.put(key, val);
    }

    public void populate (int n) {
        for (int i = 0; i < n; i++) {
            data.put(String.valueOf(i), i);
        }
    }
}
