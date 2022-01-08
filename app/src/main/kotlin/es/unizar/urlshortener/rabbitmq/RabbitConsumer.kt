package es.unizar.urlshortener.rabbitmq

import es.unizar.urlshortener.core.*
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Component
class RabbitConsumer(
    private val qrService: QRService,
    private val qrCodeRepository: CodeQRRepositoryService,
    private val reachabilityService: ReachabilityService,
    private val securityService: SecurityService,
    private val shortUrlRepository: ShortUrlRepositoryService
){
    @RabbitListener(queues = ["QRCODE_queue"])
    fun consumeMessageFromQueueQR(qrCode: QRCode2) {
        println("Message recieved from queue : ${qrCode.hash}")
        if(qrCodeRepository.findByKey(qrCode.hash)==null){
            val qr = qrService.generateQR("http://localhost:8080/tiny-${qrCode.hash}", qrCode.format)
            //Añadir directorio
            //val path: Path = Paths.get(System.getProperty("user.dir") + "/qrGenerated/tiny-${qrCode.hash}.png")
            //println("QR code generated at "+ path)
            //Files.write(path, qr)
            //Añadir en BD
            qrCodeRepository.save(QRCode(qrCode.hash, qrCode.format, qr))
            println("QR code saved at repository: ${qrCode.hash}")
        }
        else{
            println("QR is already saved at repository: ${qrCode.hash}")
        }
    }

    /* @RabbitListener(queues = ["SECURITY_queue"])
    fun consumeMessageFromQueueSeurity(secu: Secu) {

        var shortUrl = shortUrlRepository.findByKey(secu.hash)
        if(shortUrl != null){
            if(securityService.isSafe(shortUrl.redirection.target)){
                println("La url es segura")
                shortUrl.properties.safe = true
                shortUrlRepository.save(shortUrl)
            }
            else{
                shortUrlRepository.deleteById(secu.hash)
            }
        }
        else{
            println("Url no conocida, no se compruba seguridad")
        }
    } */

    @RabbitListener(queues = ["VALIDITY_queue"])
    fun consumeMessageFromQueueValid(valid: Valid) {
        var shortUrl = shortUrlRepository.findByKey(valid.hash)
        if(shortUrl != null){
            if(reachabilityService.isReachable(shortUrl.redirection.target)){
                println("La url es reachable")
                shortUrl.reachable = true
                if(securityService.isSafe(shortUrl.redirection.target)){
                    println("La url es segura")
                    shortUrl.properties.safe = true
                    Thread.sleep(5_000)
                    shortUrlRepository.save(shortUrl)
                }else{
                    shortUrlRepository.deleteById(valid.hash)
                }
            }
            else{
                shortUrlRepository.deleteById(valid.hash)
            }
        }
        else{
            println("Url no conocida, no se comprueba reach")
        }
    }
}