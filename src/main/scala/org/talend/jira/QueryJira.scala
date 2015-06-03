package org.talend.jira

import java.net.URL
import java.security.cert.X509Certificate
import scala.collection.mutable.HashSet
import scala.io.Source
import org.talend.jira.internal.JiraParser
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import net.liftweb.json.JArray
import net.liftweb.json.JField
import net.liftweb.json.JString
import net.liftweb.json.JsonParser
import sun.misc.BASE64Encoder
import java.net.URLEncoder

/**
 * Jira Query helper.
 *  The max level at which we want to retrieve the linked issues.
 */
class QueryJira(jiraLogin: String, jiraPassword: String, issueTypes: List[String] = List("New Feature", "Work Item", "Bug"), var maxLevel: Int = 3) {

  private var rootLevel = 0
  private val queriedIssues: HashSet[JiraIssue] = new HashSet

  private def query(jqlString: String): String = {
    val rootUrl = "https://jira.talendforge.org/rest/api/2/search?jql=" // TODO  externalize the url

    val connection = new URL(rootUrl + jqlString).openConnection().asInstanceOf[HttpsURLConnection]
    val encoder = new BASE64Encoder()
    val credentials: String = encoder.encode((jiraLogin + ":" + jiraPassword).getBytes)
    connection.setRequestProperty("Authorization", "Basic " + credentials)
    val hv = new HostnameVerifier() {
      def verify(urlHostName: String, session: SSLSession) = true
    }
    connection.setHostnameVerifier(hv)
    val trustAllCerts = Array[TrustManager](new X509TrustManager() {
      def getAcceptedIssuers: Array[X509Certificate] = null
      def checkClientTrusted(certs: Array[X509Certificate], authType: String) {}
      def checkServerTrusted(certs: Array[X509Certificate], authType: String) {}
    })

    val sc = SSLContext.getInstance("SSL")
    sc.init(null, trustAllCerts, new java.security.SecureRandom())
    connection.setSSLSocketFactory(sc.getSocketFactory())
    val inputStream = connection.getInputStream
    val result = Source.fromInputStream(inputStream).mkString

    // connection.disconnect() // TODO here?
    result
  }

  /**
 * @param jqlString the query in JQL language.
 * @return the list of issues returned by the query
 */
def queryIssues(jqlString: String): List[JiraIssue] = {
    val issuesStr = this.query(URLEncoder.encode(jqlString, "UTF-8"))
    val json = JsonParser.parse(issuesStr)
    val allIssues = json \ "issues" \ "key"
    val all = for {
      JArray(o) <- allIssues
      JField("key", JString(key)) <- o
    } yield key

    all.map(key => new JiraIssue(key))

  }

  // TODO call and  test this method 
  private def queryLinkedJiraIssues(jiraIssue: JiraIssue, rootIssueKey: String): JiraIssue = {
    queriedIssues.add(jiraIssue)
    val src = query("key=" + jiraIssue.issueKey)
    new JiraParser().extractJiraIssueFromJson(jiraIssue, src)
    jiraIssue

  }

  /**
   * Recursive method to browse all linked Jira issues up to a given level.
   *
   * @param issue the issue from which we want to get the linked issues.
   * @param issueTypes the list of issue types to browse
   * @return all linked issues
   */
  def browse(issue: JiraIssue): HashSet[JiraIssue] = {
    browse(issue, issue.issueKey, 0)
  }

  /**
   * Recursive method to browse all linked Jira issues up to a given level.
   *
   * @param issue the issue from which we want to get the linked issues.
   * @param level the level at which we are browsing the linked issues.
   * @param rootIssueKey the key of the root issue
   * @return all linked issues
   */
  private def browse(issue: JiraIssue, rootIssueKey: String, level: Int = 0): HashSet[JiraIssue] = {
    if (level > this.maxLevel) HashSet()
    else {
      if (canBrowse(issue)) {
        // only keep not queried issues
        val linkedIssues = this.queryLinkedJiraIssues(issue, rootIssueKey).linkedIssues.filter(canBrowse)

        // println("\t\tbrowsing: " + issue.issueKey )
        linkedIssues.par.foreach(i => {
          i.rootIssue = rootIssueKey
          browse(i, rootIssueKey, level + 1)  // TODO maybe need to check canBrowse here instead of before...
          // println("csv;" + rootIssueKey + ";" + issue.issueKey + ";" + i.issueKey)
        })

        // avoid printing twice the same issues when different paths lead to the same issues.
        issue.linkedIssues = linkedIssues
        linkedIssues
      } else HashSet()

    }

  }

  private def canBrowse(issue: JiraIssue): Boolean = {
    val result = !queriedIssues.contains(issue) && // not already browsed (avoid loops)
      (
        (issue.sourceIssue.startsWith("TDQ") && (
            issue.issueKey .startsWith("DOCT") || issue.issueKey.startsWith("QAI"))) || // Doct and QAI issues having a TDQ as source issue
        (issue.issueKey.startsWith("TDQ") && issueTypes.contains(issue.issueType)) || // TDQ issue with appropriate type
        (issue.issueKey == issue.rootIssue)) // issue root issue    
    result
  }

}
