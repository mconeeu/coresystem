package eu.mcone.coresystem.api.bukkit.overwatch.report;

import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.api.core.overwatch.report.ReportReason;
import org.bukkit.entity.Player;

public interface ReportMethod {

    Report report(ReportManager manager, Player reporter, Player reported, ReportReason reportReason);

}
