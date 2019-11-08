package com.atlassian.tutorial.myPlugin.impl;



import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;
import io.atlassian.util.concurrent.Promise;
import org.joda.time.DateTime;
import com.atlassian.jira.component.ComponentAccessor;


import java.net.URI;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JiraImpl {

    private static final String JIRA_URL = "https://enetel.atlassian.net";
    private static final String JIRA_ADMIN_USERNAME = "eienetel@enetelsolutions.com";
    private static final String JIRA_ADMIN_PASSWORD = "ONHH9OV7gujvpMVd5BDfF651";


    public ClientResponse getClientResponse() {
        ClientResponse response;
        String auth = new String(Base64.encode(JIRA_ADMIN_USERNAME + ":" + JIRA_ADMIN_PASSWORD));
        final String headerAuthorization = "Authorization";
        final String headerAuthorizationValue = "Basic " + auth;
        final String headerType = "application/json";
        Client client = Client.create();

        WebResource webResource = client.resource(JIRA_URL);
        response =  webResource.header(headerAuthorization, headerAuthorizationValue).type(headerType).accept(headerType).get(ClientResponse.class);

        return response;
    }

    public List<Issue> getAllIssues(String input) throws URISyntaxException {
        URI serverUri = new URI(JIRA_URL);
//        BasicHttpAuthenticationHandler authenticationHandler = new BasicHttpAuthenticationHandler(JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);
//        HttpClient httpClient = new AsynchronousHttpClientFactory().createClient(serverUri, authenticationHandler);

        final JiraRestClient jiraRestClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(serverUri, JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);

        Promise<SearchResult> searchJqlPromiseTest = jiraRestClient.getSearchClient().searchJql("project = EISD", 10000, 0, null);
        int totalIssueNo = searchJqlPromiseTest.claim().getTotal();
        int x = totalIssueNo / 100 + 1;
        int y = 0;
        ArrayList<Issue> fullList = new ArrayList<>();

        for (int i=0; i<x; i++) {
            Iterable<Issue> issueList = getPartialIssueList(jiraRestClient, "project = EISD", 100, y).claim().getIssues();
            issueList.forEach(fullList::add);
            y = y + 100;
        }

        DateTime date = DateTime.now();

        String newSubject = "";



        for (Issue issue : fullList) {
            if (issue.getSummary().equals(input)) {
                newSubject = "[JIRA] " + "(" + issue.getKey() + ") " + input;
                Comment comment = new Comment(issue.getCommentsUri(), "test comment sale", issue.getReporter(), issue.getReporter(), date, date, null, issue.getId());
                jiraRestClient.getIssueClient().addComment(issue.getCommentsUri(), comment).claim();
            }
        }

        return fullList;
    }

    public Issue getIssueByKey(String issueKey) throws Exception {
        final URI jiraServerUri = new URI(JIRA_URL);
        final JiraRestClient restClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(jiraServerUri, JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);
        Promise issuePromise = restClient.getIssueClient().getIssue(issueKey);
        return Optional.ofNullable((Issue) issuePromise.claim()).orElseThrow(() -> new Exception("No such issue"));
    }

    private Promise<SearchResult> getPartialIssueList(JiraRestClient jiraRestClient, String projectName, int maxResultNo, int startIndex) {
        return jiraRestClient.getSearchClient().searchJql(projectName, maxResultNo, startIndex, null);
    }


}

