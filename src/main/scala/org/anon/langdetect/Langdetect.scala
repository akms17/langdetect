package org.anon.langdetect

import java.io.File

import org.rogach.scallop.ScallopConf

import scala.io.Source


/**
 * Based of the simple approach by Cavnar et al  [http://www.let.rug.nl/vannoord/TextCat/textcat.pdf]
 * Needs proper tokenization and hence may not be the best for east asian languages.
 */
class Langdetect(profilesDir: String) {

  val profiles = readProfiles(new File(profilesDir))

  import NGramProfileBuilder._

  def identifyLanguage(text : String
                     , maxChars: Int = 10
                     , distance: (Profile, Profile) => Int = defaultDistance
                     ) : (String, Int) = {
    val textProfile = NGramProfileBuilder.build(text, maxChars).take(300)
    profiles.map {
      case(lang, prof) =>
        (lang, distance(textProfile, prof.filter(_.length<=maxChars)))
    }.sortWith(termFreqComparator).last
  }

  private def readProfiles(dir : File) : Seq[(String, Seq[String])] = {
    dir.listFiles.map {
      f =>
        val languageName = f.getName.split('.').head
        val lines = Source.fromFile(f, "ISO-8859-1").getLines().toList
        languageName -> lines
    }.toSeq
  }

  private def defaultDistance(p1: Profile, p2: Profile) : Int = {
    val max = 30
    val termDistances = for {key <- p1
                             rank = p1.indexOf(key)
                             otherRank = if (p2.contains(key)) p2.indexOf(key) else 999
                             diff = Math.abs(rank - otherRank) } yield Math.min(diff, max)
    termDistances.sum
  }

}

object Langdetect extends App {

  val opts = new Opts(args)

  val langdetect = new Langdetect(opts.profilesDir())
  val input = Source.stdin.getLines().mkString(" ")
  Console.out.println(langdetect.identifyLanguage(input, opts.maxChars()))


  class Opts(args: Seq[String]) extends ScallopConf(args) {

    val profilesDir = opt[String](descr = "profiles dir for language detection", noshort = true, argName = "string")
    val maxChars = opt[Int](descr = "max characters in ngram sliding window", default = Some(10), noshort = true, argName = "integer")
    banner( """Reads text from stdin and outpts predicted language""")
  }

}
