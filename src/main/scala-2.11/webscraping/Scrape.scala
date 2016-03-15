package webscraping

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.SimpleExtractor
import rcollection.map.HashMap

/**
  * Utility methods for scraping a site.
  */
object Scrape {


  val Sites = List(
    "arstechnica.com", "espn.com",
    "nationalgeographic.com", "wiredtohunt.com", "popsugar.com", "en.wikipedia.org/wiki/History_of_the_United_States",
    "en.wikipedia.org/wiki/Japan", "en.wikipedia.org/wiki/Tank",
    "en.wikipedia.org/wiki/Hair", "en.wikipedia.org/wiki/Circus", "en.wikipedia.org/wiki/Spider-Man")

  val browser = new Browser()

  /**
    * Scrapes all non-stop words from a web page.
    *
    * @param url
    * The core name of the url to scrape. Do not include : [https://] OR [.com].
    * @return
    * A sequence of all words that are not stop words in the body of the document.
    */
  def scrapeText(url: String): Seq[String] = {
    val doc = browser.get(s"http://$url")
    val allBodyText = doc >> SimpleExtractor("body")

    val filteredWords = allBodyText.filterNot((element) => StopWords.All.exists(element.equals(_)))

    filteredWords.map(_.replace(".", "")).head.split(' ')
  }

  def textFrequency(url: String): HashMap[CharSequence, Int] = {
    val map = new HashMap[CharSequence, Int]

    val allText = scrapeText(url)
    allText.foreach(insertOrIncrement(_, map))
    map
  }

  def insertOrIncrement(word: String, targetMap: HashMap[CharSequence, Int]): Unit = {
    targetMap.updateEither(word, eitherFunc)
  }

  def eitherFunc: Option[Int] => Int = {
    case Some(value) => value + 1
    case None => 1
  }

}
