package space.itoncek.csyt.countdown;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountdownTest {
    @org.junit.jupiter.api.Test
    void getDigits() {
        HashMap<Integer, String> test = new HashMap<>();

        test.put(0, "00:00");
        test.put(10, "00:10");
        test.put(45, "00:45");
        test.put(60, "01:00");
        test.put(70, "01:10");
        test.put(105, "01:45");
        test.put(600, "10:00");
        test.put(610, "10:10");
        test.put(645, "10:45");
        test.put(660, "11:00");
        test.put(670, "11:10");
        test.put(705, "11:45");

        for (Map.Entry<Integer, String> entry : test.entrySet()) {
            String s = LogicAdapter.getDigits(entry.getKey());
            assertEquals(s, entry.getValue());
        }
    }
}