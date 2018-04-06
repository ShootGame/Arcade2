package pl.themolka.arcade.destroy;

import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;
import pl.themolka.arcade.team.TeamsModule;

@Deprecated
@ModuleInfo(id = "Destroy",
        dependency = {
                MatchModule.class},
        loadBefore = {
                FiltersModule.class,
                TeamsModule.class})
@ModuleVersion("1.0")
public class DestroyModule extends Module<DestroyGame> {
}
