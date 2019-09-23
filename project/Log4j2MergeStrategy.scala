import sbtassembly.MergeStrategy
import java.io.{File, FileOutputStream}

import org.apache.logging.log4j.core.config.plugins.processor._

import scala.collection.JavaConverters.asJavaEnumerationConverter

object Log4j2MergeStrategy {

  val plugincache = new MergeStrategy {

    val name = "log4j2::plugincache"

    def apply(tempDir: File, path: String, files: Seq[File]): Either[String, Seq[(File, String)]] = {
      val file = MergeStrategy.createMergeTarget(tempDir, path)
      val out = new FileOutputStream(file)

      val aggregator = new PluginCache()
      val filesEnum = files.toIterator.map(_.toURI.toURL).asJavaEnumeration

      try {
        aggregator.loadCacheFiles(filesEnum)
        aggregator.writeCache(out)
        Right(Seq(file -> path))
      }
      finally {
        out.close()
      }
    }
  }
}
