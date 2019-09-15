package caliban

import caliban.GraphQL._
import caliban.TestUtils.{ resolverIO, QueryIO }
import caliban.schema.Schema.Typeclass
import caliban.schema.Schema
import zio.console.putStrLn
import zio.{ App, Runtime, UIO, ZIO }

object IntrospectionTestApp extends App {
  implicit val runtime: Runtime[Environment] = this

  val introspectionQuery =
    """
    {
      __schema {
        queryType {
          name
          description
        }
        types {
          name
          description
        }
      }
      __type(name: "Character") {
        name
        kind
        description
        fields {
          name
          type {
            name
            kind
            ofType {
              name
              kind
              ofType {
                name
                kind
              }
            }
          }
        }
      }
    }
    """

  implicit val schema: Typeclass[QueryIO] = Schema.gen[QueryIO]
  val graph: GraphQL[QueryIO]             = graphQL[QueryIO]

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    (for {
      result <- graph.execute(introspectionQuery, resolverIO)
      _      <- putStrLn(result.mkString("\n"))
    } yield ()).foldM(ex => putStrLn(ex.toString).as(1), _ => UIO.succeed(0))

//    """
//    query IntrospectionQuery {
//      __schema {
//        queryType { name }
//        mutationType { name }
//        subscriptionType { name }
//        types {
//          ...FullType
//        }
//        directives {
//          name
//          description
//          locations
//          args {
//            ...InputValue
//          }
//        }
//      }
//    }
//
//    fragment FullType on __Type {
//      kind
//      name
//      description
//      fields(includeDeprecated: true) {
//        name
//        description
//        args {
//          ...InputValue
//        }
//        type {
//          ...TypeRef
//        }
//        isDeprecated
//        deprecationReason
//      }
//      inputFields {
//        ...InputValue
//      }
//      interfaces {
//        ...TypeRef
//      }
//      enumValues(includeDeprecated: true) {
//        name
//        description
//        isDeprecated
//        deprecationReason
//      }
//      possibleTypes {
//        ...TypeRef
//      }
//    }
//
//    fragment InputValue on __InputValue {
//      name
//      description
//      type { ...TypeRef }
//      defaultValue
//    }
//
//    fragment TypeRef on __Type {
//      kind
//      name
//      ofType {
//        kind
//        name
//        ofType {
//          kind
//          name
//          ofType {
//            kind
//            name
//            ofType {
//              kind
//              name
//              ofType {
//                kind
//                name
//                ofType {
//                  kind
//                  name
//                  ofType {
//                    kind
//                    name
//                  }
//                }
//              }
//            }
//          }
//        }
//      }
//    }
//      """

}