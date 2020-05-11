# Eager PR Updates

Starting from Bitbucket 7.0+ internal references on repositories aren't generated or updated as often as they were in previous versions.

The issue is reported here: https://jira.atlassian.com/browse/BSERV-12284

As a result, CI tools which use refs/pull-requests/*/from - the references might not be available and fail the build. This can be resolved by calling an API
or viewing the diff of a pull-request.

With this plugin, when a pull-request is opened or the source branch of the pull-request on the repository is changed then it will call the SDK 
to get the effectiveDiff of the pull-request. As a result of this, the internal ref is updated and can be used.

Tested on Bitbucket 6.8.2 && 7.1.2

## Installing

Download the latest jar file from the releases tab. 
On the Bitbucket Instance: Global Admin -> Manage Apps -> Upload App and Select the downloaded jar file.

To package the jar file yourself, 
Clone this repository, Setup the [atlassian-sdk](https://developer.atlassian.com/server/framework/atlassian-sdk/set-up-the-atlassian-plugin-sdk-and-build-a-project/) and run:
`atlas-package` to package this plugin. 

On the Bitbucket Instance: Global Admin -> Manage Apps -> Upload App and Select the packaged jar file.
