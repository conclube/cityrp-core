package me.conclure.cityrp.common.sender;

import me.conclure.cityrp.common.utility.Delegable;
import net.kyori.adventure.bossbar.BossBar;

public interface BossBarSender {
    void showBossBar(BossBar bossBar);

    void hideBossBar(BossBar bossBar);
}
