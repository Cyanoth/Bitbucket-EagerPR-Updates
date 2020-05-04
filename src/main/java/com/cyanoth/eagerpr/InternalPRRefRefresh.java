package com.cyanoth.eagerpr;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.scm.ScmService;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Scanned
@Component("InternalPRRefRefresh")
class InternalPRRefRefresh {
    private static final Logger log = LoggerFactory.getLogger(InternalPRRefRefresh.class);

    private final ScmService scmService;

    @Autowired
    InternalPRRefRefresh(@ComponentImport ScmService scmService) {
        this.scmService = scmService;
    }

    /**
     * Make the target repository of a pull-request update its internal refs, if hook is enabled.
     * @param pullRequest The pull-request object
     */
    void refreshInternalPrRefs(PullRequest pullRequest) {
        try {
            final long perfStartTime = log.isDebugEnabled() ? System.currentTimeMillis() : 0;

            triggerInternalRefUpdate(pullRequest);

            final long perfEndtime = log.isDebugEnabled() ? System.currentTimeMillis() : 0;

            log.debug("EagerPr: RefreshInternalPRRefresh triggered internal ref update for {} it took: {}",
                    getPullRequestString(pullRequest),
                    (perfEndtime - perfStartTime) + "ms");

        }
        catch (Exception e) { // Catch all here so we don't bubble-up to the event-handler
            log.error("EagerPr: RefreshInternalPRRefresh has failed", e) ;
        }
    }

    /**
     * Make Bitbucket update the internal pull-request refs
     * @param pullRequest  The pull-request object
     */
    private void triggerInternalRefUpdate(PullRequest pullRequest) {
        // Retrieving the effective diff forces the pull request refs to be updated (Source, same call from Bitbucket 7.1.1 Sourcecode)
        scmService.getPullRequestCommandFactory(pullRequest).effectiveDiff().call();
    }

    /**
     * @param pullRequest The pull-request object
     * @return  Friendly string with information about the pull-request
     */
    private String getPullRequestString(PullRequest pullRequest) {
        return String.format("pull-request id: %d repository: %s/%s",
                pullRequest.getId(),
                pullRequest.getToRef().getRepository().getProject().getKey(),
                pullRequest.getToRef().getRepository().getSlug());
    }
}
