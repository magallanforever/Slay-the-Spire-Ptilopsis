package Ptilopsis.ui;

import com.megacrit.cardcrawl.core.Settings;

public final class ThreadUiText {
    private ThreadUiText() {
    }

    public static String selectCardPrompt() {
        return isChinese() ? "选择 1 张牌作为线程函数（不会消耗原牌）" : "Choose 1 card as this Thread's function. The original card is not consumed.";
    }

    public static String emptyMutableTitle() {
        return isChinese() ? "空可变线程" : "Empty Mutable Thread";
    }

    public static String emptyMutableBody() {
        return isChinese() ? "点击后选择 1 张手牌作为函数挂载。" : "Click to mount a card from your hand as this Thread's function.";
    }

    public static String mutableTitle() {
        return isChinese() ? "可变线程" : "Mutable Thread";
    }

    public static String fixedTitle() {
        return isChinese() ? "固定线程" : "Fixed Thread";
    }

    public static String functionBody(String name) {
        return isChinese() ? "函数：" + name : "Function: " + name;
    }

    public static String fixedBody(String name) {
        return isChinese() ? "固定函数：" + name : "Fixed function: " + name;
    }

    public static String remove() {
        return isChinese() ? "去除函数" : "Remove";
    }

    public static String close() {
        return isChinese() ? "关闭" : "Close";
    }

    public static String mutableMarker() {
        return isChinese() ? "变" : "M";
    }

    public static String fixedMarker() {
        return isChinese() ? "固" : "F";
    }

    public static String emptyMarker() {
        return "+";
    }

    private static boolean isChinese() {
        return Settings.language == Settings.GameLanguage.ZHS;
    }
}
