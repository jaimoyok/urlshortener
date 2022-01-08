package es.unizar.urlshortener
import es.unizar.urlshortener.core.usecases.*
import es.unizar.urlshortener.infrastructure.delivery.QRServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.HashServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.SecurityServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.ReachabilityServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.ValidatorServiceImpl
import es.unizar.urlshortener.infrastructure.repositories.*
import es.unizar.urlshortener.rabbitmq.RabbitConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Wires use cases with service implementations, and services implementations with repositories.
 *
 * **Note**: Spring Boot is able to discover this [Configuration] without further configuration.
 */
@Configuration
class ApplicationConfiguration(
    @Autowired val shortUrlEntityRepository: ShortUrlEntityRepository,
    @Autowired val clickEntityRepository: ClickEntityRepository,
    @Autowired val codeQREntityRepository: QRCodeEntityRepository
) {
    @Bean
    fun clickRepositoryService() = ClickRepositoryServiceImpl(clickEntityRepository)

    @Bean
    fun shortUrlRepositoryService() = ShortUrlRepositoryServiceImpl(shortUrlEntityRepository)

    @Bean
    fun validatorService() = ValidatorServiceImpl()

    @Bean
    fun codeQRRepositoryService() = CodeQRRepositoryServiceImpl(codeQREntityRepository)

    @Bean
    fun securityService() = SecurityServiceImpl()

    @Bean 
    fun reachabilityService() = ReachabilityServiceImpl()
    
    @Bean
    fun hashService() = HashServiceImpl()

    @Bean
    fun expiredService() = ExpiredUseCaseImpl(shortUrlRepositoryService())

    @Bean
    fun redirectUseCase() = RedirectUseCaseImpl(shortUrlRepositoryService(), expiredService())

    @Bean
    fun logClickUseCase() = LogClickUseCaseImpl(clickRepositoryService())

    @Bean
    fun createShortUrlUseCase() = CreateShortUrlUseCaseImpl(shortUrlRepositoryService(), validatorService(), hashService(), securityService(), reachabilityService(), qrUrlUseCase())

    @Bean
    fun qrService() = QRServiceImpl()

    @Bean
    //fun qrUrlUseCase() = QRGeneratorUseCaseImpl(shortUrlRepositoryService(), qrService())
    fun qrUrlUseCase() = QRGeneratorUseCaseImpl(shortUrlRepositoryService(), qrService(), reachabilityService(), codeQRRepositoryService())

    @Bean
    fun rbConsumer() = RabbitConsumer(qrService(), codeQRRepositoryService(), reachabilityService(), securityService(), shortUrlRepositoryService())

}