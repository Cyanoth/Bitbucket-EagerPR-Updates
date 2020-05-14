# Eager PR Updates

Starting from Bitbucket 7.0+ internal references on repositories aren't generated or updated as often as they weIre in previous versions.

The issue is reported here: https://jira.atlassian.com/browse/BSERV-12284

As a result, CI tools which use refs/pull-requests/*/from - the references might not be available and fail the build. This can be resolved by calling an API
or viewing the diff of a pull-request.

With this plugin, when a pull-request is opened or the source branch of the pull-request on the repository is changed then it will call the SDK.
to get the effectiveDiff of the pull-request. As a result of this, the internal ref is updated and can be used.

Tested on Bitbucket 6.8.2 & 7.1.2

**__Do the refs get updated before a build is started/triggered?__**

During the plugin development & a variety of different tests, it was not possible to reproduce a situation where the `refs/pull-requests/*/from` were still out-of-date when a build got triggered.

It also inferred from the comments on [BSERV-12284](https://jira.atlassian.com/browse/BSERV-12284) that the behaviour this plugin does is similar to that of Bitbucket 6, where a ref update would have been triggered on pull-request open or source branch change and a webhook would have fired regardless of whether the internal ref had updated or not.

We have been running this plugin on our Bitbucket 7.1 production environment which has 1000's of pull-requests triggered daily. We have yet to have a report about the from refs being out-of-date when a build has started.

In short, this plugin **does not** guarantee that the refs will be up-to-date before the build is triggered, but we've yet to have seen a case where it hasn't.

## Installing

Download the latest jar file from the releases tab. 
On the Bitbucket Instance: Global Admin -> Manage Apps -> Upload App and Select the downloaded jar file.

Alternatively, to package the plugin yourself: 
Clone this repository, Setup the [atlassian-sdk](https://developer.atlassian.com/server/framework/atlassian-sdk/set-up-the-atlassian-plugin-sdk-and-build-a-project/) and run:
`atlas-package` to package this plugin.

On the Bitbucket Instance: Global Admin -> Manage Apps -> Upload App and Select the packaged jar file.

## Troubleshooting

You can enable debug logs on this plugin by sending the following curl command:

```
curl -u BITBUCKET_ADMIN_USERNAME:BITBUCKET_ADMIN_PASSWORD -v -X PUT -H "Content-Type: application/json" https://BITBUCKET_URL/rest/api/latest/logs/logger/com.cyanoth.eagerpr/debug
```
