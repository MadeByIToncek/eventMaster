package space.itoncek.csyt.countdown;

public class LogicAdapter {

    public static String getDigits(int rem) {
        int min = (int) Math.floor(rem / 60f);
        int sec = rem - (min * 60);

        return String.format("%02d%02d", min, sec);
    }
}
