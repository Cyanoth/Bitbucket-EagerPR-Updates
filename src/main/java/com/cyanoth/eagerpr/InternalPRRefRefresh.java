package com.cyanoth.eagerpr;

import com.atlassian.bitbucket.hook.repository.RepositoryHook;
import com.atlassian.bitbucket.hook.repository.RepositoryHookService;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.scope.RepositoryScope;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.util.Operation;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atlassian.bitbucket.scm.ScmService;

@Scanned
@Component("InternalPRRefRefresh")
class InternalPRRefRefresh {
    private static final Logger log = LoggerFactory.getLogger(InternalPRRefRefresh.class);

    private final ScmService scmService;
    private final RepositoryHookService repositoryHookService;
    private final SecurityService securityService;

    @Autowired
    InternalPRRefRefresh(@ComponentImport ScmService scmService,
                                @ComponentImport final RepositoryHookService repositoryHookService,
                                @ComponentImport final SecurityService securityService) {
        this.scmService = scmService;
        this.repositoryHookService = repositoryHookService;
        this.securityService = securityService;
    }

    /**
     * Make the target repository of a pull-request update its internal refs, if hook is enabled.
     * @param pullRequest The pull-request object
     */
    void refreshInternalPrRefs(PullRequest pullRequest) {
        try {
            final long perfStartTime = log.isDebugEnabled() ? System.currentTimeMillis() : 0;

            // Only run this on repositories that have the Post-hook enabled
            if (!(isHookEnabled(pullRequest.getToRef().getRepository())))
                return;

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
     * Check whether the plugin repository post-receive hook is enabled/
     * @param repository The target repository of the pull-request
     * @return True - Hook enabled. False otherwise.
     * @throws Exception Thrown by securityService, caller must handle.
     */
    private boolean isHookEnabled(Repository repository) throws Exception {
        return this.securityService.withPermission(Permission.REPO_ADMIN, "Check Post Receive Hook State")
            .call((Operation<Boolean, Exception>) () -> {
                RepositoryHook hook = repositoryHookService.getByKey(new RepositoryScope(repository), PluginProperties.HOOK_KEY);
                return hook != null && hook.isEnabled();
            });
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
