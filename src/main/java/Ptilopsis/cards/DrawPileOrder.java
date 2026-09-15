package Ptilopsis.cards;

import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

final class DrawPileOrder {
    private DrawPileOrder() {
    }

    static <T> int sortAndCountInversions(List<T> bottomToTop, ToIntFunction<T> cost) {
        Comparator<T> ascending = Comparator.comparingInt(cost);
        int inversions = 0;
        // CardGroup draws from the last element, so traversal and storage order are reversed.
        for (int top = bottomToTop.size() - 1; top >= 0; top--) {
            for (int below = top - 1; below >= 0; below--) {
                if (ascending.compare(bottomToTop.get(top), bottomToTop.get(below)) > 0) {
                    inversions++;
                }
            }
        }
        bottomToTop.sort(ascending.reversed());
        return inversions;
    }
}
