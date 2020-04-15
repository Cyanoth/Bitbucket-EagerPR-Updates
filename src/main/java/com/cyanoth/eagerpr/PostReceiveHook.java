package com.cyanoth.eagerpr;

import com.atlassian.bitbucket.hook.repository.PostRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PostRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookRequest;

import javax.annotation.Nonnull;

public class PostReceiveHook implements PostRepositoryHook<RepositoryHookRequest> {

    @Override
    public void postUpdate(@Nonnull PostRepositoryHookContext context,
                           @Nonnull RepositoryHookRequest hookRequest) {
        // No Behaviour
    }

}
