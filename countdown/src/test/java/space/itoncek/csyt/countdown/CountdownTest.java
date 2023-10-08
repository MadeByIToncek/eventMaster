package space.itoncek.csyt.countdown;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountdownTest {
    @org.junit.jupiter.api.Test
    void getDigits() {
        HashMap<Integer, String> test = new HashMap<>();

        test.put(0, "0000");
        test.put(10, "0010");
        test.put(45, "0045");
        test.put(60, "0100");
        test.put(70, "0110");
        test.put(105, "0145");
        test.put(600, "1000");
        test.put(610, "1010");
        test.put(645, "1045");
        test.put(660, "1100");
        test.put(670, "1110");
        test.put(705, "1145");

        for (Map.Entry<Integer, String> entry : test.entrySet()) {
            String s = LogicAdapter.getDigits(entry.getKey());
            assertEquals(s, entry.getValue());
        }
    }
}