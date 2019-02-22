package com.github.pukkaone.grapid.web.autoconfigure;

import com.github.pukkaone.grapid.core.VersionExecutor;
import com.github.pukkaone.grapid.core.VersionRouter;
import com.github.pukkaone.grapid.web.GraphQLController;
import java.util.Collection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configures GraphQL server.
 */
@ComponentScan(basePackageClasses = GraphQLController.class)
@Configuration
public class GraphQLServerAutoConfiguration {

  @Bean
  public VersionRouter versionRouter(Collection<VersionExecutor> versionExecutors) {
    return new VersionRouter(versionExecutors);
  }
}
