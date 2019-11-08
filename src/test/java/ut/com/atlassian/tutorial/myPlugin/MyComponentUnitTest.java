package ut.com.atlassian.tutorial.myPlugin;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.tutorial.myPlugin.impl.JiraImpl;

import io.atlassian.util.concurrent.Promise;
import org.junit.Test;
import com.atlassian.tutorial.myPlugin.api.MyPluginComponent;
import com.atlassian.tutorial.myPlugin.impl.MyPluginComponentImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Autowired
    private JiraImpl jira = new JiraImpl();
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }

    @Test
    public void test()
    {
        try {
            String input = "Предмет *0*10011-16845/2019";
            List<Issue> totalIssueList = jira.getAllIssues(input);

//            Issue issue = jira.getIssueByKey("EISD-73");



            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
