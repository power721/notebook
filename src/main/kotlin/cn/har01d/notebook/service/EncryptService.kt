package cn.har01d.notebook.service

import cn.har01d.notebook.core.Const
import cn.har01d.notebook.core.Error
import cn.har01d.notebook.core.exception.AppException
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

@Service
class EncryptService(private val configService: ConfigService) {
    var secretKey: String = configService.get(Const.SECRET_KEY, UUID.randomUUID().toString())

    @Scheduled(cron = "0 0 4 * * *")
    fun updateSecretKey() {
        configService.save(Const.SECRET_KEY, UUID.randomUUID().toString())
    }

    fun decrypt(data: String, sign: String, time: String): String {
        if (sign(data, time) != sign) {
            throw AppException("数据校验失败", Error.DATA_SIGN_ERROR)
        }

        try {
            val cipherData: ByteArray = Base64.getDecoder().decode(data)
            val salt = cipherData.copyOfRange(8, 16)
            val secret = "$secretKey-$time"
            val md5 = MessageDigest.getInstance("MD5")
            val keyAndIV =
                generateKeyAndIV(32, 16, 1, salt, secret.toByteArray(), md5)
            val key = SecretKeySpec(keyAndIV[0], "AES")
            val iv = IvParameterSpec(keyAndIV[1])
            val encrypted = cipherData.copyOfRange(16, cipherData.size)
            val aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding")
            aesCBC.init(Cipher.DECRYPT_MODE, key, iv)
            val decryptedData = aesCBC.doFinal(encrypted)
            return String(decryptedData)
        } catch (e: Exception) {
            throw AppException("数据解密失败", Error.DATA_DECRYPT_ERROR, e)
        }
    }

    fun encrypt(special: Boolean, data: ByteArray, sign: String, time: String): String {
        return try {
            val prefix = if (special) "Notebook" else secretKey
            val password = "$prefix-$sign-${time}5"
            val sr = SecureRandom()
            val salt = ByteArray(8)
            sr.nextBytes(salt)
            val keyAndIV = generateKeyAndIV(
                32, 16, 1, salt, password.toByteArray(),
                MessageDigest.getInstance("MD5")
            )
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", BouncyCastleProvider.PROVIDER_NAME)
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(keyAndIV[0], "AES"), IvParameterSpec(keyAndIV[1]))
            val combinedData = String(data) + sign
            val encryptedData = cipher.doFinal(combinedData.toByteArray())
            val prefixAndSaltAndEncryptedData = ByteArray(16 + encryptedData.size)
            System.arraycopy("Salted__".toByteArray(), 0, prefixAndSaltAndEncryptedData, 0, 8)
            System.arraycopy(salt, 0, prefixAndSaltAndEncryptedData, 8, 8)
            System.arraycopy(encryptedData, 0, prefixAndSaltAndEncryptedData, 16, encryptedData.size)
            Base64.getEncoder().encodeToString(prefixAndSaltAndEncryptedData)
        } catch (e: Exception) {
            throw AppException("数据加密失败", Error.DATA_ENCRYPT_ERROR, e)
        }
    }

    fun sign(data: String, salt: String): String {
        val md = MessageDigest.getInstance("MD5")
        md.update("$salt-$data".toByteArray())
        val digest: ByteArray = md.digest()
        return Base64.getEncoder().encodeToString(DatatypeConverter.printHexBinary(digest).lowercase().toByteArray())
    }

    fun generateKeyAndIV(
        keyLength: Int,
        ivLength: Int,
        iterations: Int,
        salt: ByteArray,
        password: ByteArray,
        md: MessageDigest
    ): Array<ByteArray?> {
        val digestLength = md.digestLength
        val requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength
        val generatedData = ByteArray(requiredLength)
        var generatedLength = 0
        return try {
            md.reset()

            while (generatedLength < keyLength + ivLength) {
                if (generatedLength > 0) md.update(generatedData, generatedLength - digestLength, digestLength)
                md.update(password)
                md.update(salt, 0, 8)
                md.digest(generatedData, generatedLength, digestLength)

                for (i in 1 until iterations) {
                    md.update(generatedData, generatedLength, digestLength)
                    md.digest(generatedData, generatedLength, digestLength)
                }
                generatedLength += digestLength
            }

            val result = arrayOfNulls<ByteArray>(2)
            result[0] = generatedData.copyOfRange(0, keyLength)
            if (ivLength > 0) result[1] = generatedData.copyOfRange(keyLength, keyLength + ivLength)
            result
        } finally {
            Arrays.fill(generatedData, 0.toByte())
        }
    }

}
