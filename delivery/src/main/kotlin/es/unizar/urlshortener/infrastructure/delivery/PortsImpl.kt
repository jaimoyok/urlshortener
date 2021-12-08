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
    override fun isSafe(url: String): Boolean {
        val restTemplate: RestTemplate = RestTemplate()
        val ResourceUrl: String = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=AIzaSyBdtrER5q0nYLD3l7-iJB7PynRL_xmUV3w";
        val headers: HttpHeaders  = HttpHeaders()
        val requestJson: JSONObject = json {
            "threatInfo" to  json {
                "threatTypes" to arrayOf("MALWARE", "SOCIAL_ENGINEERING")
                "platformTypes" to "WINDOWS"
                "threatEntryTypes" to "URL"
                "threatEntries" to  json {
                    "url" to url
                }
            }
        }
        val  entity: HttpEntity<JSONObject> = HttpEntity<JSONObject>(requestJson,headers)
        val response = restTemplate.postForObject(ResourceUrl, entity, JSONObject::class.java) 
        var safe: Boolean = false
        if (response!!.isEmpty())
            safe = true
        return safe
    }
}   

/**
 * Implementation of the port [HashService].
 */
@Suppress("UnstableApiUsage")
class HashServiceImpl : HashService {
    override fun hasUrl(url: String) = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString()
}