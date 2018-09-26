package mongooses.core.server.jobs.systemmetrics;

import mongooses.core.server.services.systemmetrics.SystemMetricsService;
import mongooses.core.shared.domainmodel.loader.DomainModelSnapshotLoader;
import mongooses.core.shared.entities.SystemMetricsEntity;
import webfx.framework.orm.domainmodel.DataSourceModel;
import webfx.framework.orm.entity.UpdateStore;
import webfx.platforms.core.services.appcontainer.ApplicationContainer;
import webfx.platforms.core.services.appcontainer.spi.ApplicationJob;
import webfx.platforms.core.services.log.Logger;
import webfx.platforms.core.services.scheduler.Scheduled;
import webfx.platforms.core.services.scheduler.Scheduler;
import webfx.platforms.core.services.update.UpdateArgument;
import webfx.platforms.core.services.update.UpdateService;
import webfx.platforms.core.util.async.Future;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author Bruno Salmon
 */
public final class ProvidedSystemMetricsRecorderJob implements ApplicationJob {

    private Scheduled metricsCapturePeriodicTimer;
    private Scheduled metricsCleaningPeriodicTimer;


    @Override
    public Future<Void> onStart() {
        // Stopping the activity if there is actually no metrics service registered for this platform
        if (SystemMetricsService.getProvider() == null) {
            String errorMessage = "ProvidedSystemMetricsRecorderJob will not start as no SystemMetricsServiceProvider is registered for this platform";
            Logger.log(errorMessage);
            ApplicationContainer.stoptApplicationJob(this);
            return Future.failedFuture(errorMessage);
        }

        Logger.log("Starting system metrics recorder activity...");
        DataSourceModel dataSourceModel = DomainModelSnapshotLoader.getDataSourceModel();
        // Starting a periodic timer to capture metrics every seconds and record it in the database
        metricsCapturePeriodicTimer = Scheduler.schedulePeriodic(1000, () -> {
            // Creating an update store for metrics entity
            UpdateStore store = UpdateStore.create(dataSourceModel);
            // Instantiating a new system metrics entity and asking the system metrics service to fill that entity
            SystemMetricsService.takeSystemMetricsSnapshot(store.insertEntity(SystemMetricsEntity.class));
            // Asking the update store to record this in the database
            store.executeUpdate().setHandler(asyncResult -> {
                if (asyncResult.failed())
                    Logger.log("Inserting metrics in database failed!", asyncResult.cause());
            });
        });

        // Deleting old metrics records (older than 1 day) regularly (every 12h)
        metricsCleaningPeriodicTimer = Scheduler.schedulePeriodic(12 * 3600 * 1000, () ->
            UpdateService.executeUpdate(new UpdateArgument("delete from metrics where lt_test_set_id is null and date < ?", new Object[]{Instant.now().minus(1, ChronoUnit.DAYS)}, dataSourceModel.getId())).setHandler(ar -> {
                if (ar.failed())
                    Logger.log("Deleting metrics in database failed!", ar.cause());
                else
                    Logger.log("" + ar.result().getRowCount() + " metrics records have been deleted from the database");
            }));

        return Future.succeededFuture();
    }

    @Override
    public Future<Void> onStop() {
        if (metricsCapturePeriodicTimer != null) {
            Logger.log("Stopping system metrics recorder activity...");
            metricsCapturePeriodicTimer.cancel();
            metricsCapturePeriodicTimer = null;
            metricsCleaningPeriodicTimer.cancel();
            metricsCleaningPeriodicTimer = null;
        }
        return Future.succeededFuture();
    }
}