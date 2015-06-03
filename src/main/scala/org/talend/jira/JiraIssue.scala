package org.talend.jira

import scala.collection.mutable.HashSet

/** Jira Issue */
class JiraIssue(key: String) {
  val issueKey = key
  var issueProject = key.substring(0, key.indexOf('-'))
  var issueType = ""
  var summary = ""
  var priority = ""
  var status = ""
  var hasLinkedDoctIssue = false
  var hasLinkedQaiIssue = false
  var linkedIssues: HashSet[JiraIssue] = new HashSet
  /** root issue linked to this issue. Several other issues may exist that link this issue to the root issue. */
  var rootIssue = key
  /** source issue: direct link to the issue which is linked to this issue. Each issue should have only one source (when it's built from the QueryJira class). */
  var sourceIssue = key

  /**
   * same key means same jira issue.
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  override def equals(o: Any) = o match {
    case that: JiraIssue => that.issueKey.equalsIgnoreCase(this.issueKey)
    case _ => false
  }

  override def hashCode = issueKey.toUpperCase.hashCode

  /**
   * The issue is linked to another issue.
   * When the other issue is a DOCT or QAI, the appropriate boolean is set to true.
   *
   * @param another the other Jira issue to link with this one.
   * @return true if the element was not yet present in the set, false otherwise.
   */
  def link(another: JiraIssue): Boolean = {
    // check whether this issue is linked to Doct or QAI
    if (!hasLinkedDoctIssue && another.issueProject == "DOCT") this.hasLinkedDoctIssue = true
    if (!hasLinkedQaiIssue && another.issueProject == "QAI") this.hasLinkedQaiIssue = true

    another.sourceIssue = this.issueKey + "->" + another.sourceIssue

    linkedIssues.add(another)
  }

  /* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
  override def toString = {
    details
  }

  private def details(): String = {
    rootIssue + tabPrint(sourceIssue) + tabPrint(issueKey) + tabPrint(issueType) + tabPrint(priority) + tabPrint(summary) + tabPrint(status) + tabPrint(issueProject) + tabPrint(String.valueOf(hasLinkedDoctIssue)) + tabPrint(String.valueOf(hasLinkedQaiIssue))
  }

  private def tabPrint(str: String): String = {
    if (str != null && !str.isEmpty()) "\t" + str
    else ""
  }
}