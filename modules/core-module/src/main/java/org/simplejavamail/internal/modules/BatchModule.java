package org.simplejavamail.internal.modules;

import org.simplejavamail.api.internal.batchsupport.LifecycleDelegatingTransport;
import org.simplejavamail.api.mailer.AsyncResponse;
import org.simplejavamail.api.mailer.config.OperationalConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * This interface only serves to hide the Batch implementation behind an easy-to-load-with-reflection class.
 */
public interface BatchModule {

	String NAME = "Advanced batch processing module";

	/**
	 * Executes using a single-execution ExecutorService, which shutdown immediately after the thread finishes.
	 *
	 * @see java.util.concurrent.Executors#newSingleThreadExecutor()
	 */
	@Nonnull
	AsyncResponse executeAsync(@Nonnull String processName, @Nonnull Runnable operation);

	/**
	 * Executes using a the given ExecutorService, which is left running after the thread finishes running.
	 */
	@Nonnull
	AsyncResponse executeAsync(@Nonnull ExecutorService executorService, @Nonnull String processName, @Nonnull Runnable operation);

	/**
	 * @return A NonJvmBlockingThreadPoolExecutor instance that by default doesn't block the JVM from exiting
	 * and produces properly named thread.
	 */
	@Nonnull
	ExecutorService createDefaultExecutorService(final int threadPoolSize, final int keepAliveTime);

	/**
	 * Initializes the connection pool cluster if not initialized yet.
	 * <p>
	 * Creates connection pool for the cluster key and session combination if it doesn't exist yet.
	 */
	void registerToCluster(@Nonnull OperationalConfig operationalConfig, @Nonnull final UUID clusterKey, @Nonnull Session session);

	/**
	 * @param stickySession Indicates whether transport should be from this specific Session, or any session instance from the cluster. Useful when testing connections.
	 *
	 * @return A (new) {@link Transport} for the given session from the SMTP connection pool.
	 */
	@Nonnull
	LifecycleDelegatingTransport acquireTransport(@Nonnull UUID clusterKey, @Nonnull Session session, boolean stickySession);

	/**
	 * Shuts down connection pool(s) and closes remaining open connections. Waits until all connections still in use become available again to deallocate them as well.
	 */
	@Nonnull
	Future<?> shutdownConnectionPools(@Nonnull Session session);
}