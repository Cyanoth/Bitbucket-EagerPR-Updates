package com.cyanoth.eagerpr;

import com.atlassian.bitbucket.event.pull.PullRequestOpenedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestReopenedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestRescopedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import org.springframework.stereotype.Component;

@Scanned
@ExportAsService(PullRequestEventsListener.class)
@Component("PullRequestEventsListener")
public class  PullRequestEventsListener implements InitializingBean, DisposableBean {

    private final EventPublisher eventPublisher;
    private final InternalPRRefRefresh internalPRRefRefresh;

    @Autowired
    PullRequestEventsListener(@ComponentImport EventPublisher eventPublisher,
            InternalPRRefRefresh internalPRRefRefresh) {
        this.eventPublisher = eventPublisher;
        this.internalPRRefRefresh = internalPRRefRefresh;
    }

    @Override
    public void afterPropertiesSet() {
        eventPublisher.register(this);
    }

    @Override
    public void destroy() {
        eventPublisher.unregister(this);
    }

    @EventListener
    public void pullRequestOpenedEvent(PullRequestOpenedEvent event) {
        internalPRRefRefresh.refreshInternalPrRefs(event.getPullRequest());
    }

    @EventListener
    public void pullRequestReopenedEvent(PullRequestReopenedEvent event) {
        internalPRRefRefresh.refreshInternalPrRefs(event.getPullRequest());
    }

    @EventListener
    public void pullRequestRescopedEvent(PullRequestRescopedEvent event) {
        // Only care if the from (source) branch of the pull-request has updated since that affects refs/pull-requests/**/from
        // Changes to the to (target) branch won't affect the hash of refs/pull-requests/**/from so it would be a waste of resources doing anything.
        if (event.isFromHashUpdated())
            internalPRRefRefresh.refreshInternalPrRefs(event.getPullRequest());
    }
}
