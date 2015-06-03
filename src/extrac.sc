package org.talend.jira

import scala.io.Source
import io.Source
import scala.collection.mutable.HashSet

import net.liftweb.json._
import net.liftweb.json.JsonParser

object extrac {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  var ji: JiraIssue = new JiraIssue("TDQ-452")    //> ji  : org.talend.jira.JiraIssue = 	TDQ-452	TDQ	false	false

   val src2 = """{"expand":"names,schema","startAt":0,"maxResults":50,"total":1,"issues":[{"expand":"editmeta,renderedFields,transitions,changelog,operations","id":"230691","self":"https://jira.talendforge.org/rest/api/2/issue/230691","key":"PMDQ-252","fields":{"progress":{"progress":0,"total":1152000,"percent":0},"summary":"Classification component on Spark","issuetype":{"self":"https://jira.talendforge.org/rest/api/2/issuetype/14","id":"14","description":"A new feature of the product, which has yet to be developed.","iconUrl":"https://jira.talendforge.org/images/icons/issuetypes/requirement.png","name":"New Feature","subtask":false},"customfield_10080":"-2147483648","customfield_10232":null,"customfield_10244":null,"customfield_10235":null,"customfield_10234":null,"timespent":null,"reporter":{"self":"https://jira.talendforge.org/rest/api/2/user?username=ctoum","name":"ctoum","emailAddress":"ctoum@talend.com","avatarUrls":{"16x16":"https://jira.talendforge.org/secure/useravatar?size=xsmall&avatarId=10192","24x24":"https://jira.talendforge.org/secure/useravatar?size=small&avatarId=10192","32x32":"https://jira.talendforge.org/secure/useravatar?size=medium&avatarId=10192","48x48":"https://jira.talendforge.org/secure/useravatar?avatarId=10192"},"displayName":"christophe toum","active":true},"customfield_10181":null,"updated":"2015-02-03T04:17:16.000-0600","created":"2014-12-10T05:18:04.000-0600","description":"We need a classification component. Classification is the bread and butter for ML and Predictive Analytics. Why? Because it takes the customer attributes into account, unlike recommendation engines that are based only on transactional data: clicks, purchases, likes, ratings, etc. Most Predictive Analytics apps that do churn prediction, x-sell / upsell, fraud detection, etc. are built on classification.","priority":{"self":"https://jira.talendforge.org/rest/api/2/priority/3","iconUrl":"https://jira.talendforge.org/images/icons/priorities/major.png","name":"Major","id":"3"},"issuelinks":[{"id":"104095","self":"https://jira.talendforge.org/rest/api/2/issueLink/104095","type":{"id":"10050","name":"Parent/Child","inward":"is child of","outward":"is parent of","self":"https://jira.talendforge.org/rest/api/2/issueLinkType/10050"},"outwardIssue":{"id":"234206","key":"TDQ-9956","self":"https://jira.talendforge.org/rest/api/2/issue/234206","fields":{"summary":"Create the API for the classification component","status":{"self":"https://jira.talendforge.org/rest/api/2/status/10004","description":"created but still not accepted","iconUrl":"https://jira.talendforge.org/images/icons/statuses/open.png","name":"New","id":"10004"},"priority":{"self":"https://jira.talendforge.org/rest/api/2/priority/4","iconUrl":"https://jira.talendforge.org/images/icons/priorities/minor.png","name":"Minor","id":"4"},"issuetype":{"self":"https://jira.talendforge.org/rest/api/2/issuetype/10","id":"10","description":"Work item for the scum process","iconUrl":"https://jira.talendforge.org/images/icons/issuetypes/improvement.png","name":"Work Item","subtask":false}}}},{"id":"101259","self":"https://jira.talendforge.org/rest/api/2/issueLink/101259","type":{"id":"10050","name":"Parent/Child","inward":"is child of","outward":"is parent of","self":"https://jira.talendforge.org/rest/api/2/issueLinkType/10050"},"outwardIssue":{"id":"230421","key":"TDQ-9794","self":"https://jira.talendforge.org/rest/api/2/issue/230421","fields":{"summary":"Create spark components for classification","status":{"self":"https://jira.talendforge.org/rest/api/2/status/10006","description":"","iconUrl":"https://jira.talendforge.org/images/icons/statuses/visible.png","name":"Accepted","id":"10006"},"priority":{"self":"https://jira.talendforge.org/rest/api/2/priority/4","iconUrl":"https://jira.talendforge.org/images/icons/priorities/minor.png","name":"Minor","id":"4"},"issuetype":{"self":"https://jira.talendforge.org/rest/api/2/issuetype/14","id":"14","description":"A new feature of the product, which has yet to be developed.","iconUrl":"https://jira.talendforge.org/images/icons/issuetypes/requirement.png","name":"New Feature","subtask":false}}}}],"customfield_10570":{"self":"https://jira.talendforge.org/rest/api/2/customFieldOption/10960","value":"0","id":"10960"},"customfield_10571":{"self":"https://jira.talendforge.org/rest/api/2/customFieldOption/10970","value":"0","id":"10970"},"subtasks":[],"customfield_10572":{"self":"https://jira.talendforge.org/rest/api/2/customFieldOption/10980","value":"0","id":"10980"},"customfield_10573":{"self":"https://jira.talendforge.org/rest/api/2/customFieldOption/10990","value":"0","id":"10990"},"status":{"self":"https://jira.talendforge.org/rest/api/2/status/10018","description":"","iconUrl":"https://jira.talendforge.org/images/icons/statuses/generic.png","name":"Scheduled","id":"10018"},"customfield_10670":null,"labels":["P1","R&D_Signoff"],"workratio":0,"customfield_10257":null,"customfield_10254":null,"customfield_10255":null,"customfield_10220":null,"customfield_10252":null,"customfield_10253":null,"customfield_10250":null,"customfield_10251":null,"project":{"self":"https://jira.talendforge.org/rest/api/2/project/10531","id":"10531","key":"PMDQ","name":"PM DQ","avatarUrls":{"16x16":"https://jira.talendforge.org/secure/projectavatar?size=xsmall&pid=10531&avatarId=10011","24x24":"https://jira.talendforge.org/secure/projectavatar?size=small&pid=10531&avatarId=10011","32x32":"https://jira.talendforge.org/secure/projectavatar?size=medium&pid=10531&avatarId=10011","48x48":"https://jira.talendforge.org/secure/projectavatar?pid=10531&avatarId=10011"},"projectCategory":{"self":"https://jira.talendforge.org/rest/api/2/projectCategory/10040","id":"10040","description":"","name":"Product Requirements"}},"environment":null,"customfield_10191":null,"aggregateprogress":{"progress":0,"total":1152000,"percent":0},"lastViewed":"2015-02-06T12:32:03.159-0600","customfield_10770":"9223372036854775807","components":[{"self":"https://jira.talendforge.org/rest/api/2/component/13934","id":"13934","name":"Components"}],"timeoriginalestimate":1152000,"customfield_10150":null,"customfield_10260":null,"votes":{"self":"https://jira.talendforge.org/rest/api/2/issue/PMDQ-252/votes","votes":0,"hasVoted":false},"customfield_10261":null,"customfield_10263":null,"resolution":null,"fixVersions":[{"self":"https://jira.talendforge.org/rest/api/2/version/14500","id":"14500","description":"","name":"6.0 GA","archived":false,"released":false,"releaseDate":"2015-06-15"}],"customfield_10264":null,"resolutiondate":null,"customfield_10211":null,"customfield_10210":null,"customfield_10122":null,"aggregatetimeoriginalestimate":1152000,"customfield_10123":null,"customfield_10258":null,"customfield_10124":{"self":"https://jira.talendforge.org/rest/api/2/customFieldOption/10043","value":"All","id":"10043"},"customfield_10970":null,"customfield_10972":null,"customfield_10971":null,"duedate":null,"customfield_10163":null,"watches":{"self":"https://jira.talendforge.org/rest/api/2/issue/PMDQ-252/watchers","watchCount":1,"isWatching":false},"customfield_11272":null,"customfield_10101":"Classification component on Spark","customfield_10375":null,"customfield_10100":"","customfield_10376":null,"customfield_11076":"9223372036854775807","customfield_10270":null,"customfield_10271":null,"customfield_10374":null,"customfield_11078":"0|i0m3un:","customfield_11077":"9223372036854775807","customfield_11072":"0|i0banx:","assignee":{"self":"https://jira.talendforge.org/rest/api/2/user?username=ctoum","name":"ctoum","emailAddress":"ctoum@talend.com","avatarUrls":{"16x16":"https://jira.talendforge.org/secure/useravatar?size=xsmall&avatarId=10192","24x24":"https://jira.talendforge.org/secure/useravatar?size=small&avatarId=10192","32x32":"https://jira.talendforge.org/secure/useravatar?size=medium&avatarId=10192","48x48":"https://jira.talendforge.org/secure/useravatar?avatarId=10192"},"displayName":"christophe toum","active":true},"customfield_11071":"PMDQ-251","customfield_11070":null,"customfield_10200":null,"customfield_10126":null,"aggregatetimeestimate":1152000,"customfield_10172":null,"customfield_10171":{"self":"https://jira.talendforge.org/rest/api/2/customFieldOption/10101","value":"Other","id":"10101"},"versions":[],"customfield_11170":"0|i0m32f:","timeestimate":1152000,"customfield_10179":null,"customfield_10030":null,"customfield_10177":null,"customfield_10174":null,"aggregatetimespent":null}}]}"""
                                                  //> src2  : String = {"expand":"names,schema","startAt":0,"maxResults":50,"tota
                                                  //| l":1,"issues":[{"expand":"editmeta,renderedFields,transitions,changelog,ope
                                                  //| rations","id":"230691","self":"https://jira.talendforge.org/rest/api/2/issu
                                                  //| e/230691","key":"PMDQ-252","fields":{"progress":{"progress":0,"total":11520
                                                  //| 00,"percent":0},"summary":"Classification component on Spark","issuetype":{
                                                  //| "self":"https://jira.talendforge.org/rest/api/2/issuetype/14","id":"14","de
                                                  //| scription":"A new feature of the product, which has yet to be developed.","
                                                  //| iconUrl":"https://jira.talendforge.org/images/icons/issuetypes/requirement.
                                                  //| png","name":"New Feature","subtask":false},"customfield_10080":"-2147483648
                                                  //| ","customfield_10232":null,"customfield_10244":null,"customfield_10235":nul
                                                  //| l,"customfield_10234":null,"timespent":null,"reporter":{"self":"https://jir
                                                  //| a.talendforge.org/rest/api/2/user?username=ctoum","name":"ctoum","emailAddr
                                                  //| ess":"ctoum@talend.com"
                                                  //| Output exceeds cutoff limit.

