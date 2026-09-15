package Ptilopsis.cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class DrawPileOrderTest {
    public static void main(String[] args) {
        assertSort(new int[]{}, 0, new int[]{});
        assertSort(new int[]{1}, 0, new int[]{1});
        assertSort(new int[]{0, 1, 2}, 0, new int[]{0, 1, 2});
        assertSort(new int[]{2, 1, 0}, 3, new int[]{0, 1, 2});
        assertSort(new int[]{2, 0, 1, 1}, 3, new int[]{0, 1, 1, 2});
        assertSort(new int[]{1, 1, 1}, 0, new int[]{1, 1, 1});
        assertSort(new int[]{Integer.MAX_VALUE, 2, 0}, 3, new int[]{0, 2, Integer.MAX_VALUE});

        List<String> topFirst = Arrays.asList("1-first", "0-first", "1-second", "0-second");
        List<String> stored = new ArrayList<>(topFirst);
        Collections.reverse(stored);
        int inversions = DrawPileOrder.sortAndCountInversions(stored, value -> value.charAt(0) - '0');
        check(inversions == 3, "equal-cost cards should not add inversions");
        List<String> drawn = new ArrayList<>();
        while (!stored.isEmpty()) {
            drawn.add(stored.remove(stored.size() - 1));
        }
        check(drawn.equals(Arrays.asList("0-first", "0-second", "1-first", "1-second")),
                "equal-cost cards must preserve their original draw order");
        System.out.println("draw_pile_order_ok");
    }

    private static void assertSort(int[] topFirst, int expectedInversions, int[] expectedDrawOrder) {
        List<Integer> stored = new ArrayList<>();
        for (int i = topFirst.length - 1; i >= 0; i--) {
            stored.add(topFirst[i]);
        }
        int inversions = DrawPileOrder.sortAndCountInversions(stored, Integer::intValue);
        check(inversions == expectedInversions, "wrong inversion count for " + Arrays.toString(topFirst));
        for (int expected : expectedDrawOrder) {
            int next = stored.remove(stored.size() - 1);
            check(next == expected, "draw pile returned the wrong next card");
        }
        check(stored.isEmpty(), "sorting lost or duplicated cards");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
