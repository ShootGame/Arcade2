package pl.themolka.arcade.capture.flag;

import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.region.IRegionFieldStrategy;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionFieldStrategy;

public class FlagCapture {
    public static final IRegionFieldStrategy DEFAULT_FIELD_STRATEGY = RegionFieldStrategy.EXACT;
    public static final Filter DEFAULT_FILTER = Filters.undefined();

    private final CaptureGame game;
    private final Flag flag;

    private IRegionFieldStrategy fieldStrategy = DEFAULT_FIELD_STRATEGY;
    private Filter filter = DEFAULT_FILTER;
    private Region region;

    public FlagCapture(CaptureGame game, Flag flag) {
        this.game = game;
        this.flag = flag;
    }

    public boolean canCapture() {
        return this.filter.filter(this.flag).isNotFalse();
    }

    public IRegionFieldStrategy getFieldStrategy() {
        return this.fieldStrategy;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public Flag getFlag() {
        return this.flag;
    }

    public Region getRegion() {
        return this.region;
    }

    public void setFieldStrategy(IRegionFieldStrategy fieldStrategy) {
        this.fieldStrategy = fieldStrategy;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
