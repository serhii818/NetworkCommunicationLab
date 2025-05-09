/**
 * ASCII color codes for purpose of beauty
 */
public class ASCII {
    // Reset
    public static final String RESET = "\u001B[0m";

    public static final String BOLD = "\u001B[1m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String BLINK = "\u001B[5m";
    public static final String REVERSE = "\u001B[7m";
    public static final String HIDDEN = "\u001B[8m";
    public static final String STRIKETHROUGH = "\u001B[9m";

    // Regular Colors
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Background
    public static final String BLACK_BG = "\u001B[40m";
    public static final String RED_BG = "\u001B[41m";
    public static final String GREEN_BG = "\u001B[42m";
    public static final String YELLOW_BG = "\u001B[43m";
    public static final String BLUE_BG = "\u001B[44m";
    public static final String PURPLE_BG = "\u001B[45m";
    public static final String CYAN_BG = "\u001B[46m";
    public static final String WHITE_BG = "\u001B[47m";

    // Bright Colors
    public static final String B_BLACK = "\u001B[90m";
    public static final String B_RED = "\u001B[91m";
    public static final String B_GREEN = "\u001B[92m";
    public static final String B_YELLOW = "\u001B[93m";
    public static final String B_BLUE = "\u001B[94m";
    public static final String B_PURPLE = "\u001B[95m";
    public static final String B_CYAN = "\u001B[96m";
    public static final String B_WHITE = "\u001B[97m";

    // Bright background
    public static final String B_BLACK_BG = "\u001B[100m";
    public static final String B_RED_BG = "\u001B[101m";
    public static final String B_GREEN_BG = "\u001B[102m";
    public static final String B_YELLOW_BG = "\u001B[103m";
    public static final String B_BLUE_BG = "\u001B[104m";
    public static final String B_PURPLE_BG = "\u001B[105m";
    public static final String B_CYAN_BG = "\u001B[106m";
    public static final String B_WHITE_BG = "\u001B[107m";

    public static String color(int R, int G, int B) {
        return String.format("\u001B[38;2;%d;%d;%dm", R, G, B);
    }

    public static String bg(int R, int G, int B) {
        return String.format("\u001B[48;2;%d;%d;%dm", R, G, B);
    }
}
