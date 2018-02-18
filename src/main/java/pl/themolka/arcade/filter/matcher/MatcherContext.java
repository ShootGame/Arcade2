package pl.themolka.arcade.filter.matcher;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MatcherContext {
    private final Map<String, Matcher> matcherMap = new LinkedHashMap<>();

    public Matcher getMatcher(String id) {
        return this.getMatcher(id, null);
    }

    public Matcher getMatcher(String id, Matcher def) {
        return this.matcherMap.getOrDefault(id, def);
    }

    public Set<String> getMatcherIds() {
        return this.matcherMap.keySet();
    }

    public Collection<Matcher> getMatchers() {
        return this.matcherMap.values();
    }

    public boolean hasMatcher(Matcher matcher) {
        return this.matcherMap.containsValue(matcher);
    }

    public boolean hasMatcher(String id) {
        return this.matcherMap.containsKey(id);
    }

    public boolean isEmpty() {
        return this.matcherMap.isEmpty();
    }

    public boolean registerMatcher(String id, Matcher matcher) {
        return this.matcherMap.putIfAbsent(id, matcher) == null;
    }

    public boolean unregisterMatcher(String id) {
        return this.matcherMap.remove(id) != null;
    }
}
