package tools

import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


object Helper {
    fun hexToPartnerCode(hexStr: String): String? {
        val output = StringBuilder("")
        var i = 0
        while (i < hexStr.length) {
            val str = hexStr.substring(i, i + 2)
            output.append(str.toInt(16).toChar())
            i += 2
        }
        val code = when (output.isEmpty()) {
            true -> null
            false -> output.toString()
        }

        return when (code?.startsWith("partner~")) {
            true -> code
            else -> {
                println("$hexStr -> $code")
                println("- Partner code should start with: partner~")
                null
            }
        }
    }

    @Throws(java.lang.Exception::class)
    fun zipFolder(sourceFolderPath: Path, zipPath: Path) {
        val zos = ZipOutputStream(FileOutputStream(zipPath.toFile()))
        Files.walkFileTree(sourceFolderPath, object : SimpleFileVisitor<Path>() {
            @Throws(IOException::class)
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                zos.putNextEntry(ZipEntry(sourceFolderPath.relativize(file).toString()))
                Files.copy(file, zos)
                zos.closeEntry()
                return FileVisitResult.CONTINUE
            }
        })
        zos.close()
    }

}