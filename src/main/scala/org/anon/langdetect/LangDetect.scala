package org.anon.langdetect

import java.io.File

import org.rogach.scallop.ScallopConf
import org.log4s._
import scala.io.Source


/**
 * Based of the simple approach by Cavnar et al  [http://www.let.rug.nl/vannoord/TextCat/textcat.pdf]
 * Needs proper tokenization and hence may not be the best for east asian languages.
 */
class LangDetect(profilesDir: String)  {

  private[this] val logger = getLogger

  val profiles = readProfiles(new File(profilesDir))

  import NGramProfileBuilder._

  def identifyLanguage(text : String
                     , maxChars: Int = 10
                     , distance: (Profile, Profile) => Int = defaultDistance
                     ) : (String, Int) = {
    val textProfile = NGramProfileBuilder.build(text, maxChars).take(300)
    val profileScores = profiles.map {
      case(lang, prof) =>
        (lang, distance(textProfile, prof.filter(_.length<=maxChars)))
    }.sortWith(termFreqComparator)
    logger.info(s"Profile Scores : ${profileScores.map(x=> (x._1, 1000.0/x._2.toDouble)).mkString(",")}")
    profileScores.last
  }

  private def readProfiles(dir : File) : Seq[(String, Seq[String])] = {
    val profilesLoaded = dir.listFiles.map {
      f =>
        val languageName = f.getName.split('.').head
        val lines = Source.fromFile(f, "ISO-8859-1").getLines().toList
        languageName -> lines
    }.toSeq
    logger.info(s"Language profiles loaded ${profilesLoaded.map(_._1).mkString(",")}")
    profilesLoaded
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

object LangDetect extends App {

  val opts = new Opts(args)

  val langdetect = new LangDetect(opts.profilesDir())
  val input = Source.stdin.getLines().mkString(" ")
  val result = langdetect.identifyLanguage(input, opts.maxChars())
  Console.out.println(result._1)


  class Opts(args: Seq[String]) extends ScallopConf(args) {

    val profilesDir = opt[String](descr = "profiles dir for language detection", noshort = true, argName = "string")
    val maxChars = opt[Int](descr = "max characters in ngram sliding window", default = Some(10), noshort = true, argName = "integer")
    banner( """Reads text from stdin and outpts predicted language""")
  }

}
