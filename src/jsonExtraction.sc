package org.talend.jira
import net.liftweb.json._
import scala.io.Source


/** Extract Objects from Jira json results.
*/
object jsonExtraction {
  val filename = "/home/scorreia/softwares/scala-SDK-3.0.4-2.11-2.11-linux.gtk.x86_64/workspace/jira/pmdq256.json"
                                                  //> filename  : String = /home/scorreia/softwares/scala-SDK-3.0.4-2.11-2.11-linu
                                                  //| x.gtk.x86_64/workspace/jira/pmdq256.json
  val str = Source.fromFile(filename).getLines.mkString
                                                  //> str  : String = {"expand":"names,schema","startAt":0,"maxResults":50,"total"
                                                  //| :1,"issues":[{"expand":"editmeta,renderedFields,transitions,changelog,operat
                                                  //| ions","id":"230696","self":"https://jira.talendforge.org/rest/api/2/issue/23
                                                  //| 0696","key":"PMDQ-256","fields":{"progress":{"progress":0,"total":864000,"pe
                                                  //| rcent":0},"summary":"Necessary refactoring to complete compliance with the A
                                                  //| pache license","issuetype":{"self":"https://jira.talendforge.org/rest/api/2/
                                                  //| issuetype/14","id":"14","description":"A new feature of the product, which h
                                                  //| as yet to be developed.","iconUrl":"https://jira.talendforge.org/images/icon
                                                  //| s/issuetypes/requirement.png","name":"New Feature","subtask":false},"customf
                                                  //| ield_10080":"-2147483648","customfield_10232":null,"customfield_10244":null,
                                                  //| "customfield_10235":null,"customfield_10234":null,"timespent":null,"reporter
                                                  //| ":{"self":"https://jira.talendforge.org/rest/api/2/user?username=ctoum","nam
                                                  //| e":"ctoum","emailAddress
                                                  //| Output exceeds cutoff limit.
  implicit val formats = DefaultFormats           //> formats  : net.liftweb.json.DefaultFormats.type = net.liftweb.json.DefaultFo
                                                  //| rmats$@31732b56

  abstract class AbstractFields{
    def summary: String
    def issuetype: IssueType
  }
  abstract class BaseIssue {
    def id: String
    def key: String
  }
  case class IssueLinks(id: String, outwardIssue: Issue)
  case class IssueType(name: String, subtask: Boolean)
  case class ProjectCategory(self: String, id: String, description: String, name: String)
  case class Project(key: String, name: String, projectCategory: ProjectCategory)
  case class Fields(val summary: String, val issuetype: IssueType, val issuelinks: List[IssueLinks] /*, project:Project*/ ) extends AbstractFields
  case class ParentFields(summary: String, issuetype: IssueType, issuelinks: List[IssueLinks], project: Project) extends AbstractFields
  case class Issue(id: String, key: String, fields: Fields) extends BaseIssue
  case class ParentIssue(val id: String, key: String, val fields: ParentFields) extends BaseIssue
  case class Result(expand: String, startAt: Int, issues: List[ParentIssue])

  val json = parse(str)                           //> json  : net.liftweb.json.JValue = JObject(List(JField(expand,JString(names,
                                                  //| schema)), JField(startAt,JInt(0)), JField(maxResults,JInt(50)), JField(tota
                                                  //| l,JInt(1)), JField(issues,JArray(List(JObject(List(JField(expand,JString(ed
                                                  //| itmeta,renderedFields,transitions,changelog,operations)), JField(id,JString
                                                  //| (230696)), JField(self,JString(https://jira.talendforge.org/rest/api/2/issu
                                                  //| e/230696)), JField(key,JString(PMDQ-256)), JField(fields,JObject(List(JFiel
                                                  //| d(progress,JObject(List(JField(progress,JInt(0)), JField(total,JInt(864000)
                                                  //| ), JField(percent,JInt(0))))), JField(summary,JString(Necessary refactoring
                                                  //|  to complete compliance with the Apache license)), JField(issuetype,JObject
                                                  //| (List(JField(self,JString(https://jira.talendforge.org/rest/api/2/issuetype
                                                  //| /14)), JField(id,JString(14)), JField(description,JString(A new feature of 
                                                  //| the product, which has yet to be developed.)), JField(iconUrl,JString(https
                                                  //| ://jira.talendforge.org
                                                  //| Output exceeds cutoff limit.
  val r = json.extract[Result]                    //> r  : org.talend.jira.jsonExtraction.Result = Result(names,schema,0,List(Par
                                                  //| entIssue(230696,PMDQ-256,ParentFields(Necessary refactoring to complete com
                                                  //| pliance with the Apache license,IssueType(New Feature,false),List(IssueLink
                                                  //| s(101264,Issue(224844,TDQ-9536,Fields(Complete the Apache license compatibi
                                                  //| lity of TOS DQ,IssueType(Work Item,false),List()))), IssueLinks(103539,Issu
                                                  //| e(233019,TDQ-9896,Fields(Download net.sourceforge.sqlexplorer.nl after inst
                                                  //| allation,IssueType(New Feature,false),List())))),Project(PMDQ,PM DQ,Project
                                                  //| Category(https://jira.talendforge.org/rest/api/2/projectCategory/10040,1004
                                                  //| 0,,Product Requirements))))))

  println("nb issues = " + r.issues.size)         //> nb issues = 1
  r.issues.foreach(i => {
    println(i.key)
    println(i.fields.summary)
    // println(i.fields.issuelinks.size + " linked issues")
    println("project = " + i.fields.project.name )
    val linkedIssues = i.fields.issuelinks
    linkedIssues.foreach(j => {
      println("linked to " + j.outwardIssue.key + " of type " + j.outwardIssue.fields.issuetype.name)
    })
    val features = linkedIssues.filter(_.outwardIssue.fields.issuetype.name.equalsIgnoreCase("New Feature"))

    features.foreach(k => println("Feature " + k.outwardIssue.key))
    //+ " in project " + k.outwardIssue.fields.project.name ))
  })                                              //> PMDQ-256
                                                  //| Necessary refactoring to complete compliance with the Apache license
                                                  //| project = PM DQ
                                                  //| linked to TDQ-9536 of type Work Item
                                                  //| linked to TDQ-9896 of type New Feature
                                                  //| Feature TDQ-9896
  // json.extract[List[Issues]]

}