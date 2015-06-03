package org.talend.jira.test
import net.liftweb.json._

object Test  {

  def main(args: Array[String]) {
    implicit val formats = DefaultFormats

    case class Child(name: String, age: Int, birthdate: Option[java.util.Date])
    case class Address(street: String, city: String)
    case class Person(name: String, address: Address, children: List[Child])
    val json = parse("""
         { "name": "joe",
           "address": {
             "street": "Bulevard",
             "city": "Helsinki"
           },
           "children": [
             {
               "name": "Mary",
               "age": 5
               "birthdate": "2004-09-04T18:06:22Z"
             },
             {
               "name": "Mazy",
               "age": 3
             }
           ]
         }
       """)
     var p  = json.extract[Person]
    
    println(p.name + " habite " + p.address.city + " et a " + p.children.size + " enfant(s)") 
    

  }

}