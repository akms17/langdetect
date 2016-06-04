package org.anon.langdetect

import java.io.{File, PrintWriter}

import org.rogach.scallop.ScallopConf

import scala.io.Source

object LanguageProfileGenerator extends App {

  val opts = new Opts(args)

  def genProfiles(inputDir: String
                , outputDir: String
                , maxChars: Int = 10) = {
    val dir = new File(inputDir)
    require(dir.isDirectory, "needed directory with language text examples")
    for (f <- dir.listFiles) {
      val language = f.getName
      val lines = Source.fromFile(f).getLines().mkString(" ")
      val profile = NGramProfileBuilder.build(lines, maxChars)
      val outFile = new File(s"$outputDir/$language.lm")
      new PrintWriter(outFile) {
        write(profile.take(300).mkString("\n"))
        close()
      }
    }
  }

  genProfiles(opts.samplesDir(), opts.profilesDir(), opts.maxChars())

  class Opts(args: Seq[String]) extends ScallopConf(args) {

    val samplesDir = opt[String](descr = "input dir with language samples", noshort = true, argName = "string")
    val profilesDir = opt[String](descr = "output dir for language profiles", noshort = true, argName = "string")
    val maxChars = opt[Int](descr = "max characters in ngram sliding window", default = Some(10), noshort = true, argName = "integer")
    banner( """Build language profiles from input samples""")
  }

}
