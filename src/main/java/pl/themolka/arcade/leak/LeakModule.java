package pl.themolka.arcade.leak;

import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;
import pl.themolka.arcade.team.TeamsModule;

@Deprecated
@ModuleInfo(id = "Leak",
        dependency = {
                MatchModule.class},
        loadBefore = {
                TeamsModule.class,
                FiltersModule.class})
@ModuleVersion("1.0")
public class LeakModule extends Module<LeakGame> {
}
