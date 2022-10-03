package just.khao.com.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@MapperScan(
        basePackages = {"com.formalhaut.fmh.repository.neo4j"},
        sqlSessionFactoryRef = "neo4jSqlSessionFactory")
public class Neo4jConfig {

    @Bean(name = {"neo4jDataSource"})
    public DataSource dataSource(
            @Qualifier("neo4jHikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = {"neo4jHikariConfig"})
    @ConfigurationProperties(prefix = "spring.datasource.neo4j.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = {"neo4jTxManager"})
    public PlatformTransactionManager txManager(
            @Qualifier("neo4jDataSource")DataSource dataSource) {

        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        transactionManager.setRollbackOnCommitFailure(true);
        return transactionManager;
    }

    @Bean(name = {"neo4jSqlSessionFactory"})
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("neo4jDataSource") DataSource dataSource) throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("/mybatis-config.xml"));
        return (SqlSessionFactory) sqlSessionFactoryBean.getObject();
    }
}

