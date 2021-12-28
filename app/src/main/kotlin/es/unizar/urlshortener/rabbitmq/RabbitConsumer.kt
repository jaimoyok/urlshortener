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
    private val qrCodeRepository: CodeQRRepositoryService
){
    @RabbitListener(queues = ["QR_queue"])
    fun consumeMessageFromQueue(qrCode: QRCode2) {
        println("Message recieved from queue : ${qrCode.hash}")
        val qr = qrService.generateQR("http://localhost:8080/tiny-${qrCode.hash}", qrCode.format)
        //Añadir directorio
        //val path: Path = Paths.get(System.getProperty("user.dir") + "/qrGenerated/tiny-${qrCode.hash}.png")
        //println("QR code generated at "+ path)
        //Files.write(path, qr)
        //Añadir en BD
        qrCodeRepository.save(QRCode(qrCode.hash, qrCode.format, qr))
        println("QR code saved at repository: ${qrCode.hash}")
    }
}