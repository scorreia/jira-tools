package org.talend.jira.internal
import net.liftweb.json._
import net.liftweb.json.JsonParser._
import org.talend.jira.JiraIssue

class JiraParser {

  val debug = true

  implicit val formats = DefaultFormats

  abstract class AbstractFields {
    def summary: String
    def issuetype: IssueType
    def status: Status
  }
  abstract class BaseIssue {
    def id: String
    def key: String
  }

  case class IssueLinks(id: String, outwardIssue: Option[Issue], inwardIssue: Option[Issue])
  case class IssueType(name: String, subtask: Boolean)
  case class ProjectCategory(self: String, id: String, description: String, name: String)
  case class Project(key: String, name: String, projectCategory: ProjectCategory)
  case class Status(name: String)
  case class Fields(val summary: String, val issuetype: IssueType, val issuelinks: List[IssueLinks] /*, project:Project*/ , val status: Status) extends AbstractFields
  case class ParentFields(summary: String, issuetype: IssueType, issuelinks: List[IssueLinks], project: Project, val status: Status) extends AbstractFields
  case class Issue(id: String, key: String, fields: Fields) extends BaseIssue
  case class ParentIssue(val id: String, key: String, val fields: ParentFields) extends BaseIssue
  case class Result(expand: String, startAt: Int, issues: List[ParentIssue])

  private def chooseInOutIssue(x: Option[Issue]) = x match {
    case Some(i) => i
    case None => null
  }

  /**
   * @param jiraIssue the given issue which has been used to query jira.
   * @param jsonSrc the json string resulting from the jira query query of the given issue
   * @return the updated jira issue
   */
  def extractJiraIssueFromJson(jiraIssue: JiraIssue, jsonSrc: String): JiraIssue = {

    val json = JsonParser.parse(jsonSrc)

    try {
      val r = json.extract[Result]
      if (r.issues.size > 1) println("WARNING: more than one issue in json. Expected only " + jiraIssue)
      r.issues.foreach(i => {
        if (i.key.compareTo(jiraIssue.issueKey) != 0) println("ERROR: found " + i.key + " in json. Expected: " + jiraIssue.issueKey)

        jiraIssue.summary = i.fields.summary
        jiraIssue.issueProject = i.fields.project.key
        jiraIssue.issueType = i.fields.issuetype.name
        jiraIssue.status = i.fields.status.name
        val linkedIssues = i.fields.issuelinks

        linkedIssues.foreach(k => {
          var inoutIssue = chooseInOutIssue(k.outwardIssue)
          if (inoutIssue == null) inoutIssue = chooseInOutIssue(k.inwardIssue)

          //          if (inoutIssue != null && // TODO externalize the filters here
          //            (issueTypes.contains(inoutIssue.fields.issuetype.name) ||
          //              inoutIssue.key.startsWith("DOCT") || // get all kinds of DOCT issues 
          //              inoutIssue.key.startsWith("QAI"))) { // get all kinds of QAI issues
          val j = new JiraIssue(inoutIssue.key)
          j.rootIssue = "" // reset root issue because it's a linked issue
          j.issueType = inoutIssue.fields.issuetype.name
          j.summary = inoutIssue.fields.summary
          j.status = inoutIssue.fields.status.name

          // TODO compute DOCT and QAI flags here?
          
          // TODO could filter out PM issue here? (currently done in QueryJira)
          jiraIssue.link(j)
          //          } else {
          //            if (debug) println("No issue linked to " + k.id)
          //          }
        })
      })
    } catch {
      case t: net.liftweb.json.MappingException => println("ERROR: Cannot parse json of " + jiraIssue.issueKey  + "\n" + jsonSrc) // todo: handle error
    }
    jiraIssue
  }

}