  val json2 = JsonParser.parse(src2)              //> json2  : net.liftweb.json.JValue = JObject(List(JField(expand,JString(names
                                                  //| ,schema)), JField(startAt,JInt(0)), JField(maxResults,JInt(50)), JField(tot
                                                  //| al,JInt(1)), JField(issues,JArray(List(JObject(List(JField(expand,JString(e
                                                  //| ditmeta,renderedFields,transitions,changelog,operations)), JField(id,JStrin
                                                  //| g(230691)), JField(self,JString(https://jira.talendforge.org/rest/api/2/iss
                                                  //| ue/230691)), JField(key,JString(PMDQ-252)), JField(fields,JObject(List(JFie
                                                  //| ld(progress,JObject(List(JField(progress,JInt(0)), JField(total,JInt(115200
                                                  //| 0)), JField(percent,JInt(0))))), JField(summary,JString(Classification comp
                                                  //| onent on Spark)), JField(issuetype,JObject(List(JField(self,JString(https:/
                                                  //| /jira.talendforge.org/rest/api/2/issuetype/14)), JField(id,JString(14)), JF
                                                  //| ield(description,JString(A new feature of the product, which has yet to be 
                                                  //| developed.)), JField(iconUrl,JString(https://jira.talendforge.org/images/ic
                                                  //| ons/issuetypes/requirem
                                                  //| Output exceeds cutoff limit.

