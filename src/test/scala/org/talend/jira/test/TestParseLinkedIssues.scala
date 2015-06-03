package org.talend.jira.test
import net.liftweb.json._
import scala.io.Source
import org.talend.jira.internal.JiraParser
import org.talend.jira.JiraIssue

object TestParseLinkedIssues {

  val files = Array(("pmdq256.json", "PMDQ-256"), ("TDQ-9794.json", "TDQ-9794"))

  def main(args: Array[String]): Unit = {

    files.foreach(s => {
      val filename = "./" + s._1
      val jsonSrc = Source.fromFile(filename).getLines.mkString
      val query = new JiraParser();
      val jiraIssue = new JiraIssue(s._2) // can put any jira name here actually
      // val issueTypes = List("New Feature", "Bug","Work item")
      query.extractJiraIssueFromJson(jiraIssue, jsonSrc)
      jiraIssue.linkedIssues.foreach(i => println("Issue: " + s._2 + " linked to " + i.issueKey))

    })

  }

}