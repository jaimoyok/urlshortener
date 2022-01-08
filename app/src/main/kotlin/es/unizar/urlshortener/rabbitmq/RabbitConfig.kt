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

    val QUEUE_VALIDITY = "VALIDITY_queue"
    val EXCHANGE_VALIDITY = "VALIDITY_exchange"
    val ROUTING_KEY_VALIDITY = "VALIDITY_routingKey"

    @Bean
    fun queueQR() = Queue(QUEUE_QR)
    @Bean 
    fun queueValidity() = Queue(QUEUE_VALIDITY)


    @Bean
    fun exchangeQR() = TopicExchange(EXCHANGE_QR)
    @Bean
    fun exchangeValidity() = TopicExchange(EXCHANGE_VALIDITY)

    @Bean
    fun bindingQR() = BindingBuilder.bind(queueQR()).to(exchangeQR()).with(ROUTING_KEY_QR)
    @Bean
    fun bindingValidity() = BindingBuilder.bind(queueValidity()).to(exchangeValidity()).with(ROUTING_KEY_VALIDITY)


    @Bean
    fun converter() = Jackson2JsonMessageConverter()

    @Bean
    fun template(connectionFactory: ConnectionFactory): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = converter()
        return rabbitTemplate
    }
}