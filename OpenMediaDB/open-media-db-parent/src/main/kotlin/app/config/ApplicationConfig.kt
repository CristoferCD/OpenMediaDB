package app.config

import DataManagerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ApplicationConfig {
    @Bean
    open fun dataManagerFactory(): DataManagerFactory {
        return DataManagerFactory()
    }
}