package ut.com.thales.IssuePrime.JiraIssuePrimePlugin;

import org.junit.Test;
import com.thales.IssuePrime.JiraIssuePrimePlugin.api.MyPluginComponent;
import com.thales.IssuePrime.JiraIssuePrimePlugin.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}