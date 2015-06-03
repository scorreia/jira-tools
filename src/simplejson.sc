import net.liftweb.json._


object simplejson {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

implicit val formats = DefaultFormats             //> formats  : net.liftweb.json.DefaultFormats.type = net.liftweb.json.DefaultFo
                                                  //| rmats$@74dca977

  
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
       """)                                       //> json  : net.liftweb.json.JValue = JObject(List(JField(name,JString(joe)), JF
                                                  //| ield(address,JObject(List(JField(street,JString(Bulevard)), JField(city,JStr
                                                  //| ing(Helsinki))))), JField(children,JArray(List(JObject(List(JField(name,JStr
                                                  //| ing(Mary)), JField(age,JInt(5)), JField(birthdate,JString(2004-09-04T18:06:2
                                                  //| 2Z)))), JObject(List(JField(name,JString(Mazy)), JField(age,JInt(3)))))))))
json.extract[Person]                              //> res0: simplejson.Person = Person(joe,Address(Bulevard,Helsinki),List(Child(M
                                                  //| ary,5,Some(Sat Sep 04 20:06:22 CEST 2004)), Child(Mazy,3,None)))

}