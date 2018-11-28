package pl.themolka.arcade.dominator;

import java.util.Map;

/**
 * Only one object dominates and when it has more power than all other together.
 */
public class Majority<T> extends AbstractDominator<T> {
    private final Exclusive<T> exclusive = new Exclusive<>();
    private final Lead<T> lead = new Lead<>();

    @Override
    public Map<T, Integer> getDominators(Map<T, Integer> input) {
        Map.Entry<T, Integer> dominator = this.findSingleLeader(input);
        if (dominator == null) {
            return this.empty();
        }

        T leader = dominator.getKey();
        int power = dominator.getValue();

        for (Map.Entry<T, Integer> original : input.entrySet()) {
            if (!original.getKey().equals(leader)) { // Don't iterate the current leader
                power -= original.getValue();

                if (power <= 0) {
                    break;
                }
            }
        }

        return power > 0 ? this.singleton(dominator) : this.empty();
    }

    private Map.Entry<T, Integer> findSingleLeader(Map<T, Integer> original) {
        original = this.lead.getDominators(original);

        if (original != null) {
            original = this.exclusive.getDominators(original);

            if (original != null) {
                for (Map.Entry<T, Integer> entry : original.entrySet()) {
                    return entry;
                }
            }
        }

        return null;
    }
}
