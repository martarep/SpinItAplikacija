
package hr.algebra.jndi;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;


public class InitialDirContextCloseable extends InitialDirContext implements AutoCloseable {

    public InitialDirContextCloseable(Hashtable<?, ?> environment) throws NamingException {
        super(environment);
    }

}
