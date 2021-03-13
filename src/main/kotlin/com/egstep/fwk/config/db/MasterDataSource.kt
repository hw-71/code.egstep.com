package com.egstep.fwk.config.db

import ch.qos.logback.classic.Logger
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.transaction.ChainedTransactionManager
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * 데이터 소스: DB에 접근하는 창구
 */
//@Profile("tmp")           --> @Profile(<profileName>): 해당 프로파일에서만 실행됨
@Configuration  // spring boot 가 실행될 때 읽는 파일
@EnableJpaRepositories(basePackages = ["com.egstep.code.repo.jpa"],
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "primaryTransactionManager")
class MasterDataSource {
    companion object {
        private val log = LoggerFactory.getLogger(MasterDataSource::class.java) as Logger
    }

    @Bean(name = ["dsMaster"])                                          // dsMaster 라는 이름으로 데이터소스 ds 빈 생성
    fun dsMaster(                                                       // @Value 파라미터는 application.yml 파일에서 읽어옴
        @Value("\${db.jpa.master.url}") url: String,
        @Value("\${db.common.minIdle}") minIdle: Int,
        @Value("\${db.common.maxPoolSize}") maxPoolSize: Int,
        @Value("\${db.common.idleTimeout}") idleTimeout: Long,
        @Value("\${db.master.userName}") userName: String,
        @Value("\${db.master.password}") password: String         // -> application-secret.yml에 정의 돼있어서 secret 프로파일일 때에만 읽어올 수 있음
    ): DataSource {
        log.info("=============== JPA Primary DataSource Setting Start =============== ")

        val ds = HikariDataSource()                                     // 히카리 데이터소스 생성
        ds.jdbcUrl = url
        ds.username = userName
        ds.password = password
        ds.minimumIdle = 5
        ds.maximumPoolSize = 100
        ds.idleTimeout = 3000
        ds.connectionInitSql = "set time zone 'Asia/Seoul'"

        log.info("=============== JPA Public DataSource Setting End   =============== ")

        return ds
    }

    /**
     * 엔티티 매니저 팩토리: jpa entity 를 관리
     */
    @Bean(name = ["entityManagerFactory"])
    @Order(Ordered.LOWEST_PRECEDENCE)
    fun entityManagerFactory(
        @Qualifier("dsMaster") dataSource: DataSource,
        @Value("\${db.common.dialect}") dialect: String,
        @Value("\${db.jpa.master.schema}") schema: String,
        @Value("\${db.jpa.master.ddl-auto}") ddlAuto: String,
        @Value("\${db.jpa.master.entity-packages}") entityPackages: Array<String>
    ): LocalContainerEntityManagerFactoryBean {

        val vendorAdapter = HibernateJpaVendorAdapter()
        vendorAdapter.setDatabase(Database.POSTGRESQL)
        vendorAdapter.setGenerateDdl(true)

        val properties: HashMap<String, Any> = hashMapOf()
        properties["hibernate.default_schema"] = schema
        properties["hibernate.hbm2ddl.auto"] = ddlAuto
        properties["hibernate.ddl-auto"] = ddlAuto
        properties["hibernate.dialect"] = dialect
        properties["hibernate.physical_naming_strategy"] = "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy"
        properties["hibernate.cache.use_second_level_cache"] = false
        properties["hibernate.cache.use_query_cache"] = false
        properties["hibernate.show_sql"] = false
        properties["javax.persistence.validation.mode"] = "none"

        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.jpaVendorAdapter = vendorAdapter
        em.setPackagesToScan(*entityPackages)
        em.setJpaPropertyMap(properties)

        return em
    }

    /**
     * 트랜잭션 매니저: 트랜잭션 관리
     */
    @Bean(name=["primaryTransactionManager"])
    @Order(Ordered.LOWEST_PRECEDENCE)
    fun transactionManager(@Qualifier("entityManagerFactory") entityManagerFactory: EntityManagerFactory,
                           @Qualifier("dsMaster") dataSource: DataSource
    ): PlatformTransactionManager {
        val jtm = JpaTransactionManager(entityManagerFactory)
        val dstm = DataSourceTransactionManager()
        dstm.dataSource = dataSource

        val ctm = ChainedTransactionManager(jtm, dstm)
        return ctm
    }


}


