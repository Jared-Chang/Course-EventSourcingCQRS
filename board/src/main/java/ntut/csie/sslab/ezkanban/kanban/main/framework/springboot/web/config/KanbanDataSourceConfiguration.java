package ntut.csie.sslab.ezkanban.kanban.main.framework.springboot.web.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration("DataSourceConfiguration")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"ntut.csie.sslab.ezkanban.kanban", "ntut.csie.sslab.ddd.adapter","ntut.csie.sslab.ddd.framework"},
        entityManagerFactoryRef = "kanbanEntityManagerFactory",
        transactionManagerRef= "kanbanTransactionManager"
)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class KanbanDataSourceConfiguration {

    public static final String [] ENTITY_PACKAGES  = {
            "ntut.csie.sslab.ezkanban.kanban",
            "ntut.csie.sslab.ddd.usecase"};

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.kanban")
    public DataSourceProperties kanbanDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.kanban.configuration")
    public DataSource kanbanDataSource() {
        return kanbanDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();

    }

    @Bean(name = "kanbanEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean kanbanEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(kanbanDataSource())
                .packages(ENTITY_PACKAGES)
                .build();
    }

    @Bean
    public PlatformTransactionManager kanbanTransactionManager(
            final @Qualifier("kanbanEntityManagerFactory") LocalContainerEntityManagerFactoryBean kanbanEntityManagerFactory) {
        return new JpaTransactionManager(kanbanEntityManagerFactory.getObject());
    }

}
