package org.anon.langdetect

import org.scalatest.{FlatSpec, MustMatchers}

import scala.io.Source

class LangDetectionTest extends FlatSpec with MustMatchers {
  val inputProfilesDir = "src/test/resources/profiles"
  val testFilesLocation = "src/test/resources/articles"

  val langdetect = new LangDetect(inputProfilesDir)

  def textFromFile(file: String): String = Source.fromFile(file).getLines().mkString(" ")

  val articleExpectedLangMap = Map(
      textFromFile(s"$testFilesLocation/equipeArticle") -> "fr"
    , textFromFile(s"$testFilesLocation/elPaisArticle") -> "es"
    , textFromFile(s"$testFilesLocation/bbcArticle") -> "en"
    , textFromFile(s"$testFilesLocation/dieWeltArticle") -> "de"
    , textFromFile(s"$testFilesLocation/marcaArticle") -> "es"
  )

  "language detection" should "detect expected language for articles" in {
    articleExpectedLangMap.nonEmpty mustBe true
    articleExpectedLangMap.foreach {
      case(k,v) => langdetect.identifyLanguage(k)._1 mustBe v
    }
  }
}