  val issues2 = json2 \ "issues" \ "fields" \ "issuelinks"
                                                  //> issues2  : net.liftweb.json.JsonAST.JValue = JArray(List(JObject(List(JFiel
                                                  //| d(id,JString(104095)), JField(self,JString(https://jira.talendforge.org/res
                                                  //| t/api/2/issueLink/104095)), JField(type,JObject(List(JField(id,JString(1005
                                                  //| 0)), JField(name,JString(Parent/Child)), JField(inward,JString(is child of)
                                                  //| ), JField(outward,JString(is parent of)), JField(self,JString(https://jira.
                                                  //| talendforge.org/rest/api/2/issueLinkType/10050))))), JField(outwardIssue,JO
                                                  //| bject(List(JField(id,JString(234206)), JField(key,JString(TDQ-9956)), JFiel
                                                  //| d(self,JString(https://jira.talendforge.org/rest/api/2/issue/234206)), JFie
                                                  //| ld(fields,JObject(List(JField(summary,JString(Create the API for the classi
                                                  //| fication component)), JField(status,JObject(List(JField(self,JString(https:
                                                  //| //jira.talendforge.org/rest/api/2/status/10004)), JField(description,JStrin
                                                  //| g(created but still not accepted)), JField(iconUrl,JString(https://jira.tal
                                                  //| endforge.org/images/ico
                                                  //| Output exceeds cutoff limit.

 // issues2(0)
    case class Link(id: String, self:String)
  
  
val linkIssues = for {
		JArray(issueList) <- issues2
    JObject(o) <- issueList
    JField("self", JString(key)) <- o
  } yield (key)                                   //> linkIssues  : List[String] = List(https://jira.talendforge.org/rest/api/2/i
                                                  //| ssueLink/104095, https://jira.talendforge.org/rest/api/2/issueLink/101259)



  // jiraIssue.issueType = json \ "issues" \ "fields" \ "issuetype" \ "name"
  /*    jiraIssue.summary = compact(render(json \ "issues" \ "fields" \ "summary"))
    jiraIssue.priority = compact(render(json \ "issues" \ "fields" \ "priority" \ "name"))
    jiraIssue.status = compact(render(json \ "issues" \ "fields" \ "status" \ "name"))


    val linkedIssues = for {
      JObject(o) <- issues
     JField("key", JString(key)) <- o
      
      JField("name", JString(name)) <- o
    } yield (key, name)
    // println(linkedIssues)

    linkedIssues.foreach({case(key,name) => {
      //if (!"Bug".equalsIgnoreCase(jiraIssue.issueType) && !"Work Item".equalsIgnoreCase(jiraIssue.issueType))
      val j = new JiraIssue(key)
      j.issueType  = name
      jiraIssue.link(j)

    }})
    jiraIssue

*/

}