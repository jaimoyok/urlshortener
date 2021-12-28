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
    
    val QUEUE = "QRCODE_queue"
    val EXCHANGE = "QRCODE_exchange"
    val ROUTING_KEY = "QRCODE_routingKey"

    @Bean
    fun queue() = Queue(QUEUE)

    @Bean
    fun exchange() = TopicExchange(EXCHANGE)

    @Bean
    fun binding() = BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY)

    @Bean
    fun converter() = Jackson2JsonMessageConverter()

    @Bean
    fun template(connectionFactory: ConnectionFactory): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = converter()
        return rabbitTemplate
    }
}