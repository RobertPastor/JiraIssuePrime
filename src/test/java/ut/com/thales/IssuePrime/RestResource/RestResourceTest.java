package ut.com.thales.IssuePrime.RestResource;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.thales.IssuePrime.RestResource.RestResource;
import com.thales.IssuePrime.RestResource.RestResourceModel;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.GenericEntity;

public class RestResourceTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {
        RestResource resource = new RestResource();

        Response response = resource.getMessage();
        final RestResourceModel message = (RestResourceModel) response.getEntity();

        assertEquals("wrong message","Hello World",message.getMessage());
    }
}
