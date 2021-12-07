package es.unizar.urlshortener.infrastructure.delivery

import com.google.common.hash.Hashing
import es.unizar.urlshortener.core.HashService
import es.unizar.urlshortener.core.ValidatorService
import es.unizar.urlshortener.core.SecurityService
import org.apache.commons.validator.routines.UrlValidator
import java.nio.charset.StandardCharsets
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageConfig
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.CharacterSetECI
import com.google.zxing.qrcode.QRCodeWriter
import es.unizar.urlshortener.core.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

/**
 * Implementation of the port [ValidatorService].
 */
class ValidatorServiceImpl : ValidatorService {
    override fun isValid(url: String) = urlValidator.isValid(url)
    companion object {
        val urlValidator = UrlValidator(arrayOf("http", "https"))
    }
}

/**
 * Implementation of the port [SecurityService].
 */
class SecurityServiceImpl : SecurityService {
    override fun isSafe(url: String) = true
    // companion object {
    //     val secure: Boolean = true
    //     // String uri = "https://webrisk.googleapis.com/v1/uris:search?threatTypes=MALWARE&uri=http%3A%2F%2Ftestsafebrowsing.appspot.com%2Fs%2Fmalware.html"
    //     // RestTemplate restTemplate = new RestTemplate()
    //     // Object[] response = restTemplate.getForObject(uri,Object[].class)
    //     // if (response.isEmpty()) secure = true
    // }
}

/**
 * Implementation of the port [HashService].
 */
@Suppress("UnstableApiUsage")
class HashServiceImpl : HashService {
    override fun hasUrl(url: String) = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString()
}

/**
 * Implementation of the port [QRService]
 */
class QRServiceImpl : QRService {
    override fun generateQR(url: String, format: QRFormat): ByteArray {
        println("Estoy en QRService")
        // Check Size
        if (format.height <= 0 || format.width <= 0) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Height and width must be greater than 0")
        }
        // Check colors
        val hexRegex = Regex("0x[0-9a-fA-F]{8}")
        if (!format.color.matches(hexRegex) || !format.background.matches(hexRegex)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Colors must be in hexadecimal format.")
        }
        // Check response type
        if (!arrayListOf("PNG", "JPEG").contains(format.typeImage)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "The image type must be 'PNG' or 'JPEG'.")
        }
        // Add options
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.ERROR_CORRECTION] = format.errorCorrectionLevel
        hints[EncodeHintType.CHARACTER_SET] = CharacterSetECI.UTF8
        // It can be a parameter
        //val margin = 5
        //hints[EncodeHintType.MARGIN] = if (format.height > format.width) 100 * margin / format.height else 100 * margin / format.width

        val qrImage: BufferedImage
        try {
            val color = format.color.substring(2).toLong(16).toInt()
            val background = format.background.substring(2).toLong(16).toInt()
            val qrCodeWriter = QRCodeWriter()
            qrImage = MatrixToImageWriter.toBufferedImage(
                qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, format.width, format.height, hints),
                MatrixToImageConfig(color, background)
            )
        } catch (e: WriterException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "QR encoding has failed")
        }

        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            ImageIO.write(qrImage, format.typeImage, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "QR image generation has failed")
        }
    }
}