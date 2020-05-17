package app.config

import DataManagerFactory
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ApplicationConfig {
    @Bean
    open fun dataManagerFactory(): DataManagerFactory {
        return DataManagerFactory()
    }

    @Bean
    open fun openApi() : OpenAPI {
        return OpenAPI().info(Info().title("Open media DB"))
    }
}