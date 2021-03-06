package org.talend.jira
import java.io._
import java.net.URLEncoder

/**
 * @author scorreia
 * The main object to retrieve jira issues and all linked issues.
  *
  * 2016-06: update. Use of Epics and Epic links = cf[11071]
 */
object RetrieveJiraIssues {
  private var issueCount = 0

  /**
   * This methods expects 3 arguments: <br>
   * - the Jira login <br>
   * - the Jira password <br>
   * - the search query in JQL language <br>
   *
   * @param args the argument array.
   */
  def main(args: Array[String]) {


    // check minimum number of arguments
    val nbArgs = 3
    if (args.size < nbArgs) {
      println("Expected " + nbArgs + " arguments! \n Please, give the login, password to jira, a search query surrounded by single quotes and optionally a search depth")
      println("The search query can be for example: \n\"\"\"project = TDQ AND type = Epic AND fixVersion = '6.3 GA'\"\"\"")
      println("Given " + args.size + " arguments: ")
      args.foreach(println)
    } else {
      args.foreach(println)
      if (args.size == 4) run(args(0), args(1), args(2), Integer.valueOf(args(3)))
      else run(args(0), args(1), args(2))
    }
  }


  /**
   * Recursively retrieves linked query down to a given depth.
    *
    * @param user the Jira login
   * @param passwd the Jira password
   * @param userQuery the JQL query
   * @param depth the number of intermediate issues between two linked issues
   */
  def run(user: String, passwd: String, userQuery: String, depth: Int = 3) {
    println("Retrieving " + userQuery + " with depth " + depth)
    val startAt = System.currentTimeMillis()

    val issueTypes = List("New Feature", "Work Item", "Bug", "Epic")
    val query = new QueryJira(user, passwd, issueTypes, depth);


    val jiraIssues = query.queryIssues(userQuery)
    // val jiraIssues = List(new JiraIssue("TDQ-12192"))

    // present initial list of issues
    println("#############  List of issues to browse  #################")
    jiraIssues.foreach(j => println(j.issueKey))
    println()
    println("#################################################")

    val filename = "issues.tsv"
    val file = new File(filename)
    val writer = new PrintWriter(file)

    println("Writing results to file: " + file.getAbsoluteFile())
    println()

    // write header
    writer.write("Main issue\tRoot issue\t Linked issues\t Source \t Type \t Priority \t Summary \t Status \t Project\t has DOCT \thas QAI \t level  \t aggregate time estimate \t fixVersion(Max) \n")

//    jiraIssues.distinct.foreach(jiraIssue => {

      jiraIssues.foreach(jiraIssue => {
      val issues = query.browse(jiraIssue)
      writer.write(recursivePrint(jiraIssue))

      // print to console too
      println(recursivePrint(jiraIssue))
      println()

      // count issues
      issueCount += recursiveCount(jiraIssue)
    })
    writer.close()
    val time = System.currentTimeMillis() - startAt
    println("Done " + issueCount + " in " + time / 1000.0 + "s.")
  }

  private def recursiveCount(issue: JiraIssue): Int = {
    var c = 1
    if (!issue.linkedIssues.isEmpty) {
      issue.linkedIssues.foreach(l => c += recursiveCount(l))
    }
    c
  }

  /**
   * Recursively print all issues linked to the given Jira issue.
   *
   * @param issue the initial issue
   * @param isRoot whether it's the root issue
   * @return the string to print
   */
  private def recursivePrint(issue: JiraIssue, isRoot: Boolean = true): String = {
    val sb = new StringBuilder()
    if (isRoot) sb.append(issue.issueKey + "\t" + issue.toString + "\n")
    else sb.append("\t" + issue.toString + "\n")
    issue.linkedIssues.foreach(l => sb.append(recursivePrint(l, false)))
    sb.toString
  }


}