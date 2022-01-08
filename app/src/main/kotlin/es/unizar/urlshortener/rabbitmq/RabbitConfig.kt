package es.unizar.urlshortener.rabbitmq

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitConfig {
    //References: https://www.youtube.com/watch?v=o4qCdBR4gUM&ab_channel=JavaTechie
    
    val QUEUE_QR = "QRCODE_queue"
    val EXCHANGE_QR = "QRCODE_exchange"
    val ROUTING_KEY_QR = "QRCODE_routingKey"

    val QUEUE_SECURITY = "SECURITY_queue"
    val EXCHANGE_SECURITY = "SECURITY_exchange"
    val ROUTING_KEY_SECURITY = "SECURITY_routingKey"

    val QUEUE_REACH = "REACH_queue"
    val EXCHANGE_REACH = "REACH_exchange"
    val ROUTING_KEY_REACH = "REACH_routingKey"

    @Bean
    fun queueQR() = Queue(QUEUE_QR)
    @Bean 
    fun queueSecurity() = Queue(QUEUE_SECURITY)
    @Bean
    fun queueReach() = Queue(QUEUE_REACH)


    @Bean
    fun exchangeQR() = TopicExchange(EXCHANGE_QR)
    @Bean
    fun exchangeSecurity() = TopicExchange(EXCHANGE_SECURITY)
    @Bean
    fun exchangeReach() = TopicExchange(EXCHANGE_REACH)

    @Bean
    fun bindingQR() = BindingBuilder.bind(queueQR()).to(exchangeQR()).with(ROUTING_KEY_QR)
    @Bean
    fun bindingSecurity() = BindingBuilder.bind(queueSecurity()).to(exchangeSecurity()).with(ROUTING_KEY_SECURITY)
    @Bean
    fun bindingReach() = BindingBuilder.bind(queueReach()).to(exchangeReach()).with(ROUTING_KEY_REACH)


    @Bean
    fun converter() = Jackson2JsonMessageConverter()

    @Bean
    fun template(connectionFactory: ConnectionFactory): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = converter()
        return rabbitTemplate
    }
}