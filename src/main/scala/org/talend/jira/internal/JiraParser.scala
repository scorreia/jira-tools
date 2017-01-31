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
    def priority : Priority
  }
  abstract class BaseIssue {
    def id: String
    def key: String
  }

  case class IssueLinks(id: String, outwardIssue: Option[Issue], inwardIssue: Option[Issue])
  case class IssueType(name: String, subtask: Boolean)
  // case class ProjectCategory(self: String, id: String, description: String, name: String)
  case class Project(key: String, name: String/*, projectCategory: ProjectCategory*/)
  case class Status(name: String)
  case class Priority(name:String)
  case class FixVersion(name: String, releaseDate: Option[String])
  case class Fields(val summary: String, val issuetype: IssueType, val issuelinks: List[IssueLinks] ,
                    val status: Status, val priority: Priority, val timeestimate:Option[String],
                    val aggregatetimeestimate:Option[String]) extends AbstractFields
  case class ParentFields(summary: String, issuetype: IssueType, issuelinks: List[IssueLinks], project: Project,
                          val status: Status, val priority: Priority, val timeestimate:Option[String],
                          val aggregatetimeestimate:Option[String], val fixVersions: List[FixVersion]) extends AbstractFields
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
        if (i.key.compareTo(jiraIssue.issueKey) != 0) {
          // println("ERROR: found " + i.key + " in json. Expected: " + jiraIssue.issueKey)
          // main issue is not the given issue. This happens when the main issue is an issue in an Epic.
          // In this case, simply extract this issue and link it with the jiraIssue (that represents the Epic)
          val issueInEpic = createJiraIssue(i)
          jiraIssue.link(issueInEpic)

        } else { // it's a regular linked issue

        copyFieldsInformation(i, jiraIssue)

        // manage fixVersions
        val fixVersions = i.fields.fixVersions
        if (!fixVersions.isEmpty) jiraIssue.fixVersion = fixVersions.maxBy(f => f.name).name

        val linkedIssues = i.fields.issuelinks
        linkedIssues.foreach(k => {
          var inoutIssue = chooseInOutIssue(k.outwardIssue)
          if (inoutIssue == null) inoutIssue = chooseInOutIssue(k.inwardIssue)

          //          if (inoutIssue != null && // TODO externalize the filters here
          //            (issueTypes.contains(inoutIssue.fields.issuetype.name) ||
          //              inoutIssue.key.startsWith("DOCT") || // get all kinds of DOCT issues 
          //              inoutIssue.key.startsWith("QAI"))) { // get all kinds of QAI issues

          val j = createJiraIssue(inoutIssue)
          // TODO compute DOCT and QAI flags here?

          // TODO could filter out PM issue here? (currently done in QueryJira)
          jiraIssue.link(j)
          //          } else {
          //            if (debug) println("No issue linked to " + k.id)
          //          }
        })
      }
      })
    } catch {
      case t: net.liftweb.json.MappingException => println("ERROR: Cannot parse json of " + jiraIssue.issueKey + "\n" + jsonSrc, t) // todo: handle error
    }
    jiraIssue
  }


  private def copyFieldsInformation(src: ParentIssue, dest: JiraIssue) = {
    dest.summary = src.fields.summary
    dest.issueProject = src.fields.project.key
    dest.issueType = src.fields.issuetype.name
    dest.status = src.fields.status.name
    dest.priority = src.fields.priority.name
    dest.timeestimate = src.fields.timeestimate.getOrElse(dest.timeestimate)
    dest.aggregatetimeestimate = src.fields.aggregatetimeestimate.getOrElse(dest.aggregatetimeestimate)
    dest
  }

  private def createJiraIssue(src: Issue)  = {
    val dest = new JiraIssue(src.key)
    dest.rootIssue = "" // reset root issue because it's a linked issue
    dest.issueType = src.fields.issuetype.name
    dest.summary = src.fields.summary
    dest.status = src.fields.status.name
    dest.priority = src.fields.priority.name
    dest.timeestimate = src.fields.timeestimate.getOrElse(dest.timeestimate)
    dest.aggregatetimeestimate = src.fields.aggregatetimeestimate.getOrElse(dest.aggregatetimeestimate)
    dest
  }


  private def createJiraIssue(src: ParentIssue)  = {
    val dest = new JiraIssue(src.key)
    dest.rootIssue = "" // reset root issue because it's a linked issue
    dest.issueType = src.fields.issuetype.name
    dest.summary = src.fields.summary
    dest.status = src.fields.status.name
    dest.priority = src.fields.priority.name
    dest.timeestimate = src.fields.timeestimate.getOrElse(dest.timeestimate)
    dest.aggregatetimeestimate = src.fields.aggregatetimeestimate.getOrElse(dest.aggregatetimeestimate)
    dest
  }

}