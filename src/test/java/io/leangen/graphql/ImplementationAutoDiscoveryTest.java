package io.leangen.graphql;

import graphql.schema.GraphQLSchema;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.types.GraphQLInterface;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests whether auto-discovered interface implementation types are correctly registered in the schema
 */
public class ImplementationAutoDiscoveryTest {

    @Test
    public void hiddenTypesTest() {
        GraphQLSchema schema = schemaFor(new ManualService());
        assertNull(schema.getType("One"));
        assertNull(schema.getType("Two"));
    }
    
    @Test
    public void explicitTypesTest() {
        GraphQLSchema schema = schemaFor(new ManualExplicitService());
        assertNotNull(schema.getType("One"));
        assertNotNull(schema.getType("Two"));
    }

    @Test
    public void discoveredTypesTest() {
        GraphQLSchema schema = schemaFor(new AutoService());
        assertNotNull(schema.getType("One"));
        assertNotNull(schema.getType("Two"));
    }
    
    @Test
    public void discoveredExplicitTypesTest() {
        GraphQLSchema schema = schemaFor(new AutoExplicitService());
        assertNotNull(schema.getType("One"));
        assertNotNull(schema.getType("Two"));
    }
    
    private GraphQLSchema schemaFor(Object service) {
        return new TestSchemaGenerator()
                .withOperationsFromSingleton(service)
                .generate();
    }
    
    @GraphQLInterface(name = "Manual")
    interface Manual {}
    
    @GraphQLInterface(name = "Auto", implementationAutoDiscovery = true)
    interface Auto {}

    public static class One implements Manual, Auto {
        public String getOne() {
            return "one";
        }
    }

    public static class Two implements Manual, Auto {
        public String getTwo() {
            return "two";
        }
    }

    public static class ManualService {
        @GraphQLQuery(name = "find")
        public Manual findFlat(@GraphQLArgument(name = "one") boolean one) {
            return one ? new One() : new Two();
        }
    }

    public static class ManualExplicitService extends ManualService {
        @GraphQLQuery(name = "one")
        public One findOne() {
            return new One();
        }
        
        @GraphQLQuery(name = "two")
        public Two findTwo() {
            return new Two();
        }
    }

    public static class AutoService {
        @GraphQLQuery(name = "find")
        public Auto findDeep(@GraphQLArgument(name = "one") boolean one) {
            return one ? new One() : new Two();
        }
    }

    public static class AutoExplicitService extends AutoService {
        @GraphQLQuery(name = "one")
        public One findOne() {
            return new One();
        }

        @GraphQLQuery(name = "two")
        public Two findTwo() {
            return new Two();
        }
    }
}
