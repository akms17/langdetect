package org.anon.langdetect

/**
  * Build a ngram profile for a given a piece of text.
  */
object NGramProfileBuilder {

  type TermFreq = (String, Int)
  type Profile = Seq[String]

  def build(text: String,  maxChars: Int) : Profile = {
    val punctuationCleanup = (s: String) => s.replaceAll("[^\\p{L}\\s]","")
    val whitespaceNormalize = (s: String) => s.trim().replaceAll("[\\s\\t]+"," ")
    val ngrams = extractNgrams(text, maxChars, punctuationCleanup.compose(whitespaceNormalize))
    ngrams.filter(_.nonEmpty)
      .groupBy(identity)
      .mapValues(c => c.length) //generate term frequencies
      .toSeq
      .sortWith(termFreqComparator) //sort by frequencies
      .map(_._1) //extract profile
  }


  // Extract the n-grams from a piece of text into a sequence. Spaces are represented as _.
  def extractNgrams(text: String
                  , maxChars : Int
                  , cleanText: (String) => String ) : Seq[String] = {
    val normalized = "_" + cleanText(text.toLowerCase).replace(" ","_") + "_"
    for {
      start <- 0 to normalized.length
      length <- 1 to maxChars
      if start+length <= normalized.length
    } yield normalized.substring(start, start+length)
  }

  def termFreqComparator(a : TermFreq, b : TermFreq) = a._2 > b._2
}
