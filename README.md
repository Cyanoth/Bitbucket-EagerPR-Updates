# Eager PR Updates

Starting from Bitbucket 7.0+ internal references on repositories aren't generated or updated as often as they were in previous versions.

The issue is reported here: https://jira.atlassian.com/browse/BSERV-12284

As a result, CI tools which use refs/pull-requests/*/from - the references might not be available and fail the build. This can be resolved by calling an API
or viewing the diff of a pull-request.

Instead, this plugin adds a PostReceiveHook at a project or repository scope. If enabled, when a pull-request is opened or the target branch of the pull-request
on the repository is changed then it will call the SDK to get the effectiveDiff of the pull-request. As a result of this, the internal ref is updated and can be used.

Tested on Bitbucket 7.1.1

`atlas-package` to package this plugin, then install it on Bitbucket Server through the UPM.