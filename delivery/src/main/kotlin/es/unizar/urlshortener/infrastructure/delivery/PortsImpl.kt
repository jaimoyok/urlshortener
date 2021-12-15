package es.unizar.urlshortener.infrastructure.delivery

import com.google.common.hash.Hashing
import es.unizar.urlshortener.core.HashService
import es.unizar.urlshortener.core.ValidatorService
import es.unizar.urlshortener.core.SecurityService
import net.minidev.json.JSONObject
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.web.client.RestTemplate
import java.nio.charset.StandardCharsets
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import java.util.Deque
import java.util.ArrayDeque
import java.net.URL
import java.net.HttpURLConnection
import java.net.UnknownHostException
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageConfig
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.CharacterSetECI
import com.google.zxing.qrcode.QRCodeWriter
import org.springframework.beans.factory.annotation.Value
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

//from https://stackoverflow.com/questions/41861449/kotlin-dsl-for-creating-json-objects-without-creating-garbage
fun json(build: JsonObjectBuilder.() -> Unit): JSONObject {
    return JsonObjectBuilder().json(build)
}

class JsonObjectBuilder {
    private val deque: Deque<JSONObject> = ArrayDeque()

    fun json(build: JsonObjectBuilder.() -> Unit): JSONObject {
        deque.push(JSONObject())
        this.build()
        return deque.pop()
    }

    infix fun <T> String.To(value: T) {
        deque.peek().put(this, value)
    }
}

/**
 * Implementation of the port [SecurityService].
 */

class SecurityServiceImpl : SecurityService {

    @Value("\${google.api-key}")
    lateinit var apiKey: String

    override fun isSafe(url: String): Boolean {
        val restTemplate: RestTemplate = RestTemplate()
        val ResourceUrl: String = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + apiKey;
        val headers: HttpHeaders  = HttpHeaders()
        val requestJson: JSONObject = json {
            "threatInfo" to json {
                "threatTypes" to arrayOf("MALWARE", "SOCIAL_ENGINEERING")
                "platformTypes" to "WINDOWS"
                "threatEntryTypes" to "URL"
                "threatEntries" to json {
                    "url" to url
                }
            }
        }
        val  entity: HttpEntity<JSONObject> = HttpEntity<JSONObject>(requestJson,headers)
        val response = restTemplate.postForObject(ResourceUrl, entity, JSONObject::class.java) 
        var safe: Boolean = false
        if (response!!.isEmpty()){
            safe = true
        }
        return safe
    }
}

class ReachabilityServiceImpl : ReachabilityService {
    override fun isReachable(url: String): Boolean {
        var res = false;
        try{
            val urlt = URL(url)

            val con = urlt.openConnection() as HttpURLConnection

            if (con.responseCode == 200){
                res = true;
            }
        }catch (e: Exception){
            
        }catch (uhe: UnknownHostException){
            throw UnreachableUrlException(url)
        }
        return res;
    }
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